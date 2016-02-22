package org.belichenko.a.weathertoday;


import org.belichenko.a.weathertoday.data_structure.MainData;
import org.belichenko.a.weathertoday.search_structure.MainSearch;

import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Get weather data
 */
public class Retrofit implements MyConstants {
    private static final String ENDPOINT = "http://api.worldweatheronline.com/free/v2";
    private static ApiInterface apiInterface;

    static {
        initialize();
    }

    interface ApiInterface {
        @GET("/weather.ashx")
        void getWeatherData(@QueryMap Map<String,String> filters, Callback<MainData> callback);
        @GET("/search.ashx")
        void getListOfCity(@QueryMap Map<String,String> filters, Callback<MainSearch> callback);
    }

    public static void initialize() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        apiInterface = restAdapter.create(ApiInterface.class);
    }

    public static void getWeatherData(Map<String,String> filters, Callback<MainData> callback) {
        apiInterface.getWeatherData(filters ,callback);
    }
    public static void getListOfCity(Map<String,String> filters, Callback<MainSearch> callback) {
        apiInterface.getListOfCity(filters ,callback);
    }
}