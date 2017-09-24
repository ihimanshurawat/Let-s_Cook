package com.example.andro.letscook.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.andro.letscook.Adapters.RecipeAdapter;
import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.victor.loading.book.BookLoading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecipesFragment extends Fragment {

    RecyclerView vegetarianRecipeRecyclerView;
    List<Recipe> vegetarianRecipeList;
    RecipeAdapter vegetarianRecipeAdapter;

    RecyclerView nonVegetarianRecipeRecyclerView;
    List<Recipe> nonVegetarianRecipeList;
    RecipeAdapter nonVegetarianRecipeAdapter;

    RecyclerView dessertsRecyclerView;
    List<Recipe> dessertsRecipeList;
    RecipeAdapter dessertsRecipeAdapter;

    DatabaseReference databaseReference;

    //BookLoading References
    BookLoading vegetarianBookLoading,nonVegetarianBookLoading,dessertsBookLoading;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.recipes_fragment,container,false);

        Bundle bundle=getArguments();

        vegetarianBookLoading=v.findViewById(R.id.recipes_fragment_vegetarian_book_loading);
        nonVegetarianBookLoading=v.findViewById(R.id.recipes_fragment_non_vegetarian_book_loading);
        dessertsBookLoading=v.findViewById(R.id.recipes_fragment_desserts_book_loading);

        vegetarianBookLoading.start();
        nonVegetarianBookLoading.start();
        dessertsBookLoading.start();

        vegetarianRecipeRecyclerView= v.findViewById(R.id.recipes_fragment_vegetarian_recycler_view);
        nonVegetarianRecipeRecyclerView=v.findViewById(R.id.recipes_fragment_non_vegetarian_recycler_view);
        dessertsRecyclerView=v.findViewById(R.id.recipes_fragment_desserts_recycler_view);

        databaseReference= DatabaseUtility.getDatabase().getReference();

        vegetarianRecipeList= new ArrayList<>();
        vegetarianRecipeAdapter=new RecipeAdapter(getContext(),vegetarianRecipeList);
        vegetarianRecipeRecyclerView.setAdapter(vegetarianRecipeAdapter);
        vegetarianRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        nonVegetarianRecipeList = new ArrayList<>();
        nonVegetarianRecipeAdapter=new RecipeAdapter(getContext(),nonVegetarianRecipeList);
        nonVegetarianRecipeRecyclerView.setAdapter(nonVegetarianRecipeAdapter);
        nonVegetarianRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        dessertsRecipeList = new ArrayList<>();
        dessertsRecipeAdapter=new RecipeAdapter(getContext(),dessertsRecipeList);
        dessertsRecyclerView.setAdapter(dessertsRecipeAdapter);
        dessertsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        //if(bundle!=null) {
            //Do Something Here
           // String cuisine=bundle.getString("cuisine");
           // if(cuisine!=null) {
                databaseReference.child("recipes").orderByChild("cuisine").equalTo("Indian").addValueEventListener(cuisineValueEventListener);
           // }


//        }else{
//            databaseReference.child("recipes").orderByChild("type").equalTo("Vegetarian").addValueEventListener(vegetarianEventListener);
//            databaseReference.child("recipes").orderByChild("type").equalTo("Non-vegetarian").addValueEventListener(nonVegetarianEventListener);
//            databaseReference.child("recipes").orderByChild("type").equalTo("Dessert").addValueEventListener(dessertsValueEventListener);
//        }
        return v;
    }




    ValueEventListener vegetarianEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount()>0) {
                vegetarianRecipeList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Recipe recipe = children.getValue(Recipe.class);
                    vegetarianRecipeList.add(recipe);
                }
                Collections.reverse(vegetarianRecipeList);
                vegetarianRecipeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener nonVegetarianEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount()>0) {
                nonVegetarianRecipeList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Recipe recipe = children.getValue(Recipe.class);
                    nonVegetarianRecipeList.add(recipe);
                }
                Collections.reverse(nonVegetarianRecipeList);
                nonVegetarianRecipeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener dessertsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount()>0) {
                Log.i("ChildrenCount",dataSnapshot.getChildrenCount()+"");
                dessertsRecipeList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Recipe recipe = children.getValue(Recipe.class);
                    dessertsRecipeList.add(recipe);
                }
                Collections.reverse(dessertsRecipeList);
                dessertsRecipeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    ValueEventListener cuisineValueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            vegetarianRecipeList.clear();
            nonVegetarianRecipeList.clear();
            dessertsRecipeList.clear();
            if(dataSnapshot.getChildrenCount()>0) {
                for (DataSnapshot children : dataSnapshot.getChildren()){
                    if(children.child("type").getValue().toString().equals("Vegetarian")){
                        Recipe recipe=children.getValue(Recipe.class);
                        vegetarianRecipeList.add(recipe);
                    }
                    if(children.child("type").getValue().toString().equals("Non-vegetarian")){
                        Recipe recipe=children.getValue(Recipe.class);
                        nonVegetarianRecipeList.add(recipe);
                    }
                    if(children.child("type").getValue().toString().equals("Dessert")){
                        Recipe recipe= children.getValue(Recipe.class);
                        dessertsRecipeList.add(recipe);
                    }
                }
                vegetarianBookLoading.stop();
                nonVegetarianBookLoading.stop();
                dessertsBookLoading.stop();

                vegetarianBookLoading.setVisibility(View.GONE);
                nonVegetarianBookLoading.setVisibility(View.GONE);
                dessertsBookLoading.setVisibility(View.GONE);

                vegetarianRecipeRecyclerView.setVisibility(View.VISIBLE);
                nonVegetarianRecipeRecyclerView.setVisibility(View.VISIBLE);
                dessertsRecyclerView.setVisibility(View.VISIBLE);

                vegetarianRecipeAdapter.notifyDataSetChanged();
                nonVegetarianRecipeAdapter.notifyDataSetChanged();
                dessertsRecipeAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
