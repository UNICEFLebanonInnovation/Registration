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

    User(String first_name, String last_name){
        this.first_name= first_name;
        this.last_name = last_name;
    }
    User(){}
    User (String first_name, String father_name, String last_name, String mother_name, String dob, String gender)
    {
        this.first_name = first_name;
        this.father_name = father_name;
        this.last_name = last_name;
        this.mother_name = mother_name;
        this.dob = dob;
        this.gender = gender;
    }

    public String toString()
    {
        //Object u = new User();
        Class uClass = this.getClass();
        final Field[] fields = uClass.getFields();
        String a = "";
        for(int i =0; i<fields.length;i++)
        {
           // a+ fields[i].getName() + " = "+ fields[i].get
            //f = uClass.getDeclaredField(fields[i].getName())
            try {
                Field f = uClass.getDeclaredField(fields[i].getName());
                String v = ""+ f.get(this);
                Log.d("USER", f+" = "+v);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this.first_name+" "+this.father_name+" "+this.last_name +" "+"";
    }

    public User(HashMap<String, String> userData)
    {
        Class uClass = this.getClass();

        for (Map.Entry<String, String> entry : userData.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            Field f = null;
            try {
                f = uClass.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            f.setAccessible(true);

            try {
                f.set(this,""+value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Log.d("NewUser", this.toString());
    }

    public Map<String, String> getPost() {

        String day = dob.substring(0, 2);
        String month = dob.substring(2, 4);
        String year = dob.substring(4);

        Map<String, String> postParams = new HashMap<>();
        postParams.put("first_name", this.first_name);
        postParams.put("father_name", this.father_name);
        postParams.put("last_name", this.last_name);
        postParams.put("mother_name", this.last_name);
        postParams.put("gender", this.gender);
        postParams.put("birthday_day", day);
        postParams.put("birthday_month", month);
        postParams.put("birthday_year", year);
        return postParams;
        //", father_name, last_name, mother_name  ";
    }

}
