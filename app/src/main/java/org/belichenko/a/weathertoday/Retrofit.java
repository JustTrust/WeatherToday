package org.belichenko.a.weathertoday;


import com.squareup.okhttp.OkHttpClient;

import org.belichenko.a.weathertoday.data_structure.MainData;
import org.belichenko.a.weathertoday.search_structure.MainSearch;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Get weather currentData
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
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(new OkClient(okHttpClient))
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