package gmd.socialmediaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class MainActivity extends AppCompatActivity {

    public FirebaseAuth mFirebaseAuth;
    public FirebaseFirestore mFirestore;
    public FirebaseAuth.AuthStateListener mAuthStateListner;

    TextView profileText;


    public RecyclerView mRecyclerView;
    public RecyclerView.LayoutManager mLayoutManager;
    public RecyclerView.Adapter mAdapter;

    private final ArrayList<Post> posts = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public boolean loginToken = false;

    public GestureDetectorCompat gestureObject;

    public LinearLayout profile_view;
    public int position;
    private static int RESULT_LOAD_IMG = 1;

    private Integer itemPosition;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        getAllPosts();
        getAllUsers();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

            }
        }, 5000);

        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {
                    openLoginScreen();
                }
            }
        };

        itemPosition = 0;

        profileText = findViewById(R.id.profileTitle);
        profileText.setText(loginToken + "");

        profile_view = findViewById(R.id.profile_layout);
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return true;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRecyclerView.setLayoutManager(mLayoutManager);
        Button uploadButton = (Button)findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Uploader.class);
                startActivity(i);
            }
        });


        handler.postDelayed(new Runnable() {
            public void run() {
                mAdapter = new MainAdapter(MainActivity.this, posts);
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 5000);

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        profile_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = v.getVisibility();
                if (visibility == View.VISIBLE)
                    v.setVisibility(View.GONE);
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureObject.onTouchEvent(event);
                return false;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    class LearnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            setLayoutAnimation_slideDown(profile_view);
            if ((e1.getY() < e2.getY()) && (e2.getY() - e1.getY() > 100)) {
                int visibility = profile_view.getVisibility();
                if (visibility == View.GONE)
                    profile_view.setVisibility(View.VISIBLE);
            }

            if ((e2.getX() < e1.getX()) && (e1.getX() - e2.getX() > 100)) {
                mRecyclerView.smoothScrollToPosition(itemPosition + 1);
                itemPosition = itemPosition + 1;
                Toast.makeText(MainActivity.this, itemPosition.toString(), Toast.LENGTH_SHORT).show();
            }

            if ((e1.getX() < e2.getX()) && (e2.getX() - e1.getX() > 100)) {
                if (itemPosition != 0) {
                    mRecyclerView.smoothScrollToPosition(itemPosition - 1);
                    itemPosition = itemPosition - 1;
                    Toast.makeText(MainActivity.this, itemPosition.toString(), Toast.LENGTH_SHORT).show();

                }
            }

            if ((e2.getY() < e1.getY()) && (e1.getY() - e2.getY() > 100)) {
                openUserData();
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
        intent.putStringArrayListExtra("links", getUserPostsLinks(posts.get(itemPosition).getUserid()));
        intent.putStringArrayListExtra("dates", getUserPostsDate(posts.get(itemPosition).getUserid()));
        intent.putExtra("username", posts.get(itemPosition).getUsername());
        startActivity(intent);
    }

    public void openLoginScreen() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.putStringArrayListExtra("names", getAllUserNames());
        startActivity(intent);
    }

    public void addPost(Post post) {
        mFirestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //TODO added successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO failed to add
                    }
                });
    }

    public void getAllPosts() {
        mFirestore.collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                posts.add(document.toObject(Post.class));
                            }
                        } else {
                            //TODO failed to load data
                        }
                    }
                });
    }

    public ArrayList<String> getAllUserNames() {
        ArrayList<String> names = new ArrayList<>();
        for (User user : users) {
            names.add(user.getUsername());
        }
        return names;
    }

    public ArrayList<String> getUserPostsLinks(String userId) {
        ArrayList<String> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (userId.equals(post.getUserid())) {
                if (!post.getVideourl().isEmpty()) userPosts.add(post.getVideourl());
                else if (!post.getImageurl().isEmpty()) userPosts.add(post.getImageurl());
            }
        }
        return userPosts;
    }

    public ArrayList<String> getUserPostsDate(String userId) {
        ArrayList<String> userPostsDate = new ArrayList<>();
        for (Post post : posts) {
            if (userId.equals(post.getUserid())) {
                userPostsDate.add(post.getDate().toString());
            }
        }
        return userPostsDate;
    }

    public ArrayList<Post> getUserPosts(String userId) {
        ArrayList<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            userPosts.add(post);
        }
        return userPosts;
    }

    public User getUserProfile(String name) {
        for (User user : users) {
            if (name.equals(user.getUsername())) {
                return user;
            }
        }
        //empty user
        return new User();
    }

    public void updateUserInsertCount(String userId) {
        mFirestore.collection("users").document(userId).update("numberOfPosts", getCurrentUserPostCount(userId) + 1);
    }

    public int getCurrentUserPostCount(String userId) {
        int count = 0;
        for (Post post : posts) {
            if (userId.equals(post.getUserid())) {
                count++;
            }
        }
        return count;
    }

    public void getAllUsers() {
        mFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                        } else {
                            //TODO failed to load users message?
                        }
                    }

                });
    }
}

