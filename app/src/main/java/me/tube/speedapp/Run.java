package me.tube.speedapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Run extends AppCompatActivity {
    private Location l = null;
    List<Pair<Long, Double>> tfi = new ArrayList<Pair<Long, Double>>();
    TextView tw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        start();
        Chronometer cm = (Chronometer) findViewById(R.id.chronometer2);
        cm.start();
        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        });
        tw = (TextView) findViewById(R.id.speedtw);

    }

    private void start() {
        String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(perm, 5);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 50, ll);
    }
    LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
                l = location;
                tfi.add(new Pair<Long, Double>(System.currentTimeMillis(),location.getSpeed()*3.6));
                tw.setText(Double.toString(location.getSpeed()*3.6));
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
    @Override
    protected void onStop() {
        save();
        super.onStop();
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
    }
    private void save(){
        File f = getFilesDir();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        f = new File(f.getAbsoluteFile() + "/"+ sdf.format(c.getTime()));
        try {
            if (!f.createNewFile()) {
                System.out.println("Failed to create file");
                JsonArray jo = new JsonArray();
                for (Pair<Long,Double> p:tfi) {
                    JsonArray a = new JsonArray();
                    a.add(p.first);
                    a.add(p.second);
                    jo.add(a);
                }
                Gson gson = new Gson();
                gson.toJson(jo,new com.google.gson.stream.JsonWriter(new PrintWriter(f)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
