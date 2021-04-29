package com.fbartnitzek.tasteemall.location;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.ZoomOutPageTransformer;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.data.pojo.User;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
    public static final String BASE_URI = LOG_TAG + ".BASE_URI";
    public static final String BASE_ENTITY = LOG_TAG + ".BASE_ENTITY";
    public static final String REVIEW_URI = LOG_TAG + ".REVIEW_URI";


    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private String mBaseEntity;
    private Uri mBaseUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        setContentView(R.layout.activity_show_map);

        if (getIntent() != null) {
            mBaseEntity = getIntent().getStringExtra(BASE_ENTITY);
            mBaseUri = getIntent().getParcelableExtra(BASE_URI);
        } else if (savedInstanceState != null) {
            mBaseEntity = savedInstanceState.getString(BASE_ENTITY);
            mBaseUri = savedInstanceState.getParcelable(BASE_URI);
        } else {
            throw new RuntimeException("wrong kind of call...");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            // TODO
            TextView titleView = (TextView) findViewById(R.id.action_bar_title);
            titleView.setText(getString(R.string.title_show_map));
//            if (mLocationName != null) {
//                titleView.setText(getString(R.string.title_show_map_location, mLocationName));
//            } else {
//                titleView.setText(getString(R.string.title_show_map));
//            }
        }

        mViewPager = (ViewPager) findViewById(R.id.map_pager);
        mPagerAdapter = new ShowMapPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        // TODO restore pager Position
//        mViewPager.setCurrentItem(pos < 0 ? 0 : pos);


            // TODO
        supportPostponeEnterTransition();

    }

    // TODO: called by fragments
    protected void setHeading(String fragment, String location) {
        TextView titleView = (TextView) findViewById(R.id.action_bar_title);
        if (titleView != null) {
            titleView.setText(getString(R.string.title_show_map));
            if (location != null) {
//                titleView.setText(getString(R.string.title_show_map_location, mLocationName));
                titleView.setText(getString(R.string.title_show_map_reviews_of_entity_location, fragment, location));
            } else {
//                titleView.setText(getString(R.string.title_show_map));
                titleView.setText(getString(R.string.title_show_map_all_entity, fragment));
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BASE_ENTITY, mBaseEntity);
        outState.putParcelable(BASE_URI, mBaseUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    private ShowBaseMapFragment getFragment(int position) {
//        Log.v(LOG_TAG, "getFragment, hashCode=" + this.hashCode() + ", " + "position = [" + position + "]");
//        return (ShowReviewMapFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        return (ShowBaseMapFragment) mPagerAdapter.instantiateItem(mViewPager, position);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.v(LOG_TAG, "onOptionsItemSelected, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        if (mViewPager == null) {
            Log.e(LOG_TAG, "onOptionsItemSelected: no viewpager...");
            return super.onOptionsItemSelected(item);
        }

        ShowBaseMapFragment fragment = getFragment(mViewPager.getCurrentItem());
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
//        Log.v(LOG_TAG, "scheduleStartPostponedTransition, hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
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

    private Uri calcReviewUri(Uri origUri) {

        try {
            JSONObject jsonObject = new JSONObject(DatabaseContract.getJson(origUri));
            String rootEntity = jsonObject.keys().next();   // just 1 rootElement
            if (Review.ENTITY.equals(rootEntity)) {
//                Log.v(LOG_TAG, "calcReviewUri, json already reviewEntity =" + jsonObject.toString());
                return origUri;

            } else {    // get Or and add wrapping review
                JSONObject prevRootEntity = jsonObject.getJSONObject(rootEntity);
//                Log.v(LOG_TAG, "calcReviewUri, prevRootEntity=" + prevRootEntity.toString());
                JSONObject newBaseObject = new JSONObject().put(Review.ENTITY, new JSONObject());

                JSONObject prevOrEntity = null;
                if (prevRootEntity.has(DatabaseContract.OR)) {
                    prevOrEntity = prevRootEntity.getJSONObject(DatabaseContract.OR);
                    prevRootEntity.remove(DatabaseContract.OR);
                }

                if (Producer.ENTITY.equals(rootEntity)) {
                    newBaseObject.getJSONObject(Review.ENTITY)
                            .put(Drink.ENTITY, new JSONObject()
                                    .put(Producer.ENTITY, prevRootEntity));
                    if (prevOrEntity != null) {
                        newBaseObject.getJSONObject(Review.ENTITY)
                                .put(DatabaseContract.OR, new JSONObject()
                                        .put(Review.ENTITY, new JSONObject()
                                                .put(Drink.ENTITY, prevOrEntity)
                                        ));
                    }
                } else {    // drink or location or user
                    newBaseObject.getJSONObject(Review.ENTITY)
                            .put(rootEntity, prevRootEntity);
                    if (prevOrEntity != null) {
                        newBaseObject.getJSONObject(Review.ENTITY)
                                .put(DatabaseContract.OR, new JSONObject()
                                        .put(Review.ENTITY, prevOrEntity)   // contains already old rootEntity
                                );
                    }
                }
                Log.v(LOG_TAG, "calcReviewUri, newBaseObject=" + newBaseObject.toString() + "]");
                return DatabaseContract.buildUriWithJson(newBaseObject);

            }

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("reviewify json did not work: " + e.getMessage());
        }
    }

    private class ShowMapPagerAdapter extends FragmentStatePagerAdapter {

        public ShowMapPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:{
                    ShowReviewMapFragment fragment = new ShowReviewMapFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(REVIEW_URI, calcReviewUri(mBaseUri));
                    fragment.setArguments(args);
                    return fragment;}

                case 1:{
                    ShowProducerMapFragment fragment = new ShowProducerMapFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(REVIEW_URI, calcReviewUri(mBaseUri));
                    fragment.setArguments(args);
                    return fragment;}
                default:
                    throw new RuntimeException("wrong position for getItem: " + position);
            }
        }

        @Override
        public int getCount() {
            // everything as review...!
            switch (mBaseEntity) {
                case Review.ENTITY:
                case Drink.ENTITY:
                case Producer.ENTITY:
                case User.ENTITY:
                case Location.ENTITY:
                    return 2;
                default:
                    throw new RuntimeException("wrong mBaseEntity for pager-pages: " + mBaseEntity);
            }
        }
    }

}
