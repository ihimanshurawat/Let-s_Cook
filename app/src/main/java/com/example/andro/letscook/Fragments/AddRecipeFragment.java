package com.example.andro.letscook.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.example.andro.letscook.Support.FireStoreUtility;
import com.example.andro.letscook.Support.StorageUtility;
import com.example.andro.letscook.Support.UniqueIdGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import static android.app.Activity.RESULT_OK;



public class AddRecipeFragment extends Fragment {

    //Material EditText References
    MaterialEditText recipeID,recipeName,recipeDescription,recipeCuisine,
            recipeServing,recipeCookTime,recipePrepTime,recipeType;
    //Upload Url
    String recipeImageUrl;

    //Button References
    Button recipeUploadImage,submitButton;
    //Firebase Storage Reference
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;

    FirebaseFirestore db;


    private String id;
    private String name;
    private String description;
    private int prepTime;
    private int cookTime;
    private int servings;
    private String cuisine;
    private String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.add_recipe_fragment,container,false);

        firebaseStorage= StorageUtility.getFirebaseStorageReference();

        databaseReference= DatabaseUtility.getDatabase().getReference();

        db= FireStoreUtility.getFirebaseFirestore();

        recipeUploadImage=v.findViewById(R.id.add_recipe_fragment_upload_image_button);
        submitButton=v.findViewById(R.id.add_recipe_fragment_submit_button);

        recipeID=v.findViewById(R.id.add_recipe_fragment_recipe_id_material_edit_text);
        recipeName=v.findViewById(R.id.add_recipe_fragment_recipe_name_material_edit_text);
        recipeDescription=v.findViewById(R.id.add_recipe_fragment_description_material_edit_text);
        recipeCuisine=v.findViewById(R.id.add_recipe_fragment_cuisine_material_edit_text);
        recipeServing=v.findViewById(R.id.add_recipe_fragment_servings_material_edit_text);
        recipeCookTime=v.findViewById(R.id.add_recipe_fragment_cook_time_material_edit_text);
        recipePrepTime=v.findViewById(R.id.add_recipe_fragment_prep_time_material_edit_text);
        recipeType=v.findViewById(R.id.add_recipe_fragment_recipe_type_material_edit_text);


        recipeUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id= new UniqueIdGenerator().getUniqueID();
                name=recipeName.getText().toString();
                description=recipeDescription.getText().toString();
                cuisine=recipeCuisine.getText().toString();
                servings=Integer.parseInt(recipeServing.getText().toString());
                cookTime=Integer.parseInt(recipeCookTime.getText().toString());
                prepTime=Integer.parseInt(recipePrepTime.getText().toString());
                type=recipeType.getText().toString();
                Recipe newRecipe= new Recipe(id,name,cuisine,type,recipeImageUrl,description,servings,prepTime,cookTime,0);
                databaseReference.child("recipes").push().setValue(newRecipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Recipe Added ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK)
        {
            Uri selectedimg = data.getData();
            Log.i("ImageData",selectedimg+"");
            StorageReference storageReference=firebaseStorage.getReference().child("recipes").child(recipeID.getText()+"");
            storageReference.putFile(selectedimg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    recipeUploadImage.setEnabled(true);
                    recipeImageUrl=taskSnapshot.getDownloadUrl()+"";
                    Toast.makeText(getContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {


                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    recipeUploadImage.setEnabled(false);

                }
            });



        }

    }
}
