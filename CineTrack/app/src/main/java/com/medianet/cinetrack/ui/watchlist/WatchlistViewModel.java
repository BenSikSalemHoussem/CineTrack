package com.medianet.cinetrack.ui.watchlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.firebase.FirestoreRepository;
import com.medianet.cinetrack.utils.UiState;

import java.util.List;

public class WatchlistViewModel extends ViewModel {

    private final FirestoreRepository firestoreRepo = new FirestoreRepository();

    private final MutableLiveData<UiState<List<Movie>>>  moviesState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<TvShow>>> seriesState = new MutableLiveData<>();

    public LiveData<UiState<List<Movie>>>  getMoviesState() { return moviesState; }
    public LiveData<UiState<List<TvShow>>> getSeriesState() { return seriesState; }

    public void loadFavorites() {
        moviesState.setValue(UiState.loading());
        firestoreRepo.getFavorites(
                movies -> moviesState.setValue(UiState.success(movies)),
                e     -> moviesState.setValue(UiState.error(e.getMessage()))
        );
    }

    public void loadTvFavorites() {
        seriesState.setValue(UiState.loading());
        firestoreRepo.getTvFavorites(
                shows -> seriesState.setValue(UiState.success(shows)),
                e     -> seriesState.setValue(UiState.error(e.getMessage()))
        );
    }
}
