package org.belichenko.a.weathertoday.data_structure;

import java.util.ArrayList;

/**
 * Weather on one day
 */
public class Weather {
    public ArrayList<Astronomy> astronomy;
    public ArrayList<Hourly> hourly;
    public String date;
    public String maxtemp;
    public String maxtempF;
    public String mintempC;
    public String mintempF;
    public String uvIndex;
}
