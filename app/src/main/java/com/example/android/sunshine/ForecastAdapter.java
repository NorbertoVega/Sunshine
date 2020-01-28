package com.example.android.sunshine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;

    private final ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler{
        void onListItemClick(String weatherData);
    }

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null)
            return 0;
        else
            return mWeatherData.length;
    }

    public void setWeatherData(String[] data){
        mWeatherData = data;
        notifyDataSetChanged();
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView){
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        void bind(int position){
            mWeatherTextView.setText(mWeatherData[position]);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onListItemClick(mWeatherData[getAdapterPosition()]);
        }
    }
}
