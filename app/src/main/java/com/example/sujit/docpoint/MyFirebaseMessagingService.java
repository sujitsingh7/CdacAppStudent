package com.example.sujit.docpoint;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.sql.Timestamp;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    int counter;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title,message,activity;




        title="";
        message="";
        activity="";

        if(remoteMessage.getData().size()>0)
        {
            title = remoteMessage.getData().get("title");
             message = remoteMessage.getData().get("body");
             activity  = remoteMessage.getData().get("click_action");


        }

//
//           title = remoteMessage.getNotification().getTitle();
//            message = remoteMessage.getNotification().getBody();
//            activity = remoteMessage.getNotification().getClickAction();

       MyNotificationManager.getInstance(getApplicationContext()).displayNotification(title,message,activity);



    }

}
