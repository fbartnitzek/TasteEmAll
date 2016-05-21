package com.fbartnitzek.tasteemall;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.GoogleMap;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ShowMapActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShowMapActivity.class.getName();
    public static final String EXTRA_REVIEWS_URI = LOG_TAG + ".EXTRA_REVIEWS_URI";
    private static final String FRAGMENT_TAG = LOG_TAG + "_SHOW_MAP_FRAGMENT_TAG";
    public static final String EXTRA_PRODUCERS_URI = LOG_TAG + ".EXTRA_PRODUCERS_URI";
    public static final String EXTRA_REVIEWS_SORT_ORDER = LOG_TAG + ".EXTRA_REVIEWS_SORT_ORDER";
    public static final String EXTRA_PRODUCERS_SORT_ORDER = LOG_TAG + ".EXTRA_PRODUCERS_SORT_ORDER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        setContentView(R.layout.activity_show_map);

        if (findViewById(R.id.container_show_map_fragment) != null) {
            if (savedInstanceState != null) {
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            supportPostponeEnterTransition();

            ShowMapFragment fragment = new ShowMapFragment();

            if (getIntent() != null) {
                Bundle args = new Bundle();
                if (getIntent().hasExtra(EXTRA_REVIEWS_URI)) {
                    args.putParcelable(EXTRA_REVIEWS_URI, getIntent().getParcelableExtra(EXTRA_REVIEWS_URI));
                }
                if (getIntent().hasExtra(EXTRA_PRODUCERS_URI)) {
                    args.putParcelable(EXTRA_PRODUCERS_URI, getIntent().getParcelableExtra(EXTRA_PRODUCERS_URI));
                }
                if (getIntent().hasExtra(EXTRA_REVIEWS_SORT_ORDER)) {
                    args.putString(EXTRA_REVIEWS_SORT_ORDER, getIntent().getStringExtra(EXTRA_REVIEWS_SORT_ORDER));
                }
                if (getIntent().hasExtra(EXTRA_PRODUCERS_SORT_ORDER)) {
                    args.putString(EXTRA_PRODUCERS_SORT_ORDER, getIntent().getStringExtra(EXTRA_PRODUCERS_SORT_ORDER));
                }
                fragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_show_map_fragment, fragment, FRAGMENT_TAG)
                    .commit();

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    private ShowMapFragment getFragment() {
        return (ShowMapFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ShowMapFragment fragment = getFragment();
        if (fragment != null) {
            switch (item.getItemId()) {
                case R.id.action_map_type_none:
                    fragment.setMapType(GoogleMap.MAP_TYPE_NONE);
                    return true;
                case R.id.action_map_type_normal:
                    fragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                case R.id.action_map_type_satellite:
                    fragment.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                case R.id.action_map_type_terrain:
                    fragment.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                case R.id.action_map_type_hybrid:
                    fragment.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                default:
                    // nothing
            }
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