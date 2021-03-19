package com.e.letsplant.fragments;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.e.letsplant.adapters.PostAdapter;
import com.e.letsplant.data.Post;
import com.e.letsplant.R;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends MainFragment {

    private ArrayList<Post> postsDataSource;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    public FeedFragment() {

    }

    public static FeedFragment newInstance(String param1, String param2) {
        return new FeedFragment();
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

        postsDataSource = new ArrayList<>();
        postsDataSource.add(new Post("Name1"));
        postsDataSource.add(new Post("Name2"));
        postsDataSource.add(new Post("Name3"));
        postsDataSource.add(new Post("Name4"));
        postsDataSource.add(new Post("Name5"));
        postsDataSource.add(new Post("Name6"));
        postsDataSource.add(new Post("Name7"));
        postsDataSource.add(new Post("Name8"));
        postsDataSource.add(new Post("Name9"));
        postsDataSource.add(new Post("Name10"));
        postsDataSource.add(new Post("Name11"));
        postsDataSource.add(new Post("Name12"));
        postsDataSource.add(new Post("Name13"));
        postsDataSource.add(new Post("Name14"));
        postsDataSource.add(new Post("Name15"));
        postsDataSource.add(new Post("Name16"));
        postsDataSource.add(new Post("Name17"));
        postsDataSource.add(new Post("Name18"));
        postsDataSource.add(new Post("Name19"));
        postsDataSource.add(new Post("Name20"));
        postsDataSource.add(new Post("Name21"));
        postsDataSource.add(new Post("Name22"));
        postsDataSource.add(new Post("Name23"));
        postsDataSource.add(new Post("Name24"));
        postsDataSource.add(new Post("Name25"));
        postsDataSource.add(new Post("Name26"));
        postsDataSource.add(new Post("Name27"));
        postsDataSource.add(new Post("Name28"));
        postsDataSource.add(new Post("Name29"));
        postsDataSource.add(new Post("Name30"));
        postsDataSource.add(new Post("Name31"));
        postsDataSource.add(new Post("Name32"));
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = rootView.findViewById(R.id.feedRecyclerView);
        postAdapter = new PostAdapter(getActivity(), postsDataSource);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postAdapter);

        return rootView;
    }
}