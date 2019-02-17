package com.example.challengehub.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.challengehub.R;
import com.example.challengehub.adapter.ChallengeAdapter;
import com.example.challengehub.misc.RequestHandler;
import com.example.challengehub.misc.Utilities;
import com.example.challengehub.model.Challenge;
import com.example.challengehub.model.Video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ChallengeFragment extends Fragment {

    private final String TAG = getClass().toString();

    private View rootView;
    private GridView gridView;

    private ArrayList<Challenge> challengeList;

    private ChallengeAdapter challengeAdapter;

    private StringRequest challengeListRequest;

    public ChallengeFragment() {

    }

    public static ChallengeFragment getInstance() {

        ChallengeFragment fragment = new ChallengeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_challenge, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUI();

        getChallenges();
    }

    private void setupUI() {

        challengeList = new ArrayList<>();

        gridView = rootView.findViewById(R.id.grid_view);

        challengeAdapter = new ChallengeAdapter(getActivity(), challengeList);
        gridView.setAdapter(challengeAdapter);
        gridView.setEmptyView(rootView.findViewById(R.id.list_empty_text));

    }


    private void getChallenges() {

        challengeListRequest = new StringRequest(Request.Method.GET,
                Utilities.DATA_SERVER + "challenges",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Type listType = new TypeToken<ArrayList<Challenge>>() {}.getType();
                            ArrayList<Challenge> challenges = new Gson().fromJson(response, listType);
                            challengeList.addAll(challenges);
                            challengeAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
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

        challengeListRequest.setTag(TAG);

        challengeListRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(getContext()).getRequestQueue().add(challengeListRequest);
    }
}
