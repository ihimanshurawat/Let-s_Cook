package com.example.andro.letscook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andro.letscook.Fragments.EditProfileFragment;
import com.example.andro.letscook.Fragments.RecipiesFragment;
import com.example.andro.letscook.PojoClass.User;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.example.andro.letscook.Support.FirebaseAuthUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AllRecipes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Profile Items
    ImageView profileImageView;
    TextView nameTextView;
    TextView emailTextView;

    //User Credentials
    String userEmail;
    String userName;
    String userProfile;

    //Edit Profile
    TextView editProfile;

    //Firebase Auth
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    //Firebase Database

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    //Drawer Reference
    DrawerLayout drawer;

    //FragmentManager Reference
    FragmentManager fragmentManager;
    String arr[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth= FirebaseAuthUtility.getAuth();
        currentUser=mAuth.getCurrentUser();


        databaseReference= DatabaseUtility.getDatabase().getReference();

        if(currentUser!=null) {
            arr = currentUser.getEmail().split("\\.");
        }else{
            finish();
        }

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (currentUser!= null) {
                    if (dataSnapshot.hasChild(arr[0])) {
                        User existingUser = dataSnapshot.child(arr[0]).getValue(User.class);
                        userEmail = existingUser.getEmail() + "";
                        userProfile = existingUser.getProfileUrl() + "";
                        userName = existingUser.getName() + "";

                        nameTextView.setText(userName);
                        emailTextView.setText(userEmail);
                        Glide.with(AllRecipes.this).load(userProfile)
                                .apply(RequestOptions.circleCropTransform()).into(profileImageView);

                    } else {

                        User newUser = new User(currentUser.getEmail(), currentUser.getDisplayName()
                                ,null, currentUser.getPhotoUrl() + "", 0, null);
                        databaseReference.child("users").child(arr[0]).setValue(newUser);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fragmentManager=getSupportFragmentManager();

        RecipiesFragment recipiesFragment=new RecipiesFragment();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_all_recipies_frame_layout,recipiesFragment,"Recipies Fragment")
                .commit();



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//
//            userEmail = currentUser.getEmail();
//            userName = currentUser.getDisplayName();
//            userProfile = currentUser.getPhotoUrl()+"";




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
                Toast.makeText(AllRecipes.this,"Edit Profile",Toast.LENGTH_LONG).show();
                //FragmentManager fragmentManager=getSupportFragmentManager();
                EditProfileFragment editProfileFragment=new EditProfileFragment();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_all_recipies_frame_layout,editProfileFragment,"Edit Profile")
                        .setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).commit();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_recipes, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
