package com.medianet.cinetrack.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.repository.MovieRepository;
import com.medianet.cinetrack.utils.UiState;

public class DetailViewModel extends ViewModel {

    private final MovieRepository repository = new MovieRepository();
    private final MutableLiveData<UiState<Movie>> detailState = new MutableLiveData<>();

    public LiveData<UiState<Movie>> getDetailState() { return detailState; }

    public void loadDetail(String movieId) {
        repository.getMovieDetail(movieId)
                .observeForever(state -> detailState.setValue(state));
    }
}