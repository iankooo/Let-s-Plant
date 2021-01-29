package com.e.letsplant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class FeedActivity extends AppCompatActivity {
    private ArrayList<PostModel> postsDataSource;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.app_bar_explore: {
                        return true;
                    }
                    case R.id.app_bar_plants: {
                        Intent intent = new Intent(FeedActivity.this, PlantsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.app_bar_alerts: {
                        return true;
                    }
                    case R.id.app_bar_profile: {
                        Intent intent = new Intent(FeedActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                }
                return true;
            }
        });
        postsDataSource = new ArrayList<>();
        postsDataSource.add(new PostModel("Name1"));
        postsDataSource.add(new PostModel("Name2"));
        postsDataSource.add(new PostModel("Name3"));
        postsDataSource.add(new PostModel("Name4"));
        postsDataSource.add(new PostModel("Name5"));
        postsDataSource.add(new PostModel("Name6"));
        postsDataSource.add(new PostModel("Name7"));
        postsDataSource.add(new PostModel("Name8"));
        postsDataSource.add(new PostModel("Name9"));
        postsDataSource.add(new PostModel("Name10"));
        postsDataSource.add(new PostModel("Name11"));
        postsDataSource.add(new PostModel("Name12"));
        postsDataSource.add(new PostModel("Name13"));
        postsDataSource.add(new PostModel("Name14"));
        postsDataSource.add(new PostModel("Name15"));
        postsDataSource.add(new PostModel("Name16"));
        postsDataSource.add(new PostModel("Name17"));
        postsDataSource.add(new PostModel("Name18"));
        postsDataSource.add(new PostModel("Name19"));
        postsDataSource.add(new PostModel("Name20"));
        postsDataSource.add(new PostModel("Name21"));
        postsDataSource.add(new PostModel("Name22"));
        postsDataSource.add(new PostModel("Name23"));
        postsDataSource.add(new PostModel("Name24"));
        postsDataSource.add(new PostModel("Name25"));
        postsDataSource.add(new PostModel("Name26"));
        postsDataSource.add(new PostModel("Name27"));
        postsDataSource.add(new PostModel("Name28"));
        postsDataSource.add(new PostModel("Name29"));
        postsDataSource.add(new PostModel("Name30"));
        postsDataSource.add(new PostModel("Name31"));
        postsDataSource.add(new PostModel("Name32"));

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.feedRecyclerView);
        postAdapter = new PostAdapter(this, postsDataSource);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postAdapter);
    }
}