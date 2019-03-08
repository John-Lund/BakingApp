package com.example.android.bakingapp.widget;


import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.database.IngredientsObject;
import com.example.android.bakingapp.repo.Repository;

import java.util.List;

public class BakingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<IngredientsObject> mIngredientsObjectList;

    public BakingWidgetFactory(Context context) {
        mContext = context;
        mIngredientsObjectList = Repository.getIngredientsObjectsList();
    }

    @Override
    public void onCreate() {
        loadData();
    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (mIngredientsObjectList == null || mIngredientsObjectList.size() == 0) {
            return 0;
        } else {
            return mIngredientsObjectList.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.baking_widget_list_item);
        view.setTextViewText(R.id.recipe_name_text_view, mIngredientsObjectList.get(position).getRecipeTitle());
        view.setTextViewText(R.id.recipe_ingredients_text_view, mIngredientsObjectList.get(position).getIngredientsString());
        view.setTextViewText(R.id.portions_text_view,
                mContext.getResources().getString(R.string.portions, String.valueOf(mIngredientsObjectList.get(position).getPortions())));
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadData() {
        mIngredientsObjectList = Repository.getIngredientsObjectsList();
    }
}





















