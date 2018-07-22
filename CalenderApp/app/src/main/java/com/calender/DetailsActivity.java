package com.calender;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.calender.databinding.ActivityDetailsBinding;
import com.calender.model.Post;

public class DetailsActivity extends AppCompatActivity {

    private Post post;

    public static final void StartActivity(Context context, Post post) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(Constant.POST_DETAILS_ARG, post);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding activityDetailsBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_details);
        Intent intent = getIntent();
        if (intent != null) {
            post = intent.getParcelableExtra(Constant.POST_DETAILS_ARG);
            activityDetailsBinding.setPost(post);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addTaskMenu:
                AddTaskActivity.StartActivity(this);
                break;
            case R.id.shareMenu:
                ShareActivity.StartActivity(this, post);
                break;
            case R.id.chatMenu:
                UserActivity.StartActivity(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
