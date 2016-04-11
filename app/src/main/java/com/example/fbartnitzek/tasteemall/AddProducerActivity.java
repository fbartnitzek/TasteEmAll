package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddProducerActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddProducerActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AddProducerFragment addProducerFragment = AddProducerFragment.newInstance();

        if (savedInstanceState == null) {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", new fragment");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_container, addProducerFragment)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", fragment should already exist");
        }

    }
}
