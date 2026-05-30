package com.medianet.cinetrack.ui.tvshows;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.adapter.TvShowListAdapter;
import com.medianet.cinetrack.data.api.model.TvShow;
import com.medianet.cinetrack.data.firebase.FirestoreRepository;

import java.util.ArrayList;
import java.util.List;

public class TvShowListFragment extends Fragment {

    private TvShowsViewModel viewModel;
    private TvShowListAdapter adapter;
    private FirestoreRepository firestoreRepo;

    private final List<TvShow> allShows = new ArrayList<>();

    private RecyclerView      recyclerView;
    private LottieAnimationView lottieLoading;
    private View              layoutError;
    private TextView          textError, textCount;
    private EditText          editSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv_show_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView  = view.findViewById(R.id.recyclerAllShows);
        lottieLoading = view.findViewById(R.id.lottieLoading);
        layoutError   = view.findViewById(R.id.layoutError);
        textError     = view.findViewById(R.id.textError);
        textCount     = view.findViewById(R.id.textCount);
        editSearch    = view.findViewById(R.id.editSearch);

        firestoreRepo = new FirestoreRepository();
        viewModel     = new ViewModelProvider(this).get(TvShowsViewModel.class);

        adapter = new TvShowListAdapter(
                show -> {
                    Bundle args = new Bundle();
                    args.putString("tvShowId", show.getId());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_tvShowList_to_tvShowDetail, args);
                },
                (show, pos) -> {
                    if (adapter.isFavorite(show.getId())) {
                        firestoreRepo.removeTvShowFromFavorites(show.getId(),
                                unused -> {
                                    adapter.toggleFavorite(show.getId());
                                    Toast.makeText(getContext(),
                                            "Retiré des favoris", Toast.LENGTH_SHORT).show();
                                },
                                e -> Toast.makeText(getContext(),
                                        "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        firestoreRepo.addTvShowToFavorites(show,
                                unused -> {
                                    adapter.toggleFavorite(show.getId());
                                    Toast.makeText(getContext(),
                                            "♥ " + show.getDisplayTitle() + " ajouté aux favoris",
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

        firestoreRepo.getTvShowFavoriteIds(ids -> adapter.setFavoriteIds(ids));

        view.findViewById(R.id.btnRetry).setOnClickListener(v ->
                viewModel.loadShows()
        );

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        viewModel.loadShows();

        viewModel.getShowsState().observe(getViewLifecycleOwner(), state -> {
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
                    allShows.clear();
                    allShows.addAll(state.data);
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
        List<TvShow> filtered = new ArrayList<>();
        String q = query.trim().toLowerCase();
        for (TvShow s : allShows) {
            if (q.isEmpty() || s.getDisplayTitle().toLowerCase().contains(q)) {
                filtered.add(s);
            }
        }
        adapter.setShows(filtered);
        textCount.setText(filtered.size() + (q.isEmpty() ? " séries" : " / " + allShows.size() + " séries"));
    }
}
