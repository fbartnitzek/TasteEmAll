package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class AddDrinkActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddDrinkActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            Log.v(LOG_TAG, "onCreate - init toolbar..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(this, false);
            String readableDrink = getString(Utils.getDrinkName(drinkType));
            getSupportActionBar().setTitle(
                    getString(R.string.title_add_drink_activity,
                            readableDrink));
        }

        //  Fragment gets added by default from xml fragment entry
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private AddDrinkFragment getFragment() {
        return (AddDrinkFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_add_drink);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                AddDrinkFragment fragment = getFragment();
                if (fragment != null) {
                    Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                    fragment.insertData();
                }
                break;
            default:
                Log.e(LOG_TAG, "onOptionsItemSelected - other option selected..., hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }

        return super.onOptionsItemSelected(item);
    }
}
