package com.app.paul.newsapp.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.app.paul.newsapp.News;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>>{

    private String mUrl;

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        if(mUrl != null){
            return Query.getNewsData(mUrl);
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
