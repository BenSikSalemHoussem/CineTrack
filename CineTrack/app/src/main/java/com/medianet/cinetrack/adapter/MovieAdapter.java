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
import com.medianet.cinetrack.data.api.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private List<Movie> movies = new ArrayList<>();
    private final OnMovieClickListener listener;

    public MovieAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() { return movies.size(); }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView textTitle, textYear, textRating;

        MovieViewHolder(View itemView) {
            super(itemView);
            imgPoster   = itemView.findViewById(R.id.imgPoster);
            textTitle   = itemView.findViewById(R.id.textTitle);
            textYear    = itemView.findViewById(R.id.textYear);
            textRating  = itemView.findViewById(R.id.textRating);
        }

        void bind(Movie movie) {
            textTitle.setText(movie.getDisplayTitle());
            textYear.setText(movie.getYear());
            //textRating.setText("★ " + String.format("%.1f", movie.getVoteAverage()));
            textRating.setText(movie.getVoteAverageStr());

            Glide.with(itemView.getContext())
                    .load(movie.getFullPosterUrl())
                    .placeholder(R.color.bg_card)
                    .centerCrop()
                    .into(imgPoster);

            itemView.setOnClickListener(v -> listener.onMovieClick(movie));
        }
    }
}
