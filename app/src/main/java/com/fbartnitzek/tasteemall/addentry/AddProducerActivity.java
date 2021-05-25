package com.fbartnitzek.tasteemall.addentry;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

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
        if (findViewById(R.id.container_add_producer_fragment) != null) {

            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            supportPostponeEnterTransition();   // wait until Fragment-Views are done

//            AddProducerFragment fragment = getFragment();
            AddProducerFragment2 fragment = getFragment();

            // use name from calling activity or contentUri for edit
            if (fragment == null) {
                fragment = new AddProducerFragment2();
                if (getIntent().hasExtra(PRODUCER_NAME_EXTRA)) {
                    fragment.setProducerName(getIntent().getStringExtra(PRODUCER_NAME_EXTRA));
                } else if (getIntent().getData() != null) {
                    fragment.setContentUri(getIntent().getData());
                }
            }


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_add_producer_fragment, fragment, ADD_PRODUCER_FRAGMENT_TAG)
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

    private AddProducerFragment2 getFragment() {
        return (AddProducerFragment2) getSupportFragmentManager().findFragmentByTag(ADD_PRODUCER_FRAGMENT_TAG);
    }

    // TODO: might be better directly in fragment...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        } else if (itemId == R.id.action_save) {
            AddProducerFragment2 fragment = getFragment();
            if (fragment != null) {
                Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                fragment.saveData();
            }
            return true;
        } else {
            Log.e(LOG_TAG, "onOptionsItemSelected - pressed something unusual..., hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy. Some common places where it might make
     * sense to call this method are:
     *
     * (1) Inside a Fragment's onCreateView() method (if the shared element
     *     lives inside a Fragment hosted by the called Activity).
     *
     * (2) Inside a Picasso Callback object (if you need to wait for Picasso to
     *     asynchronously load/scale a bitmap before the transition can begin).
     *
     * (3) Inside a LoaderCallback's onLoadFinished() method (if the shared
     *     element depends on data queried by a Loader).
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void scheduleStartPostponedTransition(final View view) {
        // http://www.androiddesignpatterns.com/2015/03/activity-postponed-shared-element-transitions-part3b.html
        Log.v(LOG_TAG, "scheduleStartPostponedTransition, hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
        if (view == null) {    //simple transition
            supportStartPostponedEnterTransition(); //does not work as expected

        } else {    // shared element transition
            view.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public boolean onPreDraw() {
                            view.getViewTreeObserver().removeOnPreDrawListener(this);
                            supportStartPostponedEnterTransition();
                            return true;
                        }
                    });
        }
    }
}
