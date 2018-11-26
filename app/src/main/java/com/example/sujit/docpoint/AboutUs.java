package com.example.sujit.docpoint;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {

   TextView descTextView,mentorTextView,developerTextView;

    Toolbar mToolbar;CollapsingToolbarLayout mCollapsingToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mToolbar =(Toolbar)findViewById(R.id.about_us_toolbar);
        mCollapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




//
      descTextView=findViewById(R.id.desc_text_view);
      mentorTextView=findViewById(R.id.mentor_text_view);
      developerTextView=findViewById(R.id.developer_text_view);

//
        descTextView.setText(getResources().getString(R.string.aboutus));
        mentorTextView.setText(getResources().getString(R.string.mentor));
        developerTextView.setText(getResources().getString(R.string.developer));

    }
}
