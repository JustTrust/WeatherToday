package org.belichenko.a.weathertoday.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MainActivity;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.data_structure.Hourly;
import org.belichenko.a.weathertoday.data_structure.Weather;
import org.belichenko.a.weathertoday.data_structure.WeatherDesc;

import java.util.ArrayList;

/**
 *
 */
public class WeekFragment extends Fragment {

    private static WeekFragment fragment = new WeekFragment();
    public MainActivity md;
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
        wr = md.mainData.data.weather;
        ListView list = (ListView) rootView.findViewById(R.id.listView);
        list.setAdapter(new MyAdapter(App.getAppContext(), wr));
        return rootView;
    }


    class MyAdapter extends ArrayAdapter<Weather> {

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

            if (hourly != null) {

                Hourly carrenthourly = hourly.get(0);
                ArrayList<WeatherDesc> weatherDesc = carrenthourly.weatherDesc;
                holder.textTemperat.setText(carrenthourly.tempC);
                holder.textTemperatSence.setText(carrenthourly.FeelsLikeC);
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