package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class BreweryActivity extends AppCompatActivity {
// TODO: other name
    private static final String FRAGMENT_TAG = "Brewery_Tag";
    private static final String LOG_TAG = BreweryActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_brewery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        if (getIntent().hasExtra(BreweryActivityFragment.PATTERN_NAME)) {
            //new: forward pattern from MainActivity
            Log.v(LOG_TAG, "onCreate, " + "with extra PATTERN_NAME");
            args.putCharSequence(BreweryActivityFragment.PATTERN_NAME,
                    getIntent().getCharSequenceExtra(BreweryActivityFragment.PATTERN_NAME));
        } else {    // load existing
            Log.v(LOG_TAG, "onCreate, " + "with extra BREWERY_URI");
            args.putParcelable(BreweryActivityFragment.BREWERY_URI, getIntent().getData());
        }
        BreweryActivityFragment fragment = new BreweryActivityFragment();
        fragment.setArguments(args);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.brewery_container, fragment, FRAGMENT_TAG)
                    .commit();
        }

    }

}
