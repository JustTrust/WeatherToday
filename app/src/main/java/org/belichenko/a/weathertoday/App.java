package org.belichenko.a.weathertoday;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * App class for getting context
 */
public class App extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}