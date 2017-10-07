package com.example.andro.letscook.Support;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Created by himanshurawat on 05/10/17.
 */

public class FireStoreUtility {
    private static FirebaseFirestore firebaseFirestore;

    public static FirebaseFirestore getFirebaseFirestore(){
        if(firebaseFirestore==null){
            firebaseFirestore=FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            firebaseFirestore.setFirestoreSettings(settings);
        }
        return firebaseFirestore;
    }

}
