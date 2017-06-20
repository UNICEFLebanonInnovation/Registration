package com.example.rzahab.generator;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rzahab on 6/16/2017.
 */

public class User {
    String first_name, father_name, last_name, mother_name, name_equiv, father_name_equiv, last_name_equiv, mother_name_equiv, gender, dob;

    private Map<String, String> postParams;

    User(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    User() {
    }

    User(String first_name, String father_name, String last_name, String mother_name, String dob, String gender) {
        this.first_name = first_name;
        this.father_name = father_name;
        this.last_name = last_name;
        this.mother_name = mother_name;
        this.dob = dob;
        this.gender = gender;
    }

    public User(HashMap<String, String> userData) {
        Class uClass = this.getClass();
        postParams = new HashMap<>();

        for (Map.Entry<String, String> entry : userData.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            postParams.put(key, value);

            Field f = null;
            try {
                f = uClass.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            f.setAccessible(true);

            try {
                f.set(this, "" + value);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Log.d("NewUser", this.toString());
    }

    public String toString() {
        //Object u = new User();
        Class uClass = this.getClass();
        final Field[] fields = uClass.getFields();

        for (int i = 0; i < fields.length; i++) {
            try {
                Field f = uClass.getDeclaredField(fields[i].getName());
                String v = "" + f.get(this);
                Log.d("USER", f + " = " + v);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this.first_name + " " + this.father_name + " " + this.last_name + " " + "";
    }

    public Map<String, String> asPost() {
        return postParams;
    }

}
