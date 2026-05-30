package com.medianet.cinetrack.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.medianet.cinetrack.R;
import com.medianet.cinetrack.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText inputEmail, inputPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView textError;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layoutEmail    = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        inputEmail     = findViewById(R.id.inputEmail);
        inputPassword  = findViewById(R.id.inputPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        progressBar    = findViewById(R.id.progressBar);
        textError      = findViewById(R.id.textError);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnLogin.setOnClickListener(v -> {
            String email    = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            if (validate(email, password)) viewModel.login(email, password);
        });

        findViewById(R.id.textGoToRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        findViewById(R.id.textForgot).setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        viewModel.getAuthState().observe(this, state -> {
            switch (state.status) {
                case LOADING:
                    btnLogin.setEnabled(false);
                    btnLogin.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    textError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    break;
                case ERROR:
                    btnLogin.setEnabled(true);
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(state.message);
                    break;
            }
        });
    }

    private boolean validate(String email, String password) {
        boolean ok = true;

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

        return ok;
    }
}