package com.terbo.weathertoday.core.models;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("main")
    private String mainWeather;
    @SerializedName("description")
    private String descriptionWeather;

    public String getMainWeather() {
        return mainWeather;
    }

    public String getDescriptionWeather() {
        return descriptionWeather;
    }

    @Override
    public String toString() {
        return "WeatherModel{" +
                "main='" + mainWeather + '\'' +
                ", description='" + descriptionWeather + '\'' +
                '}';
    }
}
