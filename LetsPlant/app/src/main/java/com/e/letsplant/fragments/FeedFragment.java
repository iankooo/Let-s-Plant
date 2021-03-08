package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.e.letsplant.adapters.PostAdapter;
import com.e.letsplant.data.Plant;
import com.e.letsplant.data.PostModel;
import com.e.letsplant.R;
import com.e.letsplant.data.User;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends MainFragment {

    private ArrayList<PostModel> postsDataSource;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FeedFragment() {

    }

    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = rootView.findViewById(R.id.feedRecyclerView);
        postAdapter = new PostAdapter(getActivity(), postsDataSource);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postAdapter);

        return rootView;
    }
}