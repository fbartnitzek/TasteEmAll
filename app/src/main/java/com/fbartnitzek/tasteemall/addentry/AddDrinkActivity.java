package com.fbartnitzek.tasteemall.addentry;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.fbartnitzek.tasteemall.R;

public class AddDrinkActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddDrinkActivity.class.getName();
    private static final String ADD_DRINK_FRAGMENT_TAG = "ADD_DRINK_FRAGMENT_TAG";
    public static final String PATTERN_EXTRA = "EXTRA_PRE_FILLED_PATTERN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        // explicitly add fragment, toolbar from fragment...
        if (findViewById(R.id.container_add_drink_fragment) != null) {

            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            supportPostponeEnterTransition();   // wait until Fragment-Views are done

            // edit or add
            AddDrinkFragment fragment = getFragment();
            if (fragment == null) {
                fragment = new AddDrinkFragment();
                if (getIntent().getData() != null) {
                    fragment.setContentUri(getIntent().getData());
                } else if (getIntent().hasExtra(PATTERN_EXTRA)) {
                    fragment.setmPreFilledPattern(getIntent().getStringExtra(PATTERN_EXTRA));
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_add_drink_fragment, fragment, ADD_DRINK_FRAGMENT_TAG)
                        .commit();
            } else {
                Log.v(LOG_TAG, "onCreate - old fragment exists, hashCode=" + this.hashCode()  + "]");
            }

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + "]");
        }

        // add toolbar from fragment (when view is initialized)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private AddDrinkFragment getFragment() {
        return (AddDrinkFragment) getSupportFragmentManager().findFragmentByTag(ADD_DRINK_FRAGMENT_TAG);
    }

    // TODO: might be better directly in fragment...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_save:
                AddDrinkFragment fragment = getFragment();
                if (fragment != null) {
                    Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                    fragment.saveData();
                }
                return true;
            default:
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                supportStartPostponedEnterTransition();
                            }
                            return true;
                        }
                    });
        }
    }
}
