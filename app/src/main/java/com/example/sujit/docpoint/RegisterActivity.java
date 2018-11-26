package com.example.sujit.docpoint;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mRef, mRef1,mRef2,mRef3;


//    TextInputLayout mEmailTextInput, mPasswordTextInput, mUsernameTextInput, mPrnNoTextInput;

    EditText mEmailEditText, mPasswordEditText, mUsernameEditText, mPrnNoEditText;

    ProgressBar mProgressBar;
    Button signUpButton;

    String mEmail, mPassword, mUsername;
    String mPrnNo;
    String mTerm;
    ValueEventListener valueEventListener;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor;

//    RadioButton radioButton1, radioButton2, radioButton3;
//    RadioGroup radioGroup;

    Map userMap;
boolean hasChild;
Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userMap = new HashMap<String,Object>();
        context=this;

//        mEmailTextInput = findViewById(R.id.emailTextInputLayout);
//        mPasswordTextInput = findViewById(R.id.passwordTextInputLayout);
//        mUsernameTextInput = findViewById(R.id.usernameTextInputLayout);
//        mPrnNoTextInput = findViewById(R.id.prnTextInputLayout);

        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mUsernameEditText = findViewById(R.id.usernameEditText);
        mPrnNoEditText = findViewById(R.id.prnEditText);

        preferences = getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);
        editor = preferences.edit();



        mProgressBar = findViewById(R.id.progressBar);
//        radioButton1 = findViewById(R.id.radio_term1);
//        radioButton2 = findViewById(R.id.radio_term2);
//        radioButton3 = findViewById(R.id.radio_term3);
//        radioGroup = findViewById(R.id.radio_group);
        mRef2=FirebaseDatabase.getInstance().getReference().child("users");
       // mRef2.keepSynced(true);
        mRef3=FirebaseDatabase.getInstance().getReference().child("batch");

        signUpButton = findViewById(R.id.signUpRegisterButton);


        if(!new NetworkConnectionTest().isNetworkAvailable(context)) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You don't have an internet connection ")
                    .setCancelable(false)
                    .setPositiveButton("Connect to Internet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.setClickable(false);

                mUsername = mUsernameEditText.getText().toString();
                mEmail = mEmailEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                mPrnNo = mPrnNoEditText.getText().toString();

                if (!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword))
                {

                    if (validatePrn(mPrnNo)) {
                        mProgressBar.setVisibility(View.VISIBLE);

                       //checks if user already has a child with that prn...
                 valueEventListener = mRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(mPrnNo))
                                {
                                    hasChild=true;
                                }
                                else
                                {
                                    hasChild=false;

                                }

                                Log.i("hasChild",String.valueOf(hasChild));

                              //  mRef2.removeEventListener(valueEventListener);
                                register(hasChild);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    } else {

                        if (!validatePrn(mPrnNo))
                            mPrnNoEditText.setError("Wrong Prn No !");
                        else
                            Toast.makeText(RegisterActivity.this, "Term is required !", Toast.LENGTH_SHORT).show();

                    }


                }
                else {
                    if (TextUtils.isEmpty(mUsername))
                        mUsernameEditText.setError("Username is required !");
                    else if (TextUtils.isEmpty(mEmail))
                        mEmailEditText.setError("Email is required !");
                    else if (TextUtils.isEmpty(mPassword))
                        mPasswordEditText.setError("Password is required !");
                    else {

                    }
                }


            }
        });


    }
    public void register(boolean hasChild) {

        mRef2.removeEventListener(valueEventListener);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if(hasChild)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "You are already a registered user! Please Sign In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this,SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }


   else {

    mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                String uid = currentUser.getUid();

                String batch_substring = String.valueOf(mPrnNo).substring(0, 2);
                int batch_start = Integer.parseInt(batch_substring);
                int batch_end = batch_start + 3;
                String batch = "20" + String.valueOf(batch_start) + "-" + "20" + String.valueOf(batch_end);

                mRef = FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(mPrnNo));
                mRef1 = FirebaseDatabase.getInstance().getReference().child("uid").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mRef1.setValue(mPrnNo);
                mRef.keepSynced(true);
                String tokenId= FirebaseInstanceId.getInstance().getToken();

                userMap = new HashMap<>();
                userMap.put("name", mUsername);
                userMap.put("email", mEmail);
                userMap.put("password", mPassword);
                userMap.put("prn", mPrnNo);
                userMap.put("uid", uid);
                userMap.put("batch", batch);
                userMap.put("token",tokenId);

                editor.putString("mail",mEmail);
                editor.putString("password",mPassword);
                editor.commit();

               // int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
//                RadioButton radioButton = (RadioButton) findViewById(selectedId);
//                String tag = String.valueOf(radioButton.getTag());
//                userMap.put("term", tag);

             mRef3.child(batch).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if(dataSnapshot.exists())
                     {
                         String term=String.valueOf(dataSnapshot.getValue());
                         userMap.put("term",term.substring(4));
                         Log.i("term",dataSnapshot.getValue().toString());
                         mRef.setValue(userMap, new DatabaseReference.CompletionListener() {
                             @Override
                             public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                 if (databaseError == null) {
                                     mProgressBar.setVisibility(View.INVISIBLE);

                                     Intent intent;
                                     intent = new Intent(RegisterActivity.this, OnBoardActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                     startActivity(intent);
                                     finish();

                                 } else {
                                     mProgressBar.setVisibility(View.INVISIBLE);
                                     signUpButton.setClickable(true);
                                 }

                             }
                         });

                }
         }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });




            } else {
                signUpButton.setClickable(true);
            }


        }
    });
}

}

 public boolean validatePrn(String mPrnNo){

        String prn_string = String.valueOf(mPrnNo);
        char startindex=prn_string.charAt(0);
        Log.i("prn_substring", prn_string.substring(5,9));
        if(prn_string.length()==12 && prn_string.substring(5,9).equals("1072")&&startindex=='1')
            return true;
        else
            return false;

    }


}
