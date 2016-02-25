package org.belichenko.a.weathertoday.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MainActivity;
import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.data_structure.Astronomy;
import org.belichenko.a.weathertoday.data_structure.Hourly;
import org.belichenko.a.weathertoday.data_structure.Weather;
import org.belichenko.a.weathertoday.data_structure.WeatherDesc;
import org.belichenko.a.weathertoday.data_structure.WeatherIconUrl;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class TodayFragment extends Fragment implements MyConstants{

    @Bind(R.id.imageView_today)
    ImageView image;
    @Bind(R.id.date_today_id)
    TextView date;
    @Bind(R.id.overall_today_id)
    TextView overall;
    @Bind(R.id.air_temperature_today_id)
    TextView air_temp;
    @Bind(R.id.feels_like_id)
    TextView feels_like;
    @Bind(R.id.atmo_press_id)
    TextView atmo_pressure;
    @Bind(R.id.susns_id)
    TextView sun_rise;
    @Bind(R.id.moons_id)
    TextView moon_rise;
    @Bind(R.id.wind_speed_id)
    TextView wind_speed;

    private ArrayList<Weather> wr;
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
        ButterKnife.bind(this, rootView);
        updateFragment();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    void updateFragment() {

        MainActivity md = (MainActivity) getActivity();
        if (md == null || md.mainData == null || md.mainData.data == null) {
            return;
        }

        wr = md.mainData.data.weather;
        if (wr == null) {
            return;
        }
        Weather currentWeather = wr.get(0);
        if (currentWeather == null) {
            return;
        }

        date.setText(currentWeather.date);

        ArrayList<Hourly> hr = currentWeather.hourly;
        Hourly currentWeatherHr = hr.get(0);
        if (currentWeatherHr == null) {
            return;
        }
        ArrayList<WeatherDesc> lstDesc = currentWeatherHr.weatherDesc;
        if (lstDesc != null) {
            WeatherDesc wdd = lstDesc.get(0);
            overall.setText(wdd.value);
        }
        ArrayList<WeatherIconUrl> lstDescImage = currentWeatherHr.weatherIconUrl;
        if (lstDescImage != null) {
            WeatherIconUrl wiu = lstDescImage.get(0);
            Picasso.with(App.getAppContext()).load(wiu.value).into(image);
        }

        atmo_pressure.setText(currentWeatherHr.pressure);
        wind_speed.setText(currentWeatherHr.windspeedKmph);

        ArrayList<Astronomy> as = currentWeather.astronomy;
        Astronomy astro = as.get(0);
        if (astro != null) {
            sun_rise.setText(astro.sunrise);
            moon_rise.setText(astro.moonrise);
        }

        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        String nameTemp = mPrefs.getString(STORED_TEMP, "C°");

        if (nameTemp.equals("C°")) {
            air_temp.setText(currentWeatherHr.tempC + " " + nameTemp);
            feels_like.setText(currentWeatherHr.FeelsLikeC + " " + nameTemp);
        } else {
            air_temp.setText(currentWeatherHr.tempF + " " + nameTemp);
            feels_like.setText(currentWeatherHr.FeelsLikeF + " " + nameTemp);
        }
    }

}