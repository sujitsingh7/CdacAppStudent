package com.example.sujit.docpoint;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    TextInputLayout mPasswordTextInput,mPrnNoTextInput;

    EditText mPasswordEditText,mPrnNoEditText;

    FirebaseAuth mAuth;
    DatabaseReference mRef;

    Button signInButton;

    String prn;
    String password;
    String email;

    ProgressBar mProgressBar;

    TextView registerText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference();

        mPasswordTextInput = findViewById(R.id.passwordTextInputLayout);
        mPrnNoTextInput  = findViewById(R.id.prnTextInputLayout);
        registerText = findViewById(R.id.register_textview);

        mPrnNoEditText = findViewById(R.id.prnEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mProgressBar = findViewById(R.id.progressBar);
        signInButton = findViewById(R.id.signInButton);


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

    public void signIn(String email,String password)
    {
        if(email.length()>0) {
            Log.i("signin","email.length>0");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("signin","signed in");
                                // Sign in success, update UI with the signed-in user's information
                                //  Log.d(TAG, "signInWithEmail:success");
                                Intent intent = new Intent(SignInActivity.this, HomeScreenActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                startActivity(intent);


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
