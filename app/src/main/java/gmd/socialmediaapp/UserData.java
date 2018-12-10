package gmd.socialmediaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class UserData extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> LinkSet;
    private ArrayList<String> DateSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinkSet = getIntent().getStringArrayListExtra("links");
        DateSet = getIntent().getStringArrayListExtra("dates");
        setContentView(R.layout.activity_user_data);
        mRecyclerView = findViewById(R.id.data_recycle_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DataAdapter(this, LinkSet, DateSet, getIntent().getStringExtra("username"));
        mRecyclerView.setAdapter(mAdapter);
    }
}
