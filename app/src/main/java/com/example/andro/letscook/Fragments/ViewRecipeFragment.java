package com.example.andro.letscook.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.andro.letscook.Adapters.DirectionAdapter;
import com.example.andro.letscook.Adapters.IngredientAdapter;
import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.FireStoreUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ViewRecipeFragment extends Fragment {

    TextView recipeNameTextView, recipeTotalTimeTextView,recipeCookTimeTextView
            ,recipePrepTimeTextView,recipeServingTextView,recipeDescriptionTextView;

    ImageView recipeImageView;

    List<String> ingredientList;
    List<String> directionList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout ingredientsLinearLayout;
    LinearLayout directionsLinearLayout;

    IngredientAdapter ingredientAdapter;
    DirectionAdapter directionAdapter;

    ViewGroup.LayoutParams layoutParams;
    LinearLayout.LayoutParams textViewLayoutParams;

    FirebaseFirestore db;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //Removing Event Listeners
        databaseReference.child("ingredients").removeEventListener(ingredientValueEventListener);
        databaseReference.child("directions").removeEventListener(directionValueEventListener);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.view_recipe_fragment,container,false);
        Bundle bundle=getArguments();
        Recipe recipe=(Recipe)bundle.getSerializable("Recipe");

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        db= FireStoreUtility.getFirebaseFirestore();
        ingredientsLinearLayout=v.findViewById(R.id.view_recipe_fragment_ingredient_linear_layout);
        directionsLinearLayout=v.findViewById(R.id.view_recipe_fragment_directions_linear_layout);

        //Recipe ImageView
        recipeImageView= v.findViewById(R.id.view_recipe_fragment_recipe_image_view);

        recipeNameTextView= v.findViewById(R.id.view_recipe_fragment_recipe_name_text_view);
        recipeTotalTimeTextView= v.findViewById(R.id.view_recipe_fragment_total_time_text_view);
        recipeCookTimeTextView= v.findViewById(R.id.view_recipe_fragment_cook_time_text_view);
        recipePrepTimeTextView= v.findViewById(R.id.view_recipe_fragment_prep_time_text_view);
        recipeServingTextView= v.findViewById(R.id.view_recipe_fragment_servings_text_view);
        recipeDescriptionTextView= v.findViewById(R.id.view_recipe_fragment_description_text_view);


        if(recipe!=null) {
            recipeNameTextView.setText(recipe.getName());
            recipeServingTextView.setText(Integer.toString(recipe.getServings()));
            recipePrepTimeTextView.setText(getTime(recipe.getPrepTime()));
            recipeCookTimeTextView.setText(getTime(recipe.getCookTime()));
            recipeTotalTimeTextView.setText(getTime(recipe.getCookTime()+recipe.getPrepTime()));
            recipeDescriptionTextView.setText(recipe.getDescription());
            //Glide
            Glide.with(getContext().getApplicationContext()).load(recipe.getImageUrl()).into(recipeImageView);

        }
        
        layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        databaseReference.child("ingredients").
                child(Integer.toString(recipe.getId())).
                addValueEventListener(ingredientValueEventListener);

        databaseReference.child("directions").
                child(Integer.toString(recipe.getId())).
                addValueEventListener(directionValueEventListener);



        return v;
    }

    public String getTime(int x){

        if(x>60){

            return (x/60)+"h "+ (x%60)+"'";
        }
        else{

            return (x%60)+"m";
        }
    }

    ValueEventListener directionValueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount()>0){
                String heading=null;
                for(DataSnapshot children:dataSnapshot.getChildren()){
                    directionList=new ArrayList<>();
                    directionAdapter=new DirectionAdapter(getContext(),directionList);
                    for(DataSnapshot insideChildren: children.getChildren()){
                        if (insideChildren.getKey().equals("heading")) {
                            heading = insideChildren.getValue().toString();
                        } else {
                            directionList.add(insideChildren.getValue().toString());
                        }
                    }
                    TextView headingTextView = new TextView(getContext());
                    if (heading != null) {
                        headingTextView.setText(heading);
                    }
                    //Padding and Margin for Heading TextView
                    headingTextView.setPadding(5, 5, 5, 5);
                    textViewLayoutParams.setMargins(20, 5, 20, 5);
                    headingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    headingTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    headingTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));

                    RecyclerView directionRecyclerView = new RecyclerView(getContext());
                    directionRecyclerView.setAdapter(directionAdapter);
                    directionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    directionAdapter.notifyDataSetChanged();
                    directionsLinearLayout.addView(headingTextView, textViewLayoutParams);
                    directionsLinearLayout.addView(directionRecyclerView, layoutParams);


                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    ValueEventListener ingredientValueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount()>0){
                String heading=null;
                for(DataSnapshot children : dataSnapshot.getChildren()) {
                    Log.i("GetKey", children.getKey());
                    ingredientList = new ArrayList<>();
                    ingredientAdapter = new IngredientAdapter(getContext(), ingredientList);
                    for (DataSnapshot insideChildren : children.getChildren()) {
                        if (insideChildren.getKey().equals("heading")) {
                            heading = insideChildren.getValue().toString();
                        } else {
                            ingredientList.add(insideChildren.getValue().toString());
                        }
                        Log.i("IsInfinite", insideChildren.getKey());
                    }

                    TextView headingTextView = new TextView(getContext());
                    if (heading != null) {
                        headingTextView.setText(heading);
                    }
                    //Padding and Margin for Heading TextView
                    headingTextView.setPadding(5, 5, 5, 5);
                    textViewLayoutParams.setMargins(20, 5, 20, 5);
                    headingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    headingTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    headingTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));

                    RecyclerView directionRecyclerView = new RecyclerView(getContext());
                    directionRecyclerView.setAdapter(ingredientAdapter);
                    directionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    ingredientAdapter.notifyDataSetChanged();
                    ingredientsLinearLayout.addView(headingTextView, textViewLayoutParams);
                    ingredientsLinearLayout.addView(directionRecyclerView, layoutParams);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
