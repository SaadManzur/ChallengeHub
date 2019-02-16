package com.example.challengehub.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.challengehub.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchLiveFragment extends Fragment {

    private final String TAG = getClass().toString();

    private View rootView;

    public WatchLiveFragment() {

    }

    public static WatchLiveFragment getInstance() {

        WatchLiveFragment watchLiveFragment = new WatchLiveFragment();
        Bundle bundle = new Bundle();
        watchLiveFragment.setArguments(bundle);
        return watchLiveFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_watch_live, container, false);

        return rootView;
    }



}
