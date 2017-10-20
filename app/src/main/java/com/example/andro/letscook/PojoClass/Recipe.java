package com.example.andro.letscook.PojoClass;

import java.io.Serializable;
import java.util.List;


public class Recipe implements Serializable {

    private String id;
    private String name;
    private String cuisine;
    private String type;
    private String subType;
    private String imageUrl;
    private String description;
    private int servings;
    private int prepTime;
    private int cookTime;
    private int favourites;
    private List<String> mainIngredient;

    //Required By FireBase Database
    public Recipe(){

    }

    public Recipe(String id, String name, String cuisine, String type, String subType, String imageUrl, String description, int servings, int prepTime, int cookTime, int favourites, List<String> mainIngredient) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.type = type;
        this.subType = subType;
        this.imageUrl = imageUrl;
        this.description = description;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.favourites = favourites;
        this.mainIngredient = mainIngredient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getFavourites() {
        return favourites;
    }

    public void setFavourites(int favourites) {
        this.favourites = favourites;
    }

    public List<String> getMainIngredient() {
        return mainIngredient;
    }

    public void setMainIngredient(List<String> mainIngredient) {
        this.mainIngredient = mainIngredient;
    }
}
