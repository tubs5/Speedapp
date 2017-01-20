package me.tube.speedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button)findViewById(R.id.button)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),Run.class);
                startActivity(i);
            }
        });

        final File f = getFilesDir();
        final String[] files = f.list();
        ListView lw = (ListView) findViewById(R.id.listView);
        lw.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Arrays.asList(files)));
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               File File2 = new File(f.getAbsolutePath()+"/"+files[i]);
            }
        });






    }
}
