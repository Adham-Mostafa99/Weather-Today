package com.terbo.weathertoday.data.network;

import android.util.Log;

import com.terbo.weathertoday.core.models.WeatherInformation;
import com.terbo.weathertoday.data.abstracts.WeatherApi;
import com.terbo.weathertoday.presentation.abstracts.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherByCity extends GetWeather {

    private String city;

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public void getWeatherInfo(WeatherData weatherData) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherInformation> call = weatherApi.getWeatherByCity(city, key);
        call.enqueue(new Callback<WeatherInformation>() {
            @Override
            public void onResponse(Call<WeatherInformation> call, Response<WeatherInformation> response) {
                if (!response.isSuccessful())
                    Log.v("TAG", "NotLoaded: " + response.code());
                else
                    weatherData.data(extractResponse(response));
            }

            @Override
            public void onFailure(Call<WeatherInformation> call, Throwable t) {
                Log.v("TAG", "error: " + t.getMessage());
            }
        });
    }
}
