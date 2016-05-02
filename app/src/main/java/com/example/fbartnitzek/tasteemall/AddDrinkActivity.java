package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class AddDrinkActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddDrinkActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        AddDrinkFragment fragment = AddDrinkFragment.newInstance();

        if (savedInstanceState == null) {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "no saved state - new fragment");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_container, fragment)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", fragment should already exist");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(this, false);
            String readableDrinkType = getString(Utils.getDrinkName(drinkType));
            getSupportActionBar().setTitle(
                    getString(R.string.title_add_drink_activity,
                    readableDrinkType));
        }
    }
}
