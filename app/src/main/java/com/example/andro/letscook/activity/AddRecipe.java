package com.example.andro.letscook.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.andro.letscook.R;
import com.example.andro.letscook.fragment.AddRecipeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class AddRecipe extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        fragmentManager=getSupportFragmentManager();

        AddRecipeFragment addRecipeFragment=new AddRecipeFragment();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_add_recipe_frame_layout,addRecipeFragment,"Add Recipe Fragment")
                .commit();

    }
}
