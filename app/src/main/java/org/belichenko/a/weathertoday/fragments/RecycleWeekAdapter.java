package org.belichenko.a.weathertoday.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.data_structure.Hourly;
import org.belichenko.a.weathertoday.data_structure.Weather;
import org.belichenko.a.weathertoday.data_structure.WeatherDesc;
import org.belichenko.a.weathertoday.data_structure.WeatherIconUrl;

import java.util.ArrayList;
import java.util.List;

public class RecycleWeekAdapter extends RecyclerView.Adapter<RecycleWeekAdapter.ViewHolder>
        implements MyConstants {

    private OnItemClickWatcher<Weather> watcher;
    private List<Weather> listOfDays;
    private Context context;

    public RecycleWeekAdapter(List<Weather> objects, Context cont, OnItemClickWatcher<Weather> watcher) {
        this.listOfDays = objects;
        this.context = cont;
        this.watcher = watcher;
    }

    @Override
    public RecycleWeekAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, watcher, listOfDays);
    }

    @Override
    public void onBindViewHolder(RecycleWeekAdapter.ViewHolder holder, int position) {

        holder.currentData.setText(listOfDays.get(position).date);
        ArrayList<Hourly> hourly = listOfDays.get(position).hourly;
        SharedPreferences mPrefs = App.getAppContext().getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        if (hourly != null) {

            Hourly currentHourly = hourly.get(0);
            ArrayList<WeatherIconUrl> lstDescImage = currentHourly.weatherIconUrl;
            if (lstDescImage != null) {
                WeatherIconUrl wiu = lstDescImage.get(0);
                Picasso.with(App.getAppContext())
                        .load(wiu.value)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.imageItem);
            }
            String nameTemp = mPrefs.getString(STORED_TEMP, "C°");
            if (nameTemp.equals("C°")) {
                holder.temperature.setText(currentHourly.tempC + " " + nameTemp);
                holder.feelsLike.setText(currentHourly.FeelsLikeC + " " + nameTemp);
            } else {
                holder.temperature.setText(currentHourly.tempF + " " + nameTemp);
                holder.feelsLike.setText(currentHourly.FeelsLikeF + " " + nameTemp);
            }
            ArrayList<WeatherDesc> weatherDesc = currentHourly.weatherDesc;
            holder.pressure.setText(currentHourly.pressure + " "
                    + App.getAppContext().getString(R.string.pressure_description));
            holder.wind.setText(currentHourly.windspeedKmph + " "
                    + App.getAppContext().getString(R.string.wind_description));

            if (weatherDesc != null) {
                WeatherDesc detailDesc = weatherDesc.get(0);
                holder.summary.setText(detailDesc.value);
            } else {
                holder.summary.setText(App.getAppContext().getString(R.string.no_information));
            }

        } else {
            holder.currentData.setText(App.getAppContext().getString(R.string.no_weather_data));
        }
    }

    @Override
    public int getItemCount() {
        if (listOfDays == null) {
            return 0;
        } else {
            return listOfDays.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView currentData;
        public TextView summary;
        public ImageView imageItem;
        public TextView temperature;
        public TextView feelsLike;
        public TextView pressure;
        public TextView wind;

        public ViewHolder(View itemView, final OnItemClickWatcher<Weather> watcher, final List<Weather> list) {
            super(itemView);
            rootView = itemView;
            imageItem = (ImageView) rootView.findViewById(R.id.iconW);
            temperature = (TextView) rootView.findViewById(R.id.text_temperatW);
            feelsLike = (TextView) rootView.findViewById(R.id.text_temperat_senceW);
            pressure = (TextView) rootView.findViewById(R.id.atmo_press_id);
            currentData = (TextView) rootView.findViewById(R.id.text_dataW);
            summary = (TextView) rootView.findViewById(R.id.text_informationW);
            wind = (TextView) rootView.findViewById(R.id.wind_speed_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watcher.onItemClick(v, getAdapterPosition(), list.get(getAdapterPosition()));
                }
            });
        }
    }
}
