package com.example.sujit.docpoint;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {


    DatabaseReference mRef;
    FirebaseAuth mAuth;
    String mCurrentUid,mPrn;
    String email,username,batch,term;


    TextView userNameTextView,emailTextView,batchTextView,termTextView,changePasswordTextView,verifyEmailTextView,prnTextView;
    ImageView editUsernameImage,editEmailImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUid=mAuth.getCurrentUser().getUid();

        userNameTextView=findViewById(R.id.userNameTextView);
        emailTextView=findViewById(R.id.emailTextView);



        editUsernameImage=findViewById(R.id.editUserNameButton);
        editEmailImage=findViewById(R.id.editEmailButton);

        prnTextView=findViewById(R.id.prnTextView);

        batchTextView=findViewById(R.id.batchTextView);
        termTextView=findViewById(R.id.termTextView);

        changePasswordTextView=findViewById(R.id.changePasswordTextView);
        verifyEmailTextView=findViewById(R.id.verifyEmailTextView);

        if(mAuth.getCurrentUser().isEmailVerified())
        {
            verifyEmailTextView.setClickable(false);
            verifyEmailTextView.setText("You are verified");



        }

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);

        mRef.child("uid").child(mCurrentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null)
                {
                    mPrn=String.valueOf(dataSnapshot.getValue());

                    if(!TextUtils.isEmpty(mPrn))
                    mRef.child("users").child(mPrn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot!=null)
                            {
                                email=String.valueOf(dataSnapshot.child("email").getValue());
                                batch=String.valueOf(dataSnapshot.child("batch").getValue());
                                term=String.valueOf(dataSnapshot.child("term").getValue());
                                username=String.valueOf(dataSnapshot.child("name").getValue());

                                emailTextView.setText(email);
                                batchTextView.setText(batch);
                                termTextView.setText(term);
                                userNameTextView.setText(username);
                                prnTextView.setText(mPrn);



                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        editEmailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(emailTextView.getText()))
                {

                    Intent intent= new Intent(MyProfileActivity.this,ChangeEmailUsernameActivity.class);
                    intent.putExtra("value",email);
                    intent.putExtra("type","email");
                    intent.putExtra("prn",mPrn);
                    startActivity(intent);



                }


            }
        });
        editUsernameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(userNameTextView.getText()))
                {

                    Intent intent= new Intent(MyProfileActivity.this,ChangeEmailUsernameActivity.class);
                    intent.putExtra("value",username);
                    intent.putExtra("type","username");
                    intent.putExtra("prn",mPrn);
                    startActivity(intent);


                }

            }
        });


        changePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent= new Intent(MyProfileActivity.this,ChangeEmailUsernameActivity.class);
                intent.putExtra("type","password");
                intent.putExtra("value",email);
                intent.putExtra("prn",mPrn);
                startActivity(intent);

            }
        });

        verifyEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyEmailTextView.getText().equals("You are verified"))

                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MyProfileActivity.this, "Verification link sent to "+mAuth.getCurrentUser().getEmail()+"Please sign in again!", Toast.LENGTH_LONG).show();

                            Intent intent= new Intent(MyProfileActivity.this,SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            mAuth.signOut();
                            startActivity(intent);
                        }
                        else{

                            Toast.makeText(MyProfileActivity.this, "Sorry Unable to send verification link ", Toast.LENGTH_SHORT).show();
                        }


                    }
                });




            }
        });





    }
}
