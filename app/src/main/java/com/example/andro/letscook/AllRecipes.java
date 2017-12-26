package com.example.andro.letscook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andro.letscook.activity.AddRecipe;
import com.example.andro.letscook.fragment.RecipesFragment;
import com.example.andro.letscook.activity.EditProfile;
import com.example.andro.letscook.activity.FavouriteRecipe;
import com.example.andro.letscook.pojo.User;
import com.example.andro.letscook.support.DatabaseUtility;
import com.example.andro.letscook.support.FirebaseAuthUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class AllRecipes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Profile Items
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;

    //User Credentials
    private String userEmail;
    private String userName;
    private String userProfile;

    //Edit Profile
    private TextView editProfile;

    //Firebase User
    private FirebaseUser currentUser;
    //FirebaseAuth Reference
    private FirebaseAuth mAuth;
    //Firebase DatabaseReference
    private DatabaseReference databaseReference;
    //FireStore
    private FirebaseFirestore db;

    //Drawer Reference
    private DrawerLayout drawer;

    //FragmentManager Reference
    private FragmentManager fragmentManager;
    private String arr[];

    private String key;

    private User existingUser;

    private Context context;

    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser()==null){
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child("users").
                removeEventListener(userProfileValueEventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);
        Toolbar toolbar = findViewById(R.id.app_bar_all_recipes_toolbar);
        setSupportActionBar(toolbar);
        
        mAuth= FirebaseAuthUtility.getAuth();
        currentUser=mAuth.getCurrentUser();

        databaseReference= DatabaseUtility.getDatabase().getReference();

        //db=FireStoreUtility.getFirebaseFirestore();

        if(currentUser!=null) {
            arr = currentUser.getEmail().split("\\.");
        }else{
            finish();
        }
        context=this;

//        db.collection("users").whereEqualTo("email",currentUser.getEmail()).limit(1).get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//                if(documentSnapshots.isEmpty()){
//                    User newUser = new User(currentUser.getEmail(), currentUser.getDisplayName()
//                            ,null, currentUser.getPhotoUrl() + "", 0, null,"All");
//                    db.collection("users").add(newUser);
//
//                }else{
//                    List<DocumentSnapshot> documentSnapshot=documentSnapshots.getDocuments();
//                    Log.i("IsWorking",documentSnapshot.get(0).getId());
//                    key=documentSnapshot.get(0).getId();
//                    existingUser=documentSnapshot.get(0).toObject(User.class);
//                    userEmail = existingUser.getEmail();
//                    userProfile = existingUser.getProfileUrl();
//                    userName = existingUser.getName();
//                    nameTextView.setText(userName);
//                    emailTextView.setText(userEmail);
//                    Glide.with(context.getApplicationContext()).load(userProfile).apply(RequestOptions.circleCropTransform()).into(profileImageView);
//                }
//
//            }
//        });

        //To Manage User Profiles
        if(currentUser!=null) {
            databaseReference.child("users").orderByChild("email").equalTo(currentUser.getEmail()).limitToFirst(1).addValueEventListener(userProfileValueEventListener);
        }
        fragmentManager=getSupportFragmentManager();

        launchRecipeFragment(existingUser);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Nav Header
        View NavigationHeader = navigationView.getHeaderView(0);
        nameTextView=NavigationHeader.findViewById(R.id.nav_header_all_recipies_name_text_view);
        emailTextView=NavigationHeader.findViewById(R.id.nav_header_all_recipies_email_text_view);
        profileImageView=NavigationHeader.findViewById(R.id.nav_header_all_recipies_profile_image_view);
        editProfile=NavigationHeader.findViewById(R.id.nav_header_all_recipies_edit_profile_text_view);


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                Toast.makeText(AllRecipes.this,"Edit Profile",Toast.LENGTH_SHORT).show();
                //Launch Edit Profile Fragment
                launchEditProfileFragment(key);
            }
        });
    }



    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_recipes, menu);
        final MenuItem addRecipe=menu.findItem(R.id.all_recipies_add_recipie);
        //if(arr[0].equals("rawath54@gmail")){
            addRecipe.setVisible(true);
            this.invalidateOptionsMenu();
      //  }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.all_recipies_add_recipie){
            startActivity(new Intent(AllRecipes.this, AddRecipe.class));

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.activity_all_recipes_drawer_edit_profile) {
            launchEditProfileFragment(key);
        } else if (id == R.id.activity_all_recipes_drawer_recipes) {
            launchRecipeFragment(existingUser);

        } else if (id == R.id.activity_all_recipes_drawer_favourites) {
            startActivity(new Intent(AllRecipes.this, FavouriteRecipe.class));

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.log_out) {

            Toast.makeText(this,"See you again :)",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchRecipeFragment(User existingUser){
        RecipesFragment recipesFragment = new RecipesFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("User",existingUser);
        recipesFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_all_recipies_frame_layout, recipesFragment, "Recipes Fragment")
                .commit();
    }

    public void launchEditProfileFragment(String key){
        Intent i=new Intent(AllRecipes.this, EditProfile.class);
        i.putExtra("Key",key);
        startActivity(i);
    }

    ValueEventListener userProfileValueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount()>0){
                for(DataSnapshot child : dataSnapshot.getChildren()) {

                    key=child.getKey();

                    databaseReference.child("users").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            existingUser = dataSnapshot.getValue(User.class);
                            userEmail = existingUser.getEmail();
                            userProfile = existingUser.getProfileUrl();
                            userName = existingUser.getName();
                            nameTextView.setText(userName);
                            emailTextView.setText(userEmail);
                            Glide.with(context.getApplicationContext()).load(userProfile).apply(RequestOptions.circleCropTransform()).into(profileImageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
            else{
                User newUser = new User(currentUser.getUid(),currentUser.getEmail(), currentUser.getDisplayName()
                        ,null, currentUser.getPhotoUrl() + "", 0, null,"All");
                databaseReference.child("users").push().setValue(newUser);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

            //Toast.makeText(AllRecipes.this,"Unable to Access Database",Toast.LENGTH_SHORT).show();
        }
    };



}
