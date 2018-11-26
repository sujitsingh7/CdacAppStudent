package com.example.sujit.docpoint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

//
//    private ImageView splashImage;
//    private ImageView splashImage1;
//    private LinearLayout linearLayout;


    ImageView splashImage;
    TextView splashTextView;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashImage=findViewById(R.id.splashimage);
splashImage.setTranslationY(-1000f);
splashImage.animate().translationYBy(1000f).setDuration(2000);

splashTextView=findViewById(R.id.splashTextView);

splashTextView.animate().alpha(1.0f).setDuration(2000);




        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();


      //  mCurrentUser.getProviderData().

//        splashImage = findViewById(R.id.splash_image);
//        splashImage1=findViewById(R.id.splash_image1);
//        linearLayout =findViewById(R.id.linearLayout);

//        Animation animationUtils = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
//        linearLayout.startAnimation(animationUtils);

        KenBurnsView kbv = (KenBurnsView) findViewById(R.id.image);


        AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
        RandomTransitionGenerator generator = new RandomTransitionGenerator(2000, ACCELERATE_DECELERATE);
        kbv.setTransitionGenerator(generator); //set new transition on kenburns view


        kbv.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {



            }

            @Override
            public void onTransitionEnd(Transition transition) {

                if(mCurrentUser == null)
                {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();

                }



                else {
                    startActivity(new Intent(MainActivity.this, HomeScreenActivity.class));
                    finish();
                }


            }
        });

      /*  Thread timer = new Thread()

        {
            @Override
            public void run() {
                try {
                    sleep(2200);
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
        */




    }
}
