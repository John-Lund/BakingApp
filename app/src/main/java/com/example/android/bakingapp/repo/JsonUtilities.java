package com.example.android.bakingapp.repo;

import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class JsonUtilities {
    // todo: enter API string below
    private static final String URL = "[enter url string here]";

    public static List<Recipe> getRecipes() {
        URL url = createUrl(URL);
        String jsonResponce = "";
        try {
            jsonResponce = loadJSON(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonResponce != null && !jsonResponce.isEmpty()) {
            return extractRecipeObjects(jsonResponce);
        } else {
            return null;
        }
    }

    private static List<Recipe> extractRecipeObjects(String jsonResponse) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Recipe recipe = new Recipe(
                        jsonObject.optInt("id", 0),
                        jsonObject.optString("name", "Unknown Cake"),
                        null,
                        null,
                        jsonObject.optInt("servings", 1),
                        jsonObject.optString("image", ""));
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                JSONArray ingredientsArray = jsonObject.optJSONArray("ingredients");
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject jsonObjectIngredient = ingredientsArray.optJSONObject(j);
                    if (jsonObjectIngredient != null) {
                        ingredients.add(new Ingredient(
                                jsonObjectIngredient.optDouble("quantity", 0),
                                jsonObjectIngredient.optString("measure", ""),
                                jsonObjectIngredient.optString("ingredient", "")));
                    }
                }
                recipe.setIngredients(ingredients);
                ArrayList<Step> steps = new ArrayList<>();
                JSONArray stepsArray = jsonObject.optJSONArray("steps");
                for (int k = 0; k < stepsArray.length(); k++) {
                    JSONObject jsonObjectStep = stepsArray.optJSONObject(k);
                    if (jsonObjectStep != null) {
                        steps.add(new Step(
                                jsonObjectStep.optInt("id", 0),
                                jsonObjectStep.optString("shortDescription", ""),
                                jsonObjectStep.optString("description", ""),
                                jsonObjectStep.optString("videoURL", ""),
                                jsonObjectStep.optString("thumbnailURL", "")));
                    }
                }
                recipe.setSteps(steps);
                recipes.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    private static URL createUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }

    private static String loadJSON(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        if (url == null) {
            return null;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = convertStream(inputStream);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String convertStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            String output = reader.readLine();
            while (output != null) {
                builder.append(output);
                output = reader.readLine();
            }
        }
        return builder.toString();
    }
}