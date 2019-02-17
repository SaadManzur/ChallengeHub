package com.example.challengehub.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.challengehub.R;
import com.example.challengehub.fragment.ChallengeFragment;
import com.example.challengehub.fragment.LiveFragment;
import com.example.challengehub.fragment.LiveStreamListFragment;
import com.example.challengehub.fragment.WatchLiveFragment;
import com.example.challengehub.misc.Utilities;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;


public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().toString();
    private final int DEVICE_ID_REQUEST_CODE = 423;

    private FrameLayout fragmentHolder;
    private Toolbar toolbar;
    private Drawer drawer;

    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Utilities.hasAllPermissions(this,  Manifest.permission.READ_PHONE_STATE )) {
            Utilities.requestPermissions(this, DEVICE_ID_REQUEST_CODE, Manifest.permission.READ_PHONE_STATE);
        }
        else {
            getDeviceIMEI();
        }

        setupUI();

        drawer.setSelection(1, true);
    }

    private void setupUI() {

        toolbar = findViewById(R.id.toolbar);
        fragmentHolder = findViewById(R.id.fragment_holder);

        PrimaryDrawerItem liveFragmentItem = new PrimaryDrawerItem()
                                                        .withIdentifier(2)
                                                        .withIcon(GoogleMaterial.Icon.gmd_videocam)
                                                        .withName(R.string.live_drawer_name);

        PrimaryDrawerItem watchLiveFragmentItem = new PrimaryDrawerItem()
                                                            .withIdentifier(1)
                                                            .withIcon(GoogleMaterial.Icon.gmd_live_tv)
                                                            .withName(R.string.watch_live_drawer_name);

        PrimaryDrawerItem challengeFragmentItem = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withIcon(GoogleMaterial.Icon.gmd_star)
                .withName(R.string.challenge_drawer_name);

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(deviceId)
                                .withEmail("anonymous@nothing.com")
                                .withIcon(getResources().getDrawable(R.drawable.person))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                        .withActivity(this)
                        .withToolbar(toolbar)
                        .withAccountHeader(accountHeader)
                        .addDrawerItems(
                                watchLiveFragmentItem,
                                liveFragmentItem,
                                challengeFragmentItem
                        )
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                switch(position)
                                {
                                    case 1:
                                        switchFragment(LiveStreamListFragment.getInstance());
                                        break;

                                    case 2:
                                        switchFragment(LiveFragment.getInstance());
                                        break;

                                    case 3:
                                        switchFragment(ChallengeFragment.getInstance());
                                }

                                return false;
                            }
                        })
                        .build();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch(requestCode) {

            case DEVICE_ID_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getDeviceIMEI();
                }
                else {
                    Toast.makeText(this, "You need to grant camera access to live stream.",
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    private void switchFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .commit();
    }



    @SuppressLint("MissingPermission")
    private void getDeviceIMEI() {

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
    }

}
