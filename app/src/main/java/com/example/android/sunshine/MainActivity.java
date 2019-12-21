package com.example.android.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        loadWeatherData();
    }

    public void loadWeatherData(){
        String locationQuery = SunshinePreferences.getPreferredWeatherLocation(this);
        URL weatherSearchUrl = NetworkUtils.buildUrl(locationQuery);
        new FetchWeatherTask().execute(weatherSearchUrl);
    }

    private class FetchWeatherTask extends AsyncTask<URL, Void, String>{

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
            if(weatherResults != null && !weatherResults.isEmpty()){
                mWeatherTextView.setText(weatherResults);
            }
        }
    }
}
