package com.terbo.weathertoday.core.models;

import com.google.gson.annotations.SerializedName;

public class WeatherInformation {

    @SerializedName("name")
    private String name;
    @SerializedName("weather")
    private Weather[] weather;
    @SerializedName("main")
    private Main main;

    public String getName() {
        return name;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }
}




