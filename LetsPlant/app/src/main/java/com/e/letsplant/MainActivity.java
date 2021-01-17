package com.e.letsplant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView signTextView;
    private LinearLayout emailLinearLayout;
    private LinearLayout confirmPasswordLinearLayout;
    private LinearLayout forgetPasswordLinearLayout;
    private Button signButton;
    private LinearLayout orLinearLayout;
    private TextView backToTextView;
    private Boolean viewSignIn = true;
    private EditText passwordEditText;

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
        passwordEditText = findViewById(R.id.passwordEditText);
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
                emailLinearLayout.setVisibility(View.GONE);
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
            if (passwordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) view).setImageResource(R.drawable.ic_eye);
                //Show Password
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.ic_hide);
                //Hide Password
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}