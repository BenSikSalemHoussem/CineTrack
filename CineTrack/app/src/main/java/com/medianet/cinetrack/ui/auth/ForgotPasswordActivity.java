package com.medianet.cinetrack.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.medianet.cinetrack.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout layoutEmail;
    private TextInputEditText inputEmail;
    private MaterialButton btnSend;
    private ProgressBar progressBar;
    private TextView textError, textSuccess, textGoToLogin;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        layoutEmail   = findViewById(R.id.layoutEmail);
        inputEmail    = findViewById(R.id.inputEmail);
        btnSend       = findViewById(R.id.btnSend);
        progressBar   = findViewById(R.id.progressBar);
        textError     = findViewById(R.id.textError);
        textSuccess   = findViewById(R.id.textSuccess);
        textGoToLogin = findViewById(R.id.textGoToLogin);

        btnSend.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            if (!validate(email)) return;
            sendResetEmail(email);
        });

        textGoToLogin.setOnClickListener(v -> finish());
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    private boolean validate(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Email invalide");
            return false;
        }
        layoutEmail.setError(null);
        return true;
    }

    private void sendResetEmail(String email) {
        showLoading(true);

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    showLoading(false);
                    // Cacher le bouton et afficher le succès
                    btnSend.setVisibility(View.GONE);
                    layoutEmail.setEnabled(false);
                    textSuccess.setVisibility(View.VISIBLE);
                    textSuccess.setText("✓ Un lien de réinitialisation a été envoyé à " + email + "\nVérifiez votre boîte mail.");
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(getFriendlyError(e.getMessage()));
                });
    }

    private void showLoading(boolean loading) {
        //btnSend.setEnabled(!loading);
        btnSend.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        textError.setVisibility(View.GONE);
        textSuccess.setVisibility(View.GONE);
    }

    //Les erreurs Firebase sont traduites en messages lisibles
    private String getFriendlyError(String error) {
        if (error == null) return "Une erreur est survenue";
        if (error.contains("no user record"))   return "Aucun compte trouvé avec cet email";
        if (error.contains("badly formatted"))  return "Format d'email invalide";
        if (error.contains("network"))          return "Pas de connexion internet";
        return "Erreur : " + error;
    }
}