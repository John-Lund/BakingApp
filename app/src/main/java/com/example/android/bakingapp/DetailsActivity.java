package com.example.android.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.example.android.bakingapp.databinding.ActivityDetailsBinding;
import com.example.android.bakingapp.repo.Constants;

public class DetailsActivity extends AppCompatActivity implements StepsFragment.StepListener, VideoFragment.FullScreenModeListener {

    private DetailsViewModel mViewModel;
    private ActivityDetailsBinding mDetailsBinding;
    private StepsFragment mStepsFragment;
    private VideoFragment mVideoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        // sets up view model with recipe index for current recipe taken from received intent
        setUpViewModel(getIntent().getIntExtra(Constants.RECIPE_INDEX, 0));

        if (savedInstanceState == null) {
            // checks to see if layout is for tablet or phone
            // and applies the correct layout
            if (getString(R.string.device_type).equals(Constants.TABLET)) {
                mViewModel.setThisIsAPhone(false);
                setUpTabletLayout();
            } else {
                mViewModel.setThisIsAPhone(true);
                setUpPhoneLayout();
            }
        }
        setUpToolbar();
    }

    // restores info screen if it was open before rotation
    // and checks internet connection to warn user if there is no connection
    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel.getIsInfoMenuOpen()) {
            openInfoScrim();
        }
        if (!checkInternet()) {
            Toast.makeText(this, getString(R.string.internet_warning_toast), Toast.LENGTH_LONG).show();
        }
    }

    // checks internet connection
    private boolean checkInternet() {
        ConnectivityManager manager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        activeNetwork = (manager != null) ? manager.getActiveNetworkInfo() : null;
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    // interface method from video fragment to hide elements of UI when video is full screen
    @Override
    public void setFullScreenMode() {
        mDetailsBinding.detailsToolbar.setVisibility(View.GONE);
        mDetailsBinding.detailsScrim.setVisibility(View.GONE);
        mDetailsBinding.detailsToolbar.setVisibility(View.GONE);
    }
    // sets up the tool bar
    public void setUpToolbar() {
        setSupportActionBar(mDetailsBinding.detailsToolbar);
        mDetailsBinding.detailTitleTextView.setText(mViewModel.getCurrentRecipe().getName());
        mDetailsBinding.detailsToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        mDetailsBinding.detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        onBackPressed();
            }
        });
    }
    // setting up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu_item, menu);
      return true;
    }

    // opens and closes info scrim when option is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mViewModel.getIsInfoMenuOpen()) {
            openInfoScrim();
        } else {
            closeInfoScrim();
        }
        return true;
    }
    // closes info scrim and lets view model know about it
    private void closeInfoScrim() {
        mDetailsBinding.detailsScrim.setVisibility(View.GONE);
        mDetailsBinding.detailsToolbar.setVisibility(View.VISIBLE);
        mViewModel.setIsInfoMenuOpen(false);
    }
    //opens info scrim and populates views with relevant data
    private void openInfoScrim() {
        mDetailsBinding.detailsScrim.setVisibility(View.VISIBLE);
        mDetailsBinding.scrimIngredientsTextView.setText(mViewModel.getIngredients());
        mDetailsBinding.scrimPortionsTextView.setText(getString(R.string.portions, String.valueOf(mViewModel.getPortions())));
        mDetailsBinding.detailsScrim.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetailsBinding.detailsScrim.setVisibility(View.GONE);
                mViewModel.setIsInfoMenuOpen(false);
                mDetailsBinding.detailsToolbar.setVisibility(View.VISIBLE);
                return true;
            }
        });
        mViewModel.setIsInfoMenuOpen(true);
        if (getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
            mDetailsBinding.detailsToolbar.setVisibility(View.GONE);
        }
    }
    // closes info scrim, if open, before going back
    @Override
    public void onBackPressed() {
        if(mDetailsBinding.detailsScrim.getVisibility() == View.VISIBLE){
            mDetailsBinding.detailsScrim.setVisibility(View.GONE);
            mDetailsBinding.detailsToolbar.setVisibility(View.VISIBLE);
            mViewModel.setIsInfoMenuOpen(false);
            return;
        }
        super.onBackPressed();
    }
    // for phone layout, displays video fragment when recipe step is selected
    @Override
    public void onStepClick(int index) {
        if (mVideoFragment == null) mVideoFragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_mobile_frame_layout, mVideoFragment, "mVideoFragment")
                .addToBackStack(null)
                .commit();
    }
    // sets up initial phone layout with steps fragment displayed
    private void setUpPhoneLayout() {
        mViewModel.setCurrentStep(0);
        mStepsFragment = new StepsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.details_mobile_frame_layout, mStepsFragment, "mStepsFragment")
                .commit();
    }
    // sets up tablet layout with both fragments displayed
    private void setUpTabletLayout() {
        mViewModel.setCurrentStep(0);
        mStepsFragment = new StepsFragment();
        mVideoFragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.details_steps_frame, mStepsFragment)
                .add(R.id.details_video_frame, mVideoFragment)
                .commit();
    }
    // sets up view model to be shared with this activity and both fragments
    private void setUpViewModel(int index) {
        mViewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        mViewModel.setCurrentRecipe(index);
    }
}















