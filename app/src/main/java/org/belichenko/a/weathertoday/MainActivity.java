package org.belichenko.a.weathertoday;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.belichenko.a.weathertoday.data_structure.CurrentCondition;
import org.belichenko.a.weathertoday.data_structure.ErrorData;
import org.belichenko.a.weathertoday.data_structure.MainData;
import org.belichenko.a.weathertoday.data_structure.Weather;
import org.belichenko.a.weathertoday.data_structure.WeatherDesc;
import org.belichenko.a.weathertoday.data_structure.WeatherIconUrl;
import org.belichenko.a.weathertoday.fragments.OnItemClickWatcher;
import org.belichenko.a.weathertoday.fragments.SettingFragment;
import org.belichenko.a.weathertoday.fragments.TodayFragment;
import org.belichenko.a.weathertoday.fragments.WeekFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
        implements MyConstants, OnItemClickWatcher<Weather> {

    private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    // The {@link ViewPager} that will host the section contents.
    private ViewPager mViewPager;

    // Keeps all information about weather
    public MainData mainData;
    // Icons for tabs
    private int[] tabIcons = {
            R.drawable.ic_event_white_24dp,
            R.drawable.ic_date_range_white_24dp,
            R.drawable.ic_settings_applications_white_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Weather today");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons(tabLayout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataFromSite();
                Snackbar.make(view, "Weather update, please wait.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // if we open activity from notification don't update data
        Intent i= getIntent();
        if (i.getBooleanExtra(UPDATE_DATA, true)) {
            updateDataFromSite();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        if (mainData != null) {
            Gson gson = new Gson();
            String md = gson.toJson(mainData);
            edit.putString(STORED_MAIN_STRUCTURE, md);
            edit.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        String md = sharedPref.getString(STORED_MAIN_STRUCTURE, null);
        if (md != null) {
            Gson gson = new Gson();
            mainData = gson.fromJson(md, MainData.class);
        }
    }

    private void updateDataFromSite() {

        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        StringBuilder city = new StringBuilder(sharedPref.getString(STORED_CITY, "Kharkiv"));
        String country = sharedPref.getString(STORED_COUNTRY, "");
        if (!country.isEmpty()) {
            city.append(", ");
            city.append(country);
        }
        filter.put("q", city.toString());
        filter.put("format", "json");
        filter.put("num_of_days", String.valueOf(sharedPref.getInt(STORED_DAYS, 4)));
        filter.put("fx24", "yes"); // first item described all day
        filter.put("tp", "24"); // time period
        filter.put("lang", sharedPref.getString(STORED_LANG, "ru"));
        filter.put("key", API_KEY);

        Retrofit.getWeatherData(filter, new Callback<MainData>() {
            @Override
            public void success(MainData cd, Response response) {
                if (cd != null) {
                    if (cd.data.error == null) {
                        mainData = cd;
                        makeNotification();
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    } else if (cd.data.error.size() > 0) {
                        for (ErrorData errorData : cd.data.error) {
                            Toast.makeText(MainActivity.this, errorData.msg, Toast.LENGTH_LONG).show();
                        }
                        mainData = null;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, getString(R.string.update_error), Toast.LENGTH_LONG).show();
                Log.d(TAG, "failure() called with: " + "error = [" + error + "]");
            }
        });
    }

    public void makeNotification() {

        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        if (!mPrefs.getBoolean(STORED_SHOW_NOTIF, true)) {
            return; // don't show if user switch off notification
        }
        if (mainData == null || mainData.data == null) {
            return;
        }
        ArrayList<CurrentCondition> cc = mainData.data.current_condition;
        if (cc == null || cc.size() < 1) {
            return;
        }
        CurrentCondition currentCondition = cc.get(0);
        if (currentCondition == null) {
            return;
        }
        // get current icon
        ArrayList<WeatherIconUrl> wIcon = currentCondition.weatherIconUrl;
        if (wIcon == null || wIcon.size() < 1) {
            return;
        }
        WeatherIconUrl currentIcon = wIcon.get(0);
        if (currentIcon == null || currentIcon.value.isEmpty()) {
            return;
        }

        Bitmap iconPic = null;

        try {
            iconPic = new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    try {
                        Picasso.with(App.getAppContext())
                                .load(params[0])
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .get();
                        return null;
                    }catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(currentIcon.value).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // get current temp
        String nameTemp = mPrefs.getString(STORED_TEMP, "C°");
        String city = mPrefs.getString(STORED_CITY, "");
        String firstLine;
        String secondLine;
        if (nameTemp.equals("C°")) {
            firstLine = city + " " + currentCondition.temp_C + " " + nameTemp;
        } else {
            firstLine = city + " " + currentCondition.temp_F + " " + nameTemp;
        }
        // get current weather desc
        ArrayList<WeatherDesc> wDesc = currentCondition.weatherDesc;
        if (wDesc == null || wDesc.size() < 1) {
            return;
        }
        WeatherDesc currentDesc = wDesc.get(0);
        if (currentDesc == null || currentDesc.value.isEmpty()) {
            return;
        }
        secondLine = currentDesc.value + ", "
                + getString(R.string.wind_for_notif)
                + " "
                + currentCondition.windspeedKmph
                + " "
                + getString(R.string.wind_description);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(UPDATE_DATA, false);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                NOTIFY_ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(iconPic)
                .setAutoCancel(true)
                .setContentTitle(firstLine)
                .setContentText(secondLine)
                .setVibrate(new long[]{200, 400, 200});

        Notification n = builder.build();
        nm.notify(NOTIFY_ID, n);

    }

    private void setupTabIcons(TabLayout tabLayout) {
        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        } catch (NullPointerException e) {
            Log.d(TAG, "setupTabIcons() returned: " + e);
        }
    }

    @Override
    public void onItemClick(View v, int position, Weather item) {
        TodayFragment.getInstance().updateFragment(position);
        mViewPager.setCurrentItem(0);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TodayFragment.getInstance();
                case 1:
                    return WeekFragment.getInstance();
                case 2:
                    return SettingFragment.getInstance();
            }
            return null;
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.day_header);
                case 1:
                    return getString(R.string.week_header);
                case 2:
                    return getString(R.string.setting_header);
            }
            return null;
        }
    }
}
