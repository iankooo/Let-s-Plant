package com.e.letsplant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView signTextView;
    private RelativeLayout usernameRelativeLayout;
    private RelativeLayout emailRelativeLayout;
    private RelativeLayout confirmPasswordRelativeLayout;
    private LinearLayout forgetPasswordLinearLayout;
    private Button signButton;
    private LinearLayout orLinearLayout;
    private TextView backToTextView;
    private Boolean viewSignIn = true;
    private EditText username, email, password, confirmpassword;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    String userID;

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
        emailRelativeLayout = findViewById(R.id.emailRelativeLayout);
        confirmPasswordRelativeLayout = findViewById(R.id.confirmPasswordRelativeLayout);
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
        fStore = FirebaseFirestore.getInstance();

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
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            String username = this.username.getText().toString().trim();
            String confirmPassword = this.confirmpassword.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                this.username.setError("Username is required!");
                return;
            }
            if (username.length() < 4) {
                this.username.setError("Username must be >= 4  characters");
                return;
            }
            if (!confirmPassword.equals(password)) {
                this.confirmpassword.setError("Password does not match!");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("email", email);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
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

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString().trim();
                if (TextUtils.isEmpty(mail)) {
                    resetMail.setError("Email is required!");
                    return;
                }
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        passwordResetDialog.create().show();
    }
}