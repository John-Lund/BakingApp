package com.example.android.bakingapp.repo;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.database.IngredientsDao;
import com.example.android.bakingapp.database.IngredientsDatabase;
import com.example.android.bakingapp.database.IngredientsObject;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.widget.BakingWidget;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private static List<IngredientsObject> mIngredientsObjectsList;
    private static List<Recipe> mRecipeList;
    private static Repository INSTANCE;
    private static MutableLiveData<List<Recipe>> mRecipes = new MutableLiveData<>();
    private static IngredientsDao mIngredientsDao;
    private static Context mContext;

    private Repository(Application application) {
        mIngredientsDao = IngredientsDatabase.getDatabase(application).ingredientsDao();
        new LoadRecipeData(mIngredientsDao).execute();
        mContext = application.getApplicationContext();
    }

    public static Repository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                INSTANCE = new Repository(application);
            }
        }
        return INSTANCE;
    }

    public void refreshOnlineData() {
        new LoadRecipeData(mIngredientsDao).execute();
    }

    public Recipe getRecipe(int index) {
        return mRecipeList.get(index);
    }

    public static List<IngredientsObject> getIngredientsObjectsList() {
        return mIngredientsObjectsList;
    }

    public MutableLiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    private static class LoadRecipeData extends AsyncTask<Void, Void, List<Recipe>> {
        IngredientsDao ingredientsDao;

        private LoadRecipeData(IngredientsDao ingredientsDao) {
            this.ingredientsDao = ingredientsDao;
        }

        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            List<Recipe> recipes = JsonUtilities.getRecipes();
            ingredientsDao.deleteAll();
            if (recipes != null && recipes.size() > 0) {
                for (int i = 0; i < recipes.size(); i++) {
                    String recipeTitle = recipes.get(i).getName();
                    String ingredientsString = TextUtilities.processIngredients(
                            recipes.get(i).getIngredients());
                    int jsonId = recipes.get(i).getId();
                    int portions = recipes.get(i).getServings();
                    IngredientsObject ingredientsObject = new IngredientsObject(
                            recipeTitle,
                            ingredientsString,
                            jsonId,
                            portions);
                    ingredientsDao.insert(ingredientsObject);
                }
            }
            return recipes;
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            if (recipes != null) {
                for (int i = 0; i < recipes.size(); i++) {
                    recipes.get(i).setPlaceHolderId(getPlaceHolderResourceId(recipes.get(i).getName()));
                }
            }
            mRecipes.setValue(recipes);
            mRecipeList = recipes;
            getIngredientsList();
        }
    }

    private static void getIngredientsList() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mIngredientsObjectsList = mIngredientsDao.getIngredients();
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(runnable);
        BakingWidget.updateWidgets(mContext);
    }

    private static int getPlaceHolderResourceId(String name) {
        name = name.toLowerCase();
        if (name.contains("pie")) {
            return R.drawable.placeholder_pie;
        } else if (name.contains("cake")) {
            return R.drawable.placeholder_cake;
        } else {
            return R.drawable.placeholder_brownies;
        }
    }
}





















