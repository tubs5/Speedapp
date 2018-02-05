package me.tube.speedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import me.tube.speedapp.Login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //Login
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),Run.class);
                startActivity(i);
            }
        });

        final File f = getFilesDir();
        ListView lw = (ListView) findViewById(R.id.listView);
        ArrayList<String> files = new ArrayList<>();
        for (String s : f.list()) {
            if(!s.contains("rList")){
                if(!s.contains("instant-run")){
                    files.add(s);
                }
            }
        }
        lw.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, files));
        final ArrayList<String> filesfinal = files;
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File File2 = new File(f.getAbsoluteFile()+"/"+filesfinal.get(i));
                System.out.println(File2.toString());
                try {
                    Scanner s = new Scanner(File2);
                    String full = "";
                    while (s.hasNext()){
                        full+=s.next();
                    }
                    if(!full.equals("")){
                        System.out.println(full);
                        Intent intent = new Intent(view.getContext(),RevData.class);
                        intent.putExtra("file",full);
                        intent.putExtra("path",File2.toString());
                        view.getContext().startActivity(intent);
                    }else Toast.makeText(view.getContext(),"cant load file",Toast.LENGTH_LONG).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            new DataBaseManager();
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
}
