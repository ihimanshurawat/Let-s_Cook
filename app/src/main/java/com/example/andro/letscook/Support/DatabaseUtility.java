package com.example.andro.letscook.Support;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by himanshurawat on 10/09/17.
 */

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
