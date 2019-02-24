package com.bg.biozz.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MessageAPI {

    @GET("weather")
    Call<Message> messages(@Query("q") String cityName, @Query("APPID") String key, @Query("units") String metric);

    @GET("forecast")
    Call<ForeCast> forecast(@Query("q") String cityName, @Query("APPID") String key, @Query("units") String metric);
}