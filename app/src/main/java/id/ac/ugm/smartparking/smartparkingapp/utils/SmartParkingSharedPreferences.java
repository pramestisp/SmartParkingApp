package id.ac.ugm.smartparking.smartparkingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shindy on 15-Apr-18.
 */

public class SmartParkingSharedPreferences {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "id.ac.ugm.smartparking.smartparkingapp";
    public static final String PREF_TOKEN = "token";
    public static final String PREF_USER_NAME = "name";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_LOGGED = "logged";

    public SmartParkingSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor clear() {
        sharedPreferences.edit().clear().apply();
        return null;
    }

    public void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setBoolean(String key, Boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
