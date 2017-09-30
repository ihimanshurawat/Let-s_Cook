package com.example.andro.letscook.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andro.letscook.R;

import java.util.List;



public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public Context context;
    private List<String> ingredientList;

    public IngredientAdapter(Context context, List<String> ingredientList){
        this.context=context;
        this.ingredientList=ingredientList;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.view_recipe_fragment_ingredient_item_view,parent,false);



        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {

        if(!(ingredientList.isEmpty())){
            String arr[]=ingredientList.get(position).split(":");
            holder.ingredientTextView.setText(arr[0]);
            holder.quantityTextView.setText(arr[1]);

        }

    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }


    public static class IngredientViewHolder extends RecyclerView.ViewHolder{
        TextView ingredientTextView,quantityTextView;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            ingredientTextView = itemView.findViewById(R.id.view_recipe_fragment_ingredient_item_view_ingredient_text_view);
            quantityTextView = itemView.findViewById(R.id.view_recipe_fragment_ingredient_item_view_quantity_text_view);
        }
    }

}
