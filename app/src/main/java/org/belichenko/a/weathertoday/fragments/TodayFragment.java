package org.belichenko.a.weathertoday.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.belichenko.a.weathertoday.MainActivity;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.data_structure.Astronomy;
import org.belichenko.a.weathertoday.data_structure.Hourly;
import org.belichenko.a.weathertoday.data_structure.Weather;

import java.util.ArrayList;

/**
 *
 */
public class TodayFragment extends Fragment {

    ArrayList<Weather> wr;

    TextView date;
    TextView overall;
    TextView air_temp;
    TextView feels_like_C;
    TextView atmo_pressure;
    TextView sun_rise;
    TextView sun_set;
    TextView moon_rise;
    TextView moon_set;
    TextView wind_speed;
    TextView chanceoffog;
    TextView chanceoffrost;
    TextView chanceofhightemp;
    TextView chanceofovercast;
    TextView chanceofrain;
    TextView chanceofremdry;
    TextView chanceofsnow;
    TextView chanceofsunshine;
    TextView chanceofthunder;
    TextView chanceofwindy;
    ImageView image;

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

        date = (TextView) rootView.findViewById(R.id.date_today_id);
        overall = (TextView) rootView.findViewById(R.id.overall_today_id);
        air_temp = (TextView) rootView.findViewById(R.id.air_temperature_today_id);
        feels_like_C = (TextView) rootView.findViewById(R.id.feels_like_id);
        atmo_pressure = (TextView) rootView.findViewById(R.id.atmo_press_id);
        sun_rise = (TextView) rootView.findViewById(R.id.susns_id);
//        sun_set = (TextView)rootView.findViewById(R.id.susns_id);
        moon_rise = (TextView) rootView.findViewById(R.id.moons_id);
//        moon_set = (TextView)rootView.findViewById(R.id.moons_id);
        wind_speed = (TextView) rootView.findViewById(R.id.wind_speed_id);
        image = (ImageView) rootView.findViewById(R.id.imageView_today);

        updateFragment();
        return rootView;
    }

    void updateFragment() {
        MainActivity md = (MainActivity) getActivity();

        wr = md.mainData.data.weather;

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

        overall.setText(currentWeatherHr.weatherDesc.toString());
        air_temp.setText(currentWeatherHr.tempC);
        feels_like_C.setText(currentWeatherHr.FeelsLikeC);
        atmo_pressure.setText(currentWeatherHr.pressure);
        wind_speed.setText(currentWeatherHr.windspeedKmph);

        ArrayList<Astronomy> as = currentWeather.astronomy;

        Astronomy astro = as.get(0);
        if (astro == null) {
            return;
        }
        sun_rise.setText(astro.sunrise);
        moon_rise.setText(astro.moonrise);
    }
}