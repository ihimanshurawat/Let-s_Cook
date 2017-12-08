package com.example.andro.letscook.Adapters;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andro.letscook.Activity.ViewRecipe;
import com.example.andro.letscook.PojoClass.Recipe;
import com.example.andro.letscook.R;



import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public Context context;

    public List<Recipe> recipeList;

    public RecipeAdapter(Context context,List<Recipe> recipeList){
        this.context=context;
        this.recipeList=recipeList;

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recipes_fragment_item_view,parent,false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {

        Recipe recipe=recipeList.get(position);
        if(recipe!=null) {
            holder.recipeName.setText(recipe.getName());
            holder.recipeFavourite.setText(recipe.getFavourites()+"");
            holder.recipeTotalTime.setText(getTime(recipe.getCookTime()+recipe.getPrepTime()));
            Glide.with(context.getApplicationContext()).load(recipe.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.recipeImageView);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView recipeImageView;
        TextView recipeName,recipeTotalTime,recipeFavourite;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            recipeImageView=itemView.findViewById(R.id.recipes_fragment_item_view_image_view);
            recipeName=itemView.findViewById(R.id.recipes_fragment_item_view_recipe_name_text_view);
            recipeTotalTime= itemView.findViewById(R.id.recipes_fragment_item_view_total_cook_time_text_view);
            recipeFavourite=itemView.findViewById(R.id.recipes_fragment_item_view_favourite_text_view);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Recipe recipe=recipeList.get(getAdapterPosition());
            launchViewRecipe(recipe);

        }
    }

    private void launchViewRecipe(Recipe recipe) {
        Intent i=new Intent(context, ViewRecipe.class);
        i.putExtra("Recipe",recipe);
        context.startActivity(i);
    }

    private String getTime(int x){

        if(x>60){

            return (x/60)+"h "+ (x%60)+"'";
        }
        else{

            return (x%60)+"m";
        }
    }


}
