package com.medianet.cinetrack.data.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medianet.cinetrack.utils.UiState;

public class AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<UiState<FirebaseUser>> login(String email, String password) {
        MutableLiveData<UiState<FirebaseUser>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(r -> result.setValue(UiState.success(r.getUser())))
                .addOnFailureListener(e -> result.setValue(UiState.error(e.getMessage())));
        return result;
    }

    public LiveData<UiState<FirebaseUser>> register(String email, String password) {
        MutableLiveData<UiState<FirebaseUser>> result = new MutableLiveData<>();
        result.setValue(UiState.loading());
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(r -> result.setValue(UiState.success(r.getUser())))
                .addOnFailureListener(e -> result.setValue(UiState.error(e.getMessage())));
        return result;
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void logout() {
        auth.signOut();
    }
}