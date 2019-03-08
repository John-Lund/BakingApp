package com.example.android.bakingapp;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.bakingapp.model.Recipe;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MainActivityRecyclerAdapter extends RecyclerView.Adapter<MainActivityRecyclerAdapter.ViewHolder> {
    // UI testing element
    @Nullable
    private SimpleIdlingResource mSimpleIdlingResource;
    private Application mApplication;
    private List<Recipe> mRecipes;
    private ItemClickListener mItemClickListener;

    public interface ItemClickListener {
        void onItemClick(int recipeIndex);
    }

    public MainActivityRecyclerAdapter(ItemClickListener itemClickListener,
                                       Application application,
                                       @Nullable final SimpleIdlingResource idlingResource) {
        this.mSimpleIdlingResource = idlingResource;
        this.mItemClickListener = itemClickListener;
        this.mApplication = application;
    }


    @NonNull
    @Override
    public MainActivityRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_activity_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityRecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.cakeNameTextView.setText(mRecipes.get(i).getName());
        viewHolder.cakePortionsTextView.setText(mApplication.getResources().getString(R.string.portions,
                String.valueOf(mRecipes.get(i).getServings())));
        if (!TextUtils.isEmpty(mRecipes.get(i).getImageUrlString())) {
            Picasso.with(mApplication)
                    .load(mRecipes.get(i).getImageUrlString())
                    .error(mRecipes.get(i).getPlaceHolderId())
                    .into(viewHolder.cakeImage);
        } else {
            viewHolder.cakeImage.setImageDrawable(
                    mApplication.getResources().getDrawable(mRecipes.get(i).getPlaceHolderId(),
                            null));
        }
        if(mSimpleIdlingResource != null){
            mSimpleIdlingResource.setIdleState(true);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) {
            return 0;
        } else {
            return mRecipes.size();
        }
    }

    public void setDataList(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView cakeNameTextView;
        private final TextView cakePortionsTextView;
        private final ImageView cakeImage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cakeNameTextView = itemView.findViewById(R.id.cake_name_text_view);
            this.cakePortionsTextView = itemView.findViewById(R.id.portions_text_view);
            this.cakeImage = itemView.findViewById(R.id.main_cake_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mItemClickListener.onItemClick(position);
        }
    }
}
