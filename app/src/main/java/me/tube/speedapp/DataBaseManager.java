package me.tube.speedapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Tubs on 2018-02-02.
 */

public class DataBaseManager {
    public DatabaseReference location;

    public DataBaseManager() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        Calendar c = Calendar.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("users");
        location = myRef.child(user.getUid()).child(c.get(Calendar.YEAR) + "").child(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMANY))
                .child(c.get(Calendar.DAY_OF_MONTH) + "").child("data");

    }

    public void saveData(ArrayList<ContentSaver> cs) {
        for (ContentSaver c : cs) {
            c.saveData(location);
        }
    }

}
