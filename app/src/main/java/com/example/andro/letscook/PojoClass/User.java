package com.example.andro.letscook.PojoClass;

/**
 * Created by himanshurawat on 08/09/17.
 */

public class User {

    private String email;
    private String name;
    private String description;
    private String profileUrl;
    private int numberOfFavourites;
    private String favouriteIds;

    public User(){

    }

    public User(String email, String name, String description, String profileUrl, int numberOfFavourites, String favouriteIds) {

        this.email = email;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.numberOfFavourites = numberOfFavourites;
        this.favouriteIds = favouriteIds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getNumberOfFavourites() {
        return numberOfFavourites;
    }

    public void setNumberOfFavourites(int numberOfFavourites) {
        this.numberOfFavourites = numberOfFavourites;
    }

    public String getFavouriteIds() {
        return favouriteIds;
    }

    public void setFavouriteIds(String favouriteIds) {
        this.favouriteIds = favouriteIds;
    }
}
