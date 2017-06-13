package com.example.rzahab.generator;

/**
 * Created by Rzahab on 5/31/2017.
 */

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ListItem {

    public String listItemText;
    public String listItemCreationDate;
    public String user;

    public ArrayList<String> listItemTexts;

    public String getListItemText() {
        return listItemText;
    }

    public ArrayList<String> getListItemTexts() {
        return listItemTexts;
    }

    public void setListItemText(String listItemText) {
        this.listItemText = listItemText;
    }

    public void setListItemCreationDate(String listItemCreationDate) {
        this.listItemCreationDate = listItemCreationDate;
    }

    @Override
    public String toString() {
        return this.listItemText + "\n" + this.listItemCreationDate;
    }

    public String getListItemCreationDate() {
        return listItemCreationDate;
    }

    public ListItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ListItem.class)
    }

    public ListItem(String listItemText, FirebaseUser CurrentUser) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        this.listItemCreationDate = sdf.format(new Date());
        this.listItemText = listItemText;
        this.user = CurrentUser.getUid();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("listItemText", listItemText);
        result.put("listItemCreationDate", listItemCreationDate);
        result.put("listItemUser", user);
        return result;
    }

}