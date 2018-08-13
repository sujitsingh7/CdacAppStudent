package com.example.sujit.docpoint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    private ImageView splashImage;
    private ImageView splashImage1;
    private LinearLayout linearLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();

        splashImage = findViewById(R.id.splash_image);
        splashImage1=findViewById(R.id.splash_image1);
        linearLayout =findViewById(R.id.linearLayout);

        Animation animationUtils = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        linearLayout.startAnimation(animationUtils);


        Thread timer = new Thread()

        {
            @Override
            public void run() {
                try {
                    sleep(1500);
                    if(mCurrentUser == null)
                    {
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();

                    }



                    else {
                        startActivity(new Intent(MainActivity.this, HomeScreenActivity.class));
                        finish();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                super.run();
            }

        };
        timer.start();




    }
}
