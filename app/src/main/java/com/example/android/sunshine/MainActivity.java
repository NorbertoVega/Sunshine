package com.example.android.sunshine;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Within forecast_list_item.xml //////////////////////////////////////////////////////////////
    // TODO (5) Add a layout for an item in the list called forecast_list_item.xml
    // TODO (6) Make the root of the layout a vertical LinearLayout
    // TODO (7) Set the width of the LinearLayout to match_parent and the height to wrap_content

    // TODO (8) Add a TextView with an id @+id/tv_weather_data
    // TODO (9) Set the text size to 22sp
    // TODO (10) Make the width and height wrap_content
    // TODO (11) Give the TextView 16dp of padding

    // TODO (12) Add a View to the layout with a width of match_parent and a height of 1dp
    // TODO (13) Set the background color to #dadada
    // TODO (14) Set the left and right margins to 8dp
    // Within forecast_list_item.xml //////////////////////////////////////////////////////////////


    // Within ForecastAdapter.java /////////////////////////////////////////////////////////////////
    // TODO (15) Add a class file called ForecastAdapter
    // TODO (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>

    // TODO (23) Create a private string array called mWeatherData

    // TODO (47) Create the default constructor (we will pass in parameters in a later lesson)

    // TODO (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // TODO (17) Extend RecyclerView.ViewHolder

    // Within ForecastAdapterViewHolder ///////////////////////////////////////////////////////////
    // TODO (18) Create a public final TextView variable called mWeatherTextView

    // TODO (19) Create a constructor for this class that accepts a View as a parameter
    // TODO (20) Call super(view) within the constructor for ForecastAdapterViewHolder
    // TODO (21) Using view.findViewById, get a reference to this layout's TextView and save it to mWeatherTextView
    // Within ForecastAdapterViewHolder ///////////////////////////////////////////////////////////


    // TODO (24) Override onCreateViewHolder
    // TODO (25) Within onCreateViewHolder, inflate the list item xml into a view
    // TODO (26) Within onCreateViewHolder, return a new ForecastAdapterViewHolder with the above view passed in as a parameter

    // TODO (27) Override onBindViewHolder
    // TODO (28) Set the text of the TextView to the weather for this list item's position

    // TODO (29) Override getItemCount
    // TODO (30) Return 0 if mWeatherData is null, or the size of mWeatherData if it is not null

    // TODO (31) Create a setWeatherData method that saves the weatherData to mWeatherData
    // TODO (32) After you save mWeatherData, call notifyDataSetChanged
    // Within ForecastAdapter.java /////////////////////////////////////////////////////////////////


    // TODO (33) Delete mWeatherTextView

    // TODO (34) Add a private RecyclerView variable called mRecyclerView
    // TODO (35) Add a private ForecastAdapter variable called mForecastAdapter

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        // TODO (36) Delete the line where you get a reference to mWeatherTextView


        // TODO (37) Use findViewById to get a reference to the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mErrorMessageTextView = (TextView)findViewById(R.id.error_message_text_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(false);
        // TODO (38) Create layoutManager, a LinearLayoutManager with VERTICAL orientation and shouldReverseLayout == false

        // TODO (39) Set the layoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);
        // TODO (40) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        mRecyclerView.setHasFixedSize(true);
        // TODO (41) set mForecastAdapter equal to a new ForecastAdapter
        mForecastAdapter = new ForecastAdapter();
        // TODO (42) Use mRecyclerView.setAdapter and pass in mForecastAdapter
        mRecyclerView.setAdapter(mForecastAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_pb);
        loadWeatherData();
    }

    public void loadWeatherData(){
        showWeatherData();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    private void showErrorMessage(){
        // TODO (44) Hide mRecyclerView, not mWeatherTextView
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showWeatherData(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        // TODO (43) Show mRecyclerView, not mWeatherTextView

        mRecyclerView.setVisibility(View.VISIBLE);
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
            // TODO (46) Instead of setting the text to "", set the adapter to null before refreshing

            mForecastAdapter = null;
            loadWeatherData();
            return true;
        }
        return true;
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected void onPreExecute() {
           if (mForecastAdapter == null){
               mForecastAdapter = new ForecastAdapter();
               mRecyclerView.setVisibility(View.INVISIBLE);
           }
           super.onPreExecute();
           mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0)
                return null;
            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try{
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(weatherData != null){
                showWeatherData();

                // TODO (45) Instead of iterating through every string, use mForecastAdapter.setWeatherData and pass in the weather data

                mForecastAdapter.setWeatherData(weatherData);
            }
            else
                showErrorMessage();
        }
    }
}
