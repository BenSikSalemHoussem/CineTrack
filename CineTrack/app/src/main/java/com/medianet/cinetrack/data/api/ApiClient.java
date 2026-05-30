package com.medianet.cinetrack.data.api;

import com.medianet.cinetrack.utils.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit imdbInstance;
    private static Retrofit imdbTvInstance;

    public static Retrofit getImdbInstance() {
        if (imdbInstance == null) {
            imdbInstance = new Retrofit.Builder()
                .baseUrl(Constants.IMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return imdbInstance;
    }

    public static Retrofit getImdbTvInstance() {
        if (imdbTvInstance == null) {
            imdbTvInstance = new Retrofit.Builder()
                .baseUrl(Constants.IMDB_TV_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return imdbTvInstance;
    }
}