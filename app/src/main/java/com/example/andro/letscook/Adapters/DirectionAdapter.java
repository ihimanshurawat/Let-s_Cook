package com.example.andro.letscook.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.andro.letscook.R;

import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.DirectionViewHolder> {

    public Context context;
    public List<String> directionList;

    public DirectionAdapter(Context context,List<String>directionList){
        this.context=context;
        this.directionList=directionList;

    }

    @Override
    public DirectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.view_recipe_fragment_direction_item_view,parent,false);

        return new DirectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DirectionViewHolder holder, int position) {
        holder.directionTextView.setText(directionList.get(position));

    }

    @Override
    public int getItemCount() {
        return directionList.size();
    }


    public static class DirectionViewHolder extends RecyclerView.ViewHolder{

        TextView directionTextView;
        CheckBox directionCheckBox;

        public DirectionViewHolder(View itemView) {
            super(itemView);

            directionTextView=itemView.findViewById(R.id.view_recipe_fragment_direction_item_view_direction_text_view);
            directionCheckBox=itemView.findViewById(R.id.view_recipe_fragment_direction_item_view_direction_checkbox);

        }


    }


}
