package com.example.challengehub.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.challengehub.R;
import com.example.challengehub.misc.RequestHandler;
import com.example.challengehub.misc.Utilities;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.view.R5VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchLiveFragment extends Fragment {

    private final String TAG = getClass().toString();

    private View rootView;
    private ImageButton button;
    private Button likeButton;

    private R5Configuration r5Configuration;
    private R5Stream r5Stream;

    private String videoName = null;
    private String videoId = null;
    private boolean isPlaying = false;

    public WatchLiveFragment() {

    }

    public static WatchLiveFragment getInstance(String videoId) {

        WatchLiveFragment watchLiveFragment = new WatchLiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoName", videoId);
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

        if(bundle != null ) {

            if(bundle.containsKey("videoName"))
                videoName = bundle.getString("videoName");

            if(bundle.containsKey("videoId"))
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

        likeButton = rootView.findViewById(R.id.like_button);

        if(videoId == null) {
            likeButton.setVisibility(View.GONE);
            getVideoId();
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeVideo();
            }
        });
    }

    private void likeVideo () {

        StringRequest likeRequest = new StringRequest(Request.Method.POST,
                Utilities.DATA_SERVER + "/trends", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                likeButton.setText("Voted");
                likeButton.setEnabled(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("userId", Utilities.USER_ID);
                params.put("videoId", videoId);

                return params;
            }
        };

        likeRequest.setTag(TAG);

        RequestHandler.getInstance(getActivity()).getRequestQueue().add(likeRequest);
    }

    private void getVideoId() {

        StringRequest videoIdRequest = new StringRequest(Request.Method.POST,
                Utilities.DATA_SERVER + "/getVideoByName" + videoName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);

                try {
                    JSONArray jsonArray = new JSONObject(response).getJSONArray("selectedVideos");

                    if(jsonArray.length() > 0) {
                        videoId = jsonArray.getJSONObject(0).getString("_id");

                        likeButton.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        videoIdRequest.setTag(TAG);

        RequestHandler.getInstance(getActivity()).getRequestQueue().add(videoIdRequest);
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
        r5Stream.play(videoName);
    }

    private void stop() {

        if(r5Stream != null) {
            r5Stream.stop();
        }
    }

}
