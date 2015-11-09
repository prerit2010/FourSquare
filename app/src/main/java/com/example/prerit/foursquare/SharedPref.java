package com.example.prerit.foursquare;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Prerit on 09-11-2015.
 */
public class SharedPref {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    public static final String LATITUDE = null;
    public static final String LONGITUDE = null;
    private static final String PREF_NAME = "foursquare";
    int PRIVATE_MODE = 0;

    public SharedPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void save_location(String latitude, String longitude) {
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }

    public HashMap<String, String> get_saved_location() {
        HashMap<String, String> loc = new HashMap<String, String>();
        loc.put(LATITUDE, pref.getString(LATITUDE, null));
        loc.put(LONGITUDE, pref.getString(LONGITUDE, null));
        return loc;
    }

}
