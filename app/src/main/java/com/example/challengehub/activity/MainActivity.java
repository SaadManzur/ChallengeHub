package com.example.challengehub.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.challengehub.R;
import com.example.challengehub.fragment.LiveFragment;
import com.example.challengehub.fragment.WatchLiveFragment;
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

    private FrameLayout fragmentHolder;
    private Toolbar toolbar;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        drawer.setSelection(1, true);
    }

    private void setupUI() {

        toolbar = findViewById(R.id.toolbar);
        fragmentHolder = findViewById(R.id.fragment_holder);

        PrimaryDrawerItem liveFragmentItem = new PrimaryDrawerItem()
                                                        .withIdentifier(2)
                                                        .withName(R.string.live_drawer_name);

        PrimaryDrawerItem watchLiveFragmentItem = new PrimaryDrawerItem()
                                                            .withIdentifier(1)
                                                            .withName(R.string.watch_live_drawer_name);

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("Anonymous")
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
                                liveFragmentItem
                        )
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                switch(position)
                                {
                                    case 1:
                                        switchFragment(WatchLiveFragment.getInstance());
                                        break;

                                    case 2:
                                        switchFragment(LiveFragment.getInstance());
                                        break;
                                }

                                return false;
                            }
                        })
                        .build();


    }

    private void switchFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder, fragment)
                .commit();
    }

}
