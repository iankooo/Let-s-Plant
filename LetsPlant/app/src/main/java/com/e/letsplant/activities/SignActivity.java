package com.e.letsplant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.e.letsplant.data.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
    String userUid;

    private UserViewModel userViewModel;
    FirebaseAuth firebaseAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        firebaseAuth = FirebaseAuth.getInstance();


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

        if (firebaseAuth.getCurrentUser() != null) {
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
        String textSignButton = this.signButton.getText().toString();
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String username = this.username.getText().toString().trim();

        if (isFormValid()) {
            if (textSignButton.equals("Sign In")) {
                logIn(email, password);
            } else {
                signUp(email, password, username);
            }
        }
    }

    private void logIn(String email, String password) {
        if (firebaseAuth.getCurrentUser() != null)
            userUid = (firebaseAuth.getCurrentUser()).getUid();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent intent = new Intent(SignActivity.this, SecondActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    SignActivity.this.errorAtSign(task);
                }
            }
        });
    }

    private void signUp(String email, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userUid = firebaseAuth.getCurrentUser().getUid();
                User user = new User(userUid, email, "", 0, 0, "",
                        "https://firebasestorage.googleapis.com/v0/b/let-s-plant-f845c.appspot.com/o/placeholder_profileImage.png?alt=media&token=ad5ae128-f579-40e8-ad90-0fccaeda16c7",
                        username);

                userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
                userViewModel.getUserMutableLiveData().observe(this, item -> {
                    userViewModel.setUserMutableLiveData(user);
                });

                databaseReference.child(DB_USERS).child(userUid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

            } else {
                errorAtSign(task);
            }
        });
    }

    private boolean isFormValid() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String username = this.username.getText().toString().trim();
        String confirmPassword = this.confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password is required!");
            return false;
        }
        if (password.length() < 6) {
            this.password.setError("Password must be >= 6  characters");
            return false;
        }
        if (TextUtils.isEmpty(username) && usernameRelativeLayout.getVisibility() == View.VISIBLE) {
            this.username.setError("Username is required!");
            return false;
        }
        if (username.length() < 4 && usernameRelativeLayout.getVisibility() == View.VISIBLE) {
            this.username.setError("Username must be >= 4  characters");
            return false;
        }
        if (!confirmPassword.equals(password) && confirmPasswordRelativeLayout.getVisibility() == View.VISIBLE) {
            this.confirmPassword.setError("Password does not match!");
            return false;
        }
        progressBar.setVisibility(View.VISIBLE);
        return true;
    }

    private void errorAtSign(Task<AuthResult> task) {
        Toast.makeText(SignActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
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
            firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SignActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }).setNegativeButton("No", (dialog, which) -> {
        });
        passwordResetDialog.create().show();
    }
}