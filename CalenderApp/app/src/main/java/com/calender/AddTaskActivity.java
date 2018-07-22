package com.calender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.calender.databinding.ActivityAddTaskBinding;
import com.calender.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "Calender";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private ActivityAddTaskBinding activityAddTaskBinding;
    private String userID;
    private String phoneNumber;

    public static void StartActivity(Context context) {
        context.startActivity(new Intent(context, AddTaskActivity.class));
    }

    private void createPost() {
        String postID = mFirebaseDatabase.push().getKey();
        Post post = new Post();
        post.setCreateDate(getDate());
        post.setDescription(getDescription());
        post.setTitle(getTitlePost());
        post.setId(userID);
        mFirebaseDatabase.child(postID).setValue(post);
        post.setTitle("");
        post.setDescription("");
        post.setCreateDate("");
        activityAddTaskBinding.setPost(post);
        showToast(R.string.created);
        TaskListActivity.StartActivity(this);
    }

    private void showToast(@StringRes int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidFields() {
        if (TextUtils.isEmpty(getTitlePost())) {
            Utils.GetInstance().showToast(this, "Title field is mandatory");
            return false;
        } else if (TextUtils.isEmpty(getDate())) {
            Utils.GetInstance().showToast(this, "Date field is mandatory");
            return false;
        } else if (TextUtils.isEmpty(getDescription())) {
            Utils.GetInstance().showToast(this, "Description field is mandatory");
            return false;
        } else {
            return true;
        }
    }

    private String getTitlePost() {
        return activityAddTaskBinding.titleEditText.getText().toString().trim();
    }

    private String getDate() {
        return activityAddTaskBinding.createDateEditText.getText().toString().trim();
    }

    private String getDescription() {
        return activityAddTaskBinding.descriptionEditText.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddTaskBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_add_task);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //userID = firebaseAuth.getCurrentUser().getUid();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            userID = firebaseAuth.getCurrentUser().getUid();
            phoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.POST_TABLE);
        //setContentView(R.layout.activity_add_task);
        Calendar currentDate = Calendar.getInstance();
        final DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                activityAddTaskBinding.createDateEditText.setText(Utils.GetInstance().convertDate(newDate));
            }

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        activityAddTaskBinding.createDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                datePicker.show();
                return true;
            }
        });

        activityAddTaskBinding.saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidFields())
                    createPost();
            }
        });
    }
}
