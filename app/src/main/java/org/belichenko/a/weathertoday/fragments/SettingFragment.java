package org.belichenko.a.weathertoday.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.Retrofit;
import org.belichenko.a.weathertoday.search_structure.AutoCompleteAdapter;
import org.belichenko.a.weathertoday.search_structure.MainSearch;
import org.belichenko.a.weathertoday.search_structure.SearchResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 */
public class SettingFragment extends Fragment implements MyConstants {

    @Bind(R.id.city_name)
    AutoCompleteTextView cityName;
    @Bind(R.id.search_progress_bar)
    ProgressBar search_progress_bar;
    @Bind(R.id.days_display)
    SeekBar daysDisplay;
    @Bind(R.id.rg_language)
    RadioGroup rgLanguage;
    @Bind(R.id.rg_temperath)
    RadioGroup rgTemperath;
    @Bind(R.id.image_number)
    ImageView imageNumber;
    @Bind(R.id.show_notif_checkBox)
    CheckBox showNotifCheckBox;

    private Handler handler;
    private AutoCompleteAdapter adapter;
    private ArrayList<SearchResult> searchResult;
    private static final String TAG = "SettingFragment ";
    private static SettingFragment fragment = new SettingFragment();

    public SettingFragment() {
    }

    public static SettingFragment getInstance() {
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, rootView);
        readSettingsFromPreferences();
        //sets listeners
        setListeners();
        setHandler();
        searchResult = new ArrayList<>();
        adapter = new AutoCompleteAdapter(App.getAppContext()
                , R.layout.simple_dropdown_item_2line
                , searchResult);
        cityName.setAdapter(adapter);
        return rootView;
    }

    private void setHandler() {
        handler = new msgHandler(this);
    }

    private void setListeners() {

        showNotifCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putBoolean(STORED_SHOW_NOTIF, showNotifCheckBox.isChecked());
                edit.apply();
            }
        });

        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                RadioButton rb = (RadioButton) rgLanguage.findViewById(checkedId);
                if (rb != null) {
                    edit.putString(STORED_LANG, rb.getText().toString());
                    edit.apply();
                }
            }
        });

        rgTemperath.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                RadioButton rb = (RadioButton) rgTemperath.findViewById(checkedId);
                if (rb != null) {
                    edit.putString(STORED_TEMP, rb.getText().toString());
                    edit.apply();
                }
            }
        });

        daysDisplay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Resources resources = App.getAppContext().getResources();
                final int resourceId = resources.getIdentifier("numeric_" + String.valueOf(progress + ONE_DAY)
                        , "drawable"
                        , App.getAppContext().getPackageName());
                imageNumber.setImageResource(resourceId);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putInt(STORED_DAYS, daysDisplay.getProgress() + ONE_DAY);
                edit.apply();
            }
        });

        cityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    search_progress_bar.setVisibility(View.VISIBLE);
                    handler.removeMessages(MESSAGE_TEXT_CHANGED);
                    handler.sendMessageDelayed(handler
                            .obtainMessage(MESSAGE_TEXT_CHANGED, s.toString()), DELAY_FOR_SEARCH);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString(STORED_CITY, cityName.getText().toString());
                edit.apply();
            }
        });

        cityName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchResult result = (SearchResult) parent.getItemAtPosition(position);
                if (result != null) {
                    cityName.setText(result.getCity());
                    cityName.append(", ");
                    cityName.append(result.getCountry());
                    writeCityToPreference(result);
                }
            }
        });

    }

    private void writeCityToPreference(SearchResult result) {
        if (result == null) {
            return;
        }
        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(STORED_CITY, result.getCity());
        edit.putString(STORED_COUNTRY, result.getCountry());
        edit.putString(STORED_LATITUDE, result.latitude);
        edit.putString(STORED_LONGITUDE, result.longitude);
        edit.apply();
    }

    private void readSettingsFromPreferences() {
        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);

        cityName.setText(mPrefs.getString(STORED_CITY, "Kharkiv"));

        int daysFromStorage = mPrefs.getInt(STORED_DAYS, 4);
        daysDisplay.setProgress(daysFromStorage - ONE_DAY);
        Resources resources = App.getAppContext().getResources();
        final int resourceId = resources.getIdentifier("numeric_" + String.valueOf(daysFromStorage)
                , "drawable"
                , App.getAppContext().getPackageName());
        imageNumber.setImageResource(resourceId);

        String nameLang = mPrefs.getString(STORED_LANG, "radioButton_ru");
        RadioButton rbLang;
        if (nameLang.equals("ru")) {
            rbLang = (RadioButton) rgLanguage.getChildAt(0);
        } else {
            rbLang = (RadioButton) rgLanguage.getChildAt(1);
        }
        if (rbLang != null) {
            rbLang.setChecked(true);
        }

        String nameTemp = mPrefs.getString(STORED_TEMP, "C°");
        RadioButton rbTemp;
        if (nameTemp.equals("C°")) {
            rbTemp = (RadioButton) rgTemperath.getChildAt(0);
        } else {
            rbTemp = (RadioButton) rgTemperath.getChildAt(1);
        }
        if (rbTemp != null) {
            rbTemp.setChecked(true);
        }
        showNotifCheckBox.setChecked(mPrefs.getBoolean(STORED_SHOW_NOTIF, true));
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
                searchResult.clear();
                if (ms == null || ms.search_api == null || ms.search_api.result == null) {
                    adapter.notifyDataSetInvalidated();
                    return;
                }
                searchResult.clear();
                searchResult.addAll(ms.search_api.result);
                if (searchResult.size() > 0) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyDataSetInvalidated();
                }
                search_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                search_progress_bar.setVisibility(View.GONE);
                Log.d(TAG, "failure() called with: " + "error = [" + error + "]");
            }
        });
    }

    /**
     * Receive msg from Handler
     *
     * @param msg
     */
    private void handleMessage(Message msg) {
        String cityName = (String) msg.obj;
        if (cityName.length() > 3) {
            updateDataFromSite(cityName);
        }
    }

    static class msgHandler extends Handler {
        private final WeakReference<SettingFragment> mService;

        msgHandler(SettingFragment service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SettingFragment service = mService.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

}