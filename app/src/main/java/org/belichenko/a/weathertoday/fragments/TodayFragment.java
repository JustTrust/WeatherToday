package org.belichenko.a.weathertoday.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.belichenko.a.weathertoday.R;

/**
 *
 */
public class TodayFragment extends Fragment {

    private static TodayFragment fragment = new TodayFragment();

    public TodayFragment() {
    }


    public static TodayFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("TodayFragment");
        return rootView;
    }
}
