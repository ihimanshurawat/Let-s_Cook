package com.example.andro.letscook.Support;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.andro.letscook.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class Methods extends AppCompatActivity{

    Toolbar toolbar;


    public void initializeToolbar(int toolbarId,String title){
        toolbar=findViewById(toolbarId);
        setSupportActionBar(toolbar);
        TextView appBarTitleTextView=toolbar.findViewById(R.id.app_bar_all_recipes_app_bar_title_text_view);
        appBarTitleTextView.setText(title);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setFavourite(String recipeID, final DatabaseReference databaseReference, FirebaseUser user, final ImageButton button){
        databaseReference.child("favourite").child(user.getUid()).child(recipeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    button.setTag(Constant.IS_FAV);
                    

                }else{

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
