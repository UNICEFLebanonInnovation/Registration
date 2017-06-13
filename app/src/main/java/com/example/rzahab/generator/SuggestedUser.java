package com.example.rzahab.generator;

import java.util.HashMap;

/**
 * Created by Rzahab on 6/13/2017.
 */

public class SuggestedUser {
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    private String fullName;
    private String fatherName;
    private String motherName;
    private String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public SuggestedUser(HashMap<String, String> currentUser) {
        this.fullName = currentUser.containsKey("name") ? currentUser.get("name") : "";
        this.fullName = currentUser.containsKey("lname") ? this.fullName + " " + currentUser.get("lname") : this.fullName;

        this.fatherName = currentUser.containsKey("fname") ? currentUser.get("fname") : "N/A";
        this.motherName = currentUser.containsKey("mname") ? currentUser.get("mname") : "N/A";

        this.ID = currentUser.containsKey("ID") ? currentUser.get("ID") : "N/A";

    }


}
