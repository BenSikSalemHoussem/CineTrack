package com.medianet.cinetrack.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.repository.MovieRepository;
import com.medianet.cinetrack.data.repository.TvShowRepository;
import com.medianet.cinetrack.utils.UiState;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MovieRepository  movieRepo  = new MovieRepository();
    private final TvShowRepository tvRepo     = new TvShowRepository();

    private final MutableLiveData<UiState<List<Movie>>>  moviesState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<TvShow>>> seriesState = new MutableLiveData<>();

    public LiveData<UiState<List<Movie>>>  getMoviesState() { return moviesState; }
    public LiveData<UiState<List<TvShow>>> getSeriesState() { return seriesState; }

    public void loadAllMostPopular() {
        movieRepo.getTop100Movies().observeForever(state -> {
            if (state.status == UiState.Status.SUCCESS && state.data != null) {
                List<Movie> top20 = state.data.subList(0, Math.min(20, state.data.size()));
                moviesState.setValue(UiState.success(top20));
            } else {
                moviesState.setValue(state);
            }
        });

        tvRepo.getTop250TvShows().observeForever(state -> {
            if (state.status == UiState.Status.SUCCESS && state.data != null) {
                List<TvShow> top20 = state.data.subList(0, Math.min(20, state.data.size()));
                seriesState.setValue(UiState.success(top20));
            } else {
                seriesState.setValue(state);
            }
        });
    }

}
