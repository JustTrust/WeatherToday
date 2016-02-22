package org.belichenko.a.weathertoday.data_structure;

import java.util.ArrayList;

/**
 * Weather on one day
 */
public class Weather {
    ArrayList<Astronomy> astronomy;
    ArrayList<Hourly> hourly;
    public String date;
    public String maxtemp;
    public String maxtempF;
    public String mintempC;
    public String mintempF;
    public String uvIndex;
}
