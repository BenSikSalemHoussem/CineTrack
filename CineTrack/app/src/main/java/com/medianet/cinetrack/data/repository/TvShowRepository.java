package com.medianet.cinetrack.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.medianet.cinetrack.data.api.ApiClient;
import com.medianet.cinetrack.data.api.ImdbTvApiService;
import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.utils.UiState;

import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvShowRepository {

    private static final String TAG = "TvShowRepository";

    private final ImdbTvApiService api;

    public TvShowRepository() {
        api = ApiClient.getImdbTvInstance().create(ImdbTvApiService.class);
    }

    public LiveData<UiState<List<TvShow>>> getTop250TvShows() {
        MutableLiveData<UiState<List<TvShow>>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        api.getTop250TvShows().enqueue(new Callback<List<TvShow>>() {
            @Override
            public void onResponse(Call<List<TvShow>> call, Response<List<TvShow>> response) {
                Log.d(TAG, "LIST ► URL     : " + call.request().url());
                Log.d(TAG, "LIST ► Code    : " + response.code());
                Log.d(TAG, "LIST ► Headers : " + response.headers());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "LIST ► Items   : " + response.body().size());
                    result.setValue(UiState.success(response.body()));
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null)
                            errorBody = response.errorBody().string();
                    } catch (IOException ignored) {}
                    Log.e(TAG, "LIST ► Error body: " + errorBody);
                    result.setValue(UiState.error("Erreur " + response.code() + " – voir logcat tag TvShowRepository"));
                }
            }

            @Override
            public void onFailure(Call<List<TvShow>> call, Throwable t) {
                Log.e(TAG, "LIST ► onFailure: " + t.getMessage(), t);
                result.setValue(UiState.error("Pas de connexion : " + t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<UiState<TvShow>> getTvShowDetail(String id) {
        MutableLiveData<UiState<TvShow>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());

        api.getTvShowDetail(id).enqueue(new Callback<TvShow>() {
            @Override
            public void onResponse(Call<TvShow> call, Response<TvShow> response) {
                Log.d(TAG, "DETAIL ► URL     : " + call.request().url());
                Log.d(TAG, "DETAIL ► Code    : " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(UiState.success(response.body()));
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null)
                            errorBody = response.errorBody().string();
                    } catch (IOException ignored) {}
                    Log.e(TAG, "DETAIL ► Error body: " + errorBody);
                    result.setValue(UiState.error("Erreur " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<TvShow> call, Throwable t) {
                Log.e(TAG, "DETAIL ► onFailure: " + t.getMessage(), t);
                result.setValue(UiState.error("Pas de connexion : " + t.getMessage()));
            }
        });
        return result;
    }
}
