package gmd.socialmediaapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.dift.ui.SwipeToAction;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Post> mPostSet;
    private SimpleExoPlayer player;

    public MainAdapter(Context context, ArrayList<Post> mPostSet) {
        this.context = context;
        this.mPostSet = mPostSet;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTitle.setText(mPostSet.get(position).getUsername());

        if(!mPostSet.get(position).getImageurl().isEmpty()) {
            holder.mView.setVisibility(View.VISIBLE);
            holder.mVideoView.setVisibility(View.INVISIBLE);

            Picasso.get()
                    .load(mPostSet.get(position).getImageurl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.mView);
        }

        else if(!mPostSet.get(position).getVideourl().isEmpty()){
            holder.mView.setVisibility(View.INVISIBLE);
            holder.mVideoView.setVisibility(View.VISIBLE);

            player = ExoPlayerFactory.newSimpleInstance( new DefaultRenderersFactory(context), new DefaultTrackSelector(),new DefaultLoadControl());
            holder.mVideoView.setPlayer(player);
            player.setPlayWhenReady(true);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            player.prepare(buildMediaSource( Uri.parse(mPostSet.get(position).getVideourl())));
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("app")).
                createMediaSource(uri);
    }

    @Override
    public int getItemCount() {
        return mPostSet.size();
    }


    public static class ViewHolder extends SwipeToAction.ViewHolder {
        public TextView mTitle;
        public ImageView mView;
        public PlayerView mVideoView;

        RecyclerView vertical_recycler;
        RecyclerView.Adapter verticalAdapter;
        RecyclerView.LayoutManager VretikalLayoutManager;


        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.mainTitle);
            mView = itemView.findViewById(R.id.imageView2);
            mVideoView = itemView.findViewById(R.id.videoView);

        }
    }

}
