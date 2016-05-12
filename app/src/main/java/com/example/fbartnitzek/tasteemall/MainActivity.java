package com.example.fbartnitzek.tasteemall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fbartnitzek.tasteemall.tasks.GeocodeReviewsTask;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // explicitly add fragment, toolbar from fragment...
        if (findViewById(R.id.fragment_container) != null) {

            MainFragment fragment = getFragment();
            if (fragment == null) { //create new fragment
                Log.v(LOG_TAG, "onCreate - add a new fragment, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                fragment = new MainFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment, MAIN_FRAGMENT_TAG)
                        .commit();

            } else {    // use old fragment
                Log.v(LOG_TAG, "onCreate - old fragment exists, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            }

            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            }

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

        // add toolbar from fragment (when view is initialized)
    }

    private MainFragment getFragment() {
        return (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
    }

    // might be better in the fragment...
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "onSaveInstanceState - just call super, hashCode=" + this.hashCode() + ", " + "outState = [" + outState + "]");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);  //TODO: add geocode reaction
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_geocode:
                startGeocoding();
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);   //may call fragment for others
    }

    private void startGeocoding() {
        Log.v(LOG_TAG, "startGeocoding, hashCode=" + this.hashCode() + ", " + "");
        new GeocodeReviewsTask(this, new GeocodeReviewsTask.GeocodeReviewsUpdatedHandler() {
            @Override
            public void onUpdatedReviews(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        }).execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        super.onActivityResult(requestCode, resultCode, data);
    }

}
