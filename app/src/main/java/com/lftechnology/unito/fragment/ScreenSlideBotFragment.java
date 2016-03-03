package com.lftechnology.unito.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lftechnology.unito.R;
import com.lftechnology.unito.bus.ConvertedValue;
import com.squareup.otto.Subscribe;

/**
 * Created by Grishma Shrestha <grishmashrestha@lftechnology.com> on 2/26/16.
 */
public class ScreenSlideBotFragment extends BaseFragment {

    private static final String POSITION = "position";
    private static final String DATASET = "dataset";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String[] mDataset;
    private ViewGroup mView;

    public ScreenSlideBotFragment() {
        // Required empty public constructor
    }

    @Subscribe
    public void answerAvailable(ConvertedValue val) {
        convertToCurrentUnit(val);
    }

    public static ScreenSlideBotFragment newInstance(int position, String[] dataset, String fragmentName) {
        ScreenSlideBotFragment fragment = new ScreenSlideBotFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putStringArray(DATASET, dataset);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(POSITION);
            mDataset = getArguments().getStringArray(DATASET);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (ViewGroup) inflater.inflate(R.layout.fragment_slider_bot, container, false);
        TextView tv = (TextView) mView.findViewById(R.id.scroll_bot);
        tv.setText(mDataset[mPosition]);
        return mView;
    }

    public void convertToCurrentUnit(ConvertedValue val) {
        double currentValue = changeToDouble(val.value);
        TextView tv = (TextView) mView.findViewById(R.id.to_unit);
        tv.setText(String.valueOf(currentValue));
    }

    public Double changeToDouble(String val) {
        Double returnValue;
        try {
            returnValue = Double.parseDouble(val);
        } catch (NumberFormatException e) {
            returnValue = 0.0;
        }
        return returnValue;
    }

}