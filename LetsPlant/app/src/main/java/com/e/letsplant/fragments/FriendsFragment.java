package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
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
import android.widget.Toast;

import com.e.letsplant.R;
import com.e.letsplant.adapters.UserAdapter;
import com.e.letsplant.data.User;
import com.e.letsplant.listeners.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends MainFragment {

    private final List<User> usersList = new ArrayList<>();
    private final List<User> friendsList = new ArrayList<>();

    private TextView noFriendsMessageTextView;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    public FriendsFragment() {
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);


        noFriendsMessageTextView = view.findViewById(R.id.noFriendsMessageTextView);
        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS_INFORMATION_REALTIME_DATABASE + "/");

        getAllFriends();

        return view;
    }

    private void getAllFriends() {
        databaseReference.child(firebaseUser.getUid()).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    friendsList.add(user);

                    userAdapter = new UserAdapter(getActivity(), friendsList);
                    recyclerView.setAdapter(userAdapter);
                }
                if (friendsList.size() == 0){
                    noFriendsMessageTextView.setVisibility(View.VISIBLE);
                }
                else {
                    noFriendsMessageTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    getAllFriends();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim()) && !s.equals("")) {
                    searchUsers(s);
                } else {
                    getAllFriends();
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            usersList.add(user);
                        }
                    }
                    userAdapter = new UserAdapter(getActivity(), usersList);
                    userAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}