package com.calender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.calender.adapter.PostAdapter;
import com.calender.listener.RecyclerItemClickListener;
import com.calender.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private static final String TAG = "Calender";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<Post> postLists;
    private PostAdapter postAdapter;

    public static void StartActivity(Context context) {
        context.startActivity(new Intent(context, TaskListActivity.class));
    }

    public static void StartActivity(Context context, ArrayList<Post> postList) {
        Intent intent = new Intent(context, TaskListActivity.class);
        intent.putParcelableArrayListExtra(Constant.POST_LIST_ARG, postList);
        context.startActivity(intent);
    }

    private void fetchDataFromDataBase() {
        Query query = mFirebaseDatabase.orderByChild(Constant.ID).equalTo(Utils.GetInstance().getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, " read user " + dataSnapshot.getChildren());
                List<Post> postList = new ArrayList<Post>();
                for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                    Post post = adSnapshot.getValue(Post.class);
                    if (post != null && post.getId().equalsIgnoreCase(Utils.GetInstance().getUserID()))
                        postList.add(post);
                    Log.d(TAG, "List id: " + post.getId() + " ,name: " + post.getTitle() + " ,Email: " + post.getDescription());
                }
                Utils.GetInstance().hideProgressDialog();
                postLists.clear();
                postLists.addAll(postList);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        if (postLists == null)
            postLists = new ArrayList<>();
        if (getIntent() != null) {
            List<Post> postList = getIntent().getParcelableArrayListExtra(Constant.POST_LIST_ARG);
            postLists.clear();
            if (postList != null && !postList.isEmpty())
                postLists.addAll(postList);
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.POST_TABLE);
        RecyclerView recyclerView = findViewById(R.id.postRecyclerView);
        postAdapter = new PostAdapter(this, postLists);
        recyclerView.setAdapter(postAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DetailsActivity.StartActivity(TaskListActivity.this, postLists.get(position));
            }
        }));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (postLists.isEmpty()) {
            Utils.GetInstance().showProgressDialog(this);
            fetchDataFromDataBase();
        } else {
            postAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addTaskMenu:
                AddTaskActivity.StartActivity(this);
                break;
            case R.id.chatMenu:
                //Utils.GetInstance().showToast(this, "Under Process");
                UserActivity.StartActivity(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
