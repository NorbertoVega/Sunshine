package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.SunshineDateUtils;

public class weatherProvider extends ContentProvider {

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    WeatherDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int insertRows = 0;

        switch (match){
            case CODE_WEATHER:
                database.beginTransaction();
                try{
                    for (int i = 0; i< values.length; i++){
                        long weatherDate = values[i].getAsLong(WeatherEntry.COLUMN_DATE);

                        if (!SunshineDateUtils.isDateNormalized(weatherDate))
                            throw new IllegalArgumentException("Date must be normalized to insert");

                        long id = database.insert(WeatherEntry.TABLE_NAME, null, values[i]);
                        if (id > 0)
                            insertRows ++;
                    }
                    database.setTransactionSuccessful();
                }
                finally {
                    database.endTransaction();
                }

                if (insertRows > 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return insertRows;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase database = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match){
            case CODE_WEATHER:
                returnCursor = database.query(WeatherEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;

            case CODE_WEATHER_WITH_DATE:
                String date = uri.getLastPathSegment();
                returnCursor = database.query(WeatherEntry.TABLE_NAME,
                                projection,
                                WeatherEntry.COLUMN_DATE + " = ?",
                                new String[]{date},
                        null,
                          null,
                                sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (match){
            case CODE_WEATHER:
                rowsDeleted = database.delete(WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    public void shutDown(){
        mOpenHelper.close();
        super.shutdown();
    }
}
