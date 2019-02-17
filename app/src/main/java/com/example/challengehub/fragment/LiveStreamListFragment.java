package com.example.challengehub.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.challengehub.R;
import com.example.challengehub.adapter.VideoListAdapter;
import com.example.challengehub.misc.RequestHandler;
import com.example.challengehub.misc.Utilities;
import com.example.challengehub.model.Video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveStreamListFragment extends Fragment {

    private final String TAG = getClass().toString();

    private View rootView;
    private ListView videoListView;

    private VideoListAdapter videoListAdapter;
    private List<Video> videoList;
    private String challengeId = null;

    private JsonObjectRequest streamInformationRequest;

    public LiveStreamListFragment() {

    }

    public static LiveStreamListFragment getInstance(String challengeId) {

        LiveStreamListFragment fragment = new LiveStreamListFragment();
        Bundle bundle = new Bundle();

        if(challengeId != null) {
            bundle.putString("challengeId", challengeId);
        }
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_live_stream_list, container, false);

        Bundle bundle = getArguments();

        if(bundle != null) {
            if(bundle.containsKey("challengeId")) {
                challengeId = bundle.getString("challengeId");
            }
        }

        setupUI();
        
        return rootView;
    }

    private void setupUI() {

        videoList = new ArrayList<>();
        videoListAdapter = new VideoListAdapter(getActivity(), R.layout.video_item, videoList);

        videoListView = rootView.findViewById(R.id.video_list_view);
        videoListView.setAdapter(videoListAdapter);
        videoListView.setEmptyView(rootView.findViewById(R.id.list_empty_text));

        getLiveStreams();
    }

    private void getLiveStreams() {

        videoList.clear();
        videoListAdapter.notifyDataSetChanged();

        if(challengeId == null)
            getAllStreams();
        else
            getChallengeSpecificStreams();

    }


    private void getAllStreams() {

        String[] params = new String[2];
        params[0] = "accessToken";
        params[1] = "347602146a923872538f3803eb5f3cef";

        streamInformationRequest = new JsonObjectRequest(Request.Method.GET,
                "http://" + Utilities.SERVER + ":5080/api/v1/applications/live/streams?" + Utilities.generateRequest(params),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");

                            for(int i = 0; i < data.length(); i++) {

                                videoList.add(new Video(data.get(i).toString()));
                                videoListAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });

        streamInformationRequest.setTag(TAG);

        streamInformationRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(getContext()).getRequestQueue().add(streamInformationRequest);

    }

    private void getChallengeSpecificStreams () {

        StringRequest videoIdRequest = new StringRequest(Request.Method.GET,
                Utilities.DATA_SERVER + "videos/" + challengeId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);

                try {
                    JSONArray jsonArray = new JSONObject(response).getJSONArray("selectedVideos");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONArray videoJSONArray = jsonArray.getJSONArray(i);

                        for(int j = 0; j < videoJSONArray.length(); j++) {

                            JSONObject currentVideoJSON = videoJSONArray.getJSONObject(j);

                            Type videoType = new TypeToken<Video>() {}.getType();
                            Video video = new Gson().fromJson(currentVideoJSON.toString(), videoType);
                            videoList.add(video);
                            videoListAdapter.notifyDataSetChanged();

                        }
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

        videoIdRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(getActivity()).getRequestQueue().add(videoIdRequest);
    }

}
