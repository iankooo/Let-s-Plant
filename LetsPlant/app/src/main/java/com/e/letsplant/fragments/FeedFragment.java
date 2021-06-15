package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e.letsplant.adapters.PostAdapter;
import com.e.letsplant.data.Post;
import com.e.letsplant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeedFragment extends MainFragment {
    private static Fragment instance = null;
    private TextView noPostsMessageTextView;

    private List<Post> posts;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    private List<String> friendsList;

    String userUid;

    public FeedFragment() {
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new FeedFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Let's Plant");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        userUid = firebaseAuth.getUid();
        posts = new ArrayList<>();

        noPostsMessageTextView = rootView.findViewById(R.id.noPostsMessageTextView);
        recyclerView = rootView.findViewById(R.id.feedRecyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        postAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(postAdapter);

        checkFriends();

        return rootView;
    }

    private void checkFriends() {
        friendsList = new ArrayList<>();

        databaseReference.child(DB_FOLLOW).child(userUid).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    friendsList.add(dataSnapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts() {
        databaseReference.child(DB_POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : friendsList)
                        if (post.getPublisher().equals(id)) {
                            posts.add(post);
                        }
                }
                postAdapter.notifyDataSetChanged();

                if (posts.size() == 0) {
                    noPostsMessageTextView.setVisibility(View.VISIBLE);
                } else {
                    noPostsMessageTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}