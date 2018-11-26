package com.example.sujit.docpoint;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

public class MyNotificationManager {
   private Context ctx;
    private static  MyNotificationManager mInstance;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int counter;
    public MyNotificationManager(Context ctx) {
        this.ctx = ctx;
    }
    public static synchronized MyNotificationManager getInstance(Context context)
    {
        if(mInstance==null)
            mInstance=new MyNotificationManager(context);

        return  mInstance;
    }

    public void displayNotification(String title,String message,String activity)
    {


        prefs = ctx.getSharedPreferences("com.example.sujit.docpoint", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if(!prefs.contains("counter"))
        {
            counter=1;
            Log.i("counter1",String.valueOf(counter));
        }
        else{
            counter = prefs.getInt("counter",0);
            Log.i("counter",String.valueOf(counter));
            counter++;


        }
        editor.putInt("counter",counter);
        editor.apply();




        Intent intent;

        createNotificationChannel();
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

//        if(activity.equals("SOMEACTIVITY")) {
//
//           intent = new Intent(ctx, NotificationActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
//        else if (activity.equals("MAINACTIVITY"))
//        {
//             intent = new Intent(ctx, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
//        else {
             intent = new Intent(ctx, NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // }
//        intent = new Intent(ctx, NotificationActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);






        Uri sound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.notificationsound);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, "channel_id")
                .setSmallIcon(R.drawable.ic_contract)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(sound)
                .setGroup("group")
        .setPriority(Notification.PRIORITY_HIGH);;

        if(Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);
        if(notificationManager!=null)
        {
            notificationManager.notify(1,mBuilder.build());
        }


//try {
//
//    LayoutInflater inflater = (LayoutInflater)ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    View view = inflater.inflate(R.layout.activity_home_screen, null);
//
//    NavigationView navigationView = view.findViewById(R.id.navigation_drawer);
//
//
//    Menu menuNav = navigationView.getMenu();
//    MenuItem menuNotification = menuNav.findItem(R.id.menu_notification);
//    int notification_counter = prefs.getInt("counter",0);
//    Log.i("counter_home_screen",String.valueOf(notification_counter));
//    if(notification_counter>=0)
//    {
//        ActionItemBadge.update((Activity) ctx, menuNotification, FontAwesome.Icon.faw_android, ActionItemBadge.BadgeStyles.DARK_GREY, notification_counter);
//    }
//
//}
//catch (Exception e)
//{
//    e.printStackTrace();
//    e.getCause();
//}




    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            channel.setLightColor(R.color.colorPrimaryDark);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);


            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
