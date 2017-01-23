package me.tube.speedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RevData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rev_data);
        Intent intent = getIntent();
        String file = intent.getStringExtra("file");

        Gson gson = new Gson();
        JsonArray ja = gson.fromJson(file,JsonArray.class);
        if(ja == null) System.out.println("gson is null");

        ListView lv = (ListView) findViewById(R.id.listView2);
        ListViewPopulation(ja, lv);

        GraphView gv = (GraphView) findViewById(R.id.graph);
        gv.setTitle("speed");

        DataPoint[] dp = new DataPoint[ja.size()];
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < ja.size(); i++) {
            c.setTimeInMillis( ja.get(i).getAsJsonArray().get(0).getAsLong());
            dp[i] = new DataPoint(ja.get(i).getAsJsonArray().get(0).getAsLong(),ja.get(i).getAsJsonArray().get(1).getAsDouble());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        gv.addSeries(series);

    }

    private void ListViewPopulation(JsonArray ja, ListView lv) {
        ArrayList<String> al = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < ja.size(); i++) {
            c.setTimeInMillis( ja.get(i).getAsJsonArray().get(0).getAsLong());
          al.add(sdf.format(c.getTime())+": "+ja.get(i).getAsJsonArray().get(1).getAsDouble()+"km/h");
        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,al);


        lv.setAdapter(aa);
    }
}
