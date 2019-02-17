package com.example.challengehub.model;

import android.support.v4.content.ContextCompat;

public class Video {

    private String _id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(String liveStream) {
        this.liveStream = liveStream;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public int getViewedTime() {
        return viewedTime;
    }

    public void setViewedTime(int viewedTime) {
        this.viewedTime = viewedTime;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    private String liveStream;
    private String challengeId;
    private int viewedTime;
    private int likeCount;
    private boolean isLive = true;

    public Video(String videoName) {

        this.name = videoName;
    }

    public String getVideoId() {
        return _id;
    }

    public void setVideoId(String videoId) {
        this._id = videoId;
    }

    public String getLink() {

        if(isLive) {

            String server = "169.234.78.222";
            String url = "http://" + server + ":5080/live/viewer.jsp?host=" + server + "&stream=" + _id;

            return url;
        }

        return null;
    }

}
