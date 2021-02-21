package com.e.letsplant.activities;

import androidx.fragment.app.FragmentTransaction;

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
import com.e.letsplant.fragments.AlertsFragment;
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

    private boolean isAnimationFromProfileToAlerts = false;
    private boolean isAnimationFromExploreToPlants = false;
    private FloatingActionButton floatingActionButton;
    LinearLayout profileSettingsLinearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    FirebaseAuth fAuth;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_second);
        this.floatingActionButton = findViewById(R.id.feedButton);
        this.profileSettingsLinearLayout = findViewById(R.id.profileSettingsLinearLayout);
        this.bottomSheetBehavior = BottomSheetBehavior.from(profileSettingsLinearLayout);

        if (savedInstanceState == null) {
            // Instance of feed fragment
            FeedFragment feedFragment = new FeedFragment();

            // Add Fragment to FrameLayout (fragment_container), using FragmentManager
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();// begin  FragmentTransaction
            ft.add(R.id.fragment_container, feedFragment);                                // add    Fragment
            ft.commit();                                                            // commit FragmentTransaction
        }

        fAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.app_bar_explore: {
                    isAnimationFromExploreToPlants = true;
                    ExploreFragment exploreFragment = new ExploreFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, exploreFragment) // replace fragment_container
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                case R.id.app_bar_plants: {
                    if (!isAnimationFromExploreToPlants) {
                        PlantsFragment plantsFragment = new PlantsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, plantsFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                    } else {
                        isAnimationFromExploreToPlants = false;
                        PlantsFragment plantsFragment = new PlantsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.fragment_container, plantsFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
                }
                case R.id.app_bar_alerts: {
                    if (!isAnimationFromProfileToAlerts) {
                        AlertsFragment alertsFragment = new AlertsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.fragment_container, alertsFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                    } else {
                        isAnimationFromProfileToAlerts = false;
                        AlertsFragment alertsFragment = new AlertsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, alertsFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
                }
                case R.id.app_bar_profile: {
                    isAnimationFromProfileToAlerts = true;
                    ProfileFragment profileFragment = new ProfileFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.fragment_container, profileFragment) // replace fragment_container
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
            }
            return true;
        });
    }

    public void onClickFeedButton(View view) {
        isAnimationFromProfileToAlerts = false;
        isAnimationFromExploreToPlants = false;
        FeedFragment feedFragment = new FeedFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_up, 0, 0, R.animator.slide_down)
                .replace(R.id.fragment_container, feedFragment) // replace fragment_container
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPlantItemSelected(Plant plant) {
        PlantDetailedFragment plantDetailedFragment = new PlantDetailedFragment();

        Bundle args = new Bundle();
        args.putString("title", plant.getTitle());
        args.putString("image", plant.getImage());
        plantDetailedFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, plantDetailedFragment)
                .commit();
    }

    @Override
    public void openActivityBottomSheet() {

        floatingActionButton.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        profileSettingsLinearLayout.setVisibility(View.VISIBLE);

        profileSettingsLinearLayout.findViewById(R.id.minusProfileSettingsImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    profileSettingsLinearLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        profileSettingsLinearLayout.findViewById(R.id.logoutLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    fAuth.signOut();
                    startActivity(new Intent(SecondActivity.this, SignActivity.class));
            }
        });

        profileSettingsLinearLayout.findViewById(R.id.editProfileLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivityBottomSheet();
                Intent intent = new Intent("openEditProfileSettings");
                intent.putExtra("open",true);
                sendBroadcast(intent);
            }
        });
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
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}