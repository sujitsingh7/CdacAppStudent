package com.example.sujit.docpoint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    SliderAdapter(Context context)
    {
        this.context=context;

    }


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(RelativeLayout)object;
    }

    public String[] slide_headings={
                    "  ASSIGNMENT",
            "STUDY MATERIAL",
            "AUTHENTICATION"
    };

    public String[] slide_desc={
            "While submitting your assignment file, make sure you have a pdf with name same as your prn number.\nYou also need to be connected with the college network.",
            "While downloading study materials you must have a good internet speed.It's better to download files using mobile data connection.If you use CyberRoam for this purpose make sure you are logged in.\n\n Note - If you use WPS Office to view documents,choose the option that works.(Normally two options are provided.)",
            "Signing in and registration needs to have a strong internet connection.Do use mobile data for this."
    };

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.onboard_walkthrough,container,false);

        TextView headingTextView,descTextView;

        headingTextView=view.findViewById(R.id.heading_text_view);
        descTextView=view.findViewById(R.id.desc_text_view);

        headingTextView.setText(slide_headings[position]);
        descTextView.setText(slide_desc[position]);


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);
    }
}
