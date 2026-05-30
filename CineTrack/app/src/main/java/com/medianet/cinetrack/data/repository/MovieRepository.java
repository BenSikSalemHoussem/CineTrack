package com.medianet.cinetrack.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.medianet.cinetrack.data.api.ApiClient;
import com.medianet.cinetrack.data.api.ImdbApiService;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.utils.UiState;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private final ImdbApiService api;

    public MovieRepository() {
        api = ApiClient.getImdbInstance().create(ImdbApiService.class);
    }

    public LiveData<UiState<List<Movie>>> getTop100Movies() {
        MutableLiveData<UiState<List<Movie>>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        api.getTop100Movies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Erreur : " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                result.setValue(UiState.error("Pas de connexion"));
            }
        });
        return result;
    }

    public LiveData<UiState<Movie>> getMovieDetail(String id) {
        MutableLiveData<UiState<Movie>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        api.getMovieDetail(id).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    result.setValue(UiState.error("Erreur : " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e("Pas de connexion",t.getMessage());
                result.setValue(UiState.error("Pas de connexion"));
            }
        });
        return result;
    }
}