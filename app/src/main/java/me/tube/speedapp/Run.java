package me.tube.speedapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
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
    private boolean active = false;
    // List<Pair<Long, Double>> tfi = new ArrayList<Pair<Long, Double>>();
    JsonArray ja = new JsonArray();
    TextView stw;
    TextView ptw;
    int points = 0;
    Intent i ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        Chronometer cm = (Chronometer) findViewById(R.id.chronometer2);
        cm.start();
        stw = (TextView) findViewById(R.id.speedtw);
        ptw = (TextView) findViewById(R.id.tw_points);
        LocalBroadcastManager.getInstance(this).registerReceiver(br,new IntentFilter("SpeedData"));
        startMyService();
        (findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(new Intent("SpeedServiceEnd"));
                stopService(i);




                startActivity(new Intent(view.getContext(), MainActivity.class));


            }
        });

    }
    void startMyService(){
        String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(checkCallingOrSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED && checkCallingOrSelfPermission(perm[1]) == PackageManager.PERMISSION_DENIED){
            requestPermissions(perm, 5);
        }

         i = new Intent(this,SpeedServices.class);
        startService(i);

    }


    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("SpeedData")){
                if (intent.getIntExtra("speed",-1) != -1){
                    stw.setText(String.valueOf(intent.getIntExtra("speed",0)));
                    ptw.setText(String.valueOf(++points));

                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        stopService(i);
        super.onDestroy();
    }

    /*
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

                startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        });
        tw = (TextView) findViewById(R.id.speedtw);
     /*   for (int i = 0; i < 10; i++) {
            JsonArray ja2 = new JsonArray();
            ja2.add(System.currentTimeMillis()+i*2000);
            ja2.add(i);
            ja.add(ja2);
        }
        */
    }

  /*  private void start() {
        active = true;
        String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(checkCallingOrSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED && checkCallingOrSelfPermission(perm[1]) == PackageManager.PERMISSION_DENIED){
            requestPermissions(perm, 5);
        }

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
            ja2.add(location.getSpeed() * 3.6);
            ja.add(ja2);
            // tfi.add(new Pair<Long, Double>(System.currentTimeMillis(),location.getSpeed()*3.6));
            tw.setText(Integer.toString((int)(location.getSpeed() * 3.6)));
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
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lm.removeUpdates(ll);
        save();
        active = false;

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Speed app stopped working");
        builder.setContentText("");
        nm.notify(237813,builder.build());


        super.onStop();
    }


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
    */

