package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e.letsplant.adapters.PostAdapter;
import com.e.letsplant.data.PostModel;
import com.e.letsplant.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private ArrayList<PostModel> postsDataSource;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_feed, container, false);

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