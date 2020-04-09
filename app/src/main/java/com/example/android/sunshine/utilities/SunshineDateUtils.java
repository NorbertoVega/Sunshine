package com.example.android.sunshine.utilities;

import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.sunshine.R;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class SunshineDateUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

    public static long getNormalizedDateForToday() {

        long utcNowMillis = System.currentTimeMillis();
        TimeZone currentTimeZone = TimeZone.getDefault();
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);
        long timeSinceEpochLocal = utcNowMillis + gmtOffsetMillis;
        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocal);

        return TimeUnit.DAYS.toMillis(daysSinceEpochLocal);
    }

    private static long elapsedDaysSinceEpoch(long utcDate) {
        return TimeUnit.MILLISECONDS.toDays(utcDate);
    }

    public static long normalizeDate(long date){
        long daysSinceEpoch = elapsedDaysSinceEpoch(date);
        return daysSinceEpoch * DAY_IN_MILLIS;
    }

    public static boolean isDateNormalized(long millisSinceEpoch) {
        boolean isDateNormalized = false;
        if (millisSinceEpoch % DAY_IN_MILLIS == 0) {
            isDateNormalized = true;
        }

        return isDateNormalized;
    }

    private static long getLocalMidnightFromNormalizedUtcDate(long normalizedUtcDate) {
        TimeZone timeZone = TimeZone.getDefault();

        long gmtOffset = timeZone.getOffset(normalizedUtcDate);
        return normalizedUtcDate - gmtOffset;
    }

    public static String getFriendlyDateString(Context context, long normalizedUtcMidnight, boolean showFullDate) {
        long localDate = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMidnight);
        long daysFromEpochToProvideDate = elapsedDaysSinceEpoch(localDate);
        long daysFromEpochToToday = elapsedDaysSinceEpoch(System.currentTimeMillis());

        if (daysFromEpochToProvideDate == daysFromEpochToToday || showFullDate) {
            String dayName = getDayName(context, localDate);
            String readableDate = getReadableDateString(context, localDate);
            if (daysFromEpochToProvideDate - daysFromEpochToToday < 2) {
                String localizedDayName = new SimpleDateFormat("EEEE").format(localDate);
                return readableDate.replace(localizedDayName, dayName);
            }
            else {
                return readableDate;
            }
        } else if (daysFromEpochToProvideDate < daysFromEpochToToday + 7)
            return getDayName(context, localDate);
        else {
            int flags = DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_NO_YEAR
                        | DateUtils.FORMAT_ABBREV_ALL
                        | DateUtils.FORMAT_SHOW_WEEKDAY;
            return DateUtils.formatDateTime(context, localDate, flags);
        }
    }

    private static String getReadableDateString(Context context, long timeInMillis){
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY;
        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    private static String getDayName(Context context, long dateInMillis) {

        long daysFromEpochToProvidedDate = elapsedDaysSinceEpoch(dateInMillis);
        long daysFromEpochToToday = elapsedDaysSinceEpoch(System.currentTimeMillis());
        int daysAfterToday = (int) (daysFromEpochToProvidedDate - daysFromEpochToToday);

        switch (daysAfterToday) {
            case 0:
                return context.getString(R.string.today);
            case 1:
                return context.getString(R.string.tomorrow);
            default:
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
        }
    }

}
