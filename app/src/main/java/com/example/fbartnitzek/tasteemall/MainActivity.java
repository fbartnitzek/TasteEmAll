package com.example.fbartnitzek.tasteemall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBrewerySelected(Uri uri) {
        // tablet with same fragment in mainActivity
        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewBrewery(CharSequence pattern) {
        // tablet with same fragment in mainActivity
        boolean twoPane = false;
        if (twoPane){
            // TODO
        } else {
            Intent intent = new Intent(this, BreweryActivity.class)
                    .putExtra(BreweryActivityFragment.PATTERN_NAME, pattern);
            startActivity(intent);
        }
        
        Toast.makeText(this, "create new brewery " + pattern, Toast.LENGTH_SHORT).show();
    }
}
