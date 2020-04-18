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

    public interface ForecastAdapterOnClickHandler{
        void onListItemClick(long date);
    }

    private Cursor mCursor;

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        int forecastImageViewResource = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        forecastAdapterViewHolder.forecastImageView.setImageResource(forecastImageViewResource);
        forecastAdapterViewHolder.dateTextView.setText(dateString);
        forecastAdapterViewHolder.descriptionTextView.setText(description);
        forecastAdapterViewHolder.minTextView.setText(SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius));
        forecastAdapterViewHolder.maxTextView.setText(SunshineWeatherUtils.formatTemperature(mContext, highInCelsius));
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

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView forecastImageView;
        final TextView dateTextView;
        final TextView descriptionTextView;
        final TextView minTextView;
        final TextView maxTextView;

        public ForecastAdapterViewHolder(View itemView){
            super(itemView);
            forecastImageView = itemView.findViewById(R.id.forecastImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            minTextView = itemView.findViewById(R.id.minTextView);
            maxTextView = itemView.findViewById(R.id.maxTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            mClickHandler.onListItemClick(mCursor.getLong(DetailActivity.INDEX_WEATHER_DATE));
        }
    }
}
