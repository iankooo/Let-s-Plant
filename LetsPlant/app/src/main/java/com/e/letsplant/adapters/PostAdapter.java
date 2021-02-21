package com.e.letsplant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.e.letsplant.data.PostModel;
import com.e.letsplant.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<PostModel> postsDataSource;
    private final LayoutInflater layoutInflater;

    public PostAdapter(Context context, ArrayList<PostModel> postsDataSource) {
        this.postsDataSource = postsDataSource;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String ownerName = postsDataSource.get(position).getOwnerName();
        ((PostViewHolder)holder).update(ownerName);

//        ((PostViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(),SecondActivity.class);
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return postsDataSource.size();
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView postTextView;
        public CardView cardView;

        public PostViewHolder(View itemView) {
            super(itemView);

            this.postTextView = itemView.findViewById(R.id.postTextView);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void update(String ownerName) {
            postTextView.setText(ownerName);
        }
    }
}
