package com.terbo.weathertoday.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.terbo.weathertoday.core.models.WeatherContent;

public interface SharedPreference {

    void setSharedPreferences(WeatherContent weatherContent);

    WeatherContent getSharedPreferences();
}
