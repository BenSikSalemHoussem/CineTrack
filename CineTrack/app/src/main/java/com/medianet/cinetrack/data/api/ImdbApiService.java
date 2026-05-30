package com.medianet.cinetrack.data.api;

import com.medianet.cinetrack.data.api.model.Movie;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ImdbApiService {

    @Headers({
            "X-RapidAPI-Key: " + com.medianet.cinetrack.utils.Constants.RAPIDAPI_KEY,
            "X-RapidAPI-Host: imdb-top-100-movies.p.rapidapi.com"
    })
    @GET("/")
    Call<List<Movie>> getTop100Movies();

    @Headers({
            "X-RapidAPI-Key: " + com.medianet.cinetrack.utils.Constants.RAPIDAPI_KEY,
            "X-RapidAPI-Host: imdb-top-100-movies.p.rapidapi.com"
    })
    @GET("/{id}")
    Call<Movie> getMovieDetail(@Path("id") String id);
}