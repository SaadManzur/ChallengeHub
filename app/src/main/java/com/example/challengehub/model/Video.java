package com.example.challengehub.model;

import android.support.v4.content.ContextCompat;

public class Video {

    private String videoId;
    private boolean isLive = true;

    public Video(String videoId) {

        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLink() {

        if(isLive) {

            String server = "169.234.78.222";
            String url = "http://" + server + ":5080/live/viewer.jsp?host=" + server + "&stream=" + videoId;

            return url;
        }

        return null;
    }

}
