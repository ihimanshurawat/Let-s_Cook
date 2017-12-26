package com.example.andro.letscook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.andro.letscook.MainActivity;
import com.example.andro.letscook.R;
import com.example.andro.letscook.support.DatabaseUtility;
import com.example.andro.letscook.support.FirebaseAuthUtility;
import com.example.andro.letscook.support.StorageUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.victor.loading.rotate.RotateLoading;

public class EditProfile extends AppCompatActivity {


    private Button removeAccountButton,saveChangesButton,uploadButton;

    private EditText nameEditText,descriptionEditText;

    private ImageView profileImageView;

    private String name;

    private String key;

    private String arr[];

    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;

    private FirebaseUser currentUser;

    private RotateLoading rotateLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Edit Profile");
        ActionBar ab=getSupportActionBar();
        if(ab!=null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent=getIntent();
        if(intent.getExtras()!=null){
            key=intent.getStringExtra("Key");

        }

        databaseReference= DatabaseUtility.getDatabase().getReference();
        currentUser= FirebaseAuthUtility.getAuth().getCurrentUser();
        firebaseStorage = StorageUtility.getFirebaseStorageReference();

        if(currentUser!=null) {
            arr = currentUser.getEmail().split("\\.");
        }

        removeAccountButton = findViewById(R.id.edit_profile_remove_account_button);
        uploadButton = findViewById(R.id.edit_profile_upload_button);
        saveChangesButton = findViewById(R.id.edit_profile_save_changes_button);
        nameEditText = findViewById(R.id.edit_profile_name_edit_text);
        descriptionEditText = findViewById(R.id.edit_profile_description_edit_text);
        profileImageView = findViewById(R.id.edit_profile_profile_imageview);
        rotateLoading=findViewById(R.id.edit_profile_rotate_loading);
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
                finish();
            }
        });

        removeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(EditProfile.this);
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
                                                            Toast.makeText(EditProfile.this, "Removing Account", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(EditProfile.this, MainActivity.class));
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
            Toast.makeText(EditProfile.this,"Please Enter Name to Continue",Toast.LENGTH_SHORT).show();
            nameEditText.setError("Your Name is Required");
        }else {
            databaseReference.child("users").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (!(descriptionEditText.getText().toString().equals(dataSnapshot.child("description").getValue()))) {
                        databaseReference.child("users").child(key).child("description")
                                .setValue(descriptionEditText.getText().toString().trim());
                        Toast.makeText(EditProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();

                    }
                    if (!(nameEditText.getText().toString().equals(dataSnapshot.child("name").getValue().toString()))) {
                        name = nameEditText.getText().toString().trim();
                        databaseReference.child("users").child(key).child("name").setValue(name);
                        Toast.makeText(EditProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
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
            Glide.with(EditProfile.this).load(dataSnapshot.getValue() + "").listener(new RequestListener<Drawable>() {
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
