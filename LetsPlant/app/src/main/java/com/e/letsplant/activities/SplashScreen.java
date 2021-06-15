package com.e.letsplant.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends MainActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private final FirebaseAuth.AuthStateListener fAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            Intent intent;
            String userUid = firebaseAuth.getUid();
            if (userUid != null) {
                intent = new Intent(getApplicationContext(), SecondActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), SignActivity.class);
            }
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fAuthListener != null) {
            firebaseAuth.removeAuthStateListener(fAuthListener);
        }
    }
}
