package com.calender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.calender.model.Post;
import com.calender.model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Calender";
    private static final int RC_SIGN_IN = 123;

    private ExplosionField mExplosionField;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.PhoneBuilder().build());

    private ArrayList<Post> postLists;

    private void fetchPostDataFromServer() {
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.POST_TABLE);
        Query query = mFirebaseDatabase.orderByChild(Constant.ID).equalTo(Utils.GetInstance().getUserID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, " read user " + dataSnapshot.getChildren());
                List<Post> postList = new ArrayList<Post>();
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Post post = adSnapshot.getValue(Post.class);
                    postList.add(post);
                    Log.d(TAG, "List id: " + post.getId() + " ,name: " + post.getTitle() + " ,Email: " + post.getDescription());
                }
                postLists.clear();
                postLists.addAll(postList);
                if (postLists.isEmpty()) {
                    startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
                    finish();
                } else {
                    /*Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                    intent.putParcelableArrayListExtra(TaskListActivity.POST_LIST_ARG, postLists);
                    startActivity(intent);*/
                    TaskListActivity.StartActivity(MainActivity.this, postLists);
                    finish();
                }

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
        setContentView(R.layout.activity_main);
        mExplosionField = ExplosionField.attach2Window(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        postLists = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth == null || firebaseAuth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            // get reference to 'users' node
            // store app title to 'app_title' node
            mFirebaseInstance.getReference("app_title").setValue("Realtime Database");
            mFirebaseInstance.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, " read user " + dataSnapshot.getChildren());
                    List<User> userList = new ArrayList<User>();
                    for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                        User user = adSnapshot.getValue(User.class);
                        userList.add(user);
                        Log.d(TAG, "List id: " + user.getId() + " ,name: " + user.getName() + " ,Email: " + user.getEmail()
                        + " uuid: " + firebaseAuth.getCurrentUser().getUid());
                    }
                    for (User user: userList) {
                        if (!TextUtils.isEmpty(user.getId()) &&
                                firebaseAuth.getCurrentUser().getUid().equalsIgnoreCase(user.getId())) {
                            fetchPostDataFromServer();
                            return;
                        }
                    }
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read app title value.", error.toException());
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            final IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d(TAG, " Mobile number: " + response.getPhoneNumber() + " uuID: " + response.getIdpToken());
                if (!TextUtils.isEmpty(response.getPhoneNumber())) {
                    DatabaseReference databaseReference = mFirebaseInstance.getReference().child(Constant.USER_TABLE);
                    Query queryRef = databaseReference.orderByChild(Constant.PHONE_NUMBER).equalTo(response.getPhoneNumber());
                    queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                fetchPostDataFromServer();
                            } else {
                                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}
