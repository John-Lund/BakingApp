package com.example.android.bakingapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;
import com.example.android.bakingapp.repo.Constants;
import com.squareup.picasso.Picasso;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    private Activity mActivity;
    final private ItemClickListener mItemClickListener;
    private List<Step> mSteps;
    private String mIngredients;
    private int mPortions;
    private String mCakeImageUrlString;
    private Recipe mRecipe;

    public StepsAdapter(ItemClickListener mItemClickListener, Activity activity, String cakeImageUrlString, Recipe recipe) {
        this.mItemClickListener = mItemClickListener;
        this.mActivity = activity;
        this.mCakeImageUrlString = cakeImageUrlString;
        this.mRecipe = recipe;
    }

    public interface ItemClickListener {
        void onItemClick(int index);
    }

    @NonNull
    @Override
    public StepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == 1) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.steps_list_initial_item, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.steps_list_item, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsAdapter.ViewHolder viewHolder, int i) {
        if (viewHolder.mPlayButton != null && !mActivity.getResources().getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
            if (!TextUtils.isEmpty(mCakeImageUrlString)) {
                Picasso.with(mActivity)
                        .load(mCakeImageUrlString)
                        .error(mRecipe.getPlaceHolderId())
                        .into(viewHolder.cakeImage);
            } else {
                viewHolder.cakeImage.setImageResource(mRecipe.getPlaceHolderId());
            }

            viewHolder.mPortionsTextView.setText(
                    mActivity.getResources().getString(R.string.portions, String.valueOf(mPortions)));
            viewHolder.mIngredientsTextView.setText(mIngredients);

        } else if (viewHolder.mStepNumberTextView != null) {
            viewHolder.mStepNumberTextView.setText(String.valueOf(mSteps.get(i).getId()));
            viewHolder.mStepsDescriptionTextView.setText(mSteps.get(i).getShortDescription());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) {
            return 0;
        } else {
            return mSteps.size();
        }
    }

    public void setDataList(List<Step> steps, String ingredients, int portions) {
        mSteps = steps;
        mIngredients = ingredients;
        mPortions = portions;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mStepNumberTextView;
        private TextView mStepsDescriptionTextView;
        private TextView mPortionsTextView;
        private TextView mIngredientsTextView;
        private ImageButton mPlayButton;
        private ImageView cakeImage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.steps_list_item_initial_item_play_button) != null
                    && !mActivity.getResources().getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
                this.mPortionsTextView = itemView.findViewById(R.id.steps_list_initial_item_portions_text_view);
                this.mIngredientsTextView = itemView.findViewById(R.id.steps_initial_list_item_ingredients);
                this.mPlayButton = itemView.findViewById(R.id.steps_list_item_initial_item_play_button);
                this.cakeImage = itemView.findViewById(R.id.steps_list_cake_image);
                mPlayButton.setOnClickListener(this);
            } else if (itemView.findViewById(R.id.steps_text_view) != null) {
                this.mStepsDescriptionTextView = itemView.findViewById(R.id.steps_description_text_view);
                this.mStepNumberTextView = itemView.findViewById(R.id.steps_text_view);
                itemView.setOnClickListener(this);
            }
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mItemClickListener.onItemClick(position);
        }
    }
}
