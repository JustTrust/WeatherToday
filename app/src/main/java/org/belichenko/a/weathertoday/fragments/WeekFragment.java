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
public class WeekFragment extends Fragment{

    private static WeekFragment fragment = new WeekFragment();

    public WeekFragment() {
    }

    public static WeekFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("WeekFragment");
        return rootView;
    }
}
