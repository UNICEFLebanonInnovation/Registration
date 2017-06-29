package com.example.rzahab.generator;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rzahab on 6/16/2017.
 */

public class User {
    public String getFirst_name() {
        return first_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public String getFirst_name_equiv() {
        return first_name_equiv;
    }

    public String getFather_name_equiv() {
        return father_name_equiv;
    }

    public String getLast_name_equiv() {
        return last_name_equiv;
    }

    public String getMother_name_equiv() {
        return mother_name_equiv;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    private String first_name, father_name, last_name, mother_name,
            first_name_equiv, father_name_equiv, last_name_equiv, mother_name_equiv, gender, dob;

    private Map<String, String> postParams;

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
