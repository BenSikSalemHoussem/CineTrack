package com.medianet.cinetrack.data.api;

import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.utils.Constants;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ImdbTvApiService {

    @Headers({
            "X-RapidAPI-Key: " + Constants.RAPIDAPI_KEY,
            "X-RapidAPI-Host: " + Constants.IMDB_TV_HOST
    })
    @GET("/api/imdb/top250-tv")
    Call<List<TvShow>> getTop250TvShows();

    @Headers({
            "X-RapidAPI-Key: " + Constants.RAPIDAPI_KEY,
            "X-RapidAPI-Host: " + Constants.IMDB_TV_HOST
    })
    @GET("/api/imdb/{id}")
    Call<TvShow> getTvShowDetail(@Path("id") String id);
}
