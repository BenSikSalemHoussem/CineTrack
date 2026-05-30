package com.medianet.cinetrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.data.api.model.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    public interface OnMovieClickListener  { void onClick(Movie movie); }
    public interface OnFavClickListener    { void onFav(Movie movie, int position); }

    private List<Movie> movies = new ArrayList<>();
    private Set<String> favoriteIds = new HashSet<>();
    private final OnMovieClickListener clickListener;
    private final OnFavClickListener   favListener;

    public MovieListAdapter(OnMovieClickListener click, OnFavClickListener fav) {
        this.clickListener = click;
        this.favListener   = fav;
    }

    public void setMovies(List<Movie> list) {
        this.movies = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void removeMovie(String movieId) {
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getId().equals(movieId)) {
                movies.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public int getMovieCount() { return movies.size(); }

    public void setFavoriteIds(Set<String> ids) {
        this.favoriteIds = ids;
        notifyDataSetChanged();
    }

    public void toggleFavorite(String movieId) {
        if (favoriteIds.contains(movieId)) favoriteIds.remove(movieId);
        else favoriteIds.add(movieId);
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getId().equals(movieId)) { notifyItemChanged(i); break; }
        }
    }

    public boolean isFavorite(String movieId) {
        return favoriteIds.contains(movieId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Movie m = movies.get(pos);

        h.textRank.setText(String.valueOf(pos + 1));
        h.textTitle.setText(m.getDisplayTitle());
        h.textYear.setText(m.getYear());
        h.textRating.setText("★ " + m.getRating());

        if (m.getGenre() != null && !m.getGenre().isEmpty()) {
            h.textGenre.setText(m.getGenre().get(0));
        }

        Glide.with(h.itemView.getContext())
                .load(m.getFullPosterUrl())
                .placeholder(R.color.bg_card)
                .centerCrop()
                .into(h.imgPoster);

        h.btnFav.setImageResource(favoriteIds.contains(m.getId())
                ? R.drawable.ic_bookmark_filled
                : R.drawable.ic_bookmark);

        h.itemView.setOnClickListener(v -> clickListener.onClick(m));
        h.btnFav.setOnClickListener(v -> favListener.onFav(m, pos));
    }

    @Override
    public int getItemCount() { return movies.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView   imgPoster;
        TextView    textRank, textTitle, textYear, textGenre, textRating;
        ImageButton btnFav;

        ViewHolder(View v) {
            super(v);
            imgPoster  = v.findViewById(R.id.imgPoster);
            textRank   = v.findViewById(R.id.textRank);
            textTitle  = v.findViewById(R.id.textTitle);
            textYear   = v.findViewById(R.id.textYear);
            textGenre  = v.findViewById(R.id.textGenre);
            textRating = v.findViewById(R.id.textRating);
            btnFav     = v.findViewById(R.id.btnFav);
        }
    }
}