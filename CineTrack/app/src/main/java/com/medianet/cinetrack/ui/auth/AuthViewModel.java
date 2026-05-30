package com.medianet.cinetrack.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medianet.cinetrack.data.firebase.AuthRepository;
import com.medianet.cinetrack.utils.UiState;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();
    private final MutableLiveData<UiState<FirebaseUser>> authState = new MutableLiveData<>();

    public LiveData<UiState<FirebaseUser>> getAuthState() {
        return authState;
    }

    public void login(String email, String password) {
        repository.login(email, password).observeForever(state -> authState.setValue(state));
    }

    public void register(String email, String password, String displayName) {
        repository.register(email, password).observeForever(state -> {
            // Après inscription, on met à jour le profil Firebase avec le nom
            if (state.status == UiState.Status.SUCCESS && state.data != null) {
                com.google.firebase.auth.UserProfileChangeRequest profileUpdate =
                        new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                state.data.updateProfile(profileUpdate);
            }
            authState.setValue(state);
        });
    }
}