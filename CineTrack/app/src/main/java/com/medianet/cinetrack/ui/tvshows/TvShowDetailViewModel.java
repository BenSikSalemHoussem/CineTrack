package com.medianet.cinetrack.ui.tvshows;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.repository.TvShowRepository;
import com.medianet.cinetrack.utils.UiState;

public class TvShowDetailViewModel extends ViewModel {

    private final TvShowRepository repository = new TvShowRepository();
    private final MutableLiveData<UiState<TvShow>> detailState = new MutableLiveData<>();

    public LiveData<UiState<TvShow>> getDetailState() { return detailState; }

    public void loadDetail(String id) {
        repository.getTvShowDetail(id).observeForever(state ->
                detailState.setValue(state));
    }
}
