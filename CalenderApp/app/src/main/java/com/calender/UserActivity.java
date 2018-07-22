package com.calender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.calender.adapter.UserAdapter;
import com.calender.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<User> userList;
    private UserAdapter userAdapter;

    public static void StartActivity(Context context) {
        context.startActivity(new Intent(context, UserActivity.class));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, false);
        RecyclerView userListRecyclerView = findViewById(R.id.userListRecyclerView);
        userListRecyclerView.setAdapter(userAdapter);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.USER_TABLE);
        userListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        fetchUserListFromServerRequest();
    }
}
