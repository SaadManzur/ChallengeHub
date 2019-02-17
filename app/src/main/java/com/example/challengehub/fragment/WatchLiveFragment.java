package com.example.challengehub.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.challengehub.R;
import com.example.challengehub.misc.Utilities;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.view.R5VideoView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchLiveFragment extends Fragment {

    private final String TAG = getClass().toString();

    private View rootView;
    private ImageButton button;

    private R5Configuration r5Configuration;
    private R5Stream r5Stream;

    private String videoId = null;
    private boolean isPlaying = false;

    public WatchLiveFragment() {

    }

    public static WatchLiveFragment getInstance(String videoId) {

        WatchLiveFragment watchLiveFragment = new WatchLiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoId", videoId);
        watchLiveFragment.setArguments(bundle);
        return watchLiveFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        r5Configuration = new R5Configuration(R5StreamProtocol.RTSP, Utilities.SERVER,
                8554, "live", 1.0f);
        r5Configuration.setLicenseKey(getString(R.string.license_key));
        r5Configuration.setBundleID(getActivity().getPackageName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_watch_live, container, false);

        Bundle bundle = getArguments();

        if(bundle != null && bundle.containsKey("videoId")) {
            videoId = bundle.getString("videoId");
        }

        setupUI();

        return rootView;
    }

    @Override
    public void onPause() {

        super.onPause();

        if(isPlaying) {
            togglePlay();
        }
    }

    private void setupUI() {

        button = rootView.findViewById(R.id.play_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });
    }

    private void togglePlay() {

        if(isPlaying) {
            stop();
        }
        else
            start();

        isPlaying = !isPlaying;

        button.setImageDrawable(ContextCompat.getDrawable(getActivity(), (isPlaying)?
                R.drawable.ic_stop: R.drawable.ic_play_arrow));
    }

    private void start() {

        R5VideoView r5VideoView = getActivity().findViewById(R.id.subscriber_view);

        r5Stream = new R5Stream(new R5Connection(r5Configuration));
        r5VideoView.attachStream(r5Stream);
        r5Stream.play(videoId);
    }

    private void stop() {

        if(r5Stream != null) {
            r5Stream.stop();
        }
    }

}
