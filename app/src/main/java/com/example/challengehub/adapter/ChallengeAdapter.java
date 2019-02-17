package com.example.challengehub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.challengehub.R;
import com.example.challengehub.model.Challenge;

import java.util.ArrayList;

public class ChallengeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Challenge> challenges;

    public interface Communicator{
        void update(String tag);
    }

    private Communicator delegate;

    public ChallengeAdapter(Context ctx, ArrayList<Challenge> challenges ){
        mContext = ctx;
        this.challenges = challenges;
    }


    @Override
    public int getCount() {
        return challenges.size();
    }

    @Override
    public Challenge getItem(int position) {
        return challenges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.challenge_item,null);
        }

        final TextView challenge = convertView.findViewById(R.id.tv_name);
        challenge.setText(challenges.get(position).getName());

        TextView count = convertView.findViewById(R.id.tv_count);
        count.setText(String.valueOf(challenges.get(position).getHitCount()));

        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}
