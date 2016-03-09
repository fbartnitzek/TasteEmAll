package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AddFragment addFragment = AddFragment.newInstance();

        if (savedInstanceState == null) {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", new fragment");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_container, addFragment)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", fragment should already exist");
        }

    }
}
