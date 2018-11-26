package com.example.sujit.docpoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailUsernameActivity extends AppCompatActivity {

    String value,type;
    EditText editText;
    String mPrn;

    ProgressBar progressBar;
    DatabaseReference mRef;

    FirebaseUser user;

    Button updateButton;

    SharedPreferences prefs;

    String sharedPrefPassword,sharedPrefEmail;

    TextView resetTextView;

    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_username);
        value=getIntent().getStringExtra("value");
        type=getIntent().getStringExtra("type");
        mPrn=getIntent().getStringExtra("prn");
        updateButton=findViewById(R.id.update_button);
        editText=findViewById(R.id.valueEditText);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        passwordEditText=findViewById(R.id.passwdEditText);
        mRef= FirebaseDatabase.getInstance().getReference().child("users").child(mPrn);
        mRef.keepSynced(true);
        resetTextView=findViewById(R.id.resetTextView);

        prefs = getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);

        sharedPrefEmail=prefs.getString("email","");
        sharedPrefPassword=prefs.getString("password","");

        progressBar=findViewById(R.id.progressBar);
                user = FirebaseAuth.getInstance().getCurrentUser();


        if(type.equals("email"))

        {
            resetTextView.setText("Update your email -");
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            passwordEditText.setVisibility(View.VISIBLE);
            editText.setHint("Enter your new email");

            editText.setText(value);

        }
        if(type.equals("username"))

        {
            resetTextView.setText("Update username -");
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(value);
            editText.setHint("Enter your new Username");

        }
        if(type.equals("password"))
        {

            editText.setHint("Enter your password");
            resetTextView.setText("Update Password (min. 6 characters required!)");
            editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                if(type.equals("email"))
                {
//                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                    editText.setText(value);

                    if(!TextUtils.isEmpty(editText.getText())) {

                        if(!TextUtils.isEmpty(passwordEditText.getText())){


                        //update email and reauthenticate

                        final String email = String.valueOf(editText.getText()).trim();
                        user.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            mRef.child("email").setValue(email, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        ///FirebaseAuth.getInstance().signOut();
                                                        Toast.makeText(ChangeEmailUsernameActivity.this, "Email updated.Reauthenticating your credentials..!", Toast.LENGTH_SHORT).show();


                                                        AuthCredential credential = EmailAuthProvider
                                                                .getCredential(email, passwordEditText.getText().toString());

                                                        // Prompt the user to re-provide their sign-in credentials
                                                        user.reauthenticate(credential)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {


                                                                            Toast.makeText(ChangeEmailUsernameActivity.this, "Reauthentication Successful !", Toast.LENGTH_SHORT).show();




                                                                        } else {
                                                                            // Password is incorrect
                                                                            Toast.makeText(ChangeEmailUsernameActivity.this, "", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                      /*  Intent intent = new Intent(ChangeEmailUsernameActivity.this, SignInActivity.class);

                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);*/

                                                        progressBar.setVisibility(View.INVISIBLE);

                                                    } else {

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(ChangeEmailUsernameActivity.this, "Sorry,Database could not be updated !Please contact admin !", Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });

                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Log.i("error", task.getException().toString());
                                            Toast.makeText(ChangeEmailUsernameActivity.this, "Sorry! Email Could not be updated", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    }


                    }
                    else
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        editText.setError("Enter Email");
                    }

                }
                if(type.equals("username"))
                {
//                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
//                    editText.setText(value);

                    if(!TextUtils.isEmpty(editText.getText()))
                    {

                        mRef.child("name").setValue(String.valueOf(editText.getText()), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError==null)
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Intent intent  = new Intent(ChangeEmailUsernameActivity.this,MyProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                                else
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ChangeEmailUsernameActivity.this, "Sorry, Could not update username", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                    else
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        editText.setError("Enter Username");
                    }

                }
                if(type.equals("password"))
                {
//
//                    editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);

                    if(!TextUtils.isEmpty(editText.getText()))
                    {

                       final String password=String.valueOf(editText.getText()).trim();

                        user.updatePassword(password)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                           // Log.d(TAG, "User password updated.");

                                            mRef.child("password").setValue(password, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if(databaseError==null) {


                                                       // Toast.makeText(ChangeEmailUsernameActivity.this, "Password updated.Please Reauthenticate!", Toast.LENGTH_SHORT).show();
                                                       // FirebaseAuth.getInstance().signOut();
//                                                        Intent intent = new Intent(ChangeEmailUsernameActivity.this, SignInActivity.class);
//                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                        startActivity(intent);

                                                        AuthCredential credential = EmailAuthProvider
                                                                .getCredential(value,password);

                                                        // Prompt the user to re-provide their sign-in credentials
                                                        user.reauthenticate(credential)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful()){

                                                                            Toast.makeText(ChangeEmailUsernameActivity.this, "Password changed..User Reauthenticated!", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(ChangeEmailUsernameActivity.this, MyProfileActivity.class);
                                                                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            startActivity(intent);

                                                                        } else {
                                                                            // Password is incorrect
                                                                        }
                                                                    }
                                                                });
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                    }


                                                }
                                            });







                                        }
                                        else
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Log.i("error",task.getException().toString());
                                            Toast.makeText(ChangeEmailUsernameActivity.this, "Sorry Password could not be updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                    }
                    else
                    {
                        editText.setError("Enter Password");
                    }

                }
            }


        });


    }

}
