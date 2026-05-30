package com.medianet.cinetrack.data.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.api.model.TvShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FirestoreRepository {

    private final FirebaseFirestore db;
    private final String userId;

    public FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void addMovieToWatchlist(Movie movie,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        Map<String, Object> data = new HashMap<>();
        data.put("id",          movie.getId());
        data.put("title",       movie.getTitle());
        data.put("image",       movie.getImage());
        data.put("rating",      movie.getRating());
        data.put("year",        movie.getYear());
        data.put("description", movie.getDescription());
        //data.put("director",    movie.getDirector());
        data.put("genre",       movie.getGenre());

        db.collection("users").document(userId)
                .collection("watchlist").document(movie.getId())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void removeMovieFromWatchlist(String movieId,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        db.collection("users").document(userId)
                .collection("watchlist").document(movieId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getWatchlist(Consumer<List<Movie>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userId)
                .collection("watchlist")
                .get()
                .addOnSuccessListener(query -> {
                    List<Movie> list = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) list.add(movie);
                    }
                    onSuccess.accept(list);
                })
                .addOnFailureListener(onFailure);
    }

    public void addMovieToFavorites(Movie movie,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        Map<String, Object> data = new HashMap<>();
        data.put("id",          movie.getId());
        data.put("title",       movie.getTitle());
        data.put("image",       movie.getImage());
        data.put("rating",      movie.getRating());
        data.put("year",        movie.getYear());
        data.put("description", movie.getDescription());
        data.put("genre",       movie.getGenre());

        db.collection("users").document(userId)
                .collection("favorites").document(movie.getId())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void removeMovieFromFavorites(String movieId,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        db.collection("users").document(userId)
                .collection("favorites").document(movieId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void isMovieInFavorites(String movieId, Consumer<Boolean> callback) {
        db.collection("users").document(userId)
                .collection("favorites").document(movieId)
                .get()
                .addOnSuccessListener(doc -> callback.accept(doc.exists()))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void getFavoriteIds(Consumer<Set<String>> callback) {
        db.collection("users").document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(query -> {
                    Set<String> ids = new HashSet<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        ids.add(doc.getId());
                    }
                    callback.accept(ids);
                })
                .addOnFailureListener(e -> callback.accept(new HashSet<>()));
    }

    public void getFavorites(Consumer<List<Movie>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(query -> {
                    List<Movie> list = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) list.add(movie);
                    }
                    onSuccess.accept(list);
                })
                .addOnFailureListener(onFailure);
    }

    // ── TV Show favorites ──────────────────────────────────────────────────────

    public void addTvShowToFavorites(TvShow show,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        Map<String, Object> data = new HashMap<>();
        data.put("id",            show.getId());
        data.put("title",         show.getTitle());
        data.put("image",         show.getImage());
        data.put("averageRating", show.getAverageRating());
        data.put("startYear",     show.getStartYear());
        data.put("endYear",       show.getEndYear());
        data.put("description",   show.getDescription());
        data.put("genres",        show.getGenres());

        db.collection("users").document(userId)
                .collection("tvFavorites").document(show.getId())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void removeTvShowFromFavorites(String showId,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        db.collection("users").document(userId)
                .collection("tvFavorites").document(showId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void isTvShowInFavorites(String showId, Consumer<Boolean> callback) {
        db.collection("users").document(userId)
                .collection("tvFavorites").document(showId)
                .get()
                .addOnSuccessListener(doc -> callback.accept(doc.exists()))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void getTvFavorites(Consumer<List<TvShow>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userId)
                .collection("tvFavorites")
                .get()
                .addOnSuccessListener(query -> {
                    List<TvShow> list = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        TvShow show = doc.toObject(TvShow.class);
                        if (show != null) list.add(show);
                    }
                    onSuccess.accept(list);
                })
                .addOnFailureListener(onFailure);
    }

    public void getTvShowFavoriteIds(Consumer<Set<String>> callback) {
        db.collection("users").document(userId)
                .collection("tvFavorites")
                .get()
                .addOnSuccessListener(query -> {
                    Set<String> ids = new HashSet<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        ids.add(doc.getId());
                    }
                    callback.accept(ids);
                })
                .addOnFailureListener(e -> callback.accept(new HashSet<>()));
    }
}
