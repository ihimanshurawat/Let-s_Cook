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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.andro.letscook.Adapters.DirectionAdapter;
import com.example.andro.letscook.Adapters.IngredientAdapter;
import com.example.andro.letscook.AllRecipes;
import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.Constant;
import com.example.andro.letscook.Support.FireStoreUtility;
import com.example.andro.letscook.Support.FirebaseAuthUtility;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import rm.com.clocks.ClockImageView;


public class ViewRecipeFragment extends Fragment {

    TextView recipeTotalTimeTextView,recipeCookTimeTextView
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

    FirebaseUser user;

    ClockImageView cookTimeClockImageView,prepTimeClockImageView,totalTimeClockImageView;

    ImageButton favouriteImageButton;

    Recipe recipe;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //Removing Event Listeners
        databaseReference.child("ingredients").removeEventListener(ingredientValueEventListener);
        databaseReference.child("directions").removeEventListener(directionValueEventListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        ((AllRecipes)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AllRecipes)getActivity()).getSupportActionBar().setTitle(recipe.getName());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.view_recipe_fragment,container,false);
        Bundle bundle=getArguments();
        recipe=(Recipe)bundle.getSerializable("Recipe");
        setHasOptionsMenu(true);

        ((AllRecipes)getActivity()).getSupportActionBar().setTitle(recipe.getName());

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        user= FirebaseAuthUtility.getAuth().getCurrentUser();

        ingredientsLinearLayout=v.findViewById(R.id.view_recipe_fragment_ingredient_linear_layout);
        directionsLinearLayout=v.findViewById(R.id.view_recipe_fragment_directions_linear_layout);

        //Favourite Button
        favouriteImageButton= v.findViewById(R.id.view_recipe_fragment_favorite_image_button);


        //Recipe ImageView
        recipeImageView= v.findViewById(R.id.view_recipe_fragment_recipe_image_view);

       // recipeNameTextView= v.findViewById(R.id.view_recipe_fragment_recipe_name_text_view);
        recipeTotalTimeTextView= v.findViewById(R.id.view_recipe_fragment_total_time_text_view);
        recipeCookTimeTextView= v.findViewById(R.id.view_recipe_fragment_cook_time_text_view);
        recipePrepTimeTextView= v.findViewById(R.id.view_recipe_fragment_prep_time_text_view);
        recipeServingTextView= v.findViewById(R.id.view_recipe_fragment_servings_text_view);
        recipeDescriptionTextView= v.findViewById(R.id.view_recipe_fragment_description_text_view);

        //ClockImageView
        prepTimeClockImageView= v.findViewById(R.id.view_recipe_fragment_prep_time_clock_image_view);
        cookTimeClockImageView= v.findViewById(R.id.view_recipe_fragment_cook_time_clock_image_view);
        totalTimeClockImageView= v.findViewById(R.id.view_recipe_fragment_total_time_clock_image_view);


        if(recipe!=null) {
            Calendar cal=Calendar.getInstance(TimeZone.getDefault());
            //recipeNameTextView.setText(recipe.getName());
            recipeServingTextView.setText(Integer.toString(recipe.getServings()));
            recipePrepTimeTextView.setText(getTime(recipe.getPrepTime()));
            recipeCookTimeTextView.setText(getTime(recipe.getCookTime()));
            recipeTotalTimeTextView.setText(getTime(recipe.getCookTime()+recipe.getPrepTime()));
            recipeDescriptionTextView.setText(recipe.getDescription());
            //Glide
            Glide.with(getContext().getApplicationContext()).load(recipe.getImageUrl()).into(recipeImageView);
            getClockImageViewTime(recipe.getPrepTime(),prepTimeClockImageView,cal);
            getClockImageViewTime(recipe.getCookTime(),cookTimeClockImageView,cal);
            getClockImageViewTime((recipe.getCookTime()+recipe.getPrepTime()),totalTimeClockImageView,cal);


        }
        
        layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        databaseReference.child("ingredients").
                child(recipe.getId()).
                addValueEventListener(ingredientValueEventListener);

        databaseReference.child("directions").
                child(recipe.getId()).
                addValueEventListener(directionValueEventListener);
//        databaseReference.child("favourite").child(user.getUid()).
//                child(recipe.getId()).addValueEventListener()
//

//        favouriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseReference.child("favourite").child(user.getUid()).
//            }
//        });



        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public String getTime(int x){

        if(x>60){

            return (x/60)+"h "+ (x%60)+"'";
        }
        else{

            return (x%60)+"m";
        }
    }

    public void getClockImageViewTime(int x,ClockImageView clockImageView,Calendar cal){
        int hour=cal.get(Calendar.HOUR);
        int min=cal.get(Calendar.MINUTE);
        Log.i("TimeHai",hour+"h"+min+"m");
        if((x+min)>=60){
            clockImageView.setHours((hour+((x+min)/60)));
            clockImageView.setMinutes(((x+min)%60));
        }
        if((x+min)<60){
            clockImageView.setHours(hour);
            clockImageView.setMinutes((x+min)%60);
        }

    }


    ValueEventListener isFavouriteValueEventListener=new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){

            }else{
                favouriteImageButton.setTag(Constant.IS_NOT_FAV);
                //favouriteImageButton.setImageResource(co);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


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
                    directionRecyclerView.setNestedScrollingEnabled(false);
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

                    RecyclerView ingredientRecyclerView = new RecyclerView(getContext());
                    ingredientRecyclerView.setAdapter(ingredientAdapter);
                    ingredientRecyclerView.setNestedScrollingEnabled(false);
                    ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    ingredientAdapter.notifyDataSetChanged();
                    ingredientsLinearLayout.addView(headingTextView, textViewLayoutParams);
                    ingredientsLinearLayout.addView(ingredientRecyclerView, layoutParams);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}
