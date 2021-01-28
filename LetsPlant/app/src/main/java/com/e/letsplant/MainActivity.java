package com.e.letsplant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView signTextView;
    private LinearLayout emailLinearLayout;
    private LinearLayout confirmPasswordLinearLayout;
    private LinearLayout forgetPasswordLinearLayout;
    private Button signButton;
    private LinearLayout orLinearLayout;
    private TextView backToTextView;
    private Boolean viewSignIn = true;
    private EditText username, email, password, confirmpassword;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        initialize();
        checkWhatToShowOnLayout();
    }

    private void initialize() {
        signTextView = findViewById(R.id.signTextView);
        emailLinearLayout = findViewById(R.id.emailLinearLayout);
        confirmPasswordLinearLayout = findViewById(R.id.confirmPasswordLinearLayout);
        forgetPasswordLinearLayout = findViewById(R.id.forgetPasswordLinearLayout);
        signButton = findViewById(R.id.signButton);
        orLinearLayout = findViewById(R.id.orLinearLayout);
        backToTextView = findViewById(R.id.backToTextView);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        confirmpassword = findViewById(R.id.confirmPassword);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar_cyclic);

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), FeedActivity.class));
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkWhatToShowOnLayout() {
        signTextView.setOnClickListener(view -> {
            if (this.viewSignIn) {
                emailLinearLayout.setVisibility(View.VISIBLE);
                confirmPasswordLinearLayout.setVisibility(View.VISIBLE);
                forgetPasswordLinearLayout.setVisibility(View.GONE);
                signButton.setText("Sign Up");
                orLinearLayout.setVisibility(View.GONE);
                backToTextView.setVisibility(View.VISIBLE);
                signTextView.setText("Sign In");
                this.viewSignIn = false;
            } else {
                //emailLinearLayout.setVisibility(View.GONE);
                emailLinearLayout.setVisibility(View.VISIBLE);
                confirmPasswordLinearLayout.setVisibility(View.GONE);
                forgetPasswordLinearLayout.setVisibility(View.VISIBLE);
                signButton.setText("Sign In");
                orLinearLayout.setVisibility(View.VISIBLE);
                backToTextView.setVisibility(View.GONE);
                signTextView.setText("Sign Up");
                this.viewSignIn = true;
            }
        });
    }

    public void showHidePass(View view) {
        if (view.getId() == R.id.hideImageView) {
            if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) view).setImageResource(R.drawable.ic_eye);
                //Show Password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.ic_hide);
                //Hide Password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void onSignButtonPress(View v) {
        String textSignButton = signButton.getText().toString();

        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password is required!");
            return;
        }
        if (password.length() < 6) {
            this.password.setError("Password must be >= 6  characters");
            return;
        }

        if (textSignButton.equals("Sign In")) {
            progressBar.setVisibility(View.VISIBLE);
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FeedActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "User created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FeedActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}