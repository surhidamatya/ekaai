package com.lftechnology.unito;

import android.support.v4.app.Fragment;

import com.lftechnology.unito.bus.EventBus;

/**
 * Created by Grishma Shrestha <grishmashrestha@lftechnology.com> on 3/2/16.
 */
public class BaseFragment extends Fragment {
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
}
