package com.medianet.cinetrack.ui.home;

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
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.adapter.MovieAdapter;
import com.medianet.cinetrack.adapter.TvShowAdapter;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.api.model.TvShow;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private MovieAdapter  moviesAdapter;
    private TvShowAdapter seriesAdapter;

    private RecyclerView        recyclerMovies, recyclerSeries;
    private LottieAnimationView lottieMovies, lottieSeries;
    private TextView            textErrorMovies, textErrorSeries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerMovies  = view.findViewById(R.id.recyclerMovies);
        recyclerSeries  = view.findViewById(R.id.recyclerSeries);
        lottieMovies    = view.findViewById(R.id.lottieMovies);
        lottieSeries    = view.findViewById(R.id.lottieSeries);
        textErrorMovies = view.findViewById(R.id.textErrorMovies);
        textErrorSeries = view.findViewById(R.id.textErrorSeries);

        view.findViewById(R.id.textSeeAllMovies).setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_home_to_movieList));

        view.findViewById(R.id.textSeeAllSeries).setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_home_to_tvShowList));

        moviesAdapter = new MovieAdapter(this::navigateToMovieDetail);
        seriesAdapter = new TvShowAdapter(this::navigateToTvShowDetail);

        recyclerMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerSeries.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerMovies.setAdapter(moviesAdapter);
        recyclerSeries.setAdapter(seriesAdapter);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getMoviesState().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    lottieMovies.setVisibility(View.VISIBLE);
                    recyclerMovies.setVisibility(View.GONE);
                    textErrorMovies.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    lottieMovies.setVisibility(View.GONE);
                    recyclerMovies.setVisibility(View.VISIBLE);
                    moviesAdapter.setMovies(state.data);
                    break;
                case ERROR:
                    lottieMovies.setVisibility(View.GONE);
                    textErrorMovies.setVisibility(View.VISIBLE);
                    textErrorMovies.setText(state.message);
                    break;
            }
        });

        viewModel.getSeriesState().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    lottieSeries.setVisibility(View.VISIBLE);
                    recyclerSeries.setVisibility(View.GONE);
                    textErrorSeries.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    lottieSeries.setVisibility(View.GONE);
                    recyclerSeries.setVisibility(View.VISIBLE);
                    seriesAdapter.setShows(state.data);
                    break;
                case ERROR:
                    lottieSeries.setVisibility(View.GONE);
                    textErrorSeries.setVisibility(View.VISIBLE);
                    textErrorSeries.setText(state.message);
                    break;
            }
        });

        viewModel.loadAllMostPopular();
    }

    private void navigateToMovieDetail(Movie movie) {
        Bundle args = new Bundle();
        args.putString("movieId", movie.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_home_to_detail, args);
        Toast.makeText(getContext(), movie.getDisplayTitle(), Toast.LENGTH_SHORT).show();
    }

    private void navigateToTvShowDetail(TvShow show) {
        Bundle args = new Bundle();
        args.putString("tvShowId", show.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_home_to_tvShowDetail, args);
    }
}
