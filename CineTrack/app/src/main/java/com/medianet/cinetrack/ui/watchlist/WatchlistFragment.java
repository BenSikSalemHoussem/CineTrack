package com.medianet.cinetrack.ui.watchlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.adapter.MovieListAdapter;
import com.medianet.cinetrack.adapter.TvShowListAdapter;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.firebase.FirestoreRepository;

import java.util.HashSet;
import java.util.List;

public class WatchlistFragment extends Fragment {

    private WatchlistViewModel    viewModel;
    private MovieListAdapter      moviesAdapter;
    private TvShowListAdapter     seriesAdapter;
    private FirestoreRepository   firestoreRepo;

    private RecyclerView          recyclerView;
    private LottieAnimationView   lottieLoading;
    private View                  layoutEmpty;
    private TextView              textEmpty;
    private MaterialButtonToggleGroup toggleGroup;

    private boolean showingMovies = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView  = view.findViewById(R.id.recyclerFavorites);
        lottieLoading = view.findViewById(R.id.lottieLoading);
        layoutEmpty   = view.findViewById(R.id.layoutEmpty);
        textEmpty     = view.findViewById(R.id.textEmpty);
        toggleGroup   = view.findViewById(R.id.toggleGroup);

        firestoreRepo = new FirestoreRepository();
        viewModel     = new ViewModelProvider(this).get(WatchlistViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Movies adapter
        moviesAdapter = new MovieListAdapter(
                movie -> navigateToMovieDetail(view, movie),
                (movie, pos) -> firestoreRepo.removeMovieFromFavorites(
                        movie.getId(),
                        unused -> {
                            moviesAdapter.removeMovie(movie.getId());
                            if (moviesAdapter.getMovieCount() == 0) showEmpty();
                            Toast.makeText(getContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                )
        );

        // TV shows adapter
        seriesAdapter = new TvShowListAdapter(
                show -> navigateToTvShowDetail(view, show),
                (show, pos) -> firestoreRepo.removeTvShowFromFavorites(
                        show.getId(),
                        unused -> {
                            seriesAdapter.removeShow(show.getId());
                            if (seriesAdapter.getShowCount() == 0) showEmpty();
                            Toast.makeText(getContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                )
        );

        // Default: show movies tab
        toggleGroup.check(R.id.btnMovies);
        switchToMovies();

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnMovies) switchToMovies();
            else                             switchToSeries();
        });

        // Observe movies
        viewModel.getMoviesState().observe(getViewLifecycleOwner(), state -> {
            if (!showingMovies) return;
            switch (state.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    lottieLoading.setVisibility(View.GONE);
                    bindMovies(state.data);
                    break;
                case ERROR:
                    lottieLoading.setVisibility(View.GONE);
                    showEmpty();
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Observe TV shows
        viewModel.getSeriesState().observe(getViewLifecycleOwner(), state -> {
            if (showingMovies) return;
            switch (state.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    lottieLoading.setVisibility(View.GONE);
                    bindSeries(state.data);
                    break;
                case ERROR:
                    lottieLoading.setVisibility(View.GONE);
                    showEmpty();
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.loadFavorites();
        viewModel.loadTvFavorites();
    }

    private void switchToMovies() {
        showingMovies = true;
        recyclerView.setAdapter(moviesAdapter);
        // Re-trigger display with already-loaded data
        if (viewModel.getMoviesState().getValue() != null &&
                viewModel.getMoviesState().getValue().status == com.medianet.cinetrack.utils.UiState.Status.SUCCESS) {
            bindMovies(viewModel.getMoviesState().getValue().data);
        } else {
            showLoading();
        }
    }

    private void switchToSeries() {
        showingMovies = false;
        recyclerView.setAdapter(seriesAdapter);
        if (viewModel.getSeriesState().getValue() != null &&
                viewModel.getSeriesState().getValue().status == com.medianet.cinetrack.utils.UiState.Status.SUCCESS) {
            bindSeries(viewModel.getSeriesState().getValue().data);
        } else {
            showLoading();
        }
    }

    private void bindMovies(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            showEmpty();
            return;
        }
        HashSet<String> ids = new HashSet<>();
        for (Movie m : movies) ids.add(m.getId());
        moviesAdapter.setFavoriteIds(ids);
        moviesAdapter.setMovies(movies);
        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void bindSeries(List<TvShow> shows) {
        if (shows == null || shows.isEmpty()) {
            showEmpty();
            return;
        }
        HashSet<String> ids = new HashSet<>();
        for (TvShow s : shows) ids.add(s.getId());
        seriesAdapter.setFavoriteIds(ids);
        seriesAdapter.setShows(shows);
        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showLoading() {
        lottieLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        textEmpty.setText(showingMovies ? "Aucun film favori" : "Aucune série favorite");
    }

    private void navigateToMovieDetail(View view, Movie movie) {
        Bundle args = new Bundle();
        args.putString("movieId", movie.getId());
        Navigation.findNavController(view).navigate(R.id.action_watchlist_to_detail, args);
    }

    private void navigateToTvShowDetail(View view, TvShow show) {
        Bundle args = new Bundle();
        args.putString("tvShowId", show.getId());
        Navigation.findNavController(view).navigate(R.id.action_watchlist_to_tvShowDetail, args);
    }
}
