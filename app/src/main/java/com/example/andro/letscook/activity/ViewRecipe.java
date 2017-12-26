package com.example.andro.letscook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andro.letscook.R;
import com.example.andro.letscook.adapter.DirectionAdapter;
import com.example.andro.letscook.adapter.IngredientAdapter;
import com.example.andro.letscook.pojo.Recipe;
import com.example.andro.letscook.support.FireStoreUtility;
import com.example.andro.letscook.support.FirebaseAuthUtility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import rm.com.clocks.ClockImageView;

public class ViewRecipe extends AppCompatActivity {

    private TextView recipeTotalTimeTextView,recipeCookTimeTextView
            ,recipePrepTimeTextView,recipeServingTextView,recipeDescriptionTextView;

    private ImageView recipeImageView;
    private List<String> ingredientList;
    private List<String> directionList;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private LinearLayout ingredientsLinearLayout;
    private LinearLayout directionsLinearLayout;

    private IngredientAdapter ingredientAdapter;
    private DirectionAdapter directionAdapter;

    private ViewGroup.LayoutParams layoutParams;
    private LinearLayout.LayoutParams textViewLayoutParams;

    private FirebaseUser user;

    private ClockImageView cookTimeClockImageView,prepTimeClockImageView,totalTimeClockImageView;

    private Recipe recipe;

    private Toolbar toolbar;

    private AppBarLayout appBarLayout;

    private int screenHeight,screenWidth;

    private CollapsingToolbarLayout collapsingToolbar;

    private FloatingActionButton fab;

    private Boolean isFav;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

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

        //Setup Toolbar
        initializeToolbar(R.id.activity_view_recipe_toolbar,recipe.getName());
        //initCollapsibleToolbar(recipe.getName());

        //Favourite Floating Action Button
        fab=findViewById(R.id.activity_view_recipe_favourite_fab);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        firebaseFirestore= FireStoreUtility.getFirebaseFirestore();

        user= FirebaseAuthUtility.getAuth().getCurrentUser();

        ingredientsLinearLayout=findViewById(R.id.content_view_recipe_ingredient_linear_layout);
        directionsLinearLayout=findViewById(R.id.content_view_recipe_directions_linear_layout);

        recipeImageView=findViewById(R.id.activity_view_recipe_recipe_image_view);

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

        layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        setFavouriteResource(fab);
        loadData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFav){
                    databaseReference.child("favourites").child("users").child(user.getUid()).child(recipe.getId()).removeValue();
                    databaseReference.child("favourites").child("recipes").child(recipe.getId()).child(user.getUid()).removeValue();
                    firebaseFirestore.collection("favourites").document(user.getUid()).collection("recipes").document(recipe.getId()).delete();
                    setFavouriteResource(fab);
                    setFavouriteCount();
                }else{
                    databaseReference.child("favourites").child("users").child(user.getUid()).child(recipe.getId()).setValue(true);
                    databaseReference.child("favourites").child("recipes").child(recipe.getId()).child(user.getUid()).setValue(true);
                    firebaseFirestore.collection("favourites").document(user.getUid()).collection("recipes").document(recipe.getId()).set(recipe);
                    setFavouriteResource(fab);
                    setFavouriteCount();
                }

            }
        });


    }

    private void setFavouriteCount(){

        databaseReference.child("favourites").child("recipes").child(recipe.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                FireStoreUtility.getFirebaseFirestore().collection("recipes").whereEqualTo("id",recipe.getId()).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<DocumentSnapshot> dSnapshot=documentSnapshots.getDocuments();
                        FireStoreUtility.getFirebaseFirestore().collection("recipes").document(dSnapshot.get(0).getId()).update("favourites",dataSnapshot.getChildrenCount());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setFavouriteResource(final FloatingActionButton fab) {
        databaseReference.child("favourites").child("users").child(user.getUid()).child(recipe.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    isFav=true;
                    fab.setImageResource(R.drawable.favourite_is_fav);
                }else{
                    isFav=false;
                    fab.setImageResource(R.drawable.favourite_is_not_fav);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.child("ingredients").
                child(recipe.getId()).
                addValueEventListener(ingredientValueEventListener);

        databaseReference.child("directions").
                child(recipe.getId()).
                addValueEventListener(directionValueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();

        databaseReference.child("ingredients").
                child(recipe.getId()).removeEventListener(ingredientValueEventListener);
        databaseReference.child("directions").
                child(recipe.getId()).removeEventListener(directionValueEventListener);

    }

    public String getTime(int x){

        if(x>=60){

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

    //Toolbar
    private void initializeToolbar(int toolbarId,String title){
        toolbar=findViewById(toolbarId);
        setSupportActionBar(toolbar);
        TextView appBarTitleTextView=toolbar.findViewById(R.id.app_bar_all_recipes_app_bar_title_text_view);
        appBarTitleTextView.setText(title);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    private void initCollapsibleToolbar(String title) {
//
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        screenHeight = size.y;
//        screenWidth = size.x;
//        toolbar = (Toolbar) findViewById(R.id.activity_view_recipe_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        TextView appBarTitleTextView=toolbar.findViewById(R.id.app_bar_all_recipes_app_bar_title_text_view);
//        appBarTitleTextView.setText(title);
//        getSupportActionBar().setTitle("");
//        appBarLayout = findViewById(R.id.activity_view_recipe_app_bar_layout);
//        double d=screenHeight/2.5;
//        screenHeight=(int)d;
//        appBarLayout.setLayoutParams(new AppBarLayout.LayoutParams(screenWidth,screenHeight));
//        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.activity_view_recipe_collapsing_toolbar_layout);
//        collapsingToolbar.setTitleEnabled(false);
//
//    }

    private void loadData(){

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


    }


}
