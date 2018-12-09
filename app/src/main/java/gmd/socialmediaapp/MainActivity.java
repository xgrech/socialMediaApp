package gmd.socialmediaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class MainActivity extends AppCompatActivity {

    public FirebaseAuth mFirebaseAuth;
    public static final int RC_SIGN_IN = 1;
    public FirebaseAuth.AuthStateListener mAuthStateListner;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());


    public RecyclerView mRecyclerView;
    public RecyclerView.LayoutManager mLayoutManager;
    public RecyclerView.Adapter mAdapter;

    private ArrayList<String> mmDataSet = new ArrayList<>();

    public boolean loginToken = false;

    public GestureDetectorCompat gestureObject;

    public LinearLayout profile_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    loginToken = true;
//                    Toast.makeText(MainActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN
                    );

                }
            }
        };


//        if (loginToken) {
        Toast.makeText(MainActivity.this, "Activity Start", Toast.LENGTH_SHORT).show();
//        }


        for (int i = 0; i < 10; i++) {
            mmDataSet.add("new title # " + i);
        }

        profile_view = findViewById(R.id.profile_layout);
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MainAdapter(mmDataSet);
        mRecyclerView.setAdapter(mAdapter);


        SwipeToAction swipeToAction = new SwipeToAction(mRecyclerView, new SwipeToAction.SwipeListener() {
            @Override
            public boolean swipeLeft(Object itemData) {
                return false;
            }

            @Override
            public boolean swipeRight(Object itemData) {
                return false;
            }

            @Override
            public void onClick(Object itemData) {
                openUserData();
            }

            @Override
            public void onLongClick(Object itemData) {

            }

        });

//        mRecyclerView.smoothScrollToPosition(5);
//        mRecyclerView.smoothScrollToPosition(5);


        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        profile_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = v.getVisibility();
                if (visibility == View.VISIBLE)
                    v.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListner);

    }

//    LinearLayout profile_view = findViewById(R.id.profile_layout);
//    setLayoutAnimation_slideDown(profile_view);
//
//    int action = MotionEventCompat.getActionMasked(event);
//
//        switch (action) {
//            case (MotionEvent.ACTION_UP): {
//                Toast.makeText(MainActivity.this, "Profile Start", Toast.LENGTH_SHORT).show();
//
//                int visibility = profile_view.getVisibility();
//                if (visibility == View.GONE)
//                    profile_view.setVisibility(View.VISIBLE);
//            }

//            case (MotionEvent.ACTION_UP): {
//                int visibility = profile_view.getVisibility();
//                if (visibility == View.VISIBLE)
//                    profile_view.setVisibility(View.GONE);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            setLayoutAnimation_slideDown(profile_view);

            if (e1.getY() < e2.getY()) {
                int visibility = profile_view.getVisibility();
                if (visibility == View.GONE)
                    profile_view.setVisibility(View.VISIBLE);
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }


    public void signout(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    public void setLayoutAnimation_slideDown(ViewGroup panel) {

        AnimationSet set = new AnimationSet(true);

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        // Set the duration here for animation in millis.
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.25f);
        panel.setLayoutAnimation(controller);
    }

    public void openUserData() {
        Intent intent = new Intent(this, UserData.class);
        startActivity(intent);
    }
}
