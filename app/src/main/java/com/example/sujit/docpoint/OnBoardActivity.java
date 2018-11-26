package com.example.sujit.docpoint;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoardActivity extends AppCompatActivity {


    ViewPager mSlideViewPager;
    LinearLayout mLinearLayout;

    TextView[] mDots;
    SliderAdapter sliderAdapter;

    Button finishButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        finishButton=findViewById(R.id.finish_button);

        mSlideViewPager=findViewById(R.id.view_pager);
        mLinearLayout=findViewById(R.id.linearLayout);

        sliderAdapter=new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewPageListener);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(OnBoardActivity.this,HomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);



            }
        });

    }

   public void addDotsIndicator(int position)

   {
       mDots = new TextView[3];
       mLinearLayout.removeAllViews();

       for(int i=0;i<mDots.length;i++)
       {
           mDots[i]=new TextView(this);
           mDots[i].setText(Html.fromHtml("&#8226;"));
           mDots[i].setTextSize(35);

           mDots[i].setTextColor(getResources().getColor(R.color.backgroundColor));
           mLinearLayout.addView(mDots[i]);



       }
       if(mDots.length>0)
       {
           mDots[position].setTextColor(getResources().getColor(R.color.cyan));

       }




   }
   ViewPager.OnPageChangeListener viewPageListener = new ViewPager.OnPageChangeListener() {
       @Override
       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


       }

       @Override
       public void onPageSelected(int position) {
           addDotsIndicator(position);
           if(position==mDots.length -1)
           {
               finishButton.setVisibility(View.VISIBLE);
               finishButton.setEnabled(true);





           }

       }

       @Override
       public void onPageScrollStateChanged(int state) {

       }
   };
}
