package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class AddProducerActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddProducerActivity.class.getName();
    public static final String PRODUCER_NAME_EXTRA = "producer_name_extra";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producer);

        // use name from calling activity...
        AddProducerFragment addProducerFragment;
        if (getIntent().hasExtra(PRODUCER_NAME_EXTRA)) {
            addProducerFragment = AddProducerFragment.newInstance(
                    getIntent().getStringExtra(PRODUCER_NAME_EXTRA)
            );
        } else {
            addProducerFragment = AddProducerFragment.newInstance();
        }

        if (savedInstanceState == null) {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", new fragment");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_container, addProducerFragment)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", fragment should already exist");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            Log.v(LOG_TAG, "onCreate - supportActionBar ready, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        } else {
            Log.v(LOG_TAG, "onCreate - supportActionBar not ready... , hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
    }
}
