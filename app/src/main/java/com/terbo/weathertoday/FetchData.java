package com.terbo.weathertoday;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.HashMap;

public class FetchData extends AsyncTaskLoader<HashMap<String,String>> {
    private String url;
    public FetchData(@NonNull Context context, String url) {
        super(context);
        this.url=url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public HashMap<String,String> loadInBackground() {
        if(url==null)
            return null;
        return  Fetch.doInBackground(url);
    }
}
