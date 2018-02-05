package me.tube.speedapp;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Tubs on 2018-02-02.
 */

public class ContentSaver {
    public double lat, log, height, speed;
    public long time;

    public ContentSaver(double lat, double log, double height, double speed, long time) {
        this.lat = lat;
        this.log = log;
        this.height = height;
        this.speed = speed;
        this.time = time;
    }

    public void saveData(DatabaseReference reference) {
        reference.child(time + "").child("lat").setValue(lat);
        reference.child(time + "").child("log").setValue(log);
        reference.child(time + "").child("height").setValue(height);
        reference.child(time + "").child("speed").setValue(speed);
    }


}
