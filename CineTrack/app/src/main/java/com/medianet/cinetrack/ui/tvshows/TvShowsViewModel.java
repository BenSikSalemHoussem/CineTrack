package com.medianet.cinetrack.ui.tvshows;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.repository.TvShowRepository;
import com.medianet.cinetrack.utils.UiState;

import java.util.List;

public class TvShowsViewModel extends ViewModel {

    private final TvShowRepository repository = new TvShowRepository();
    private final MutableLiveData<UiState<List<TvShow>>> showsState = new MutableLiveData<>();

    public LiveData<UiState<List<TvShow>>> getShowsState() { return showsState; }

    public void loadShows() {
        if (showsState.getValue() != null &&
                showsState.getValue().status == UiState.Status.SUCCESS) return;

        repository.getTop250TvShows().observeForever(state ->
                showsState.setValue(state));
    }
}
