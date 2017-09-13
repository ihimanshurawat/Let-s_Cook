package com.example.andro.letscook.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andro.letscook.MainActivity;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.example.andro.letscook.Support.FirebaseAuthUtility;
import com.example.andro.letscook.Support.StorageUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.twitter.sdk.android.core.AuthTokenAdapter;

/**
 * Created by himanshurawat on 02/09/17.
 */

public class EditProfileFragment extends Fragment {

    Context context;

    Button removeAccountButton,saveChangesButton,uploadButton;

    EditText nameEditText,descriptionEditText;

    ImageView profileImageView;

    String name;

    String arr[];

    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;

    FirebaseUser currentUser;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.edit_profile_fragment,container,false);
        context=getContext();
        databaseReference= DatabaseUtility.getDatabase().getReference();
        currentUser= FirebaseAuthUtility.getAuth().getCurrentUser();
        firebaseStorage = StorageUtility.getFirebaseStorageReference();

        if(currentUser!=null) {
            arr = currentUser.getEmail().split("\\.");
        }

        removeAccountButton = v.findViewById(R.id.edit_profile_fragment_remove_account_button);
        uploadButton = v.findViewById(R.id.edit_profile_fragment_upload_button);
        saveChangesButton = v.findViewById(R.id.edit_profile_fragment_save_changes_button);
        nameEditText = v.findViewById(R.id.edit_profile_fragment_name_edit_text);
        descriptionEditText = v.findViewById(R.id.edit_profile_fragment_description_edit_text);
        profileImageView = v.findViewById(R.id.edit_profile_fragment_profile_imageview);



        uploadButton.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View view) {
            //firebaseStorage.getReference().child("users").child()



//                firebaseStorage.child("users").child(arr[0]).getFile().addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
//
//                    }
//                });




            }
        });

        databaseReference.child("users").child(arr[0]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(currentUser!=null) {
                    name = dataSnapshot.child("name").getValue() + "";
                    nameEditText.setText(name);
                    descriptionEditText.setText(dataSnapshot.child("description").getValue() + "");
                    if (TextUtils.isEmpty(name)) {
                        nameEditText.setError("Are you the BLANK? from No Game No Life");
                    }
                    if(context!=null) {
                        Glide.with(context).load(dataSnapshot.child("profileUrl").getValue() + "")
                                .apply(RequestOptions.circleCropTransform()).into(profileImageView);
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child("users").child(arr[0]).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if(descriptionEditText.getText().toString()!=null){
                            databaseReference.child("users").child(arr[0]).child("description")
                                    .setValue(descriptionEditText.getText().toString().trim());
                            Toast.makeText(context,"Changes Saved",Toast.LENGTH_SHORT).show();

                        }
                        if(!(nameEditText.getText().toString().equals(dataSnapshot.child("name").getValue().toString()))){
                            name=nameEditText.getText().toString().trim();
                            databaseReference.child("users").child(arr[0]).child("name").setValue(name);
                            Toast.makeText(context,"Changes Saved",Toast.LENGTH_SHORT).show();
                        }
                        //nameEditText.setText(dataSnapshot.child("name").getValue()+"");
                        //descriptionEditText.setText(dataSnapshot.child("description").getValue()+"");


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        });




        removeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                builder.setTitle("Remove Account");
                builder.setCancelable(true);
                builder.setMessage("Are you sure you want to remove your account from " +
                        "Lets Cook");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Log.i("IsNull","NotNull");
                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    String token=task.getResult().getToken()+"";
                                    Log.i("IsNull",token);
                                    AuthCredential credential = GoogleAuthProvider.getCredential(user.getIdToken(true).toString(), null);
                                    Log.i("IsNull",credential+"");
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.i("IsNull", "Reauthenticate");
                                            if (task.isSuccessful()) {
                                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.i("IsNull", "Inside Delete");
                                                        if (task.isSuccessful()) {
                                                            Log.d("TAG", "User account deleted.");
                                                            Toast.makeText(context, "Removing Account", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(context, MainActivity.class));
                                                        }
                                                    }
                                                });


                                            }
                                        }
                                    });


                                }
                            });






                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();


            }
        });


        return v;
    }
}
