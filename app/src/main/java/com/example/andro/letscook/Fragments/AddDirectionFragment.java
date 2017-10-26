package com.example.andro.letscook.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.andro.letscook.PojoClass.Directions;
import com.example.andro.letscook.R;
import com.example.andro.letscook.Support.DatabaseUtility;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;


public class AddDirectionFragment extends Fragment {

    DatabaseReference databaseReference;

    FragmentManager fragmentManager;

    String id;

    String heading,step1,step2,step3,step4,step5,step6,step7,step8,step9,step10,step11,step12,step13,step14,step15;

    MaterialEditText headingMaterialEditText,step1MaterialEditText,step2MaterialEditText,step3MaterialEditText,step4MaterialEditText,
    step5MaterialEditText,step6MaterialEditText,step7MaterialEditText,step8MaterialEditText,step9MaterialEditText,step10MaterialEditText,
    step11MaterialEditText,step12MaterialEditText,step13MaterialEditText,step14MaterialEditText,step15MaterialEditText;

    Button addMoreDirectionButton,submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.add_direction_fragment,container,false);

        Bundle bundle=getArguments();
        id=bundle.getString("id");


        databaseReference= DatabaseUtility.getDatabase().getReference();
        fragmentManager=getActivity().getSupportFragmentManager();


        //MaterialEditText
        headingMaterialEditText=v.findViewById(R.id.add_direction_fragment_heading_edit_text);
        step1MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_1_edit_text);
        step2MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_2_edit_text);
        step3MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_3_edit_text);
        step4MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_4_edit_text);
        step5MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_5_edit_text);
        step6MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_6_edit_text);
        step7MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_7_edit_text);
        step8MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_8_edit_text);
        step9MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_9_edit_text);
        step10MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_10_edit_text);
        step11MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_11_edit_text);
        step12MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_12_edit_text);
        step13MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_13_edit_text);
        step14MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_14_edit_text);
        step15MaterialEditText= v.findViewById(R.id.add_direction_fragment_step_15_edit_text);

        //Buttons
        addMoreDirectionButton= v.findViewById(R.id.add_direction_fragment_add_more_direction_button);
        submitButton= v.findViewById(R.id.add_direction_fragment_submit_button);

        addMoreDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDirection(id);
                launchAddDirectionFragment(id);

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDirection(id);
                Toast.makeText(getActivity(),"Recipe Successfully Added",Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });


        return v;
    }

    public void addDirection(String id){
        //Heading
        if(headingMaterialEditText.getText().length()==0){
            heading=null;
        }else{
            heading=headingMaterialEditText.getText().toString();
        }
        //Step_1
        if(step1MaterialEditText.getText().length()==0){
            step1=null;
        }else{
            step1=step1MaterialEditText.getText().toString();
        }
        //Step_2
        if(step2MaterialEditText.getText().length()==0){
            step2=null;
        }else{
            step2=step2MaterialEditText.getText().toString();
        }
        //Step_3
        if(step3MaterialEditText.getText().length()==0){
            step3=null;
        }else{
            step3=step3MaterialEditText.getText().toString();
        }
        //Step_4
        if(step4MaterialEditText.getText().length()==0){
            step4=null;
        }else{
            step4=step4MaterialEditText.getText().toString();
        }
        //Step_5
        if(step5MaterialEditText.getText().length()==0){
            step5=null;
        }else{
            step5=step5MaterialEditText.getText().toString();
        }
        //Step_6
        if(step6MaterialEditText.getText().length()==0){
            step6=null;
        }else{
            step6=step6MaterialEditText.getText().toString();
        }
        //Step_7
        if(step7MaterialEditText.getText().length()==0){
            step7=null;
        }else{
            step7=step7MaterialEditText.getText().toString();
        }
        //Step_8
        if(step8MaterialEditText.getText().length()==0){
            step8=null;
        }else{
            step8=step8MaterialEditText.getText().toString();
        }
        //Step_9
        if(step9MaterialEditText.getText().length()==0){
            step9=null;
        }else{
            step9=step9MaterialEditText.getText().toString();
        }
        //Step_10
        if(step10MaterialEditText.getText().length()==0){
            step10=null;
        }else{
            step10=step10MaterialEditText.getText().toString();
        }
        //Step_11
        if(step11MaterialEditText.getText().length()==0){
            step11=null;
        }else{
            step11=step11MaterialEditText.getText().toString();
        }
        //Step_12
        if(step12MaterialEditText.getText().length()==0){
            step12=null;
        }else{
            step12=step12MaterialEditText.getText().toString();
        }
        //Step_13
        if(step13MaterialEditText.getText().length()==0){
            step13=null;
        }else{
            step13=step13MaterialEditText.getText().toString();
        }
        //Step_14
        if(step14MaterialEditText.getText().length()==0){
            step14=null;
        }else{
            step14=step14MaterialEditText.getText().toString();
        }
        //Step_15
        if(step15MaterialEditText.getText().length()==0){
            step15=null;
        }else{
            step15=step15MaterialEditText.getText().toString();
        }

        Directions directions=new Directions(heading,step1,step2,step3,step4,step5,step6,step7,step8,step9,step10,step11,step12,step13,step14,step15);
        databaseReference.child("directions").child(id).push().setValue(directions);

    }

    public void launchAddDirectionFragment(String id){
        AddDirectionFragment addDirectionFragment= new AddDirectionFragment();
        Bundle bundle=new Bundle();
        bundle.putString("id",id);
        addDirectionFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_all_recipies_frame_layout,addDirectionFragment).
                setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).commit();
    }


}
