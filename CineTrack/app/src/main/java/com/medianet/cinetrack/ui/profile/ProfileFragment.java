package com.medianet.cinetrack.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    private CircleImageView imgAvatar;
    private TextView textName, textEmail, textNameInfo,
            textEmailInfo, textMovieCount,
            textWatchlistCount, textReviewCount;
    private MaterialButton btnEditProfile, btnChangePassword, btnLogout;

    // for the profile Image
    private static final int PICK_IMAGE = 101;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imgAvatar          = view.findViewById(R.id.imgAvatar);
        textName           = view.findViewById(R.id.textName);
        textEmail          = view.findViewById(R.id.textEmail);
        textNameInfo       = view.findViewById(R.id.textNameInfo);
        textEmailInfo      = view.findViewById(R.id.textEmailInfo);
        textMovieCount     = view.findViewById(R.id.textMovieCount);
        textWatchlistCount = view.findViewById(R.id.textWatchlistCount);
        textReviewCount    = view.findViewById(R.id.textReviewCount);
        btnEditProfile     = view.findViewById(R.id.btnEditProfile);
        btnChangePassword  = view.findViewById(R.id.btnChangePassword);
        btnLogout          = view.findViewById(R.id.btnLogout);
        view.findViewById(R.id.btnEditAvatar).setOnClickListener(v -> openGallery());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Charger les infos Firebase
        loadUserInfo();

        // Observer stats
        viewModel.getStatsState().observe(getViewLifecycleOwner(), counts -> {
            textMovieCount.setText(String.valueOf(counts[0]));
            textWatchlistCount.setText(String.valueOf(counts[1]));
            textReviewCount.setText(String.valueOf(counts[2]));
        });

        // Observer update profil
        viewModel.getUpdateState().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), state.data, Toast.LENGTH_SHORT).show();
                    loadUserInfo(); // rafraîchir
                    break;
                case ERROR:
                    Toast.makeText(getContext(), state.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Modifier profil → dialog
        btnEditProfile.setOnClickListener(v -> showEditDialog());

        // Changer mot de passe → email reset
        btnChangePassword.setOnClickListener(v -> {
            viewModel.sendPasswordReset();
            Toast.makeText(getContext(),
                    "Email de réinitialisation envoyé", Toast.LENGTH_SHORT).show();
        });

        // Déconnexion
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finishAffinity();
        });

        viewModel.loadStats();
    }

    private void loadUserInfo() {
        FirebaseUser user = viewModel.getCurrentUser();
        if (user == null) return;

        String name  = user.getDisplayName() != null ? user.getDisplayName() : "Utilisateur";
        String email = user.getEmail() != null ? user.getEmail() : "";

        textName.setText(name);
        textEmail.setText(email);
        textNameInfo.setText(name);
        textEmailInfo.setText(email);

        // Photo de profil si disponible
        if (user.getPhotoUrl() != null) {
            Glide.with(requireContext())
                    .load(user.getPhotoUrl())
                    .placeholder(R.color.bg_secondary)
                    .circleCrop()
                    .into(imgAvatar);
        }
    }

    private void showEditDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.Theme_CineTrack);
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText inputNewName = dialog.findViewById(R.id.inputNewName);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);

        // Pré-remplir avec le nom actuel
        FirebaseUser user = viewModel.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            inputNewName.setText(user.getDisplayName());
        }

        btnSave.setOnClickListener(v -> {
            String newName = inputNewName.getText().toString().trim();
            if (!newName.isEmpty()) {
                viewModel.updateDisplayName(newName);
                dialog.dismiss();
            } else {
                inputNewName.setError("Nom requis");
            }
        });

        dialog.show();
    }


    // for the profile Image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE
                && resultCode == android.app.Activity.RESULT_OK
                && data != null) {
            selectedImageUri = data.getData();
            // Afficher preview immédiatement
            Glide.with(requireContext())
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(imgAvatar);
            // Uploader sur Firebase Storage
            uploadPhoto(selectedImageUri);
        }
    }

    private void uploadPhoto(Uri uri) {
        FirebaseUser user = viewModel.getCurrentUser();
        if (user == null) return;

        Toast.makeText(getContext(), "Upload en cours...", Toast.LENGTH_SHORT).show();

        com.google.firebase.storage.FirebaseStorage storage =
                com.google.firebase.storage.FirebaseStorage.getInstance();

        com.google.firebase.storage.StorageReference ref = storage.getReference()
                .child("avatars/" + user.getUid() + ".jpg");

        ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Sauvegarder l'URL dans le profil Firebase
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri)
                                    .build();
                            user.updateProfile(request)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(getContext(),
                                                    "Photo mise à jour ✓", Toast.LENGTH_SHORT).show());
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Erreur upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}