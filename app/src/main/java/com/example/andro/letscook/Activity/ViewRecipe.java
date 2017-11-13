package com.example.andro.letscook.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.andro.letscook.Adapters.DirectionAdapter;
import com.example.andro.letscook.Adapters.IngredientAdapter;
import com.example.andro.letscook.AllRecipes;
import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.Constant;
import com.example.andro.letscook.Support.FirebaseAuthUtility;
import com.example.andro.letscook.Support.Methods;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import rm.com.clocks.ClockImageView;

public class ViewRecipe extends AppCompatActivity {

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

    Toolbar toolbar;

    AppBarLayout appBarLayout;

    int screenHeight,screenWidth;

    CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        toolbar = (Toolbar) findViewById(R.id.activity_view_recipe_toolbar);
        setSupportActionBar(toolbar);
        
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.activity_view_recipe_collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);

        Intent i=getIntent();
        recipe=(Recipe)i.getSerializableExtra("Recipe");

        //initializeToolbar(R.id.activity_view_recipe_toolbar,recipe.getName());
        initCollapsibleToolbar(recipe.getName());

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        user= FirebaseAuthUtility.getAuth().getCurrentUser();

        ingredientsLinearLayout=findViewById(R.id.content_view_recipe_ingredient_linear_layout);
        directionsLinearLayout=findViewById(R.id.content_view_recipe_directions_linear_layout);

        //Favourite Button
        favouriteImageButton= findViewById(R.id.content_view_recipe_favorite_image_button);


        //Recipe ImageView
        recipeImageView= findViewById(R.id.activity_view_recipe_recipe_image_view);

        // recipeNameTextView= findViewById(R.id.content_view_recipe_recipe_name_text_view);
        recipeTotalTimeTextView= findViewById(R.id.content_view_recipe_total_time_text_view);
        recipeCookTimeTextView= findViewById(R.id.content_view_recipe_cook_time_text_view);
        recipePrepTimeTextView= findViewById(R.id.content_view_recipe_prep_time_text_view);
        recipeServingTextView= findViewById(R.id.content_view_recipe_servings_text_view);
        recipeDescriptionTextView= findViewById(R.id.content_view_recipe_description_text_view);

        //ClockImageView
        prepTimeClockImageView= findViewById(R.id.content_view_recipe_prep_time_clock_image_view);
        cookTimeClockImageView= findViewById(R.id.content_view_recipe_cook_time_clock_image_view);
        totalTimeClockImageView= findViewById(R.id.content_view_recipe_total_time_clock_image_view);

        databaseReference.child("ingredients").
                child(recipe.getId()).
                addValueEventListener(ingredientValueEventListener);

        databaseReference.child("directions").
                child(recipe.getId()).
                addValueEventListener(directionValueEventListener);


        if(recipe!=null) {
            Calendar cal=Calendar.getInstance(TimeZone.getDefault());
            //recipeNameTextView.setText(recipe.getName());
            recipeServingTextView.setText(Integer.toString(recipe.getServings()));
            recipePrepTimeTextView.setText(getTime(recipe.getPrepTime()));
            recipeCookTimeTextView.setText(getTime(recipe.getCookTime()));
            recipeTotalTimeTextView.setText(getTime(recipe.getCookTime()+recipe.getPrepTime()));
            recipeDescriptionTextView.setText(recipe.getDescription());
            //Glide
            Glide.with(ViewRecipe.this).load(recipe.getImageUrl()).into(recipeImageView);
            getClockImageViewTime(recipe.getPrepTime(),prepTimeClockImageView,cal);
            getClockImageViewTime(recipe.getCookTime(),cookTimeClockImageView,cal);
            getClockImageViewTime((recipe.getCookTime()+recipe.getPrepTime()),totalTimeClockImageView,cal);


        }

        layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



//        databaseReference.child("favourite").child(user.getUid()).
//                child(recipe.getId()).addValueEventListener()
//

//        favouriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseReference.child("favourite").child(user.getUid()).
//            }
//        });

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
                    directionAdapter=new DirectionAdapter(ViewRecipe.this,directionList);
                    for(DataSnapshot insideChildren: children.getChildren()){
                        if (insideChildren.getKey().equals("heading")) {
                            heading = insideChildren.getValue().toString();
                        } else {
                            directionList.add(insideChildren.getValue().toString());
                        }
                    }
                    TextView headingTextView = new TextView(ViewRecipe.this);
                    if (heading != null) {
                        headingTextView.setText(heading);
                    }
                    //Padding and Margin for Heading TextView
                    headingTextView.setPadding(5, 5, 5, 5);
                    textViewLayoutParams.setMargins(20, 5, 20, 5);
                    headingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    headingTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    headingTextView.setTextColor(ContextCompat.getColor(ViewRecipe.this, R.color.colorText));

                    RecyclerView directionRecyclerView = new RecyclerView(ViewRecipe.this);
                    directionRecyclerView.setAdapter(directionAdapter);
                    directionRecyclerView.setNestedScrollingEnabled(false);
                    directionRecyclerView.setLayoutManager(new LinearLayoutManager(ViewRecipe.this, LinearLayoutManager.VERTICAL, false));
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
                    ingredientAdapter = new IngredientAdapter(ViewRecipe.this, ingredientList);
                    for (DataSnapshot insideChildren : children.getChildren()) {
                        if (insideChildren.getKey().equals("heading")) {
                            heading = insideChildren.getValue().toString();
                        } else {
                            ingredientList.add(insideChildren.getValue().toString());
                        }
                        Log.i("IsInfinite", insideChildren.getKey());
                    }

                    TextView headingTextView = new TextView(ViewRecipe.this);
                    if (heading != null) {
                        headingTextView.setText(heading);
                    }
                    //Padding and Margin for Heading TextView
                    headingTextView.setPadding(5, 5, 5, 5);
                    textViewLayoutParams.setMargins(20, 5, 20, 5);
                    headingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    headingTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    headingTextView.setTextColor(ContextCompat.getColor(ViewRecipe.this, R.color.colorText));

                    RecyclerView ingredientRecyclerView = new RecyclerView(ViewRecipe.this);
                    ingredientRecyclerView.setAdapter(ingredientAdapter);
                    ingredientRecyclerView.setNestedScrollingEnabled(false);
                    ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(ViewRecipe.this, LinearLayoutManager.VERTICAL, false));
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


    private int getStatusBarHeight() {
        int height;

        Resources myResources = getResources();
        int idStatusBarHeight = myResources.getIdentifier(
                "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = getResources().getDimensionPixelSize(idStatusBarHeight);
            Toast.makeText(this,
                    "Status Bar Height = " + height,
                    Toast.LENGTH_LONG).show();
        }else{
            height = 0;
            Toast.makeText(this,
                    "Resources NOT found",
                    Toast.LENGTH_LONG).show();
        }

        return height;
    }

    //Toolbar
    public void initializeToolbar(int toolbarId,String title){
        toolbar=findViewById(toolbarId);
        setSupportActionBar(toolbar);
        TextView appBarTitleTextView=toolbar.findViewById(R.id.app_bar_all_recipes_app_bar_title_text_view);
        appBarTitleTextView.setText(title);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initCollapsibleToolbar(String title) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
        toolbar = (Toolbar) findViewById(R.id.activity_view_recipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView appBarTitleTextView=toolbar.findViewById(R.id.app_bar_all_recipes_app_bar_title_text_view);
        appBarTitleTextView.setText(title);
        getSupportActionBar().setTitle("");
        appBarLayout = findViewById(R.id.activity_view_recipe_app_bar_layout);
        double d=screenHeight/2.5;
        screenHeight=(int)d;
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(screenWidth,screenHeight));
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.activity_view_recipe_collapsing_toolbar_layout);
        collapsingToolbar.setTitleEnabled(false);

    }


}
