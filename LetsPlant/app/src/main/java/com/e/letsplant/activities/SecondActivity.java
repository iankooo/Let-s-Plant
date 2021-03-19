package com.e.letsplant.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
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

    final Fragment plantsFragment = new PlantsFragment();
    final Fragment friendsFragment = new FriendsFragment();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment exploreFragment = new ExploreFragment();
    final Fragment feedFragment = new FeedFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = feedFragment;

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

        fAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragment, "5").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, exploreFragment,"1").hide(exploreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, plantsFragment, "2").hide(plantsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, friendsFragment, "4").hide(friendsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, feedFragment, "3").commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.app_bar_explore:
                        fragmentManager.beginTransaction().hide(active).show(exploreFragment).commit();
                        active = exploreFragment;
                        return true;

                    case R.id.app_bar_plants:
                        fragmentManager.beginTransaction().hide(active).show(plantsFragment).commit();
                        active = plantsFragment;
                        return true;

                    case R.id.app_bar_alerts:
                        fragmentManager.beginTransaction().hide(active).show(friendsFragment).commit();
                        active = friendsFragment;
                        return true;

                    case R.id.app_bar_profile:
                        fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                        active = profileFragment;
                        return true;
                }
                return false;
            };

    public void onClickFeedButton(View view) {
        fragmentManager.beginTransaction().hide(active).show(feedFragment).commit();
        active = feedFragment;
    }

    @Override
    public void onPlantItemSelected(Plant plant) {
        Fragment plantDetailedFragment = new PlantDetailedFragment();

        Bundle args = new Bundle();
        args.putString("title", plant.getTitle());
        args.putString("image", plant.getImage());
        plantDetailedFragment.setArguments(args);

        fragmentManager.beginTransaction().add(R.id.fragment_container, plantDetailedFragment, "6").hide(active).show(plantDetailedFragment).commit();
        active = plantDetailedFragment;
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
                intent.putExtra("open", true);
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