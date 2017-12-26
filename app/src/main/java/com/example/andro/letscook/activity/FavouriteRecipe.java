package com.example.andro.letscook.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.andro.letscook.adapter.RecipeAdapter;
import com.example.andro.letscook.pojo.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.support.FireStoreUtility;
import com.example.andro.letscook.support.FirebaseAuthUtility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRecipe extends AppCompatActivity {

    RecyclerView favouriteRecyclerView;
    RecipeAdapter favouriteRecipeAdapter;
    List<Recipe> favouriteRecipeList;

    FirebaseUser currentUser;

    FirebaseFirestore databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_recipe);

        currentUser= FirebaseAuthUtility.getAuth().getCurrentUser();
        databaseReference= FireStoreUtility.getFirebaseFirestore();


        favouriteRecyclerView=findViewById(R.id.activity_favourite_recipe_favourite_recycler_view);
        favouriteRecipeList=new ArrayList<>();
        favouriteRecipeAdapter=new RecipeAdapter(this,favouriteRecipeList);

        favouriteRecyclerView.setAdapter(favouriteRecipeAdapter);
        favouriteRecyclerView.setLayoutManager(new GridLayoutManager(this,2,LinearLayoutManager.VERTICAL,false));

        databaseReference.collection("favourites").
                document(currentUser.getUid()).collection("recipes").get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(!documentSnapshots.isEmpty()){
                    Log.i("Works","Is In if");
                    List<DocumentSnapshot> documents=documentSnapshots.getDocuments();
                    for(int i=0;i<documents.size();i++){
                        favouriteRecipeList.add(documents.get(i).toObject(Recipe.class));
                    }

                    //favouriteRecipeList=documentSnapshots.toObjects(Recipe.class);

                    favouriteRecipeAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}
