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
    public static final String PREF_CAR_TYPE = "car_type";
    public static final String PREF_CAR_NO = "car_no";
    public static final String PREF_LOGGED = "logged";
    public static final String PREF_RESERVED = "reserved";
    public static final String PREF_ID = "reservation_id";
    public static final String PREF_TIME_FROM = "time_from";

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

    public void setInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void setLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }
}
