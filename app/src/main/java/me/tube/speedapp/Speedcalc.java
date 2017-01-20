package me.tube.speedapp;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Tobias on 1/8/2017.
 */

public class Speedcalc extends IntentService implements LocationListener {
    private Location l = null;

    public Speedcalc(){
        super("Speedcalc");

    }
    public Speedcalc(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("starting now");
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Not engough permisions exiting");
            return;
        }
        System.out.println("got though permisions");
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != this.l){
            System.out.println(location.getSpeed());
            //double length = (location.getLatitude() - l.getLatitude())+(location.getLongitude()-l.getLongitude());


            l = location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
