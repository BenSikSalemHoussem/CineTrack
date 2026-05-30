package com.medianet.cinetrack.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medianet.cinetrack.utils.UiState;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<UiState<String>> updateState = new MutableLiveData<>();
    private final MutableLiveData<int[]> statsState = new MutableLiveData<>();

    public LiveData<UiState<String>> getUpdateState() { return updateState; }
    public LiveData<int[]> getStatsState() { return statsState; }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void updateDisplayName(String newName) {
        updateState.setValue(UiState.loading());
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(unused ->
                        updateState.setValue(UiState.success("Profil mis à jour")))
                .addOnFailureListener(e ->
                        updateState.setValue(UiState.error(e.getMessage())));
    }

    public void loadStats() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();

        int[] counts = {0, 0, 0};

        // Favorites count
        db.collection("users").document(uid).collection("favorites")
                .get().addOnSuccessListener(snap -> {
                    counts[0] = snap.size();

                    // Watchlist count (trailers ouverts)
                    db.collection("users").document(uid).collection("watchlist")
                            .get().addOnSuccessListener(snap2 -> {
                                counts[1] = snap2.size();

                                // TV show favorites count
                                db.collection("users").document(uid).collection("tvFavorites")
                                        .get().addOnSuccessListener(snap3 -> {
                                            counts[2] = snap3.size();
                                            statsState.setValue(counts);
                                        })
                                        .addOnFailureListener(e -> statsState.setValue(counts));
                            })
                            .addOnFailureListener(e -> statsState.setValue(counts));
                })
                .addOnFailureListener(e -> statsState.setValue(counts));
    }

    public void sendPasswordReset() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            auth.sendPasswordResetEmail(user.getEmail());
        }
    }
}