package com.example.rzahab.generator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;


public class NewUserActivity extends AppCompatActivity {

    Generator app;
    HashMap<String, String> userData;
    EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt, dob_txt;
    String TAG;int day, month, year;
    Button date_picker;private Calendar calendar;
    Activity currentActivity;
    String dob = null;
    private boolean doubleBackToExitPressedOnce = false;
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        currentActivity = this;
        app = (Generator) this.getApplication();
        TAG = this.getClass().getSimpleName();
        userData = app.getUserData();
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date_picker = (Button) findViewById(R.id.date_picker);
        if(userData != null)
            populateData();
        else
        {
            userData = new HashMap<>();
            RadioButton rb = (RadioButton) findViewById(R.id.female);
            rb.setClickable(true);
            rb = (RadioButton) findViewById(R.id.male);
            rb.setClickable(true);
        }
    }
    private void showDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.dob = String.format("%02d", day) + "" + String.format("%02d", month) + "" + year;

        date_picker.setText(new StringBuilder().append(day).append("/")
        .append(month).append("/").append(year));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
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
        if (this.dob != null) {
            userData.put("dob", this.dob);
        } else
            return false;

        return true;
    }

    public void populateData() {
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);

        first_name_txt.setText(userData.get("first_name"));
        last_name_txt.setText(userData.get("last_name"));
        father_name_txt.setText(userData.get("father_name"));
        mother_name_txt.setText(userData.get("mother_name"));
        this.dob = userData.get("dob");

        String dob_txt = dob.substring(0, 2) + " / " + dob.substring(2, 4) + " / " + dob.substring(4);
        date_picker.setText(dob_txt);

        RadioButton checked_radio;
        Log.d(TAG, "Gender: " + userData.get("gender"));
        if (userData.get("gender").equals("female")) {
            checked_radio = (RadioButton) findViewById(R.id.female);
        } else {
            checked_radio = (RadioButton) findViewById(R.id.male);
        }

        checked_radio.setChecked(true);
    }

    public void add(View v) {
        if (!validateInput()) {
            Toast.makeText(this, R.string.error_all_info, Toast.LENGTH_LONG).show();
        } else {
            String first_name_equiv, last_name_equiv, father_name_equiv, mother_name_equiv;

            SuggestionLibrary sl = new SuggestionLibrary();
            if(!userData.containsKey("dob"))
            {
                userData.put("dob",this.dob);
            }
            if(!userData.containsKey("gender"))
            {
                RadioGroup gender_group = (RadioGroup) findViewById(R.id.gender);
                int selectedGender = gender_group.getCheckedRadioButtonId();
                String gender = (selectedGender == R.id.male) ? "male" : (selectedGender == R.id.female) ? "female" : null;
                userData.put("gender",gender);
            }
            if (sl.requireTrans(userData.get("first_name"))) {
                first_name_equiv = sl.transliterate(userData.get("first_name"));
                userData.put("first_name_equiv", first_name_equiv);
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
        IDGen idGen = new IDGen(u);
        final String UID = idGen.generateID();
        u.setUid(UID);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(userData.get("gender"))
                .child(userData.get("dob")).child(UID);

        usersRef.setValue(u, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    //app.moveToKobo(currentActivity, UID);
                    Intent previewIntent = new Intent(currentActivity, PreviewActivity.class);
                    previewIntent.putExtra("UID", "" + UID);
                    startActivity(previewIntent);
                    //finish();
                }
            }
        });

        //GsonRequest g = new GsonRequest(currentActivity, "populateUI");
        //g.post(u.asPost());
    }

    /*
    Legacy : was used when we were calling the Generator ID API

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
                    System.out.println(getString(R.string.error_saving) + databaseError.getMessage());
                } else {
                    app.moveToKobo(currentActivity, ID);
                }
            }
        });
    }
    */


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            startActivity(new Intent(this, SearchUserActivity.class));
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_search, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_delete_all:
                //deleteAllListItems();
                break;
                */
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.action_home:
                startActivity(new Intent(this, SearchUserActivity.class));
                finish();
                break;
            case R.id.action_language:
                startActivity(new Intent(this, LanguageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
