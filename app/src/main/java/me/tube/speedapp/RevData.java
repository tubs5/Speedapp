package me.tube.speedapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        final String file = intent.getStringExtra("file");
        final String path = intent.getStringExtra("path");

        Gson gson = new Gson();

        JsonArray ja = null;
        try {
           ja = gson.fromJson(file,JsonArray.class);
        }catch (JsonSyntaxException e){
            Toast.makeText(this,"Cant load file",Toast.LENGTH_LONG).show();
            Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent2);
            return;
        }
        if(ja == null) System.out.println("gson is null");
        final JsonArray ja2 = ja;
        System.out.println(ja.toString());


        ListView lv = (ListView) findViewById(R.id.listView2);
        ListViewPopulation(ja, lv);

        GraphView gv = (GraphView) findViewById(R.id.graph);
        GraphViewPopulation(ja, gv);

        Button export = (Button) findViewById(R.id.button3);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] perms = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(perms,5);
                }
                try {
                    File f = CreateExelDoc(path, ja2, view.getContext().getCacheDir());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    System.out.println(f.getAbsoluteFile());
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), "me.tubes.Speedapp", f);
                    System.out.println(uri.toString());
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setType("application/vnd.google-apps.spreadsheet");

                    startActivity(Intent.createChooser(shareIntent, "Choose app to show in"));
                } catch (NoClassDefFoundError e) {
                    Toast.makeText(view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private File CreateExelDoc(String file, JsonArray ja,File path) {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet(file.substring(file.lastIndexOf("/")+1).replace(':',','));
        sheet.createRow(0).createCell(1).setCellValue("time(ms)");
        sheet.getRow(0).createCell(0).setCellValue("date");
        sheet.getRow(0).createCell(2).setCellValue("speed(kph)");
        Calendar c = Calendar.getInstance();
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("h:mm"));

        for (int i = 0; i < ja.size(); i++) {
            sheet.createRow(i+1).createCell(1).setCellValue(ja.get(i).getAsJsonArray().get(0).getAsDouble());
            sheet.getRow(i+1).createCell(2).setCellValue(ja.get(i).getAsJsonArray().get(1).getAsDouble());

            c.setTimeInMillis(ja.get(i).getAsJsonArray().get(1).getAsLong());
            sheet.getRow(i+1).createCell(0).setCellStyle(dateStyle);
            sheet.getRow(i+1).createCell(0).setCellValue(c);
        }

            File File = new File(path.toString()+"/thing.xls");
        try {

            FileOutputStream fileOut = new FileOutputStream(File);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return File;
    }


    private void GraphViewPopulation(JsonArray ja, GraphView gv) {
        try {
            gv.setTitle("speed");

            DataPoint[] dp = new DataPoint[ja.size()];
            Calendar c = Calendar.getInstance();
            long startTime = ja.get(0).getAsJsonArray().get(0).getAsLong();
            for (int i = 0; i < ja.size(); i++) {
                long time = ja.get(i).getAsJsonArray().get(0).getAsLong() - startTime;
                if (time != 0) time /= 1000;

                dp[i] = new DataPoint(time, ja.get(i).getAsJsonArray().get(1).getAsDouble());
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
            gv.addSeries(series);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Cant Load ...",Toast.LENGTH_LONG);
        }
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
