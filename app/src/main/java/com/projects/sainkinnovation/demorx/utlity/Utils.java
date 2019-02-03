package com.projects.sainkinnovation.demorx.utlity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class Utils {

    public static boolean checkLocation(Context context){
       return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ;
    }
}
