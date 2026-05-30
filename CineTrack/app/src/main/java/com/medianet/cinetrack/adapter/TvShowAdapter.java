package com.medianet.cinetrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.data.api.model.TvShow;

import java.util.ArrayList;
import java.util.List;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.ViewHolder> {

    public interface OnClickListener { void onClick(TvShow show); }

    private List<TvShow> shows = new ArrayList<>();
    private final OnClickListener listener;

    public TvShowAdapter(OnClickListener listener) { this.listener = listener; }

    public void setShows(List<TvShow> shows) {
        this.shows = shows;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TvShow show = shows.get(pos);
        h.textTitle.setText(show.getDisplayTitle());
        h.textYear.setText(show.getYearDisplay());
        h.textRating.setText(show.getVoteAverageStr());

        Glide.with(h.itemView.getContext())
                .load(show.getFullPosterUrl())
                .placeholder(R.color.bg_card)
                .centerCrop()
                .into(h.imgPoster);

        h.itemView.setOnClickListener(v -> listener.onClick(show));
    }

    @Override
    public int getItemCount() { return shows.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView  textTitle, textYear, textRating;

        ViewHolder(View v) {
            super(v);
            imgPoster  = v.findViewById(R.id.imgPoster);
            textTitle  = v.findViewById(R.id.textTitle);
            textYear   = v.findViewById(R.id.textYear);
            textRating = v.findViewById(R.id.textRating);
        }
    }
}
