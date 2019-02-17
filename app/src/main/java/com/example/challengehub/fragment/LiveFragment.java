package com.example.challengehub.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends Fragment implements SurfaceHolder.Callback {

    private final String TAG = this.getClass().toString();
    private final int PERMISSION_REQUEST_ID = 300;

    private View rootView;
    private SurfaceView surfaceView;
    private FloatingActionButton recordButton;

    private R5Configuration r5Configuration;

    protected Camera camera;
    protected boolean isLive = false;
    protected R5Stream stream;

    private int surfaceViewWidth;
    private int surfaceViewHeight;
    private int cameraRotation = 0;
    private String challengeId = "";

    private String streamName;

    private String[] permssions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    public LiveFragment() {

    }

    public static LiveFragment getInstance(String challengeId) {

        LiveFragment liveFragment = new LiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString("challengeId", challengeId);
        liveFragment.setArguments(bundle);
        return liveFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("challengeId")) {
            challengeId = bundle.getString("challengeId");
        }

        r5Configuration = new R5Configuration(R5StreamProtocol.RTSP, Utilities.SERVER,
                8554, "live", 1.0f);
        r5Configuration.setLicenseKey(getString(R.string.license_key));
        r5Configuration.setBundleID(getActivity().getPackageName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_live, container, false);

        setupUI();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupUI() {

        surfaceView = rootView.findViewById(R.id.surface_view);

        recordButton = rootView.findViewById(R.id.live_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLive();
            }
        });
    }

    private void preview() {

        if (!hasAllPermissions()) {
            requestPermission();
        }
        else {
            setupCameraAndCallback();
        }
    }

    private boolean hasAllPermissions() {

        for(String permission : permssions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }

        return true;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            Toast.makeText(getActivity(), "You need to grant camera access to live stream.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    permssions,
                    PERMISSION_REQUEST_ID);
        }
    }

    private void setupCameraAndCallback() {

        camera = openFrontFacingCamera();
        camera.stopPreview();
        camera.setDisplayOrientation(90);
        camera.startPreview();
        surfaceView.getHolder().addCallback(this);
    }

    private void postVideo() {

        StringRequest videoCreationRequest = new StringRequest(Request.Method.POST,
                Utilities.DATA_SERVER + "videos",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //processFragmentCommunicator.hideProgress();
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    JSONObject errorObject = jsonObject.getJSONObject("error");
                    Toast.makeText(getContext(), errorObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("userId", Utilities.USER_ID);
                params.put("name", streamName);
                params.put("liveStream", streamName);
                params.put("challengeId", challengeId );

                return params;
            }
        };

        videoCreationRequest.setTag(TAG);

        RequestHandler.getInstance(getActivity()).getRequestQueue().add(videoCreationRequest);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.i(TAG, width + " " + height);

        decideViewSize();

        holder.setFixedSize(surfaceViewWidth,surfaceViewHeight);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onResume() {

        super.onResume();
        preview();
    }

    @Override
    public void onPause() {

        super.onPause();
        
        if(isLive) {
            toggleLive();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch(requestCode) {

            case PERMISSION_REQUEST_ID:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupCameraAndCallback();
                }
                else {
                    Toast.makeText(getActivity(), "You need to grant camera access to live stream.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void decideViewSize() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        List<Camera.Size> supportedSizes = camera.getParameters().getSupportedPictureSizes();

        double displayAspectRatio = ( displayHeight * 1.0) / displayWidth;

        double aspectRatio = 0.0;
        double minDifference = 999999;

        for(Camera.Size currentSize : supportedSizes) {

            aspectRatio = ( currentSize.width * 1.0 ) / currentSize.height;

            surfaceViewHeight = currentSize.width;

            if(currentSize.width < displayHeight) {
                break;
            }
        }

        surfaceViewWidth = (int) ( surfaceViewHeight / aspectRatio );
    }

    private void toggleLive() {

        if(isLive) {
            stop();
        }
        else
            start();

        isLive = !isLive;

        recordButton.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                (isLive)? R.drawable.ic_stop: R.drawable.ic_play_circle_outline));
    }

    private void start() {

        camera.stopPreview();

        R5Connection r5Connection = new R5Connection(r5Configuration);
        r5Connection.addListener(new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
                Log.d(TAG, r5ConnectionEvent.value() + " " + r5ConnectionEvent.message);
            }
        });

        stream = new R5Stream(r5Connection);
        stream.setView(surfaceView);

        R5Camera r5Camera = new R5Camera(camera, 640, 480);
        r5Camera.setOrientation(cameraRotation);
        stream.attachCamera(r5Camera);

        R5Microphone r5Microphone = new R5Microphone();
        stream.attachMic(r5Microphone);

        streamName = UUID.randomUUID().toString().substring(0,10);

        if(challengeId != null) {
            postVideo();
        }

        stream.publish(streamName, R5Stream.RecordType.Record);
        camera.startPreview();
    }

    private Camera openFrontFacingCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                    cameraRotation = cameraInfo.orientation;
                    applyDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        return cam;
    }

    private void applyDeviceRotation() {

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        cameraRotation += degrees;

        cameraRotation = cameraRotation%360;
    }

    private void stop() {

        if(stream != null) {
            stream.stop();
            camera.startPreview();
        }
    }
}
