package com.example.andro.letscook.Support;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by himanshurawat on 12/09/17.
 */

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
