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
public class ChallengeVideosFragment extends Fragment {

    private final String TAG = getClass().toString();

    private View rootView;

    private String challengeId;

    public ChallengeVideosFragment() {

    }

    public static ChallengeVideosFragment getInstance (String challengeId) {

        ChallengeVideosFragment challengeVideosFragment = new ChallengeVideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("challengeId", challengeId);
        challengeVideosFragment.setArguments(bundle);
        return challengeVideosFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_challenge_videos, container, false);



        return rootView;
    }



}
