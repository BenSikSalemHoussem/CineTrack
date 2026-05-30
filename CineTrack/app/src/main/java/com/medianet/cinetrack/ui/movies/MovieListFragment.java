package com.medianet.cinetrack.ui.movies;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.adapter.MovieListAdapter;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.firebase.FirestoreRepository;
import com.medianet.cinetrack.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class MovieListFragment extends Fragment {

    private MoviesViewModel viewModel;
    private MovieListAdapter adapter;
    private FirestoreRepository firestoreRepo;

    private final List<Movie> allMovies = new ArrayList<>();

    private RecyclerView recyclerView;
    private LottieAnimationView lottieLoading;
    private View layoutError;
    private TextView textError, textCount;
    private EditText editSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView  = view.findViewById(R.id.recyclerAllMovies);
        lottieLoading = view.findViewById(R.id.lottieLoading);
        layoutError   = view.findViewById(R.id.layoutError);
        textError     = view.findViewById(R.id.textError);
        textCount     = view.findViewById(R.id.textCount);
        editSearch    = view.findViewById(R.id.editSearch);

        firestoreRepo = new FirestoreRepository();

        viewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        adapter = new MovieListAdapter(
                movie -> {
                    Bundle args = new Bundle();
                    args.putString("movieId", movie.getId());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_movieList_to_detail, args);
                },
                (movie, pos) -> {
                    if (adapter.isFavorite(movie.getId())) {
                        firestoreRepo.removeMovieFromFavorites(movie.getId(),
                                unused -> {
                                    adapter.toggleFavorite(movie.getId());
                                    Toast.makeText(getContext(),
                                            "Retiré des favoris", Toast.LENGTH_SHORT).show();
                                },
                                e -> Toast.makeText(getContext(),
                                        "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        firestoreRepo.addMovieToFavorites(movie,
                                unused -> {
                                    adapter.toggleFavorite(movie.getId());
                                    Toast.makeText(getContext(),
                                            "♥ " + movie.getDisplayTitle() + " ajouté aux favoris",
                                            Toast.LENGTH_SHORT).show();
                                },
                                e -> Toast.makeText(getContext(),
                                        "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        firestoreRepo.getFavoriteIds(ids -> adapter.setFavoriteIds(ids));

        // Bouton retour
        view.<ImageButton>findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        // Bouton retry
        view.findViewById(R.id.btnRetry).setOnClickListener(v ->
                viewModel.loadMovies()
        );

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        viewModel.loadMovies();

        // Observer
        viewModel.getAllMoviesState().observe(getViewLifecycleOwner(), state -> {
            Log.e("staus","status::"+state);
            switch (state.status) {
                case LOADING:
                    lottieLoading.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    lottieLoading.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    allMovies.clear();
                    allMovies.addAll(state.data);
                    filter(editSearch.getText().toString());
                    break;
                case ERROR:
                    lottieLoading.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    layoutError.setVisibility(View.VISIBLE);
                    textError.setText(state.message);
                    break;
            }
        });
    }

    private void filter(String query) {
        List<Movie> filtered = new ArrayList<>();
        String q = query.trim().toLowerCase();
        for (Movie m : allMovies) {
            if (q.isEmpty() || m.getTitle().toLowerCase().contains(q)) {
                filtered.add(m);
            }
        }
        adapter.setMovies(filtered);
        textCount.setText(filtered.size() + (q.isEmpty() ? " films" : " / " + allMovies.size() + " films"));
    }
}