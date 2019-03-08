package com.example.android.bakingapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;
import com.example.android.bakingapp.repo.Repository;
import com.example.android.bakingapp.repo.TextUtilities;

import java.util.List;

public class DetailsViewModel extends AndroidViewModel {
    private static boolean mIsPlaying = true;
    private static long mPlayPosition = 0;
    private static boolean mThisIsAPhone;
    private static int mCurrentStepIndex = 0;
    private Repository mRepository;
    private Recipe mCurrentRecipe;
    private MutableLiveData<Step> mCurrentStep = new MutableLiveData<>();
    private boolean mInfoMenuIsOpen;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = Repository.getRepository(application);
        this.mInfoMenuIsOpen = false;
    }

    // controls movement through steps list
    public void switchStep(int value) {
        if (mCurrentStepIndex + value < 0) return;
        if (mCurrentStepIndex + value > mCurrentRecipe.getSteps().size() - 1) return;
        setCurrentStep(mCurrentStepIndex + value);
    }
    // checks to see if end of steps list has been reached so that
    // forward button n video fragment can be hidden
    public boolean thisIsTheFinalStep() {
        if (mCurrentStepIndex + 1 > mCurrentRecipe.getSteps().size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setCurrentStep(int index) {
        mIsPlaying = true;
        mPlayPosition = 0;
        mCurrentStepIndex = index;
        mCurrentStep.setValue(mCurrentRecipe.getSteps().get(index));
    }
    // getters and setters

    public MutableLiveData<Step> getCurrentStep() {
        return mCurrentStep;
    }

    public boolean getIsInfoMenuOpen() {
        return mInfoMenuIsOpen;
    }

    public void setIsInfoMenuOpen(Boolean value) {
        mInfoMenuIsOpen = value;
    }

    public boolean getIsPlaying() {
        return mIsPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        mIsPlaying = isPlaying;
    }

    public long getPlayPosition() {
        return mPlayPosition;
    }

    public void setPlayPosition(long playPosition) {
        mPlayPosition = playPosition;
    }

    public int getPortions() {
        return mCurrentRecipe.getServings();
    }

    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public void setCurrentRecipe(int index) {
        mCurrentRecipe = mRepository.getRecipe(index);
    }

    public List<Step> getCurrentStepsList() {
        return mCurrentRecipe.getSteps();
    }

    public String getIngredients() {
        return TextUtilities.processIngredients(mCurrentRecipe.getIngredients());
    }

    public boolean getThisIsAPhone() {
        return mThisIsAPhone;
    }

    public void setThisIsAPhone(boolean thisIsAPhone) {
        mThisIsAPhone = thisIsAPhone;
    }
}
