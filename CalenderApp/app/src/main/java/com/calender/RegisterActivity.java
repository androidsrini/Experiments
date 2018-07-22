package com.calender;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.calender.databinding.ActivityRegisterBinding;
import com.calender.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Calender";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String loginUserId, phoneNumber;
    //private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityRegisterBinding activityRegisterBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_register);
        User user = new User();
        activityRegisterBinding.setUser(user);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            loginUserId = firebaseAuth.getCurrentUser().getUid();
            phoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.USER_TABLE);
        // store app title to 'app_title' node
        mFirebaseInstance.getReference(Constant.APP_TITLE).setValue("Realtime Database");
        activityRegisterBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = activityRegisterBinding.name.getText().toString();
                String email = activityRegisterBinding.email.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(email)) {
                        createUser(name, email);
                    } else {
                        Utils.GetInstance().showToast(RegisterActivity.this, "Email field is mandatory");
                    }
                } else {
                    Utils.GetInstance().showToast(RegisterActivity.this, "Name field is mandatory");
                }
                // Check for already existed userId
                /*if (TextUtils.isEmpty(userId)) {
                    createUser(name, email);
                } else {
                    updateUser(name, email);
                }*/
            }
        });
        //setContentView(R.layout.activity_register);
    }

    private void createUser(String name, String email) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        /*if (TextUtils.isEmpty(userId)) {
            userId = loginUserId;//mFirebaseDatabase.push().getKey();
        }*/
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setId(loginUserId);
        user.setPhoneNumber(phoneNumber);
        mFirebaseDatabase.child(loginUserId).setValue(user);
        startActivity(new Intent(this, AddTaskActivity.class));
        //addUserChangeListener();
    }

    private void updateUser(String name, String email) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(loginUserId).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(loginUserId).child("email").setValue(email);
        mFirebaseDatabase.child(loginUserId).child("id").setValue(loginUserId);
        mFirebaseDatabase.child(loginUserId).child("phone_number").setValue(phoneNumber);
    }
}
