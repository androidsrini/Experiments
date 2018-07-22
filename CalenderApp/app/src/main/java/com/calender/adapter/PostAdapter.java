package com.calender.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calender.R;
import com.calender.model.Post;

import java.util.List;

/**
 * Created by intel on 22-07-2018.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> postList;
    Context context;
    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView postUserNameTextView, postTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            postUserNameTextView = (TextView) itemView.findViewById(R.id.postUserNameTextView);
            postTitleTextView = (TextView) itemView.findViewById(R.id.postTitleTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_detail_screen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postUserNameTextView.setText(post.getTitle());
        holder.postTitleTextView.setText(post.getDescription());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
