package com.medianet.cinetrack.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.medianet.cinetrack.R;
import com.medianet.cinetrack.ui.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout layoutName, layoutEmail, layoutPassword, layoutConfirm;
    private TextInputEditText inputName, inputEmail, inputPassword, inputConfirm;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView textError;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        layoutName     = findViewById(R.id.layoutName);
        layoutEmail    = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirm  = findViewById(R.id.layoutConfirm);
        inputName      = findViewById(R.id.inputName);
        inputEmail     = findViewById(R.id.inputEmail);
        inputPassword  = findViewById(R.id.inputPassword);
        inputConfirm   = findViewById(R.id.inputConfirm);
        btnRegister    = findViewById(R.id.btnRegister);
        progressBar    = findViewById(R.id.progressBar);
        textError      = findViewById(R.id.textError);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnRegister.setOnClickListener(v -> {
            String name     = inputName.getText().toString().trim();
            String email    = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirm  = inputConfirm.getText().toString().trim();
            if (validate(name, email, password, confirm)) viewModel.register(email, password, name);
        });

        findViewById(R.id.textGoToLogin).setOnClickListener(v -> finish());

        viewModel.getAuthState().observe(this, state -> {
            switch (state.status) {
                case LOADING:
                    btnRegister.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    textError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    startActivity(new Intent(this, MainActivity.class));
                    finishAffinity();
                    break;
                case ERROR:
                    btnRegister.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(state.message);
                    break;
            }
        });
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    private boolean validate(String name, String email, String password, String confirm) {
        boolean ok = true;

        if (name.isEmpty()) {
            layoutName.setError("Nom requis");
            ok = false;
        } else {
            layoutName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Email invalide");
            ok = false;
        } else {
            layoutEmail.setError(null);
        }

        if (password.length() < 6) {
            layoutPassword.setError("6 caractères minimum");
            ok = false;
        } else {
            layoutPassword.setError(null);
        }

        if (!password.equals(confirm)) {
            layoutConfirm.setError("Mots de passe différents");
            ok = false;
        } else {
            layoutConfirm.setError(null);
        }

        return ok;
    }
}