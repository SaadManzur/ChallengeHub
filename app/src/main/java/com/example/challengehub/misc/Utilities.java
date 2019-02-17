package com.example.challengehub.misc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Utilities {

    public static final String SERVER = "169.234.122.85";

    public static String generateRequest(String... values){
        if(values.length % 2 == 1)
            return null;

        StringBuilder request = new StringBuilder();
        for(int i = 0; i < values.length; i++){
            try {
                request.append(URLEncoder.encode(values[i++],"UTF-8"));
                request.append("=");
                request.append(URLEncoder.encode(values[i],"UTF-8"));
                request.append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if(request.length() > 1){
            request.setLength(request.length()-1);
        }

        return request.toString();
    }

    public static boolean hasAllPermissions(Context context, String ... permssions) {

        for(String permission : permssions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }

        return true;
    }

    public static void requestPermissions(AppCompatActivity activity, int PERMISSION_REQUEST_ID,
                                          String ... permssions) {

        for(String permission : permssions) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                Toast.makeText(activity, "You need to grant access to use this feature.",
                        Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(activity,
                        permssions,
                        PERMISSION_REQUEST_ID);
            }
        }

    }
}
