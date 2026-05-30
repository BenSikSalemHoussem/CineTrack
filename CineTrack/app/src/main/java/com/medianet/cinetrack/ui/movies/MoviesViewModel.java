package com.medianet.cinetrack.ui.movies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.repository.MovieRepository;
import com.medianet.cinetrack.utils.UiState;

import java.util.List;

public class MoviesViewModel extends ViewModel {

    private final MovieRepository repository = new MovieRepository();
    private final MutableLiveData<UiState<List<Movie>>> allMoviesState = new MutableLiveData<>();

    public LiveData<UiState<List<Movie>>> getAllMoviesState() { return allMoviesState; }

    public void loadMovies() {
        // Éviter de recharger si déjà chargé
        if (allMoviesState.getValue() != null &&
                allMoviesState.getValue().status == UiState.Status.SUCCESS) return;

        repository.getTop100Movies().observeForever(state ->
                allMoviesState.setValue(state));
    }
}