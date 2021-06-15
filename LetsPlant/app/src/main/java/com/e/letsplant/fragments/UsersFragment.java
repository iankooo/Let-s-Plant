package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.e.letsplant.App;
import com.e.letsplant.R;
import com.e.letsplant.adapters.UserAdapter;
import com.e.letsplant.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersFragment extends MainFragment {
    private static Fragment instance = null;
    private List<User> mUsers;

    private TextView noUsersMessageTextView;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private String userUid;
    private String userId;

    FirebaseAuth auth = App.getFirebaseAuthReference();
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                userId = firebaseUser.getUid();
                String userEmail = firebaseUser.getEmail();
            }
        }
    };

    public UsersFragment() {
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new UsersFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (firebaseAuth.getCurrentUser() != null)
            userUid = firebaseAuth.getCurrentUser().getUid();
        rootView = inflater.inflate(R.layout.fragment_users, container, false);

        noUsersMessageTextView = rootView.findViewById(R.id.noFriendsMessageTextView);

        recyclerView = rootView.findViewById(R.id.friendsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);

        getAllUsers();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_feed_bar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim()) && !s.equals("")) {
                    searchUsers(s);
                } else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim()) && !s.equals("")) {
                    searchUsers(s);
                } else {
                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchUsers(String query) {
        databaseReference.child(DB_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getId().equals(userUid)) {
                        if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter.updateDataSet(mUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllUsers() {
        databaseReference.child(DB_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (firebaseAuth.getCurrentUser() != null) {
                        if (!user.getId().equals(userUid)) {
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter.updateDataSet(mUsers);

                if (mUsers.size() == 0) {
                    noUsersMessageTextView.setVisibility(View.VISIBLE);
                } else {
                    noUsersMessageTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}