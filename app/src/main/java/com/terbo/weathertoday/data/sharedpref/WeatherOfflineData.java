package com.terbo.weathertoday.data.sharedpref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.terbo.weathertoday.core.models.WeatherContent;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

public class WeatherOfflineData implements SharedPreference {
    private static final String MY_PREFS_NAME = "LastUpdate";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public WeatherOfflineData(@NotNull Context context) {
        editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    public void setSharedPreferences(@NotNull WeatherContent weatherContent) {
        editor.putString("lastTime", weatherContent.getCurrentTime());
        editor.putString("city", weatherContent.getCityName());
        editor.putString("temp", weatherContent.getTemp());
        editor.putString("weather", weatherContent.getMainWeather());
        editor.putString("desc", weatherContent.getDescWeather());
        editor.apply();
    }

    @Override
    public WeatherContent getSharedPreferences() {
        if (isSharedPreferencesEmpty()) {
            String city = sharedPreferences.getString("city", "");
            String temp = sharedPreferences.getString("temp", "");
            String weather = sharedPreferences.getString("weather", "");
            String desc = sharedPreferences.getString("desc", "");
            String lastTime = sharedPreferences.getString("lastTime", "");
            return new WeatherContent(city, temp, weather, desc, lastTime);
        }
        return null;
    }

    public boolean isSharedPreferencesEmpty() {
        return (sharedPreferences.contains("temp"));
    }
}
