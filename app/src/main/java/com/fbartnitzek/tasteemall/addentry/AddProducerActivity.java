package com.fbartnitzek.tasteemall.addentry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fbartnitzek.tasteemall.R;

public class AddProducerActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddProducerActivity.class.getName();
    public static final String PRODUCER_NAME_EXTRA = "producer_name_extra";
    private static final String ADD_PRODUCER_FRAGMENT_TAG = "ADD_PRODUCER_FRAGMENT_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producer);

        // explicitly add fragment with pattern
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            AddProducerFragment fragment = getFragment();

            // use name from calling activity
            if (fragment == null) {
                fragment = new AddProducerFragment();
                if (getIntent().hasExtra(PRODUCER_NAME_EXTRA)) {
                    fragment.setProducerName(getIntent().getStringExtra(PRODUCER_NAME_EXTRA));
                } else if (getIntent().getData() != null) {
                    fragment.setContentUri(getIntent().getData());
                }
            }


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, ADD_PRODUCER_FRAGMENT_TAG)
                    .commit();
        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

        // add toolbar from fragment (when view is initialized)

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private AddProducerFragment getFragment() {
        return (AddProducerFragment) getSupportFragmentManager().findFragmentByTag(ADD_PRODUCER_FRAGMENT_TAG);
    }

    // TODO: might be better directly in fragment...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                AddProducerFragment fragment = getFragment();
                if (fragment != null) {
                    Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                    fragment.saveData();
                }
                break;
            default:
                Log.e(LOG_TAG, "onOptionsItemSelected - pressed something unusual..., hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }

        return super.onOptionsItemSelected(item);
    }
}
