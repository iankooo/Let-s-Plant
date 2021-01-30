package com.e.letsplant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if (savedInstanceState == null) {
            // Instance of feed fragment
            FeedFragment feedFragment = new FeedFragment();

            // Add Fragment to FrameLayout (fragment_container), using FragmentManager
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();// begin  FragmentTransaction
            ft.add(R.id.fragment_container, feedFragment);                                // add    Fragment
            ft.commit();                                                            // commit FragmentTransaction
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.app_bar_explore: {
                        ExploreFragment exploreFragment = new ExploreFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, exploreFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }
                    case R.id.app_bar_plants: {
                        PlantsFragment plantsFragment = new PlantsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, plantsFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }
                    case R.id.app_bar_alerts: {
                        AlertsFragment alertsFragment = new AlertsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, alertsFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }
                    case R.id.app_bar_profile: {
                       ProfileFragment profileFragment = new ProfileFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, profileFragment) // replace fragment_container
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }
                }
                return true;
            }
        });
    }
    public void onClickFeedButton(View view){
        FeedFragment feedFragment = new FeedFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, feedFragment) // replace fragment_container
                .addToBackStack(null)
                .commit();
        return;
    }
}