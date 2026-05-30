package com.medianet.cinetrack.ui.tvshows;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.firebase.FirestoreRepository;

import java.util.List;

public class TvShowDetailFragment extends Fragment {

    private TvShowDetailViewModel viewModel;
    private CastAdapter           castAdapter;
    private FirestoreRepository   firestoreRepo;
    private TvShow                currentShow;
    private boolean               isFavorite = false;

    private ImageView       imgPoster;
    private TextView        textTitle, textYear, textGenre, textRating, textDescription, textCreator;
    private LottieAnimationView lottieLoading;
    private RecyclerView    recyclerCast;
    private ImageButton     btnBack, btnBookmark, btnTrailer;
    private MaterialButton  btnAddToFavorites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv_show_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imgPoster        = view.findViewById(R.id.imgPoster);
        textTitle        = view.findViewById(R.id.textTitle);
        textYear         = view.findViewById(R.id.textYear);
        textGenre        = view.findViewById(R.id.textGenre);
        textRating       = view.findViewById(R.id.textRating);
        textDescription  = view.findViewById(R.id.textDescription);
        textCreator      = view.findViewById(R.id.textCreator);
        lottieLoading    = view.findViewById(R.id.lottieLoading);
        recyclerCast     = view.findViewById(R.id.recyclerCast);
        btnBack          = view.findViewById(R.id.btnBack);
        btnBookmark      = view.findViewById(R.id.btnBookmark);
        btnTrailer       = view.findViewById(R.id.btnTrailer);
        btnAddToFavorites = view.findViewById(R.id.btnAddToFavorites);

        firestoreRepo = new FirestoreRepository();
        castAdapter   = new CastAdapter();

        recyclerCast.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCast.setAdapter(castAdapter);

        String tvShowId = getArguments() != null ? getArguments().getString("tvShowId") : null;

        viewModel = new ViewModelProvider(this).get(TvShowDetailViewModel.class);

        viewModel.getDetailState().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    lottieLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    lottieLoading.setVisibility(View.GONE);
                    currentShow = state.data;
                    bindShow(state.data);
                    checkFavoriteStatus(state.data.getId());
                    break;
                case ERROR:
                    lottieLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        if (tvShowId != null) viewModel.loadDetail(tvShowId);

        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        btnAddToFavorites.setOnClickListener(v -> {
            if (currentShow == null) return;
            if (isFavorite) {
                firestoreRepo.removeTvShowFromFavorites(currentShow.getId(),
                        unused -> {
                            isFavorite = false;
                            updateFavoriteButton();
                            Toast.makeText(getContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } else {
                firestoreRepo.addTvShowToFavorites(currentShow,
                        unused -> {
                            isFavorite = true;
                            updateFavoriteButton();
                            Toast.makeText(getContext(), "Ajouté aux favoris ✓", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });

        btnTrailer.setOnClickListener(v -> {
            if (currentShow != null && currentShow.getTrailer() != null) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(currentShow.getTrailer())));
            } else {
                Toast.makeText(getContext(), "Trailer non disponible", Toast.LENGTH_SHORT).show();
            }
        });

        btnBookmark.setOnClickListener(v -> btnAddToFavorites.performClick());
    }

    private void bindShow(TvShow show) {
        textTitle.setText(show.getDisplayTitle());
        textYear.setText(show.getYearDisplay());
        textRating.setText(show.getVoteAverageStr());
        textDescription.setText(show.getDescription());

        if (show.getGenres() != null && !show.getGenres().isEmpty()) {
            textGenre.setText(android.text.TextUtils.join(" · ", show.getGenres()));
        }

        List<String> directorNames = show.getDirectorNames();
        textCreator.setText(!directorNames.isEmpty()
                ? android.text.TextUtils.join(", ", directorNames)
                : "Non disponible");

        List<String> castNames = show.getCastNames();
        if (!castNames.isEmpty()) castAdapter.setCast(castNames);

        Glide.with(requireContext())
                .load(show.getFullPosterUrl())
                .placeholder(R.color.bg_card)
                .centerCrop()
                .into(imgPoster);
    }

    private void checkFavoriteStatus(String showId) {
        firestoreRepo.isTvShowInFavorites(showId, exists -> {
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
