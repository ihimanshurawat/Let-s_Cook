package com.example.andro.letscook.Support;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by himanshurawat on 10/09/17.
 */

public class FirebaseAuthUtility {
    private static FirebaseAuth mAuth;

    public static FirebaseAuth getAuth(){
        if(mAuth==null){
            mAuth=FirebaseAuth.getInstance();
        }
        return mAuth;
    }


}
