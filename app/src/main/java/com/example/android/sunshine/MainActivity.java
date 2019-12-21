package com.example.android.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        mErrorMessageTextView = (TextView)findViewById(R.id.error_message_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_pb);
        loadWeatherData();
    }

    public void loadWeatherData(){
        String locationQuery = SunshinePreferences.getPreferredWeatherLocation(this);
        URL weatherSearchUrl = NetworkUtils.buildUrl(locationQuery);
        new FetchWeatherTask().execute(weatherSearchUrl);
    }

    private void showErrorMessage(){
        mWeatherTextView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showWeatherData(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemClickedId = item.getItemId();
        if (itemClickedId == R.id.action_refresh){
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }
        return true;
    }

    private class FetchWeatherTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mWeatherTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String weatherResults = null;
            try{
                weatherResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return weatherResults;
        }

        @Override
        protected void onPostExecute(String weatherResults) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if(weatherResults != null && !weatherResults.isEmpty()){
                mWeatherTextView.setText(weatherResults);
                showWeatherData();
            }
            else
                showErrorMessage();
        }
    }
}
