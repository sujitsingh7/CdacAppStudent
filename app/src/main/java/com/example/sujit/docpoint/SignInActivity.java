package com.example.sujit.docpoint;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {

//    TextInputLayout mPasswordTextInput,mPrnNoTextInput;

    EditText mPasswordEditText,mPrnNoEditText;

    FirebaseAuth mAuth;
    DatabaseReference mRef;

    Button signInButton,registerText;

    String prn;
    String password;
    String email;

    ProgressBar mProgressBar;

    TextView forgotPasswordTextView;
    Context context;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);

        context=this;

//        mPasswordTextInput = findViewById(R.id.passwordTextInputLayout);
//        mPrnNoTextInput  = findViewById(R.id.prnTextInputLayout);
        registerText = findViewById(R.id.register_textview);

        mPrnNoEditText = findViewById(R.id.prnEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mProgressBar = findViewById(R.id.progressBar);
        signInButton = findViewById(R.id.signInButton);
        forgotPasswordTextView=findViewById(R.id.forgot_passowrd_textview);


         preferences = getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);
         editor = preferences.edit();

         if(!new NetworkConnectionTest().isNetworkAvailable(context)) {


             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setMessage("You don't have an internet connection ")
                     .setCancelable(false)
                     .setPositiveButton("Connect to Internet", new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                             startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
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



        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                prn = mPrnNoEditText.getText().toString();
                password = mPasswordEditText.getText().toString();


                if( !TextUtils.isEmpty(password)&&prn.length()==12) {


                    register();
                }
                else {
                    if(prn.length()!=12)
                        mPrnNoEditText.setError("Wrong Prn No !");

                    if(TextUtils.isEmpty(password))
                        mPasswordEditText.setError("Password is required !");
                    if(TextUtils.isEmpty(prn))
                        mPrnNoEditText.setError("Prn is required !");
                }
            }
        });
    }

    public void register()
    {
        mRef.child("users").child(prn).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                mProgressBar.setVisibility(View.VISIBLE);
                email = String.valueOf(snapshot.child("email").getValue());
                Log.i("email",email);
                Log.i("snapshot",snapshot.toString());
                signIn(email,password);
            }
            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(SignInActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signIn(final String email,final String password)
    {
        if(email.length()>0) {
            Log.i("signin","email.length>0");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                editor.putString("email",email);
                                editor.putString("password",password);
                                editor.commit();

                                Log.i("signin","signed in");
                                // Sign in success, update UI with the signed-in user's information
                                //  Log.d(TAG, "signInWithEmail:success");

                                String tokenId= FirebaseInstanceId.getInstance().getToken();

                                mRef.child("users").child(prn).child("token").setValue(tokenId, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                        if(databaseError==null)
                                        {
                                            Intent intent = new Intent(SignInActivity.this, HomeScreenActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            startActivity(intent);

                                        }else{
                                            mProgressBar.setVisibility(View.INVISIBLE);

                                            Log.i("error in signing",databaseError.getMessage());
                                            Toast.makeText(SignInActivity.this, "Error in signing in", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });






                            } else {
                                Log.i("signin","signing in unsuccessful");
                                mProgressBar.setVisibility(View.INVISIBLE);
                                // If sign in fails, display a message to the user.
                                // Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });


        }
        else
        {

            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"Error in signing in!",Toast.LENGTH_SHORT);
        }



    }
}
