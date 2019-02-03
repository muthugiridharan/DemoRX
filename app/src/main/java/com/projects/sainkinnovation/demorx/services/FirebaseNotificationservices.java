package com.projects.sainkinnovation.demorx.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.views.Dashboard;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class FirebaseNotificationservices extends FirebaseMessagingService {


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Log.i("TOKEN", "sendRegistrationToServer: token==>"+token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("Notification", "onMessageReceived: remote message==>"+remoteMessage);
        if(remoteMessage.getData().isEmpty()){
            notification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),null);
        }else{
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String,String> map=remoteMessage.getData();
        String tittle=map.get("tittle");
        String Content=map.get("Content");
        Intent notificationIntent = new Intent(getApplicationContext(), Dashboard.class);
        Bundle bundle=new Bundle();
        bundle.putString("Tittle",tittle);
        bundle.putString("Content",Content);
        notificationIntent.putExtra("New Data",bundle);
//**add this line**
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//**edit this line to put requestID as requestCode**
        PendingIntent contentIntent = PendingIntent.getActivity(this, new Random().nextInt(),notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),contentIntent);
    }

    private void notification(String tittle,String Content,PendingIntent pendingIntent){
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationChannelId="GIRI";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(notificationChannelId,"GIRI",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setDescription("GIRI CHANNEL");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,notificationChannelId)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.diet)
                        .setContentTitle(tittle)
                        .setContentText(Content)
                        .setTicker("Hearty365")
                        .setContentInfo("info");
        if(pendingIntent!=null){
            builder.setContentIntent(pendingIntent);
        }
        notificationManager.notify(new Random().nextInt(),builder.build());
    }
}
