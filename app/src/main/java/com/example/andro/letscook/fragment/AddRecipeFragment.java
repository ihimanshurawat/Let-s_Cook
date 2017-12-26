package com.example.andro.letscook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.andro.letscook.pojo.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.support.DatabaseUtility;
import com.example.andro.letscook.support.FireStoreUtility;
import com.example.andro.letscook.support.StorageUtility;
import com.example.andro.letscook.support.UniqueIdGenerator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;



public class AddRecipeFragment extends Fragment {

    FragmentManager fragmentManager;


    //Material EditText References
    MaterialEditText recipeNameMaterialEditText,recipeDescriptionMaterialEditText,
            recipeCuisineMaterialEditText, recipeServingMaterialEditText,recipeCookTimeMaterialEditText,
            recipePrepTimeMaterialEditText,recipeTypeMaterialEditText,recipeSubTypeMaterialEditText,
            recipeMainIngredientMaterialEditText;
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
    private String subType;
    private List<String> mainIngredient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.add_recipe_fragment,container,false);

        fragmentManager=getActivity().getSupportFragmentManager();

        firebaseStorage= StorageUtility.getFirebaseStorageReference();

        databaseReference= DatabaseUtility.getDatabase().getReference();

        db= FireStoreUtility.getFirebaseFirestore();

        mainIngredient=new ArrayList<>();

        recipeUploadImage=v.findViewById(R.id.add_recipe_fragment_upload_image_button);
        submitButton=v.findViewById(R.id.add_recipe_fragment_submit_button);

        recipeNameMaterialEditText=v.findViewById(R.id.add_recipe_fragment_recipe_name_material_edit_text);
        recipeDescriptionMaterialEditText=v.findViewById(R.id.add_recipe_fragment_description_material_edit_text);
        recipeCuisineMaterialEditText=v.findViewById(R.id.add_recipe_fragment_cuisine_material_edit_text);
        recipeServingMaterialEditText=v.findViewById(R.id.add_recipe_fragment_servings_material_edit_text);
        recipeCookTimeMaterialEditText=v.findViewById(R.id.add_recipe_fragment_cook_time_material_edit_text);
        recipePrepTimeMaterialEditText=v.findViewById(R.id.add_recipe_fragment_prep_time_material_edit_text);
        recipeTypeMaterialEditText=v.findViewById(R.id.add_recipe_fragment_recipe_type_material_edit_text);
        recipeSubTypeMaterialEditText= v.findViewById(R.id.add_recipe_fragment_recipe_sub_type_material_edit_text);
        recipeMainIngredientMaterialEditText= v.findViewById(R.id.add_recipe_fragment_main_ingredients_edit_text);

        recipeUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Setting Recipe ID so that it can be used for Storing Image ID
                id= new UniqueIdGenerator().getUniqueID();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //id= new UniqueIdGenerator().getUniqueID();
                name=recipeNameMaterialEditText.getText().toString();
                description=recipeDescriptionMaterialEditText.getText().toString();
                cuisine=recipeCuisineMaterialEditText.getText().toString();
                servings=Integer.parseInt(recipeServingMaterialEditText.getText().toString());
                cookTime=Integer.parseInt(recipeCookTimeMaterialEditText.getText().toString());
                prepTime=Integer.parseInt(recipePrepTimeMaterialEditText.getText().toString());
                type=recipeTypeMaterialEditText.getText().toString();
                subType=recipeSubTypeMaterialEditText.getText().toString();
                String []ingredient=recipeMainIngredientMaterialEditText.getText().toString().split(":");
                for(String i:ingredient){
                    mainIngredient.add(i);
                }
                Recipe newRecipe= new Recipe(id,name,cuisine,type,subType,recipeImageUrl,description,servings,prepTime,cookTime,0,mainIngredient);
                db.collection("recipes").add(newRecipe);
                //databaseReference.child("recipes").push().setValue(newRecipe);
                launchAddIngredientFragment(id);
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
            StorageReference storageReference=firebaseStorage.getReference().child("recipes").child(id);
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
    public void launchAddIngredientFragment(String id){
        AddIngredientFragment addIngredientFragment=new AddIngredientFragment();
        Bundle bundle=new Bundle();
        bundle.putString("id",id);
        addIngredientFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_add_recipe_frame_layout,addIngredientFragment).
                setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).commit();
    }

}
