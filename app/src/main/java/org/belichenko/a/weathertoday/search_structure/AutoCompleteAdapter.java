package org.belichenko.a.weathertoday.search_structure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;

import java.util.ArrayList;

/**
 *
 */
public class AutoCompleteAdapter<T extends SearchResult> extends ArrayAdapter
        implements MyConstants {

    public static final String TAG = "updateCityFromSite()";
    private final Context mContext;
    private ArrayList<SearchResult> mResults;

    public AutoCompleteAdapter(Context context, int resource, ArrayList<SearchResult> objects) {
        super(context, resource, objects);
        mContext = context;
        mResults = objects;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public SearchResult getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
        }
        SearchResult searchResult = getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(searchResult.getCity());
        ((TextView) convertView.findViewById(R.id.text2)).setText(searchResult.getCountry());

        return convertView;
    }
}
