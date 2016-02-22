package org.belichenko.a.weathertoday.search_structure;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.Retrofit;

import java.util.LinkedHashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 */
public class AutoCompleteAdapter<T extends SearchResult> extends ArrayAdapter
        implements Filterable, MyConstants {

    private static final String MAX_RESULTS = "5";
    public static final String TAG = "updateCityFromSite()";
    private final Context mContext;
    private List<T> mResults;

    public AutoCompleteAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        mContext = context;
        mResults = objects;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public T getItem(int index) {
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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    updateDataFromSite(constraint.toString());
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            }
        };

        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private void updateDataFromSite(String city) {

        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        filter.put("q", city);
        filter.put("format", "json");
        filter.put("num_of_results", MAX_RESULTS);
        filter.put("key", API_KEY);

        Retrofit.getListOfCity(filter, new Callback<MainSearch>() {

            @Override
            public void success(MainSearch ms, Response response) {
                mResults.clear();
                if (ms == null) {
                    notifyDataSetInvalidated();
                    return;
                }
                if (ms.search_api == null) {
                    notifyDataSetInvalidated();
                    return;
                }
                if (ms.search_api.result == null) {
                    notifyDataSetInvalidated();
                    return;
                }
                mResults.clear();
                mResults.addAll((List<T>) ms.search_api.result);
                if (mResults.size() > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure() called with: " + "error = [" + error + "]");
            }
        });
    }

}
