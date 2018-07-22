package com.calender;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.calender.adapter.UserAdapter;
import com.calender.listener.RecyclerItemClickListener;
import com.calender.model.Post;
import com.calender.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<User> userList;
    private UserAdapter userAdapter;
    private RecyclerView userListRecyclerView;
    private Post post;

    public static void StartActivity(Context context, Post post) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(Constant.POST_DETAILS_ARG, post);
        context.startActivity(intent);
    }

    private void fetchUserListFromServerRequest() {
        Utils.GetInstance().showProgressDialog(this);
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.getId().equalsIgnoreCase(Utils.GetInstance().getUserID()))
                        userList.add(user);
                }
                Utils.GetInstance().hideProgressDialog();
                userAdapter.setNotifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postTaskRequest(List<User> userList) {
        for (User user: userList) {
            String postId = mFirebaseDatabase.push().getKey();
            post.setId(user.getId());
            mFirebaseInstance.getReference(Constant.POST_TABLE).child(postId).setValue(post);
        }
        Utils.GetInstance().showToast(this, "Shared successfully");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Intent intent = getIntent();
        if (intent != null) {
            post = intent.getParcelableExtra(Constant.POST_DETAILS_ARG);
        }
        userListRecyclerView = findViewById(R.id.userListRecyclerView);
        Button shareDoneButton = findViewById(R.id.shareDoneButton);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, true);
        userListRecyclerView.setAdapter(userAdapter);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.USER_TABLE);
        userListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userListRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                userAdapter.updateSelectedItem(position);
            }
        }));
        shareDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> userList = userAdapter.getSelectedUserList();
                if (!userList.isEmpty()) {
                    postTaskRequest(userList);
                } else {
                    Utils.GetInstance().showToast(ShareActivity.this, "Select user");
                }
            }
        });
        fetchUserListFromServerRequest();
    }
}
