package com.example.sujit.docpoint;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {


    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference mRef;

    TextInputLayout mPrnNoTextInput;

    EditText mPrnNoEditText;

    Button linkButton;
    String prnNo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mPrnNoEditText =findViewById(R.id.prnEditText);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //mPrnNoTextInput=findViewById(R.id.prnTextInputLayout);



        linkButton=findViewById(R.id.linkButton);
        mRef= FirebaseDatabase.getInstance().getReference();
        progressBar=findViewById(R.id.progressBar);

        mAuth  =FirebaseAuth.getInstance();
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(mPrnNoEditText.getText()))

                {
                    progressBar.setVisibility(View.VISIBLE);
                    prnNo = String.valueOf(mPrnNoEditText.getText());
                    mRef.child("users").child(String.valueOf(prnNo)).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            final String email = String.valueOf(dataSnapshot.getValue()).trim();
                            Log.i("email :", email);


                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                // mRef.child("users").child(prnNo).child("password").setValue();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Log.i("verification link sent", "true");
                                                Toast.makeText(ForgotPasswordActivity.this, "Verification Link Sent to " + email, Toast.LENGTH_LONG).show();


                                            } else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Log.i("link failed", task.getException().toString());
                                                Toast.makeText(ForgotPasswordActivity.this, "Verification Link could not be sent. Try again! ", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
    }
}
