package com.example.android.bakingapp;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.databinding.FragmentStepsBinding;
import com.example.android.bakingapp.repo.Constants;

import java.util.Objects;


public class StepsFragment extends Fragment implements StepsAdapter.ItemClickListener,
        StepsAdapterTablet.ItemClickListener {
    private DetailsViewModel mViewModel;
    private StepsAdapterTablet mStepsAdapterTablet;
    private StepsAdapter mStepsAdapter;
    private FragmentStepsBinding mBinding;
    private StepListener mStepListener;
    private LinearLayoutManager mLayoutManager;

    public StepsFragment() {
    }
    // detects if device is a phone in landscape mode and sets up correct layout
    @Override
    public void onResume() {
        super.onResume();
        if (getString(R.string.orientation).equals(Constants.LANDSCAPE) && mViewModel.getThisIsAPhone()) {
            setUpPhoneLandscapeUi();
        }
    }
    // sets up special layout for phone in landscape mode
    public void setUpPhoneLandscapeUi() {
        mBinding.stepsIngredientsLandscapeTextView.setText(mViewModel.getIngredients());
        mBinding.stepsPortionsLandscapeTextView.setText(getString(R.string.portions,
                String.valueOf(mViewModel.getCurrentRecipe().getServings())));
        mBinding.stepsLandscapeForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setCurrentStep(0);
                if (mViewModel.getThisIsAPhone()) mStepListener.onStepClick(0);
            }
        });
    }
    // sets up view layout, data binding object, listener for steps list, view model and adapter
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_steps, container, false);
        View rootView = mBinding.getRoot();
        mStepListener = (StepListener) getActivity();
        setUpViewModel();
        setUpStepsAdapter();
        return rootView;
    }
    // cleans up adapter and layout manager on destroy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStepsAdapter = null;
        mLayoutManager = null;
    }
    // gets view model set up by details activity
    private void setUpViewModel() {
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DetailsViewModel.class);
    }
    // sets up the correct recycler adapter depending on the device type
    private void setUpStepsAdapter() {
        if (mViewModel.getThisIsAPhone()) {
            mStepsAdapter = new StepsAdapter(this, getActivity(), mViewModel.getCurrentRecipe().getImageUrlString(), mViewModel.getCurrentRecipe());
            mLayoutManager = new LinearLayoutManager(getContext());
            mBinding.detailsStepsRecyclerView.setLayoutManager(mLayoutManager);
            mBinding.detailsStepsRecyclerView.setAdapter(mStepsAdapter);
            mStepsAdapter.setDataList(mViewModel.getCurrentStepsList(), mViewModel.getIngredients(), mViewModel.getPortions());
        } else {
            mStepsAdapterTablet = new StepsAdapterTablet(this, getActivity(), mViewModel.getCurrentRecipe().getImageUrlString(), mViewModel.getCurrentRecipe());
            mLayoutManager = new LinearLayoutManager(getContext());
            mBinding.detailsStepsRecyclerView.setLayoutManager(mLayoutManager);
            mBinding.detailsStepsRecyclerView.setAdapter(mStepsAdapterTablet);
            mStepsAdapterTablet.setDataList(mViewModel.getCurrentStepsList(), mViewModel.getIngredients(), mViewModel.getPortions());
        }
    }
    // interface method to change current recipe step according to user's click on list item
    @Override
    public void onTabletItemClick(int index) {
        mViewModel.setCurrentStep(index);
    }
    // interface method to change current recipe step in details activity according to user's click on list item
    @Override
    public void onItemClick(int index) {
        mViewModel.setCurrentStep(index);
        if (mViewModel.getThisIsAPhone()) mStepListener.onStepClick(index);
    }
    // interface method to communicate with details activity
    public interface StepListener {
        void onStepClick(int index);
    }
}
