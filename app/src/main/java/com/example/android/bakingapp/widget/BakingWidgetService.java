package com.example.android.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class BakingWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingWidgetFactory(this);
    }
}
