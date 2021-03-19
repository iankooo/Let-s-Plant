package com.e.letsplant.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.e.letsplant.R;
import com.e.letsplant.data.Plant;
import com.e.letsplant.fragments.FriendsFragment;
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

    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        setContentView(R.layout.activity_second);

        this.floatingActionButton = findViewById(R.id.feedButton);
        this.profileSettingsLinearLayout = findViewById(R.id.profileSettingsLinearLayout);
        this.bottomSheetBehavior = BottomSheetBehavior.from(profileSettingsLinearLayout);

        fAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment feedFragment = new FeedFragment();
        activeFragment = feedFragment;
        fragmentManager.beginTransaction().add(R.id.fragment_container, feedFragment, "").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.app_bar_explore:
                        if (!(activeFragment instanceof ExploreFragment)) {
                            Fragment exploreFragment = new ExploreFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, exploreFragment, "").commit();
                            activeFragment = exploreFragment;
                        }
                        return true;

                    case R.id.app_bar_plants:
                        if (!(activeFragment instanceof PlantsFragment)) {
                            Fragment plantsFragment = new PlantsFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, plantsFragment, "").commit();
                            activeFragment = plantsFragment;
                        }
                        return true;

                    case R.id.app_bar_alerts:
                        if (!(activeFragment instanceof FriendsFragment)) {
                            Fragment friendsFragment = new FriendsFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, friendsFragment, "").commit();
                            activeFragment = friendsFragment;
                        }
                        return true;

                    case R.id.app_bar_profile:
                        if (!(activeFragment instanceof ProfileFragment)) {
                            Fragment profileFragment = new ProfileFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, profileFragment, "").commit();
                            activeFragment = profileFragment;
                        }
                        return true;
                }
                return false;
            };

    public void onClickFeedButton(View view) {
        if (!(activeFragment instanceof FeedFragment)) {
            Fragment feedFragment = new FeedFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, feedFragment, "").commit();
            activeFragment = feedFragment;
        }
    }

    @Override
    public void onPlantItemSelected(Plant plant) {
        Fragment plantDetailedFragment = new PlantDetailedFragment();

        Bundle args = new Bundle();
        args.putString("title", plant.getTitle());
        args.putString("image", plant.getImage());
        plantDetailedFragment.setArguments(args);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, plantDetailedFragment, "").commit();
        activeFragment = plantDetailedFragment;
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
        fAuth.signOut();
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