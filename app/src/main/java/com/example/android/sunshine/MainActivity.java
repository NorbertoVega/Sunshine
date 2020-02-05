package com.example.android.sunshine;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]> {

    private static final int FORECAST_LOADER_ID = 33;

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mErrorMessageTextView = (TextView)findViewById(R.id.error_message_text_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_pb);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showWeatherData(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showLocationOnMap(){
        String address = "13106 Lago steffen";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("geo")
                    .path("0,0")
                    .appendQueryParameter("q", address);
        Uri addressUri = builder.build();
        Intent addressIntent = new Intent(Intent.ACTION_VIEW);
        addressIntent.setData(addressUri);
        if (addressIntent.resolveActivity(getPackageManager()) != null){
            startActivity(addressIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();
        if (itemClickedId == R.id.action_refresh){
            mForecastAdapter.setWeatherData(null);
            getLoaderManager().restartLoader(FORECAST_LOADER_ID, null,this);
            return true;
        }
        if (itemClickedId == R.id.action_map){
            showLocationOnMap();
            return true;
        }
        return true;
    }

    @Override
    public void onListItemClick(String weatherData) {
        Intent detailsIntent = new Intent(this, DetailActivity.class);
        detailsIntent.putExtra(Intent.EXTRA_TEXT, weatherData);
        startActivity(detailsIntent);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String[]> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] mData;

            @Override
            protected void onStartLoading() {
                if (mData != null)
                    deliverResult(mData);
                else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                if (TextUtils.isEmpty(location))
                    return null;

                URL weatherRequestUrl = NetworkUtils.buildUrl(location);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    return OpenWeatherJsonUtils
                            .getSimpleWeatherStringFromJson(MainActivity.this, jsonWeatherResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] data) {
                mData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] weatherData) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(weatherData);
        if (weatherData != null) {
            showWeatherData();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

}
