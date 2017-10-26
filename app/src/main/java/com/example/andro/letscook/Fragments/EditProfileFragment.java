package com.example.andro.letscook.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.andro.letscook.AllRecipes;
import com.example.andro.letscook.MainActivity;
import com.example.andro.letscook.PojoClass.User;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.example.andro.letscook.Support.FireStoreUtility;
import com.example.andro.letscook.Support.FirebaseAuthUtility;
import com.example.andro.letscook.Support.StorageUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.twitter.sdk.android.core.AuthTokenAdapter;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    Context context;

    Button removeAccountButton,saveChangesButton,uploadButton;

    EditText nameEditText,descriptionEditText;

    ImageView profileImageView;

    String name;

    String key;


    String arr[];

    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;

    FirebaseUser currentUser;

    RotateLoading rotateLoading;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("DestroyCalled","Destroyed");
        databaseReference.child("users").child(key).child("name").removeEventListener(nameEditTextEventListener);
        databaseReference.child("users").child(key).child("description").removeEventListener(descriptionEditTextEventListener);
        databaseReference.child("users").child(key).child("profileUrl").removeEventListener(profileImageEventListener);

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.edit_profile_fragment,container,false);
        context=getContext();
        Bundle bundle= getArguments();
        if(bundle!=null){
            key=bundle.getString("Key");

        }

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
        rotateLoading=v.findViewById(R.id.edit_profile_fragment_rotate_loading);
        rotateLoading.start();



        uploadButton.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);

            }
        });

//        db.collection("users").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.exists()) {
//                    Log.i("DocumentSnapshot","Working");
//                    name = documentSnapshot.get("name").toString();
//                    nameEditText.setText(name);
//                    if (TextUtils.isEmpty(name)) {
//                        nameEditText.setError("Your Name is Required");
//                    }
//                    if(documentSnapshot.get("description")!=null) {
//                        descriptionEditText.setText(documentSnapshot.get("description").toString());
//                    }
//
//
//                    Glide.with(getContext().getApplicationContext()).load(documentSnapshot.get("profileUrl").toString()).listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            rotateLoading.stop();
//                            return false;
//
//                        }
//                    }).apply(RequestOptions.circleCropTransform()).into(profileImageView);
//
//                }
//
//            }
//        });



        databaseReference.child("users").child(key).child("name").addValueEventListener(nameEditTextEventListener);

        databaseReference.child("users").child(key).child("description").addValueEventListener(descriptionEditTextEventListener);

        databaseReference.child("users").child(key).child("profileUrl").addValueEventListener(profileImageEventListener);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClicked();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode==RESULT_OK)
        {
            Uri selectedimg = data.getData();
            Log.i("ImageData",selectedimg+"");
            StorageReference storageReference=firebaseStorage.getReference().child("users").child(currentUser.getUid());
            storageReference.putFile(selectedimg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    databaseReference.child("users").child(key).child("profileUrl").setValue(taskSnapshot.getDownloadUrl()+"");
                    uploadButton.setEnabled(true);
                    profileImageView.setVisibility(View.VISIBLE);
                    rotateLoading.stop();


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {


                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    rotateLoading.start();
                    profileImageView.setVisibility(View.INVISIBLE);
                    uploadButton.setEnabled(false);
                }
            });
        }
    }



    //SaveButton Click Action
    public void onSaveClicked(){
//        if(TextUtils.isEmpty(nameEditText.getText().toString())) {
//            Toast.makeText(getContext(),"Please Enter Name to Continue",Toast.LENGTH_SHORT).show();
//            nameEditText.setError("Your Name is Required");
//        }
//        else{
//            HashMap<String, Object> map = new HashMap<>();
//            if (descriptionEditText.getText() != null) {
//                map.put("description", descriptionEditText.getText().toString());
//            }
//            map.put("name", nameEditText.getText().toString());
//
//            db.collection("users").document(key).update(map);
//        }


        if(TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(getContext(),"Please Enter Name to Continue",Toast.LENGTH_SHORT).show();
            nameEditText.setError("Your Name is Required");
        }else {
            databaseReference.child("users").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (!(descriptionEditText.getText().toString().equals(dataSnapshot.child("description").getValue()))) {
                        databaseReference.child("users").child(key).child("description")
                                .setValue(descriptionEditText.getText().toString().trim());
                        Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show();

                    }
                    if (!(nameEditText.getText().toString().equals(dataSnapshot.child("name").getValue().toString()))) {
                        name = nameEditText.getText().toString().trim();
                        databaseReference.child("users").child(key).child("name").setValue(name);
                        Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            });
        }
    }


    //Event Listener for Name Edit Text Field

    ValueEventListener nameEditTextEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            name = dataSnapshot.getValue() + "";
            nameEditText.setText(name);
            if (TextUtils.isEmpty(name)) {
                nameEditText.setError("Your Name is Required");
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //Event Listener for Description Edit Text Field

    ValueEventListener descriptionEditTextEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getValue()!=null) {
                descriptionEditText.setText(dataSnapshot.getValue().toString());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //Event Listener for Profile Image

    ValueEventListener profileImageEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Glide.with(getContext().getApplicationContext()).load(dataSnapshot.getValue() + "").listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    rotateLoading.stop();
                    return false;

                }
            }).apply(RequestOptions.circleCropTransform()).into(profileImageView);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };



}
