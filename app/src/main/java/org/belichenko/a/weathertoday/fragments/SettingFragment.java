package org.belichenko.a.weathertoday.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import org.belichenko.a.weathertoday.App;
import org.belichenko.a.weathertoday.MyConstants;
import org.belichenko.a.weathertoday.R;
import org.belichenko.a.weathertoday.search_structure.AutoCompleteAdapter;
import org.belichenko.a.weathertoday.search_structure.SearchResult;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class SettingFragment extends Fragment implements MyConstants {

    @Bind(R.id.city_name)
    AutoCompleteTextView cityName;
    @Bind(R.id.days_display)
    SeekBar daysDisplay;
    @Bind(R.id.rg_language)
    RadioGroup rgLanguage;
    @Bind(R.id.rg_temperath)
    RadioGroup rgTemperath;
    @Bind(R.id.image_number)
    ImageView imageNumber;

    View rootView;
    private AutoCompleteAdapter adapter;
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
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, rootView);
        //sets listeners
        setListeners();
        adapter = new AutoCompleteAdapter(App.getAppContext()
                , R.layout.simple_dropdown_item_2line
                , new ArrayList<SearchResult>());
        cityName.setAdapter(adapter);
        cityName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchResult result = (SearchResult) parent.getItemAtPosition(position);
                if (result != null) {
                    cityName.setText(result.getCity());
                    writeCityToPreference(result);
                }
            }
        });
        readSettingsFromPreferences();
        return rootView;
    }

    private void setListeners() {

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

                imageNumber.setImageDrawable(resources.getDrawable(resourceId));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putInt(STORED_DAYS, daysDisplay.getProgress());
                edit.apply();
            }
        });

        cityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
    }

    private void writeCityToPreference(SearchResult result) {
        if (result == null) {
            return;
        }
        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(STORED_CITY, result.getCity());
        edit.putString(STORED_LATITUDE, result.latitude);
        edit.putString(STORED_LONGITUDE, result.longitude);
        edit.apply();
    }

    private void readSettingsFromPreferences() {
        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        cityName.setText(mPrefs.getString(STORED_CITY, "Kharkiv"));
        daysDisplay.setProgress(mPrefs.getInt(STORED_DAYS, 4)+ ONE_DAY);

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
    }

}