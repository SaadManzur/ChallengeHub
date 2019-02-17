package com.example.challengehub.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.challengehub.R;
import com.example.challengehub.fragment.LiveFragment;
import com.example.challengehub.fragment.WatchLiveFragment;
import com.example.challengehub.model.Video;

import java.util.List;

public class VideoListAdapter extends ArrayAdapter {
    private final String TAG = "VideoAdapter";

    private Context mContext;
    private List<Video> mVideos;


    public VideoListAdapter(Context context, int resource, List<Video> videos) {
        super(context, resource);
        mContext = context;
        mVideos = videos;
    }

    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Video getItem(int position) {
        return mVideos.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_item, null);
        }

        ImageButton ibPlay = convertView.findViewById(R.id.play);
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_holder, WatchLiveFragment.getInstance(mVideos.get(position).getVideoId()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageButton ibFacebook = convertView.findViewById(R.id.facebook);
        ibFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "fb-messenger://share";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                intent.putExtra(Intent.EXTRA_TEXT, mVideos.get(position).getLink());
                mContext.startActivity(intent);
            }
        });

        TextView title = convertView.findViewById(R.id.title);
        title.setText(mVideos.get(position).getVideoId());

        return convertView;
    }

}