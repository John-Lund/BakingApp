package com.example.android.bakingapp.database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity (tableName = "ingredients_table")
public class IngredientsObject {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String recipeTitle;
    private String ingredientsString;
    private int jsonId;
    private int portions;

    @Ignore
    public IngredientsObject(String recipeTitle, String ingredientsString, int jsonId, int portions) {
        this.recipeTitle = recipeTitle;
        this.ingredientsString = ingredientsString;
        this.jsonId = jsonId;
        this.portions = portions;
    }

    public IngredientsObject(){
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getIngredientsString() {
        return ingredientsString;
    }

    public void setIngredientsString(String ingredientsString) {
        this.ingredientsString = ingredientsString;
    }

    public int getJsonId() {
        return jsonId;
    }

    public void setJsonId(int jsonId) {
        this.jsonId = jsonId;
    }
}
