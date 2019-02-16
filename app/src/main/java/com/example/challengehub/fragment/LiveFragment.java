package com.example.challengehub.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.challengehub.R;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends Fragment implements SurfaceHolder.Callback {

    private final String TAG = this.getClass().toString();
    private final int CAMERA_REQUEST_ID = 300;

    private View rootView;
    private SurfaceView surfaceView;
    private Button button;

    private R5Configuration r5Configuration;

    protected Camera camera;
    protected boolean isLive = false;
    protected R5Stream stream;

    public LiveFragment() {

    }

    public static LiveFragment getInstance() {

        LiveFragment liveFragment = new LiveFragment();
        Bundle bundle = new Bundle();
        liveFragment.setArguments(bundle);
        return liveFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        r5Configuration = new R5Configuration(R5StreamProtocol.RTSP, "169.234.78.222",
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

    private void setupUI() {

        surfaceView = rootView.findViewById(R.id.surface_view);

        button = rootView.findViewById(R.id.live_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLive();
            }
        });
    }

    private void preview() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), "You need to grant camera access to live stream.",
                        Toast.LENGTH_LONG).show();
            }
            else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_ID);
            }
        }
        else {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            surfaceView.getHolder().addCallback(this);
        }
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

            case CAMERA_REQUEST_ID:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    surfaceView.getHolder().addCallback(this);
                }
                else {
                    Toast.makeText(getActivity(), "You need to grant camera access to live stream.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void toggleLive() {

        if(isLive) {
            stop();
        }
        else
            start();

        isLive = !isLive;

        button.setText((isLive)? "Stop": "Go Live");
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
        stream.attachCamera(r5Camera);

        R5Microphone r5Microphone = new R5Microphone();
        stream.attachMic(r5Microphone);

        stream.publish("myr5stream", R5Stream.RecordType.Live);
        camera.startPreview();
    }

    private void stop() {

        if(stream != null) {
            stream.stop();
            camera.startPreview();
        }
    }
}
