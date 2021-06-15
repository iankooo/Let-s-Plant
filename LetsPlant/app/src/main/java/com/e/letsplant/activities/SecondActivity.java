package com.e.letsplant.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.e.letsplant.R;
import com.e.letsplant.data.Plant;
import com.e.letsplant.fragments.UsersFragment;
import com.e.letsplant.fragments.ExploreFragment;
import com.e.letsplant.fragments.FeedFragment;
import com.e.letsplant.fragments.PlantDetailedFragment;
import com.e.letsplant.fragments.PlantsFragment;
import com.e.letsplant.fragments.ProfileFragment;
import com.e.letsplant.interfaces.ProfileSettingsEventListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SecondActivity extends MainActivity implements PlantsFragment.OnItemSelectedListener, ProfileSettingsEventListener {

    private FloatingActionButton floatingActionButton;
    private LinearLayout profileSettingsLinearLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    private Fragment fragment = null;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        setContentView(R.layout.activity_second);

        userUid = firebaseAuth.getUid();

        this.floatingActionButton = findViewById(R.id.feedButton);
        this.profileSettingsLinearLayout = findViewById(R.id.profileSettingsLinearLayout);
        this.bottomSheetBehavior = BottomSheetBehavior.from(profileSettingsLinearLayout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragment = FeedFragment.getInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, "").commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.app_bar_explore:
                fragment = ExploreFragment.getInstance();
                break;

            case R.id.app_bar_plants:
                fragment = PlantsFragment.getInstance();
                break;

            case R.id.app_bar_alerts:
                fragment = UsersFragment.getInstance();
                break;

            case R.id.app_bar_profile:
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("profileId", userUid);
                editor.apply();
                fragment = ProfileFragment.getInstance();
                break;
        }
        if (fragment != null)
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, "").commit();
        return true;
    };

    public void onClickFeedButton(View view) {
        fragment = FeedFragment.getInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, "").commit();
    }

    @Override
    public void onPlantItemSelected(Plant plant) {
        fragment = PlantDetailedFragment.getInstance();

        Bundle args = new Bundle();
        args.putString("title", plant.getTitle());
        args.putString("image", plant.getImage());
        fragment.setArguments(args);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, "").commit();
    }

    @Override
    public void openActivityBottomSheet() {
        floatingActionButton.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        profileSettingsLinearLayout.setVisibility(View.VISIBLE);

        profileSettingsLinearLayout.findViewById(R.id.minusProfileSettingsImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivityBottomSheet();
            }
        });

        profileSettingsLinearLayout.findViewById(R.id.logoutLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        profileSettingsLinearLayout.findViewById(R.id.editProfileLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivityBottomSheet();
                Intent intent = new Intent("openEditProfileSettings");
                intent.putExtra("open", true);
                sendBroadcast(intent);
            }
        });
    }

    public void logout() {
        SharedPreferences preferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        firebaseAuth.signOut();
        startActivity(new Intent(SecondActivity.this, SignActivity.class));
        finish();
    }

    @Override
    public void closeActivityBottomSheet() {
        profileSettingsLinearLayout.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                profileSettingsLinearLayout.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    profileSettingsLinearLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}