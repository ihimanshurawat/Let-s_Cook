package com.example.andro.letscook.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andro.letscook.Adapters.IngredientAdapter;
import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ViewRecipeFragment extends Fragment {


    TextView recipeNameTextView, recipeTotalTimeTextView,recipeCookTimeTextView
            ,recipePrepTimeTextView,recipeServingTextView,recipeDescriptionTextView;

    ImageView recipeImageView;

    List<String> ingredientList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout ingredientsLinearLayout;

    IngredientAdapter ingredientAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.view_recipe_fragment,container,false);
        Bundle bundle=getArguments();
        Recipe recipe=(Recipe)bundle.getSerializable("Recipe");

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        ingredientsLinearLayout=v.findViewById(R.id.view_recipe_fragment_ingredient_linear_layout);

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
            recipeServingTextView.setText(recipe.getServings()+" servings");
            recipePrepTimeTextView.setText("prep time"+getTime(recipe.getPrepTime()));
            recipeCookTimeTextView.setText("cook time"+getTime(recipe.getCookTime()));
            recipeTotalTimeTextView.setText("total time"+getTime(recipe.getCookTime()+recipe.getPrepTime()));
            recipeDescriptionTextView.setText(recipe.getDescription());
            Glide.with(getContext().getApplicationContext()).load(recipe.getImageUrl()).into(recipeImageView);

        }


        final ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams textViewLayoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



        databaseReference.child("ingredients").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==1){
                    ingredientList=new ArrayList<>();
                    ingredientAdapter=new IngredientAdapter(getContext(),ingredientList);
                    ingredientList.clear();
                    String heading=null;
                    for(DataSnapshot children : dataSnapshot.getChildren()){
                        for(DataSnapshot insideChildren: children.getChildren()) {
                            if (insideChildren.getKey().equals("heading")) {
                                heading = insideChildren.getValue().toString();
                            } else {
                                ingredientList.add(insideChildren.getValue().toString());
                            }
                            Log.i("IsInfinite",insideChildren.getKey());
                        }
                    }
                    TextView headingTextView= new TextView(getContext());
                    if(heading!=null) {
                        headingTextView.setText(heading);
                    }
                    //Padding and Margin for Heading TextView
                    headingTextView.setPadding(5,5,5,5);
                    textViewLayoutParams.setMargins(20,5,20,5);
                    headingTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                    RecyclerView ingredientRecyclerView= new RecyclerView(getContext());
                    ingredientRecyclerView.setAdapter(ingredientAdapter);
                    ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                    ingredientAdapter.notifyDataSetChanged();
                    ingredientsLinearLayout.addView(headingTextView,textViewLayoutParams);
                    ingredientsLinearLayout.addView(ingredientRecyclerView,layoutParams);
                }


                if(dataSnapshot.getChildrenCount()>1) {
                    for (DataSnapshot children : dataSnapshot.getChildren()) {

                        ingredientList=new ArrayList<>();
                        ingredientAdapter=new IngredientAdapter(getContext(),ingredientList);
                        ingredientList.clear();
                        String heading=null;
                        Log.i("getKey",children.getKey());

                        for(DataSnapshot insideChild:children.getChildren()){
                            Log.i("getKey",insideChild.getKey());
                            if(insideChild.getKey().equals("heading")){
                                heading=insideChild.getValue().toString();
                            }else{
                                ingredientList.add(insideChild.getValue().toString());
                            }

                            TextView headingTextView= new TextView(getContext());
                            if(heading!=null) {
                                headingTextView.setText(heading);
                            }
                            //Padding and Margin for Heading TextView
                            headingTextView.setPadding(5,5,5,5);
                            textViewLayoutParams.setMargins(20,5,20,5);
                            headingTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));

                            RecyclerView ingredientRecyclerView= new RecyclerView(getContext());
                            ingredientRecyclerView.setAdapter(ingredientAdapter);
                            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                            ingredientAdapter.notifyDataSetChanged();
                            ingredientsLinearLayout.addView(headingTextView,textViewLayoutParams);
                            ingredientsLinearLayout.addView(ingredientRecyclerView,layoutParams);


                        }

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







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

}
