package com.example.andro.letscook.Support;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthUtility {
    //FirebaseAuth Reference
    private static FirebaseAuth mAuth;

    public static FirebaseAuth getAuth(){
        if(mAuth==null){
            mAuth=FirebaseAuth.getInstance();
        }
        return mAuth;
    }


}
