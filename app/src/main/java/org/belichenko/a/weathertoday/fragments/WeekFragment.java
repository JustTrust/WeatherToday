package org.belichenko.a.weathertoday.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MainActivity;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.data_structure.Weather;

import java.util.ArrayList;

/**
 *
 */
public class WeekFragment extends Fragment{

    private static WeekFragment fragment = new WeekFragment();
    public MainActivity md;
    RecycleWeekAdapter myAdapter;
    public static ArrayList<Weather> wr;

    public WeekFragment() {
    }

    public static WeekFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_week, container, false);
        md = (MainActivity) getActivity();
        if (md != null && md.mainData != null && md.mainData.data != null) {
            wr = md.mainData.data.weather;
        }

        RecyclerView list = (RecyclerView) rootView.findViewById(R.id.listView);
        list.addItemDecoration(new DividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(App.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        myAdapter = new RecycleWeekAdapter(wr, App.getAppContext(), (MainActivity) getActivity());
        list.setAdapter(myAdapter);
        return rootView;
    }

}