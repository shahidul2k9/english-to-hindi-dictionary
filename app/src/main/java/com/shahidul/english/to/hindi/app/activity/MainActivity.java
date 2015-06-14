package com.shahidul.english.to.hindi.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shahidul.english.to.hindi.app.R;
import com.shahidul.english.to.hindi.app.adapter.NavigationDrawerListAdapter;
import com.shahidul.english.to.hindi.app.config.Configuration;
import com.shahidul.english.to.hindi.app.fragment.AddNewWordFragment;
import com.shahidul.english.to.hindi.app.fragment.FavoriteListFragment;
import com.shahidul.english.to.hindi.app.fragment.HomeFragment;
import com.shahidul.english.to.hindi.app.model.NavigationDrawerItem;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final String MARKET_DETAILS = "market://details?id=";
    private static final int FONT_SIZE_SMALL = 0;
    private static final int FONT_SIZE_MEDIUM = 1;
    private static final int FONT_SIZE_LARGE = 2;
    private static final String FONT_SIZE = "font_size";
    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.toolbar)
    Toolbar toolBar;
    @InjectView(R.id.ad_view)
    AdView mAdView;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] navigationMenuTitles;
    private TypedArray navigationMenuIcons;

    private ArrayList<NavigationDrawerItem> navigationDrawerItems;
    private NavigationDrawerListAdapter adapter;

    private Fragment homeFragment;
    private Fragment favoriteListFragment;
    private Fragment addNewWordFragment;
    private int selectedNavigationItemPosition = 0;
    private InterstitialAd mInterstitialAd;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setTheme();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.english_to_hindi_add_unit_interstitial));
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        navigationMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        navigationMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navigationDrawerItems = new ArrayList<NavigationDrawerItem>();

        navigationDrawerItems.add(new NavigationDrawerItem(navigationMenuTitles[0], navigationMenuIcons.getResourceId(0, -1)));
        navigationDrawerItems.add(new NavigationDrawerItem(navigationMenuTitles[1], navigationMenuIcons.getResourceId(1, -1)));
        navigationDrawerItems.add(new NavigationDrawerItem(navigationMenuTitles[2], navigationMenuIcons.getResourceId(2, -1)));

        navigationMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new NavigationDrawerListAdapter(getApplicationContext(),
                navigationDrawerItems);
        mDrawerList.setAdapter(adapter);



        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolBar,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.app_name);
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(navigationMenuTitles[selectedNavigationItemPosition]);
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            Handler handler = new Handler();
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialAd();
                    }
                });
                Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
            }
        }, Configuration.INTERSTITIAL_ADD_INTERVAL_IN_MILLI_SECOND, Configuration.INTERSTITIAL_ADD_INTERVAL_IN_MILLI_SECOND);
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());

    }

    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        mDrawerToggle.syncState();
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_font_size:
                AlertDialog fontSizeDialog = new AlertDialog.Builder(this)
                        .setTitle("Choose font size")
                        .setSingleChoiceItems(R.array.font_size, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        saveFontSize(FONT_SIZE_SMALL);
                                        break;
                                    case 1:
                                        saveFontSize(FONT_SIZE_MEDIUM);
                                        break;
                                    case 2:
                                        saveFontSize(FONT_SIZE_LARGE);
                                        break;
                                    default:
                                        break;
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                                    }
                                },200);
                            }

                        })
                        .create();
                fontSizeDialog.show();
                break;
            case R.id.action_rate_me:
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(MARKET_DETAILS + getPackageName()));
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    public void displayView(int position) {
        getSupportActionBar().setTitle(navigationMenuTitles[position]);
        Fragment fragment = null;
        switch (position) {
            case 0:
                if ( homeFragment == null ){
                    homeFragment = new HomeFragment();
                }
                fragment = homeFragment;
                break;
            case 1:
                if ( favoriteListFragment == null ){
                    favoriteListFragment = new FavoriteListFragment();
                }
                fragment = favoriteListFragment;
                break;
            case 2:
                if ( addNewWordFragment == null ){
                    addNewWordFragment = new AddNewWordFragment();
                }
                fragment = addNewWordFragment;
                break;
            default:
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit();

        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
        selectedNavigationItemPosition = position;

    }
    private void setTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int fontSize = sharedPreferences.getInt(FONT_SIZE, FONT_SIZE_MEDIUM);
        switch (fontSize){
            case FONT_SIZE_SMALL:
                setTheme(R.style.font_small);
                break;
            case FONT_SIZE_MEDIUM:
                setTheme(R.style.font_medium);
                break;
            case FONT_SIZE_LARGE:
                setTheme(R.style.font_large);
                break;
            default:
                break;
        }
    }

    private void saveFontSize(int fontSize){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FONT_SIZE,fontSize);
        editor.apply();
    }
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            displayView(position);
        }
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
    private void showInterstitialAd() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

}
