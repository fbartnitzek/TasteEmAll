package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class ShowDrinkActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOw_DRINK_TAG";
    private static final String LOG_TAG = ShowDrinkActivity.class.getName();
    public static final String EXTRA_DRINK_URI = "EXTRA_DRINK_URI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_drink);

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
        args.putParcelable(EXTRA_DRINK_URI, getIntent().getData());

        ShowDrinkFragment fragment = new ShowDrinkFragment();
        fragment.setArguments(args);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drink_container, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }
}
