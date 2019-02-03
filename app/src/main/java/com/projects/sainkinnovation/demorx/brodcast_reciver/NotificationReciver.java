package com.projects.sainkinnovation.demorx.brodcast_reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.projects.sainkinnovation.demorx.services.FirebaseNotificationservices;

public class NotificationReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FirebaseNotificationservices.class));
    }
}
