package com.fbartnitzek.tasteemall.addentry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fbartnitzek.tasteemall.R;

public class AddReviewActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddReviewActivity.class.getName();
    private static final String ADD_REVIEW_FRAGMENT_TAG = "ADD_REVIEW_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // explicitly add fragment, toolbar from fragment...
        if (findViewById(R.id.container_add_review_fragment) != null) {

            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            // edit or add
            AddReviewFragment fragment = getFragment();
            if (fragment == null) {

                fragment = new AddReviewFragment();
                if (getIntent().getData() != null) {
                    fragment.setmContentUri(getIntent().getData());
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_add_review_fragment, fragment, ADD_REVIEW_FRAGMENT_TAG)
                        .commit();
            } else {
                Log.v(LOG_TAG, "onCreate - old fragment exists, hashCode=" + this.hashCode()  + "]");
            }

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + "]");
        }

        // add toolbar from fragment (when view is initialized)
    }

    private AddReviewFragment getFragment() {
        return (AddReviewFragment) getSupportFragmentManager().findFragmentByTag(ADD_REVIEW_FRAGMENT_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    // TODO: might be better directly in fragment...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                AddReviewFragment fragment = getFragment();
                if (fragment != null) {
                    Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                    fragment.saveData();
                }
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }


}
