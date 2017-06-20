package com.example.rzahab.generator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;



public class NewUserActivity extends AppCompatActivity {

    Generator app;
    HashMap<String, String> userData;
    EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt, dob_txt;
    String TAG;
    Activity currentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        app = (Generator) this.getApplication();
        TAG = this.getClass().getSimpleName();
        userData = app.getUserData();
        currentActivity = this;
        populateData();
    }

    public boolean validateInput() {

        EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt;
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);

        if (first_name_txt.getText().toString().trim().length() != 0) {
            userData.put("first_name", first_name_txt.getText().toString());
        } else
            return false;
        if (last_name_txt.getText().toString().trim().length() != 0) {
            userData.put("last_name", last_name_txt.getText().toString());
        } else
            return false;
        if (father_name_txt.getText().toString().trim().length() != 0) {
            userData.put("father_name", father_name_txt.getText().toString());
        } else
            return false;
        if (mother_name_txt.getText().toString().trim().length() != 0) {
            userData.put("mother_name", mother_name_txt.getText().toString());
        } else
            return false;

        return true;
    }

    public void populateData() {
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);
        dob_txt = (EditText) findViewById(R.id.dob);

        first_name_txt.setText(userData.get("first_name"));
        last_name_txt.setText(userData.get("last_name"));
        father_name_txt.setText(userData.get("father_name"));
        mother_name_txt.setText(userData.get("mother_name"));
        String dob = userData.get("dob");
        dob = dob.substring(0, 2) + " / " + dob.substring(2, 4) + " / " + dob.substring(4);
        dob_txt.setText(dob);

        RadioButton checked_radio;
        if (userData.get("gender").equals("female")) {
            checked_radio = (RadioButton) findViewById(R.id.female);
        } else {
            checked_radio = (RadioButton) findViewById(R.id.male);
        }
        checked_radio.setChecked(true);
    }

    public void add(View v) {
        if (!validateInput()) {
            Toast.makeText(this, "Make sure to provide all info", Toast.LENGTH_LONG).show();
        } else {
            String name_equiv, last_name_equiv, father_name_equiv, mother_name_equiv;

            SuggestionLibrary sl = new SuggestionLibrary();

            if (sl.requireTrans(userData.get("first_name"))) {
                name_equiv = sl.transliterate(userData.get("first_name"));
                userData.put("name_equiv", name_equiv);
            }
            if (sl.requireTrans(userData.get("last_name"))) {
                last_name_equiv = sl.transliterate(userData.get("last_name"));
                userData.put("last_name_equiv", last_name_equiv);
            }
            if (sl.requireTrans(userData.get("father_name"))) {
                father_name_equiv = sl.transliterate(userData.get("father_name"));
                userData.put("father_name_equiv", father_name_equiv);
            }
            if (sl.requireTrans(userData.get("mother_name"))) {
                mother_name_equiv = sl.transliterate(userData.get("mother_name"));
                userData.put("mother_name_equiv", mother_name_equiv);
            }
            generateID();
        }

    }

    public void generateID() {
        User u = new User(userData);
        GsonRequest g = new GsonRequest(currentActivity, "populateUI");
        g.post(u.getPost());
    }


    public void populateUI(String jsonResponse) {
        Gson gson = new GsonBuilder().create();
        final HashID hashID = gson.fromJson(jsonResponse, HashID.class);

        final String ID = hashID.getHash();
        Log.d(TAG, "Generated ID: " + ID);

        User u = new User(userData);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(userData.get("gender"))
                .child(userData.get("dob")).child(ID);

        usersRef.setValue(u, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    app.moveToKobo(currentActivity, ID);
                }
            }
        });
    }

}
