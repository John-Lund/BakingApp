package com.example.android.bakingapp.model;
import java.util.List;

public class Recipe {
    private int mId;
    private String mName;
    private List<Ingredient> mIngredients;
    private List<Step> mSteps;
    private int mServings;
    private String mImageUrlString;
    private int mPlaceHolderId;

    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String imageUrlString) {
        this.mId = id;
        this.mName = name;
        this.mIngredients = ingredients;
        this.mSteps = steps;
        this.mServings = servings;
        this.mImageUrlString = imageUrlString;
    }

    public int getPlaceHolderId() {
        return mPlaceHolderId;
    }

    public void setPlaceHolderId(int mPlaceHolderId) {
        this.mPlaceHolderId = mPlaceHolderId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Ingredient> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public List<Step> getSteps() {
        return mSteps;
    }

    public void setSteps(List<Step> mSteps) {
        this.mSteps = mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String getImageUrlString() {
        return mImageUrlString;
    }


}
