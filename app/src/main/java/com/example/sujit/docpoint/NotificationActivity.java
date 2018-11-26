package com.example.sujit.docpoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView notificationRecyclerView;
    String batch;
    String prn;
    String uid;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<NotificationModel,NotificationActivity.ViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<NotificationModel> firebaseRecyclerOptions;
    private Query query;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        prefs = getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putInt("counter",0);
        editor.apply();

        notificationRecyclerView = findViewById(R.id.notification_recycler_view);
        mRef= FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getUid();
        Log.i("uid",uid);

        mRef.child("uid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    prn = String.valueOf(dataSnapshot.child(uid).getValue());
                    Log.i("prn",prn);
                    mRef.child("users").child(prn).child("batch").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                batch=String.valueOf(dataSnapshot.getValue());
                                Log.i("batch",batch);
                                initialize();
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




    }
    class ViewHolder  extends RecyclerView.ViewHolder{

        View itemView;
        TextView titleTextView,bodyTextView,dateTextView;
        public ViewHolder(View mview) {
            super(mview);
            itemView = mview;
            titleTextView=itemView.findViewById(R.id.notification_title_textview);
            bodyTextView=itemView.findViewById(R.id.notification_body_textview);
            dateTextView=itemView.findViewById(R.id.date_textview);
        }
    }

    public void initialize()
    {



        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationRecyclerView.setHasFixedSize(true);
        notificationRecyclerView.hasFixedSize();

        prefs = getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);



        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        notificationRecyclerView.setLayoutManager(mLayoutManager);

        query = FirebaseDatabase.getInstance().getReference().child("notifications").child(batch);

        query.keepSynced(true);
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<NotificationModel>()
                .setQuery(query, NotificationModel.class).setLifecycleOwner(this)
                .build();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot!=null)
                {
                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NotificationModel, NotificationActivity.ViewHolder>(firebaseRecyclerOptions) {
                        @Override
                        protected void onBindViewHolder(@NonNull NotificationActivity.ViewHolder holder, int position, @NonNull NotificationModel model) {

                            holder.titleTextView.setText(model.getTitle());

                            holder.dateTextView.setText("\u25CF "+model.getDate());
                            holder.bodyTextView.setText(model.getBody());

                            // holder.dateTextView.setText(model.getDate());

                        }

                        @NonNull
                        @Override
                        public NotificationActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_notification_layout, parent, false);
                            NotificationActivity.ViewHolder holder = new NotificationActivity.ViewHolder(view);
                            return holder;


                        }
                    };


                }
                else{
                    Toast.makeText(NotificationActivity.this, "Nothing to show .", Toast.LENGTH_SHORT).show();
                }

                notificationRecyclerView.setAdapter(firebaseRecyclerAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }
}
