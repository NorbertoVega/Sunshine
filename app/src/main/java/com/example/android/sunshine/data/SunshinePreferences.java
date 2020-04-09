package com.example.android.sunshine.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.android.sunshine.R;

public class SunshinePreferences {

    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LONG = "coord_long";


    static public void setLocationDetails(Context context,double lat, double lon) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LONG, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    static public void resetLocationCoordinates(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(PREF_COORD_LAT);
        editor.remove(PREF_COORD_LONG);
        editor.apply();
    }

    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredUnit = sp.getString(context.getString(R.string.pref_units_key), context.getString(R.string.pref_units_metric_value));
        return preferredUnit.equals(context.getString(R.string.pref_units_metric_value));
    }

    public static double[] getLocationCoordinates(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        double[] preferredCoordinates = new double[2];

        preferredCoordinates[0] = Double.longBitsToDouble(sp.getLong(PREF_COORD_LAT, Double.doubleToRawLongBits(0.0)));
        preferredCoordinates[1] = Double.longBitsToDouble(sp.getLong(PREF_COORD_LONG, Double.doubleToRawLongBits(0.0)));

        return preferredCoordinates;
    }

    public static boolean isLocationLatLonAvailable(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean spContainsLatitude = sp.contains(PREF_COORD_LAT);
        boolean spContainsLongitude = sp.contains(PREF_COORD_LONG);
        boolean spContainBoth = false;

        if (spContainsLatitude && spContainsLongitude)
            spContainBoth = true;

        return spContainBoth;
    }

    public static long getLastNotificationTimeInMillis(Context context) {
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastNotificationTime = sp.getLong(lastNotificationKey, 0);

        return lastNotificationTime;
    }

    public static long getEllapsedTimeSinceLastNotification(Context context) {
        long lastNotificationTimeMillis = SunshinePreferences.getLastNotificationTimeInMillis(context);
        long timeSinceLastNotification = System.currentTimeMillis() - lastNotificationTimeMillis;

        return timeSinceLastNotification;
    }

    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();
    }

}