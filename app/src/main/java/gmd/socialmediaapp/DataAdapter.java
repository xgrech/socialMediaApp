package gmd.socialmediaapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> mDataSet;
    private SimpleExoPlayer player;


    public class ViewHolder extends SwipeToAction.ViewHolder {
        public TextView mTitle;
        public ImageView mView;
        public PlayerView mVideoView;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.mainTitle);
            mView = itemView.findViewById(R.id.imageView2);
            mVideoView = itemView.findViewById(R.id.videoView);
        }
    }

    public DataAdapter(Context context, ArrayList<String> mDataset) {
        this.context = context;
        this.mDataSet = mDataset;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.revertical_item, parent, false);
        DataAdapter.ViewHolder vh = new DataAdapter.ViewHolder(v);
        return vh;
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("app")).
                createMediaSource(uri);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        viewHolder.mTitle.setText(mDataSet.get(i));

        if(!mDataSet.get(position).contains(".mp4")) {
            holder.mView.setVisibility(View.VISIBLE);
            holder.mVideoView.setVisibility(View.INVISIBLE);

            Picasso.get()
                    .load(mDataSet.get(position))
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.mView);
        }

        else if(mDataSet.get(position).contains(".mp4")){
            holder.mView.setVisibility(View.INVISIBLE);
            holder.mVideoView.setVisibility(View.VISIBLE);

            player = ExoPlayerFactory.newSimpleInstance( new DefaultRenderersFactory(context), new DefaultTrackSelector(),new DefaultLoadControl());
            holder.mVideoView.setPlayer(player);
            player.setPlayWhenReady(true);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            player.prepare(buildMediaSource( Uri.parse(mDataSet.get(position))));
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
