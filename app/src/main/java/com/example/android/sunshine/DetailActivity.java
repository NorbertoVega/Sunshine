package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView mDetails;
    private String mForecast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetails = findViewById(R.id.details_text_view);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            mForecast = intent.getStringExtra(Intent.EXTRA_TEXT);
            mDetails.setText(mForecast);
        }

    }
}
