package org.belichenko.a.weathertoday.search_structure;

import java.util.ArrayList;

/**
 *
 */
public class SearchResult {
    public ArrayList<AreaName> areaName;
    public ArrayList<Country> country;
    public String latitude;
    public String longitude;

    public String getCity() {
        String result = "";
        if (areaName != null) {
            if (areaName.size() > 0) {
                result = areaName.get(0).value;
            }
        }
        return result;
    }

    public String getCountry() {
        String result = "";
        if (country != null) {
            if (country.size() > 0) {
                result = country.get(0).value;
            }
        }
        return result;
    }
}
