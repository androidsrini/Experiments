package com.calender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.calender.model.User;

public class ChatActivity extends AppCompatActivity {

    private User user;

    public static void StartActivity(Context context, User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.USER_DETAIL, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if (intent != null) {
            user = intent.getParcelableExtra(Constant.USER_DETAIL);
        }
        RecyclerView chatListView = findViewById(R.id.chatListView);

    }
}
