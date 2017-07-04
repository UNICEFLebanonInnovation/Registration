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
    private String gender;
    private String dob;
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
        this.fullName = !currentUser.containsKey("first_name") ? "" : currentUser.get("first_name");
        this.fullName = !currentUser.containsKey("last_name") ? this.fullName : (this.fullName + " " + currentUser.get("last_name"));

        this.fatherName = currentUser.containsKey("father_name") ? currentUser.get("father_name") : "N/A";
        this.motherName = currentUser.containsKey("mother_name") ? currentUser.get("mother_name") : "N/A";

        this.gender = currentUser.containsKey("gender") ? currentUser.get("gender") : null;
        this.dob = currentUser.containsKey("dob") ? currentUser.get("dob") : null;

        this.ID = currentUser.containsKey("ID") ? currentUser.get("ID") : "N/A";
    }


}
