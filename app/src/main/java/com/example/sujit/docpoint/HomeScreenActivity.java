package com.example.sujit.docpoint;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("CoE-Docket");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        navigationView =(NavigationView)findViewById(R.id.navigation_drawer);
        drawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

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

          /*  case R.id.menu_account:

                Intent menuAccount  = new Intent(HomeScreenActivity.this,MyProfileActivity.class);
                menuAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(menuAccount);

                return true;*/


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
}
