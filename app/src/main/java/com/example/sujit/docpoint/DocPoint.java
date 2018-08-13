package com.example.sujit.docpoint;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class DocPoint extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
