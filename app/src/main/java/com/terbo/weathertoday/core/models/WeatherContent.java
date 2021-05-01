package com.terbo.weathertoday.core.models;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class WeatherContent {
    private String cityName;
    private String temp;
    private String mainWeather;
    private String descWeather;
    private String currentTime;

    public WeatherContent(String cityName, String temp, String mainWeather, String descWeather, String currentTime) {
        this.cityName = cityName;
        this.temp = temp;
        this.mainWeather = mainWeather;
        this.descWeather = descWeather;
        this.currentTime = currentTime;
    }

    public String getCityName() {
        return cityName;
    }

    public String getTemp() {
        return temp;
    }

    public String getMainWeather() {
        return mainWeather;
    }

    public String getDescWeather() {
        return descWeather;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return "WeatherContent{" +
                "cityName='" + cityName + '\'' +
                ", temp='" + temp + '\'' +
                ", mainWeather='" + mainWeather + '\'' +
                ", descWeather='" + descWeather + '\'' +
                ", currentTime='" + currentTime + '\'' +
                '}';
    }
}
