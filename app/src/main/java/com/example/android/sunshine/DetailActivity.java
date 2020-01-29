package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                                        .setType("text/plane")
                                        .setText(mForecast + FORECAST_SHARE_HASHTAG)
                                        .getIntent();
        menuItem.setIntent(shareIntent);
        return true;
    }
}
