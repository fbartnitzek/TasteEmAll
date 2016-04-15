package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class ShowProducerActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOW_PRODUCER_TAG";
    private static final String LOG_TAG = ShowProducerActivity.class.getName();
    public static final String EXTRA_PRODUCER_URI = "EXTRA_PRODUCER_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_producer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            Log.v(LOG_TAG, "onCreate - supportActionBar ready, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            Log.v(LOG_TAG, "onCreate - supportActionBar not ready... , hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PRODUCER_URI, getIntent().getData());

        ShowProducerFragment fragment = new ShowProducerFragment();
        fragment.setArguments(args);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.producer_container, fragment, FRAGMENT_TAG)
                    .commit();
        }

    }

}
