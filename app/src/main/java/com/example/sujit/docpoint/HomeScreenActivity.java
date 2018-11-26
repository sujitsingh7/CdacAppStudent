package com.example.sujit.docpoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MaterialViewPager viewPager;

    ViewPager mViewPager;
  ImageButton backpressedButton;

  String batch;


    TabLayout mTabLayout;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int navigation_header;
    String header_name,header_email;

    DatabaseReference databaseReference;

    String mCurrentUid;


    NetworkConnectionTest networkConnectionTest;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        prefs = getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);
         editor = prefs.edit();
       // editor.putString(name, value);
       // editor.commit();



      networkConnectionTest = new NetworkConnectionTest();
       Context context = this;

        navigationView =(NavigationView)findViewById(R.id.navigation_drawer);



          mCurrentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
          editor.putString("uid",mCurrentUid);

          databaseReference = FirebaseDatabase.getInstance().getReference();
          databaseReference.keepSynced(true);
          databaseReference.child("uid").child(mCurrentUid).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  String prn = String.valueOf(dataSnapshot.getValue());
                  editor.putString("prn",prn);

                  databaseReference.child("users").child(prn).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                          batch = String.valueOf(dataSnapshot.child("batch").getValue());
                          String term=String.valueOf(dataSnapshot.child("term").getValue());
                          String term_string="term"+term;
                          header_name=String.valueOf(dataSnapshot.child("name").getValue());
                          header_email=String.valueOf(dataSnapshot.child("email").getValue());
                          editor.putString("batch",batch);
                          editor.putString("term",term);
                          editor.putString("term_string",term_string);

                          View headerView = navigationView.getHeaderView(0);

                          TextView headerName=headerView.findViewById(R.id.name_header);
                          TextView headerEmail = headerView.findViewById(R.id.email_header);
                          headerName.setText(header_name);
                          headerEmail.setText(header_email);


                          FirebaseMessaging.getInstance().subscribeToTopic(batch);

                          databaseReference.child("active_assignments").child(term_string).addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                  if(dataSnapshot.exists()) {
                                      String start_date = String.valueOf(dataSnapshot.child("start_date").getValue());
                                      String end_date = String.valueOf(dataSnapshot.child("end_date").getValue());
                                      String subject = String.valueOf(dataSnapshot.child("subject").getValue());
                                      String type = String.valueOf(dataSnapshot.child("type").getValue());
                                      editor.putString("start_date", start_date);
                                      editor.putString("end_date", end_date);
                                      editor.putString("subject", subject);
                                      editor.putString("type", type);
                                      editor.apply();


                                      databaseReference.child("notifications").child(batch).addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                              int counter = prefs.getInt("counter",0);
                                              counter++;
                                              editor.putInt("counter",counter);
                                              editor.apply();
                                              update();






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

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });



              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });


        viewPager = findViewById(R.id.mViewPager);

        drawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
       // mTabLayout = findViewById(R.id.tab_layout);

       // toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle("CoE-Docket");
        //setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar = viewPager.getToolbar();

        toolbar.setTitleTextColor(getResources().getColor(R.color.navigationBarColor));


       // toolbar.setElevation(15.0f);


        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);

        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = viewPager.getViewPager();
        mViewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.getPagerTitleStrip().setViewPager(viewPager.getViewPager());

        //mTabLayout.setupWithViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });











        navigationView.setNavigationItemSelectedListener(this);

//        KenBurnsView kbv = findViewById(R.id.image);
//
//        kbv.setTransitionListener(new KenBurnsView.TransitionListener() {
//            @Override
//            public void onTransitionStart(Transition transition) {
//
//            }
//            @Override
//            public void onTransitionEnd(Transition transition) {
//
//            }
//        });



       // mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());





        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();




        update();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerClose();

        switch(item.getItemId()) {

            case R.id.menu_subjects:
                Intent intent = new Intent(HomeScreenActivity.this,SubjectsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);
                return true;

           // case R.id.menu_downloads:
             //   Intent menuDownloadsIntent = new Intent(HomeScreenActivity.this,DownloadsActivity.class);
               // menuDownloadsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
             //   startActivity(menuDownloadsIntent);
              //  return true;

            case R.id.menu_account:

                Intent menuAccount  = new Intent(HomeScreenActivity.this,MyProfileActivity.class);
                menuAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(menuAccount);

                return true;

            case R.id.menu_attendance:
                Intent menuAttendance = new Intent(HomeScreenActivity.this,AttendanceResultActivity.class);
                menuAttendance.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                menuAttendance.putExtra("type","attendance");
                startActivity(menuAttendance);

                return true;


            case R.id.menu_schedule:

                Intent menuSchedule = new Intent(HomeScreenActivity.this,ScheduleActivity.class);
                menuSchedule.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                menuSchedule.putExtra("type","attendance");
                startActivity(menuSchedule);



                return true;

            case R.id.menu_result:


                Intent menuResult = new Intent(HomeScreenActivity.this,AttendanceResultActivity.class);
                menuResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                menuResult.putExtra("type","result");
                startActivity(menuResult);


                return true;


            case R.id.get_started:
                Intent getStarted  = new Intent(HomeScreenActivity.this,OnBoardActivity.class);
                getStarted.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(getStarted);



                return true;

            case R.id.about_us:

                Intent aboutUs  = new Intent(HomeScreenActivity.this,AboutUs.class);
                aboutUs.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aboutUs);



                return true;

            case R.id.about_cdac_dita:

                Intent cdacDita = new Intent(HomeScreenActivity.this,AboutCdacDita.class);
                cdacDita.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(cdacDita);


                return true;

            case R.id.menu_notification:
                Intent menuNotificationIntent = new Intent(HomeScreenActivity.this,NotificationActivity.class);
                menuNotificationIntent.putExtra("batch",batch);
                menuNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(menuNotificationIntent);
                return  true;



            case R.id.log_out:


                mAuth.signOut();
                startActivity(new Intent(HomeScreenActivity.this,SignInActivity.class));

                finish();
                return true;



        }

            return false;
    }


    public void drawerClose()

    {

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerClose();
        else
            super.onBackPressed();


    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void update(){


        Menu menuNav = navigationView.getMenu();
        MenuItem menuNotification = menuNav.findItem(R.id.menu_notification);
        int notification_counter = prefs.getInt("counter",0);
        Log.i("counter_home_screen",String.valueOf(notification_counter));
        if(notification_counter>=0)
        {

            Log.i("counter0",String.valueOf(notification_counter));
            ActionItemBadge.update(HomeScreenActivity.this, menuNotification, FontAwesome.Icon.faw_bell, ActionItemBadge.BadgeStyles.DARK_GREY, notification_counter);

        }



    }


}
