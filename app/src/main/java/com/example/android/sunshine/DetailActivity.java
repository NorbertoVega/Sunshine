package com.example.android.sunshine;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity
                            implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    public static final String[] WEATHER_DETAIL_PROJECTION = {WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID};

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_WEATHER_ID = 7;

    public static final int ID_DETAIL_WEATHER = 759;

    private String mForecastSummary;
    private Uri mUri;

    private TextView mDateTextView;
    private TextView mDescriptionTexView;
    private TextView mHighTempTextView;
    private TextView mLowTempTextView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;
    private TextView mPressureTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDateTextView = findViewById(R.id.tv_date);
        mDescriptionTexView = findViewById(R.id.tv_description);
        mHighTempTextView = findViewById(R.id.tv_high_temperature);
        mLowTempTextView = findViewById(R.id.tv_low_temperature);
        mHumidityTextView = findViewById(R.id.tv_humidity);
        mWindTextView = findViewById(R.id.tv_wind);
        mPressureTextView = findViewById(R.id.tv_pressure);

        mUri = getIntent().getData();
        if (mUri == null)
            throw new NullPointerException("Uri for detail cannot be null");

        getLoaderManager().initLoader(ID_DETAIL_WEATHER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                                        .setType("text/plane")
                                        .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                                        .getIntent();
        menuItem.setIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemClickedId = item.getItemId();

        if (itemClickedId == R.id.action_settings){
            Intent startActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(startActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
        switch (idLoader){
            case ID_DETAIL_WEATHER:
                return new CursorLoader(this,
                                    mUri,
                                    WEATHER_DETAIL_PROJECTION,
                                    null,
                                    null,
                                    null);

            default:
                throw new RuntimeException("Loader not implemented");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst())
            cursorHasValidData = true;

        if (!cursorHasValidData)
            return;

        long localDate = data.getLong(DetailActivity.INDEX_WEATHER_DATE);
        String dateText = SunshineDateUtils.getFriendlyDateString(this, localDate, true);
        mDateTextView.setText(dateText);

        int weatherId = data.getInt(DetailActivity.INDEX_WEATHER_WEATHER_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
        mDescriptionTexView.setText(description);

        double highTemp = data.getDouble(DetailActivity.INDEX_WEATHER_MAX_TEMP);
        String highTempString = SunshineWeatherUtils.formatTemperature(this, highTemp);
        Log.d("Detail ", "max: " + highTempString);
        mHighTempTextView.setText(highTempString);

        double lowTemp = data.getDouble(DetailActivity.INDEX_WEATHER_MIN_TEMP);
        String lowTempString = SunshineWeatherUtils.formatTemperature(this, lowTemp);
        mLowTempTextView.setText(lowTempString);

        float humidity = data.getFloat(DetailActivity.INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        mHumidityTextView.setText(humidityString);

        float windSpeed = data.getFloat(DetailActivity.INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(DetailActivity.INDEX_WEATHER_DEGREES);
        String windSpeedString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        mWindTextView.setText(windSpeedString);

        float pressure = data.getFloat(DetailActivity.INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);
        Log.d("Detail ", "pressure: "+pressureString);
        mPressureTextView.setText(pressureString);

        mForecastSummary = String.format("%s - %s - %s/%s", dateText, description, highTempString, lowTempString);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
