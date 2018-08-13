package com.example.sujit.docpoint;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mRef, mRef1;


    TextInputLayout mEmailTextInput, mPasswordTextInput, mUsernameTextInput, mPrnNoTextInput;

    EditText mEmailEditText, mPasswordEditText, mUsernameEditText, mPrnNoEditText;

    ProgressBar mProgressBar;
    Button signUpButton;

    String mEmail, mPassword, mUsername;
    String mPrnNo;
    String mTerm;

    RadioButton radioButton1, radioButton2, radioButton3;
    RadioGroup radioGroup;

    Map userMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userMap = new HashMap<String,Object>();

        mEmailTextInput = findViewById(R.id.emailTextInputLayout);
        mPasswordTextInput = findViewById(R.id.passwordTextInputLayout);
        mUsernameTextInput = findViewById(R.id.usernameTextInputLayout);
        mPrnNoTextInput = findViewById(R.id.prnTextInputLayout);

        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mUsernameEditText = findViewById(R.id.usernameEditText);
        mPrnNoEditText = findViewById(R.id.prnEditText);


        mProgressBar = findViewById(R.id.progressBar);
        radioButton1 = findViewById(R.id.radio_term1);
        radioButton2 = findViewById(R.id.radio_term2);
        radioButton3 = findViewById(R.id.radio_term3);
        radioGroup = findViewById(R.id.radio_group);

        signUpButton = findViewById(R.id.signUpRegisterButton);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.setClickable(false);

                mUsername = mUsernameEditText.getText().toString();
                mEmail = mEmailEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                mPrnNo = mPrnNoEditText.getText().toString();

                if (!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword) && (radioButton1.isChecked() || radioButton2.isChecked() || radioButton3.isChecked())) {

                    if (validatePrn(mPrnNo)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        register();
                    } else {
                        if (!validatePrn(mPrnNo))
                            mPrnNoEditText.setError("Wrong Prn No !");
                        else
                            Toast.makeText(RegisterActivity.this, "Term is required !", Toast.LENGTH_SHORT).show();

                    }


                } else {
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
    public void register() {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    FirebaseUser currentUser =mAuth.getCurrentUser();
                    String uid = currentUser.getUid();

                    String batch_substring = String.valueOf(mPrnNo).substring(0,2);
                    int batch_start = Integer.parseInt(batch_substring);
                    int batch_end = batch_start +3;
                    String batch ="20"+String.valueOf(batch_start)+"-"+"20"+String.valueOf(batch_end);

                    mRef = FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(mPrnNo));
                    mRef1 = FirebaseDatabase.getInstance().getReference().child("uid").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mRef1.setValue(mPrnNo);
                    mRef.keepSynced(true);

                    userMap = new HashMap<>();
                    userMap.put("name",mUsername);
                    userMap.put("email",mEmail);
                    userMap.put("password",mPassword);
                    userMap.put("prn",mPrnNo);
                    userMap.put("uid",uid);
                    userMap.put("batch",batch);

                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                   RadioButton radioButton = (RadioButton) findViewById(selectedId);
                   String tag = String.valueOf(radioButton.getTag());
                   userMap.put("term",tag);

                   mRef.setValue(userMap, new DatabaseReference.CompletionListener() {
                       @Override
                       public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                           if(databaseError==null)
                           {
                               mProgressBar.setVisibility(View.INVISIBLE);

                               Intent intent;
                               intent = new Intent(RegisterActivity.this,HomeScreenActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                               startActivity(intent);
                               finish();

                           }
                           else
                           {
                               mProgressBar.setVisibility(View.INVISIBLE);
                               signUpButton.setClickable(true);
                           }

                       }
                   });

                }
                else{
                    signUpButton.setClickable(true);
                }


            }
        });
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
