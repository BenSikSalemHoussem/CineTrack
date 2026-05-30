package com.medianet.cinetrack.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.adapter.CastAdapter;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.firebase.FirestoreRepository;

public class DetailFragment extends Fragment {

    private DetailViewModel viewModel;
    private CastAdapter castAdapter;
    private FirestoreRepository firestoreRepo;
    private Movie currentMovie;
    private boolean isFavorite = false;

    private ImageView imgPoster;
    private TextView textTitle, textYear, textGenre,
            textRating, textDescription, textDirector;
    private LottieAnimationView lottieLoading;
    private RecyclerView recyclerCast;
    private ImageButton btnBack, btnBookmark, btnTrailer;
    private MaterialButton btnAddToFavorites, btnReview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Bind views
        imgPoster        = view.findViewById(R.id.imgPoster);
        textTitle        = view.findViewById(R.id.textTitle);
        textYear         = view.findViewById(R.id.textYear);
        textGenre        = view.findViewById(R.id.textGenre);
        textRating       = view.findViewById(R.id.textRating);
        textDescription  = view.findViewById(R.id.textDescription);
        textDirector     = view.findViewById(R.id.textDirector);
        lottieLoading    = view.findViewById(R.id.lottieLoading);
        recyclerCast     = view.findViewById(R.id.recyclerCast);
        btnBack          = view.findViewById(R.id.btnBack);
        btnBookmark      = view.findViewById(R.id.btnBookmark);
        btnTrailer       = view.findViewById(R.id.btnTrailer);
        btnAddToFavorites = view.findViewById(R.id.btnAddToFavorites);
        btnReview        = view.findViewById(R.id.btnReview);

        firestoreRepo = new FirestoreRepository();
        castAdapter   = new CastAdapter();

        recyclerCast.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCast.setAdapter(castAdapter);

        // Récupérer l'ID passé depuis HomeFragment
        String movieId = getArguments() != null ? getArguments().getString("movieId") : null;
        Log.e("movieId::",movieId.toString());

        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        viewModel.getDetailState().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    lottieLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    lottieLoading.setVisibility(View.GONE);
                    currentMovie = state.data;
                    bindMovie(state.data);
                    checkFavoriteStatus(state.data.getId());
                    break;
                case ERROR:
                    lottieLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR::",state.message);
                    break;
            }
        });

        if (movieId != null) viewModel.loadDetail(movieId);

        // Bouton retour
        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Bouton favoris
        btnAddToFavorites.setOnClickListener(v -> {
            if (currentMovie == null) return;
            if (isFavorite) {
                firestoreRepo.removeMovieFromFavorites(currentMovie.getId(),
                        unused -> {
                            isFavorite = false;
                            updateFavoriteButton();
                            Toast.makeText(getContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } else {
                firestoreRepo.addMovieToFavorites(currentMovie,
                        unused -> {
                            isFavorite = true;
                            updateFavoriteButton();
                            Toast.makeText(getContext(), "Ajouté aux favoris ✓", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });

        // Bouton trailer — ouvre le trailer et ajoute automatiquement à la watchlist
        btnTrailer.setOnClickListener(v -> {
            if (currentMovie != null && currentMovie.getTrailer() != null) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(currentMovie.getTrailer())));
                firestoreRepo.addMovieToWatchlist(currentMovie, unused -> {}, e -> {});
            } else {
                Toast.makeText(getContext(), "Trailer non disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton avis → navigation vers ReviewFragment
        btnReview.setOnClickListener(v -> {
            if (currentMovie != null) {
                Bundle args = new Bundle();
                args.putString("movieId", currentMovie.getId());
                args.putString("movieTitle", currentMovie.getTitle());
                // Navigation.findNavController(view).navigate(R.id.action_detail_to_review, args);
            }
        });

        // Bouton bookmark
        btnBookmark.setOnClickListener(v ->
                btnAddToFavorites.performClick()
        );
    }

    private void bindMovie(Movie movie) {
        textTitle.setText(movie.getTitle());
        textYear.setText(movie.getYear());
        textRating.setText("★ " + movie.getRating());
        textDescription.setText(movie.getDescription());

        // Genres (tous, séparés par un point médian)
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            textGenre.setText(android.text.TextUtils.join(" · ", movie.getGenre()));
        }

        // Réalisateur (liste → jointure)
        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            textDirector.setText(android.text.TextUtils.join(", ", movie.getDirector()));
        } else {
            textDirector.setText("Non disponible");
        }

        // Scénaristes (l'API ne fournit pas de casting — on affiche les writers)
        if (movie.getWriters() != null && !movie.getWriters().isEmpty()) {
            castAdapter.setCast(movie.getWriters());
        }

        // Poster
        Glide.with(requireContext())
                .load(movie.getImage())
                .placeholder(R.color.bg_card)
                .centerCrop()
                .into(imgPoster);
    }

    private void checkFavoriteStatus(String movieId) {
        firestoreRepo.isMovieInFavorites(movieId, exists -> {
            isFavorite = exists;
            updateFavoriteButton();
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnAddToFavorites.setText("- Retirer des favoris");
            btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            btnAddToFavorites.setText("+ Ajouter aux favoris");
            btnBookmark.setImageResource(R.drawable.ic_bookmark);
        }
    }
}