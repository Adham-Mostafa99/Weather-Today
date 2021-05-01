package com.terbo.weathertoday.data.abstracts;

import com.terbo.weathertoday.core.models.WeatherInformation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherInformation> getWeatherByCity(@Query("q") String city,
                                              @Query("appid") String key);

    @GET("weather")
    Call<WeatherInformation> getWeatherByLocation(@Query("lat") double lat,
                                                  @Query("lon") double lon,
                                                  @Query("appid") String key);
}
