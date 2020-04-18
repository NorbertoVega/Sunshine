package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private final Context mContext;
    private final ForecastAdapterOnClickHandler mClickHandler;

    private static final int VIEW_TYPE_TODAY = 555;
    private static final int VIEW_TYPE_FUTURE = 666;

    private boolean mUseTodayLayout;

    public interface ForecastAdapterOnClickHandler{
        void onListItemClick(long date);
    }

    private Cursor mCursor;

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        mContext = context;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int listItemResource;

        try{
            if ( viewType == VIEW_TYPE_TODAY)
                listItemResource = R.layout.list_item_forecast_today;
            else
                listItemResource= R.layout.forecast_list_item;
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }

        View view = LayoutInflater
                .from(mContext)
                .inflate(listItemResource, viewGroup, false);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        try {
            if (mUseTodayLayout && position == 0) {
                weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

            }
            else {
                weatherImageId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);
        forecastAdapterViewHolder.dateView.setText(dateString);
        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.lowTempView.setText(SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius));
        forecastAdapterViewHolder.highTempView.setText(SunshineWeatherUtils.formatTemperature(mContext, highInCelsius));


    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0)
            return VIEW_TYPE_TODAY;
        else
            return VIEW_TYPE_FUTURE;
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        public ForecastAdapterViewHolder(View view){
            super(view);
            iconView = view.findViewById(R.id.forecastImageView);
            dateView = view.findViewById(R.id.date);
            descriptionView = view.findViewById(R.id.weather_description);
            highTempView = view.findViewById(R.id.high_temperature);
            lowTempView = view.findViewById(R.id.low_temperature);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            mClickHandler.onListItemClick(mCursor.getLong(DetailActivity.INDEX_WEATHER_DATE));
        }
    }
}
