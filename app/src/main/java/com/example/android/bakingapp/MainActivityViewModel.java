package com.example.android.bakingapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.repo.Repository;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private Repository mRepository;
    private MutableLiveData<List<Recipe>> mRecipesMutable;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = Repository.getRepository(application);
        this.mRecipesMutable = mRepository.getRecipes();
    }

    public MutableLiveData<List<Recipe>> getRecipesMutable() {
        return mRecipesMutable;
    }
}
