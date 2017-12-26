package com.example.andro.letscook.support;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class DatabaseUtility {

    //Database Root Reference
    private static FirebaseDatabase database;

    public static FirebaseDatabase getDatabase(){
        if(database==null){
            database=FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }


}
