package com.hcmut.admin.utrafficsystem.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.hcmut.admin.utrafficsystem.business.UserLocation;
import com.hcmut.admin.utrafficsystem.model.User;
import com.google.gson.Gson;

public class SharedPrefUtils {
    private static final String PREFS_KEY = "Prefs";
    private static final String USER = "USER";
    private static final String RATING = "RATING";
    private static final String NOTI_TOKEN = "NOTI_TOKEN";
    private static final String LATEST_LAT = "LATEST_LAT";
    private static final String LATEST_LNG = "LATEST_LNG";

    private static String notyficationToken;

    static public void saveUser(Context context, User user) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        pref.edit().putString(USER, gson.toJson(user)).apply();
    }

    static public User getUser(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(USER, null), User.class);
    }

    static public void saveRatingMode(Context context, boolean isCheck) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        pref.edit().putBoolean(RATING, isCheck).apply();
    }

    static public boolean getRatingMode(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(RATING, false);
    }

    static public void saveNotiToken(Context context, String token) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        pref.edit().putString(NOTI_TOKEN, token).apply();
        notyficationToken = token;
    }

    static public String getNotiToken(Context context) {
        if (notyficationToken == null) {
            SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            notyficationToken = pref.getString(NOTI_TOKEN, null);
        }
        return notyficationToken;
    }

    static public void saveLatestLocation(Context context, UserLocation userLocation) {
        if (userLocation == null) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(LATEST_LAT, String.valueOf(userLocation.getLatitude()));
        editor.putString(LATEST_LNG, String.valueOf(userLocation.getLongitude()));
        editor.apply();
    }

    static public UserLocation getLatestLocation(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        String lat = pref.getString(LATEST_LAT, null);
        String lng = pref.getString(LATEST_LNG, null);
        if (lat == null || lng == null) {
            return null;
        }
        return new UserLocation(Double.parseDouble(lat), Double.parseDouble(lng));
    }
}
