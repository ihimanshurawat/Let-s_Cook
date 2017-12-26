package com.example.andro.letscook.support;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class StorageUtility {
    //Storage Root Reference
    private static FirebaseStorage firebaseStorage;

    public static FirebaseStorage getFirebaseStorageReference(){
        if(firebaseStorage==null){
            firebaseStorage= FirebaseStorage.getInstance();
        }
        return firebaseStorage;
    }

}
