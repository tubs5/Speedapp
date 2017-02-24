package me.tube.speedapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Tubs on 2017-02-22.
 */

public class SpeedServices extends Service {
    Location l = null;
    boolean active = true;
    JsonArray ja = new JsonArray();
    final int noteid = 213;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this,"SpeedService Started",Toast.LENGTH_LONG).show();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setUsesChronometer(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText("Working");
        builder.setContentTitle("Speed Service");
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(noteid,builder.build());
        start();
    }

    @Override
    public void onDestroy() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.err.println("no perms");
            return;
        }
        System.out.println("ending service");
        Toast.makeText(this,"SpeedService Ended",Toast.LENGTH_LONG).show();
        lm.removeUpdates(ll);
        save();
        active = false;
        Notification.Builder builder = new Notification.Builder(this);
        builder.setUsesChronometer(false);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText("Stopped");
        builder.setContentTitle("Speed Service");
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(noteid,builder.build());

        super.onDestroy();

    }


    private void start() {
        active = true;
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, ll);
    }

    LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            l = location;
            JsonArray ja2 = new JsonArray();
            ja2.add(System.currentTimeMillis());
            ja2.add((int)(location.getSpeed() * 3.6));
            Intent intent = new Intent("SpeedData");
            intent.putExtra("speed",(int)(location.getSpeed() * 3.6));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            ja.add(ja2);
            // tfi.add(new Pair<Long, Double>(System.currentTimeMillis(),location.getSpeed()*3.6));
            //tw.setText(Integer.toString((int)(location.getSpeed() * 3.6)));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void save(){
        File f = getFilesDir();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        f = new File(f.getAbsoluteFile() + "/"+ sdf.format(c.getTime()));
        try {
            if (!f.createNewFile()) {
                System.out.println("Failed to create file");
                return;
            }
            Gson gson = new Gson();
            PrintWriter pw = new PrintWriter(f);
            String js = gson.toJson(ja);
            System.out.println(js);
            pw.write(js);
            pw.flush();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
