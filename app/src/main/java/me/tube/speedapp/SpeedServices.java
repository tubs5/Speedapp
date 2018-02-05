package me.tube.speedapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.ArrayList;
/**
 * Created by Tubs on 2017-02-22.
 */

public class SpeedServices extends Service {
    final int noteid = 213;
    Location l = null;
    boolean active = true;
    ArrayList<ContentSaver> cs = new ArrayList<>();
    DataBaseManager dbm;
    int reset = 0;
    LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            l = location;
            cs.add(new ContentSaver(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed(), location.getTime()));
            Intent intent = new Intent("SpeedData");
            intent.putExtra("speed", (int) (location.getSpeed() * 3.6));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            if (reset++ >= 30) {
                save();
                cs.clear();
                reset = 0;
            }
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        dbm = new DataBaseManager();
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
        reset = 0;
        cs.clear();
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

    private void save() {
        dbm.saveData(cs);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        save();
        cs.clear();
        reset = 0;
    }
}
