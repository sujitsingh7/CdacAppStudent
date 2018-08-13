package com.example.sujit.docpoint;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class termThreeFragment extends Fragment {

    TextView oneTextView,twoTextView,threeTextView,fourTextView,fiveTextView,sixTextView,sevenTextView;
    CardView oneCardView,twoCardView,threeCardView,fourCardView,fiveCardView,sixCardView,sevenCardView;

    SharedPreferences sharedPreferences3;


    DatabaseReference mDatabaseRef;

    String mCurrentUid,term,batch;
    String mPrnNo;

    String status;
    Intent intent;


    public termThreeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_term_three, container, false);


        mCurrentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


       // sharedPreferences3 = getActivity().getSharedPreferences("com.example.sujit.documentportal", Context.MODE_PRIVATE);


        oneTextView = v.findViewById(R.id.card_text_view_one);
        twoTextView = v.findViewById(R.id.card_text_view_two);
        threeTextView = v.findViewById(R.id.card_text_view_three);
        fourTextView = v.findViewById(R.id.card_text_view_four);
        fiveTextView = v.findViewById(R.id.card_text_view_five);
        sixTextView = v.findViewById(R.id.card_text_view_six);
        sevenTextView = v.findViewById(R.id.card_text_view_seven);

        oneCardView = v.findViewById(R.id.cardview1);
        twoCardView = v.findViewById(R.id.cardview2);
        threeCardView = v.findViewById(R.id.cardview3);
        fourCardView = v.findViewById(R.id.cardview4);
        fiveCardView = v.findViewById(R.id.cardview5);
        sixCardView=v.findViewById(R.id.cardview6);
        sevenCardView=v.findViewById(R.id.cardview7);


        oneTextView.setText("IT Service Management System");

        twoTextView.setText("Pervasive Computing");

        threeTextView.setText("Information System Design");

        fourTextView.setText("Android Programming");

        fiveTextView.setText("Management Development Program");

        sixTextView.setText("project");

        sevenTextView.setText("Mobile Communications");




       /* if(sharedPreferences3.getString("subject1","").length()>0)
        {

            oneTextView.setText(sharedPreferences3.getString("subject1",""));

            twoTextView.setText(sharedPreferences3.getString("subject2",""));

            threeTextView.setText(sharedPreferences3.getString("subject3",""));

            fourTextView.setText(sharedPreferences3.getString("subject4",""));

            fiveTextView.setText(sharedPreferences3.getString("subject5",""));

            sixTextView.setText(sharedPreferences3.getString("subject6",""));

        }


        mDatabaseRef.child("terms").child("term3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    oneTextView.setText(String.valueOf(dataSnapshot.child("subject1").child("name").getValue()));

                    twoTextView.setText(String.valueOf(dataSnapshot.child("subject2").child("name").getValue()));

                    threeTextView.setText(String.valueOf(dataSnapshot.child("subject3").child("name").getValue()));

                    fourTextView.setText(String.valueOf(dataSnapshot.child("subject4").child("name").getValue()));

                    fiveTextView.setText(String.valueOf(dataSnapshot.child("subject5").child("name").getValue()));

                    sixTextView.setText(String.valueOf(dataSnapshot.child("subject6").child("name").getValue()));


                    sharedPreferences3.edit().putString("subject1", String.valueOf(dataSnapshot.child("subject1").child("name").getValue())).apply();

                    sharedPreferences3.edit().putString("subject2", String.valueOf(dataSnapshot.child("subject2").child("name").getValue())).apply();

                    sharedPreferences3.edit().putString("subject3", String.valueOf(dataSnapshot.child("subject3").child("name").getValue())).apply();

                    sharedPreferences3.edit().putString("subject4", String.valueOf(dataSnapshot.child("subject4").child("name").getValue())).apply();

                    sharedPreferences3.edit().putString("subject5", String.valueOf(dataSnapshot.child("subject5").child("name").getValue())).apply();

                    sharedPreferences3.edit().putString("subject6", String.valueOf(dataSnapshot.child("subject6").child("name").getValue())).apply();







                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


*/

        //finding the term of the student....
        mDatabaseRef.child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mPrnNo = String.valueOf(dataSnapshot.child(mCurrentUid).getValue());
                    Log.i("mPrnNO",mPrnNo);

                    mDatabaseRef.child("users").child(mPrnNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                term = String.valueOf(dataSnapshot.child("term").getValue());
                                batch = String.valueOf(dataSnapshot.child("batch").getValue());
                                Log.i("term",term);


                                String term_string = "term"+"1";


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


        oneCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });

        twoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });

        threeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });
        fourCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });
        fiveCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });
        sixCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });






        return v;
    }
    public void onClicked(View view){


       final String tag = String.valueOf(view.getTag());

        if(term.equals(String.valueOf(3))) {

            mDatabaseRef.child("terms").child("term3").child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    status = String.valueOf(dataSnapshot.child("status").getValue());
                    String subject_name= String.valueOf(dataSnapshot.child("name").getValue());
                    if (!status.equals("coming")) {

                        intent = new Intent(getActivity(), StudyMaterialActivity.class);
                        intent.putExtra("term",String.valueOf(3));
                        intent.putExtra("batch",batch);
                        intent.putExtra("subject",tag);
                        intent.putExtra("subject_name",subject_name);

                        startActivity(intent);

                    } else {

                        Toast.makeText(getActivity(), "Sorry,You can't view the files right now!", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        else
        {
            Toast.makeText(getActivity(), "Please wait until you reach Term-III", Toast.LENGTH_SHORT).show();

        }





    }

}
