package org.belichenko.a.weathertoday.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MainActivity;
import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.data_structure.Hourly;
import org.belichenko.a.weathertoday.data_structure.Weather;
import org.belichenko.a.weathertoday.data_structure.WeatherDesc;
import org.belichenko.a.weathertoday.data_structure.WeatherIconUrl;

import java.util.ArrayList;

/**
 *
 */
public class WeekFragment extends Fragment {

    private static WeekFragment fragment = new WeekFragment();
    public MainActivity md;
    MyAdapter myAdapter;
    public static ArrayList<Weather> wr;

    public WeekFragment() {
    }

    public void updateList() {
        md = (MainActivity) getActivity();
        if (md != null && md.mainData != null && md.mainData.data != null) {
            wr = md.mainData.data.weather;
        }
        myAdapter.notifyDataSetChanged();
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

        ListView list = (ListView) rootView.findViewById(R.id.listView);
        myAdapter = new MyAdapter(App.getAppContext(), wr);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return rootView;
    }

    class MyAdapter extends ArrayAdapter<Weather> implements MyConstants{

        public MyAdapter(Context context, ArrayList<Weather> object) {
            super(context, R.layout.list_item, object);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) App.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();

                holder.imageItem = (ImageView) rowView.findViewById(R.id.iconW);
                holder.textTemperat = (TextView) rowView.findViewById(R.id.text_temperatW);
                holder.textTemperatSence = (TextView) rowView.findViewById(R.id.text_temperat_senceW);
                holder.textAtmospherePressure = (TextView) rowView.findViewById(R.id.text_atmosphere_pressureW);
                holder.Data = (TextView) rowView.findViewById(R.id.text_dataW);
                holder.Opis = (TextView) rowView.findViewById(R.id.text_informationW);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }
            ArrayList<Hourly> hourly = getItem(position).hourly;
            SharedPreferences mPrefs = App.getAppContext().getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
            if (hourly != null) {

                Hourly carrenthourly = hourly.get(0);
                ArrayList<WeatherDesc> weatherDesc = carrenthourly.weatherDesc;
                ArrayList<WeatherIconUrl> lstDescImage = carrenthourly.weatherIconUrl;
                if (lstDescImage != null) {
                    WeatherIconUrl wiu = lstDescImage.get(0);
                    Picasso.with(App.getAppContext()).load(wiu.value).into(holder.imageItem);
                }
                String nameTemp = mPrefs.getString(STORED_TEMP, "C°");
                if (nameTemp.equals("C°")) {
                    holder.textTemperat.setText(carrenthourly.tempC+" C");
                    holder.textTemperatSence.setText(carrenthourly.FeelsLikeC+" C");
                } else {
                    holder.textTemperat.setText(carrenthourly.tempF+" F");
                    holder.textTemperatSence.setText(carrenthourly.FeelsLikeF+" F");
                }

                holder.textAtmospherePressure.setText(carrenthourly.pressure);
                holder.Data.setText(getItem(position).date);
                if (weatherDesc != null) {
                    WeatherDesc weatherDesc1 = weatherDesc.get(0);
                    holder.Opis.setText(weatherDesc1.value);
                } else {
                    holder.Opis.setText("No detail information");
                }

            } else {
                holder.Data.setText("No weather data");
            }
            return rowView;
        }

        class ViewHolder {
            public TextView Data;
            public TextView Opis;
            public ImageView imageItem;
            public TextView textTemperat;
            public TextView textTemperatSence;
            public TextView textAtmospherePressure;
        }

    }
}