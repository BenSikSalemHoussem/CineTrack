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
import com.medianet.cinetrack.data.api.model.TvShow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TvShowListAdapter extends RecyclerView.Adapter<TvShowListAdapter.ViewHolder> {

    public interface OnClickListener  { void onClick(TvShow show); }
    public interface OnFavClickListener { void onFav(TvShow show, int position); }

    private List<TvShow>  shows       = new ArrayList<>();
    private Set<String>   favoriteIds = new HashSet<>();
    private final OnClickListener    clickListener;
    private final OnFavClickListener favListener;

    public TvShowListAdapter(OnClickListener click, OnFavClickListener fav) {
        this.clickListener = click;
        this.favListener   = fav;
    }

    public void setShows(List<TvShow> list) {
        this.shows = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void removeShow(String showId) {
        for (int i = 0; i < shows.size(); i++) {
            if (shows.get(i).getId().equals(showId)) {
                shows.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void setFavoriteIds(Set<String> ids) {
        this.favoriteIds = ids;
        notifyDataSetChanged();
    }

    public void toggleFavorite(String showId) {
        if (favoriteIds.contains(showId)) favoriteIds.remove(showId);
        else favoriteIds.add(showId);
        for (int i = 0; i < shows.size(); i++) {
            if (shows.get(i).getId().equals(showId)) { notifyItemChanged(i); break; }
        }
    }

    public boolean isFavorite(String showId) { return favoriteIds.contains(showId); }

    public int getShowCount() { return shows.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TvShow show = shows.get(pos);

        h.textRank.setText(String.valueOf(pos + 1));
        h.textTitle.setText(show.getDisplayTitle());
        h.textYear.setText(show.getYearDisplay());
        h.textRating.setText(show.getVoteAverageStr());

        if (show.getGenres() != null && !show.getGenres().isEmpty()) {
            h.textGenre.setText(show.getGenres().get(0));
        }

        Glide.with(h.itemView.getContext())
                .load(show.getFullPosterUrl())
                .placeholder(R.color.bg_card)
                .centerCrop()
                .into(h.imgPoster);

        h.btnFav.setImageResource(favoriteIds.contains(show.getId())
                ? R.drawable.ic_bookmark_filled
                : R.drawable.ic_bookmark);

        h.itemView.setOnClickListener(v -> clickListener.onClick(show));
        h.btnFav.setOnClickListener(v -> favListener.onFav(show, pos));
    }

    @Override
    public int getItemCount() { return shows.size(); }

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
