package com.lftechnology.unito.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lftechnology.unito.utils.OnKeyEvents;
import com.lftechnology.unito.R;
import com.lftechnology.unito.adapter.DrawerRecyclerViewAdapter;
import com.lftechnology.unito.bus.EventBus;
import com.lftechnology.unito.bus.FlingListener;
import com.lftechnology.unito.bus.NavigationMenuChangeDetails;
import com.lftechnology.unito.bus.ScrollListener;
import com.lftechnology.unito.bus.SwapFragment;
import com.lftechnology.unito.constant.AppConstant;
import com.lftechnology.unito.fragment.MainFragment;
import com.lftechnology.unito.helper.OnStartDragListener;
import com.lftechnology.unito.helper.SimpleItemTouchHelperCallback;
import com.lftechnology.unito.utils.GeneralUtils;
import com.lftechnology.unito.utils.SoftKeyBoard;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnStartDragListener, OnKeyEvents {
    private Toolbar toolbar;
    private LinearLayout mToolbarContainer;
    private String mSelectedConversion;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private RecyclerView mRecyclerView;
    private DrawerRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mDrawerRecyclerViewDataset;
    private ItemTouchHelper mItemTouchHelper;
    private LinearLayout linearLayout;
    private int DY = 5;

    @Override
    public void onResume() {
        super.onResume();
        EventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbarContainer = (LinearLayout) findViewById(R.id.toolbarContainer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSpinner();
        setNavigationDrawer();
    }

    private void setRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDrawerRecyclerViewDataset = getDrawerRecyclerViewDataset();
        mAdapter = new DrawerRecyclerViewAdapter(mDrawerRecyclerViewDataset, this, mSelectedConversion);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        linearLayout = (LinearLayout) findViewById(R.id.inflated_content_main);
    }

    private void setNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private String[] getDrawerRecyclerViewDataset() {
        String[] drawerRecyclerViewDataset;
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("Unito", Context.MODE_PRIVATE);
        String preference = sharedPref.getString(mSelectedConversion, "N/A");
        if (preference.equals("N/A")) {
            switch (mSelectedConversion) {
                case AppConstant.LENGTH:
                    drawerRecyclerViewDataset = getResources().getStringArray(R.array.length_options);
                    break;
                case AppConstant.TEMPERATURE:
                    drawerRecyclerViewDataset = getResources().getStringArray(R.array.temperature_options);
                    break;
                case AppConstant.TIME:
                    drawerRecyclerViewDataset = getResources().getStringArray(R.array.time_options);
                    break;
                case AppConstant.VOLUME:
                    drawerRecyclerViewDataset = getResources().getStringArray(R.array.volume_options);
                    break;
                case AppConstant.WEIGHT:
                    drawerRecyclerViewDataset = getResources().getStringArray(R.array.weight_options);
                    break;
                default:
                    drawerRecyclerViewDataset = new String[]{};
                    break;
            }
        } else {
            Gson gson = new Gson();
            String[] arrayList = gson.fromJson(preference, String[].class);
            drawerRecyclerViewDataset = arrayList;
        }
        return drawerRecyclerViewDataset;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (!Arrays.equals(getDrawerRecyclerViewDataset(), mDrawerRecyclerViewDataset)) {
                    EventBus.post(new NavigationMenuChangeDetails(mSelectedConversion));
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.unito_option_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unito_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftKeyBoard.hideSoftKeyboard(v.getContext(), v);
                return false;
            }
        });
        spinner.setOnItemSelectedListener(this);
    }

    private void setHeaderTextByFragment() {
        TextView tv = ((TextView) findViewById(R.id.nav_header));
        tv.setText(mSelectedConversion);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        spinner.setSelection(position);
        mSelectedConversion = spinner.getSelectedItem().toString();
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.inflated_content_main, MainFragment.newInstance(mSelectedConversion));
        fragmentTransaction.commit();

        setHeaderTextByFragment();
        setRecyclerView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void swapFragments(View view) {
        EventBus.post(new SwapFragment(true));
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Subscribe
    public void toggleToolBar(ScrollListener scrollListener) {
        int dy;
        if (scrollListener.moveUp) {
            if (GeneralUtils.convertPixelsToDp(mToolbarContainer.getTranslationY(), this) > -56.0) {
                float diff = mToolbarContainer.getTranslationY() - DY;
                if ((GeneralUtils.convertPixelsToDp(diff, this) < -56.0)) {
                    dy = Math.round(diff + GeneralUtils.convertDpToPixel(56, this));
                    diff = mToolbarContainer.getTranslationY() - dy;
                } else {
                    dy = DY;
                }
                mToolbarContainer.setTranslationY(diff);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(linearLayout.getWidth(), linearLayout.getHeight() + dy);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setTranslationY(linearLayout.getY() - dy);
            }
        } else {
            if (mToolbarContainer.getTranslationY() < 0.0) {
                float diff = mToolbarContainer.getTranslationY() + DY;
                if ((diff) > 0.0) {
                    dy = Math.round(diff);
                    diff = mToolbarContainer.getTranslationY() + dy;
                } else {
                    dy = DY;
                }
                mToolbarContainer.setTranslationY(diff);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(linearLayout.getWidth(), linearLayout.getHeight() - dy);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setTranslationY(linearLayout.getY() + dy);
            }
        }
    }

    @Subscribe
    public void hideOrShowToolbarAfterScrollComplete(FlingListener flingListener) {
        int dy;
        float yTranslationInDP = GeneralUtils.convertPixelsToDp(mToolbarContainer.getTranslationY(), this);

        if (flingListener.flingedUp && (yTranslationInDP > -56) && (yTranslationInDP < 0)) {
            float diff = Math.round(GeneralUtils.convertDpToPixel(-56, this));
            mToolbarContainer.setTranslationY(diff);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(linearLayout.getWidth(), (int) (linearLayout.getHeight() + linearLayout.getY()));
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setTranslationY(0);
        } else if (!flingListener.flingedUp && (yTranslationInDP < 56.0) && (GeneralUtils.convertPixelsToDp(linearLayout.getY(), this) < 56)) {
            float diff = 0;
            dy = Math.round(GeneralUtils.convertDpToPixel(56, this));
            mToolbarContainer.setTranslationY(diff);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(linearLayout.getWidth(), (int) (linearLayout.getHeight() - diff));
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setTranslationY(dy);
        }
    }

    @Override
    public void keyboardHidden() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams);
    }
}


