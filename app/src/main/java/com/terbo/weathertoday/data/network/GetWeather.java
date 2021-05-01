package com.terbo.weathertoday.data.network;

import android.util.Log;

import com.terbo.weathertoday.core.models.WeatherContent;
import com.terbo.weathertoday.core.models.WeatherInformation;
import com.terbo.weathertoday.data.abstracts.WeatherApi;
import com.terbo.weathertoday.presentation.abstracts.WeatherData;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class GetWeather {
    protected String url = "https://api.openweathermap.org/data/2.5/";
    protected String key = "96e32140b3209f2476517e342dcbf041";

    public abstract void getWeatherInfo(WeatherData weatherData);

    public WeatherContent extractResponse(@NotNull Response<WeatherInformation> response) {
        assert response.body() != null;
        String cityName = response.body().getName();
        String temp = convertFtoC(response.body().getMain().getTemp());
        String weather = response.body().getWeather()[0].getMainWeather();
        String dec = response.body().getWeather()[0].getDescriptionWeather();
        String currentTime = Calendar.getInstance().getTime().toString();
        return new WeatherContent(cityName, temp, weather, dec, currentTime);
    }

    public String convertFtoC(double temp) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(temp - 273.15);
    }
}
