package com.e.letsplant.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.e.letsplant.data.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignActivity extends MainActivity {

    private TextView signTextView;
    private RelativeLayout usernameRelativeLayout;
    private RelativeLayout confirmPasswordRelativeLayout;
    private LinearLayout forgetPasswordLinearLayout;
    private Button signButton;
    private LinearLayout orLinearLayout;
    private TextView backToTextView;
    private Boolean viewSignIn = true;
    private EditText username, email, password, confirmPassword;
    private ProgressBar progressBar;

    private String userID;
    private final String USER_REALTIME_DATABASE = "All_Users_Information_Realtime_Database";
    private UserViewModel userViewModel;

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
        usernameRelativeLayout = findViewById(R.id.usernameRelativeLayout);
        confirmPasswordRelativeLayout = findViewById(R.id.confirmPasswordRelativeLayout);
        forgetPasswordLinearLayout = findViewById(R.id.forgetPasswordLinearLayout);
        signButton = findViewById(R.id.signButton);
        orLinearLayout = findViewById(R.id.orLinearLayout);
        backToTextView = findViewById(R.id.backToTextView);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        confirmPassword = findViewById(R.id.confirmPassword);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar_cyclic);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), SecondActivity.class));
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkWhatToShowOnLayout() {
        signTextView.setOnClickListener(view -> {
            if (this.viewSignIn) {
                usernameRelativeLayout.setVisibility(View.VISIBLE);
                confirmPasswordRelativeLayout.setVisibility(View.VISIBLE);
                forgetPasswordLinearLayout.setVisibility(View.GONE);
                signButton.setText("Sign Up");
                orLinearLayout.setVisibility(View.GONE);
                backToTextView.setVisibility(View.VISIBLE);
                signTextView.setText("Sign In");
                this.viewSignIn = false;
            } else {
                usernameRelativeLayout.setVisibility(View.GONE);
                confirmPasswordRelativeLayout.setVisibility(View.GONE);
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
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            String username = this.username.getText().toString().trim();
            String confirmPassword = this.confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                this.username.setError("Username is required!");
                return;
            }
            if (username.length() < 4) {
                this.username.setError("Username must be >= 4  characters");
                return;
            }
            if (!confirmPassword.equals(password)) {
                this.confirmPassword.setError("Password does not match!");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    User user = new User(userID, email, "", 0 , 0,  "", "", username);

                    userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
                    userViewModel.getUserMutableLiveData().observe(this, item -> {
                        userViewModel.setUserMutableLiveData(user);
                    });

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(USER_REALTIME_DATABASE).child(userID).setValue(user);

                    startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void onClickForgotPassword(View v) {
        EditText resetMail = new EditText(v.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setTitle("Reset password?");
        passwordResetDialog.setMessage("Enter your email to receive reset link");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
            String mail = resetMail.getText().toString().trim();
            if (TextUtils.isEmpty(mail)) {
                resetMail.setError("Email is required!");
                return;
            }
            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(SignActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(SignActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        passwordResetDialog.setNegativeButton("No", (dialog, which) -> {});
        passwordResetDialog.create().show();
    }
}