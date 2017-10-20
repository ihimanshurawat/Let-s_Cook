package com.example.andro.letscook.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.andro.letscook.PojoClass.Ingredients;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

public class AddIngredientFragment extends Fragment {


    DatabaseReference databaseReference;

    MaterialEditText headingMaterialEditText,ingredient1MaterialEditText,ingredient2MaterialEditText,
    ingredient3MaterialEditText,ingredient4MaterialEditText,ingredient5MaterialEditText,ingredient6MaterialEditText,
    ingredient7MaterialEditText,ingredient8MaterialEditText,ingredient9MaterialEditText,ingredient10MaterialEditText,
    ingredient11MaterialEditText,ingredient12MaterialEditText,ingredient13MaterialEditText,ingredient14MaterialEditText,
    ingredient15MaterialEditText;

    Button addMoreIngredientButton,submitButton;

    String heading,ingredient_1,ingredient_2,ingredient_3,ingredient_4,ingredient_5,ingredient_6,ingredient_7,
            ingredient_8,ingredient_9,ingredient_10,ingredient_11,ingredient_12,ingredient_13,ingredient_14,ingredient_15;

    String id;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.add_ingredient_fragment,container,false);
        Bundle bundle=getArguments();
        id=bundle.getString("id");

        //Root Database Reference
        databaseReference= DatabaseUtility.getDatabase().getReference();

        //MaterialEditText
        headingMaterialEditText=v.findViewById(R.id.add_ingredient_fragment_heading_edit_text);
        ingredient1MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_1_edit_text);
        ingredient2MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_2_edit_text);
        ingredient3MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_3_edit_text);
        ingredient4MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_4_edit_text);
        ingredient5MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_5_edit_text);
        ingredient6MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_6_edit_text);
        ingredient7MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_7_edit_text);
        ingredient8MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_8_edit_text);
        ingredient9MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_9_edit_text);
        ingredient10MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_10_edit_text);
        ingredient11MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_11_edit_text);
        ingredient12MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_12_edit_text);
        ingredient13MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_13_edit_text);
        ingredient14MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_14_edit_text);
        ingredient15MaterialEditText=v.findViewById(R.id.add_ingredient_fragment_ingredient_15_edit_text);
        //Buttons
        addMoreIngredientButton=v.findViewById(R.id.add_ingredient_fragment_add_more_ingredient_button);
        submitButton=v.findViewById(R.id.add_ingredient_fragment_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Heading
                if(headingMaterialEditText.getText().length()==0){
                    heading=null;
                }else{
                    heading=headingMaterialEditText.getText().toString();
                }
                //Ingredient_1
                if(ingredient1MaterialEditText.getText().length()==0){
                    ingredient_1=null;
                }else{
                    ingredient_1=ingredient1MaterialEditText.getText().toString();
                }
                //Ingredient_2
                if(ingredient2MaterialEditText.getText().length()==0){
                    ingredient_2=null;
                }else{
                    ingredient_2=ingredient2MaterialEditText.getText().toString();
                }
                //Ingredient_3
                if(ingredient3MaterialEditText.getText().length()==0){
                    ingredient_3=null;
                }else{
                    ingredient_3=ingredient3MaterialEditText.getText().toString();
                }
                //Ingredient_4
                if(ingredient4MaterialEditText.getText().length()==0){
                    ingredient_4=null;
                }else{
                    ingredient_4=ingredient4MaterialEditText.getText().toString();
                }
                //Ingredient_5
                if(ingredient5MaterialEditText.getText().length()==0){
                    ingredient_5=null;
                }else{
                    ingredient_5=ingredient5MaterialEditText.getText().toString();
                }
                //Ingredient_6
                if(ingredient6MaterialEditText.getText().length()==0){
                    ingredient_6=null;
                }else{
                    ingredient_6=ingredient6MaterialEditText.getText().toString();
                }
                //Ingredient_7
                if(ingredient7MaterialEditText.getText().length()==0){
                    ingredient_7=null;
                }else{
                    ingredient_7=ingredient7MaterialEditText.getText().toString();
                }
                //Ingredient_8
                if(ingredient8MaterialEditText.getText().length()==0){
                    ingredient_8=null;
                }else{
                    ingredient_8=ingredient8MaterialEditText.getText().toString();
                }
                //Ingredient_9
                if(ingredient9MaterialEditText.getText().length()==0){
                    ingredient_9=null;
                }else{
                    ingredient_9=ingredient9MaterialEditText.getText().toString();
                }
                //Ingredient_10
                if(ingredient10MaterialEditText.getText().length()==0){
                    ingredient_10=null;
                }else{
                    ingredient_10=ingredient10MaterialEditText.getText().toString();
                }
                //Ingredient_11
                if(ingredient11MaterialEditText.getText().length()==0){
                    ingredient_11=null;
                }else{
                    ingredient_11=ingredient11MaterialEditText.getText().toString();
                }
                //Ingredient_12
                if(ingredient12MaterialEditText.getText().length()==0){
                    ingredient_12=null;
                }else{
                    ingredient_12=ingredient12MaterialEditText.getText().toString();
                }
                //Ingredient_13
                if(ingredient13MaterialEditText.getText().length()==0){
                    ingredient_13=null;
                }else{
                    ingredient_13=ingredient13MaterialEditText.getText().toString();
                }
                //Ingredient_14
                if(ingredient14MaterialEditText.getText().length()==0){
                    ingredient_14=null;
                }else{
                    ingredient_14=ingredient14MaterialEditText.getText().toString();
                }
                //Ingredient_2
                if(ingredient15MaterialEditText.getText().length()==0){
                    ingredient_15=null;
                }else{
                    ingredient_15=ingredient15MaterialEditText.getText().toString();
                }

                Ingredients ingredients=new Ingredients(heading,ingredient_1,ingredient_2,ingredient_3,ingredient_4,ingredient_5,ingredient_6,ingredient_7,
                        ingredient_8,ingredient_9,ingredient_10,ingredient_11,ingredient_12,ingredient_13,ingredient_14,ingredient_15);
                databaseReference.child("ingredients").child(id).push().setValue(ingredients);



            }
        });





        return v;
    }
}
