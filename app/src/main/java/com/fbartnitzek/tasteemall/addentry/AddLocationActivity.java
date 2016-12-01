package com.fbartnitzek.tasteemall.addentry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fbartnitzek.tasteemall.R;

public class AddLocationActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddLocationActivity.class.getName();
    public static final String LOCATION_INPUT_EXTRA = "location_input_extra";
    public static final String LOCATION_DESCRIPTION_EXTRA = "LOCATION_DESCRIPTION_EXTRA";
    private static final String ADD_LOCATION_FRAGMENT_TAG = "ADD_LOCATION_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        if (findViewById(R.id.container_add_location_fragment) != null) {

            if (savedInstanceState != null) {
                Log.v(LOG_TAG, "onCreate - saved state = nothing... ");
                return;
            }

            // supportPostpone...

            AddLocationFragment fragment = getFragment();

            if (fragment == null) {
                fragment = new AddLocationFragment();
            }
            if (getIntent().hasExtra(LOCATION_INPUT_EXTRA)) {
                fragment.setInput(getIntent().getStringExtra(LOCATION_INPUT_EXTRA));
                fragment.setDescription(getIntent().hasExtra(LOCATION_DESCRIPTION_EXTRA) ?
                        getIntent().getStringExtra(LOCATION_DESCRIPTION_EXTRA) : "");
            } else if (getIntent().getData() != null) {
                fragment.setContentUri(getIntent().getData());
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_add_location_fragment, fragment, ADD_LOCATION_FRAGMENT_TAG)
                    .commit();

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, ...!");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private AddLocationFragment getFragment() {
        return (AddLocationFragment) getSupportFragmentManager().findFragmentByTag(ADD_LOCATION_FRAGMENT_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                supportFinishAfterTransition();
                finish();
                return true;
            case R.id.action_save:
                AddLocationFragment fragment = getFragment();
                if (fragment != null) {
                    Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                    fragment.saveData();
                }
                return true;
            default:
                Log.e(LOG_TAG, "onOptionsItemSelected - pressed something unusual..., hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }

        return super.onOptionsItemSelected(item);
    }
}
