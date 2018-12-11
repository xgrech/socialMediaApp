package gmd.socialmediaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class UserData extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> LinkSet;
    private ArrayList<String> DateSet;

    public GestureDetectorCompat gestureObject;
    private Integer itemPosition;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemPosition = 0;

        LinkSet = getIntent().getStringArrayListExtra("links");
        DateSet = getIntent().getStringArrayListExtra("dates");
        setContentView(R.layout.activity_user_data);
        mRecyclerView = findViewById(R.id.data_recycle_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DataAdapter(this, LinkSet, DateSet, getIntent().getStringExtra("username"));
        mRecyclerView.setAdapter(mAdapter);

        gestureObject = new GestureDetectorCompat(this, new UserData.LearnGesture());


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

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if ((e1.getY() < e2.getY()) && (e2.getY() - e1.getY() > 100)) {
                if (itemPosition != 0) {
                    mRecyclerView.smoothScrollToPosition(itemPosition - 1);
                    itemPosition = itemPosition - 1;
                    Toast.makeText(UserData.this, itemPosition.toString(), Toast.LENGTH_SHORT).show();

                } else {
                    finish();
                }
            }

            if ((e2.getX() < e1.getX()) && (e1.getX() - e2.getX() > 100)) {

            }

            if ((e1.getX() < e2.getX()) && (e2.getX() - e1.getX() > 100)) {

            }

            if ((e2.getY() < e1.getY()) && (e1.getY() - e2.getY() > 100)) {
                mRecyclerView.smoothScrollToPosition(itemPosition + 1);
                itemPosition = itemPosition + 1;
                Toast.makeText(UserData.this, itemPosition.toString(), Toast.LENGTH_SHORT).show();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

}
