package com.example.android.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Animatable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.bakingapp.connectivity.ConnectivityReceiver;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.databinding.ActivityMainBinding;
import com.example.android.bakingapp.repo.Constants;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;


public class MainActivity extends AppCompatActivity implements MainActivityRecyclerAdapter.ItemClickListener {
    // resource for UI testing
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private ViewModel mViewModel;
    private ActivityMainBinding mBindingMain;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivityRecyclerAdapter mAdapter;
    private ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver(getApplication(), this);
    private IntentFilter mConnectionFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    // resource for UI testing
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
    // checks internet connection, starts sprite animations and sets up receiver to check
    // if internet connection is restored so that repository can load recipe data
    @Override
    protected void onResume() {
        super.onResume();
        if (!checkInternet()) {
            showInternetWarning();
        }
        if (getString(R.string.orientation).equals(Constants.PORTRAIT)) {
            ((Animatable) mBindingMain.cakeSpriteImageView.getBackground()).start();
            ((Animatable) mBindingMain.bakeBakeSpriteImageView.getBackground()).start();
        } else if (getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
            ((Animatable) mBindingMain.bakeBakeSpriteImageView.getBackground()).start();
        }
        registerReceiver(connectivityReceiver, mConnectionFilter);
    }

    // unregisters connectivity receiver on pause
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    // sets up activity, sets view, sets data binding object, detects device and
    // sets up relevant layout, sets up view model
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (getString(R.string.device_type).equals(Constants.PHONE)) {
            setUpPhoneAdapter();
        } else {
            setUpTabletAdapter();
        }
        setUpViewModel();
    }
    // displays internet warning scrim
    private void showInternetWarning() {
        mBindingMain.internetWarningScrim.setVisibility(View.VISIBLE);
        mBindingMain.internetWarningOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBindingMain.internetWarningScrim.setVisibility(View.GONE);
            }
        });
    }
    // closes internet warning scrim on back pressed if it is open and then goes back
    @Override
    public void onBackPressed() {
        if (mBindingMain.internetWarningScrim.getVisibility() == View.VISIBLE) {
            mBindingMain.internetWarningScrim.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }
    // checks the internet connection - returning true or false depending on status of connection
    private boolean checkInternet() {
        ConnectivityManager manager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        activeNetwork = (manager != null) ? manager.getActiveNetworkInfo() : null;
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    // sets up ain activity view model
    private void setUpViewModel() {
        // UI testing element
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        ((MainActivityViewModel) mViewModel).getRecipesMutable().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mAdapter.setDataList(recipes);
            }
        });
    }
    // sets up recycler adapter for tablets
    private void setUpTabletAdapter() {
        if (getString(R.string.orientation).equals(Constants.PORTRAIT)) {
            mLayoutManager = new GridLayoutManager(this, 2);
        } else {
            mLayoutManager = new GridLayoutManager(this, 3);
        }
        mBindingMain.mainRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainActivityRecyclerAdapter(this, getApplication(), mIdlingResource);
        mBindingMain.mainRecyclerView.setAdapter(mAdapter);
    }
    // sets up recycler adapter for phones
    private void setUpPhoneAdapter() {
        if (getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
            mLayoutManager = new GridLayoutManager(this, 2);
        } else {
            mLayoutManager = new LinearLayoutManager(this);
        }
        mBindingMain.mainRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainActivityRecyclerAdapter(this, getApplication(), mIdlingResource);
        mBindingMain.mainRecyclerView.setAdapter(mAdapter);
    }
    // adapter interface method for opening details activity with correct recipe data
    @Override
    public void onItemClick(int recipeIndex) {
        if (mBindingMain.internetWarningScrim.getVisibility() == View.VISIBLE) {
           return;
        }

        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(Constants.RECIPE_INDEX, recipeIndex);
        startActivity(intent);
    }
}
