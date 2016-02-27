package org.belichenko.a.weathertoday.fragments;


import android.view.View;

public interface OnItemClickWatcher<T> {

    void onItemClick(View v, int position, T item);

}
