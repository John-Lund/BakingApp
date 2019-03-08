package com.example.android.bakingapp.model;

public class Ingredient {
    private double mQuantity;
    private String mMeasure;
    private String mIngredient;

    public Ingredient(double quantity, String measure, String ingredient) {
        this.mQuantity = quantity;
        this.mMeasure = measure;
        this.mIngredient = ingredient;
    }

    public double getQuantity() {
        return mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return mIngredient;
    }
}
