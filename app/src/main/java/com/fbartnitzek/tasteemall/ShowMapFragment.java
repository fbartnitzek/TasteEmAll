package com.fbartnitzek.tasteemall;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.tasks.PopulateMapTask;
import com.fbartnitzek.tasteemall.tasks.QueryColumns;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

public class ShowMapFragment extends Fragment implements OnMapReadyCallback, PopulateMapTask.PopulateMapHandler {

    private static final String LOG_TAG = ShowMapFragment.class.getName();
//    private static final int MAP_REVIEWS_LOADER = 54356;
//    private static final int MAP_PRODUCERS_LOADER = 54357;
    private static final String STATE_MAP_INFO = "STATE_MAP_INFO";
    private static final String STATE_NAVIGATION_DONE = "STATE_NAVIGATION_DONE";

    private GoogleMap mMap;
    private boolean mMapReady = false;
    private Uri mReviewsUri;
    private Uri mProducersUri;
    private String mReviewsSortOrder;
    private String mProducersSortOrder;
    private View mRootView;
    private int mMapType = -1;
    private Map<LatLng, PopulateMapTask.MarkerInfo> mMarkers;
    private TextView mInfoView;
    private boolean mAlreadyLoaded = false;

    // clustering and querying really bad ...
    // TODO: new entry "Location" (with LatLng, name, ...)
    // TODO: foreignKey in Review and Producer, queries and Adapter here


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_NAVIGATION_DONE)) {
            mAlreadyLoaded =  savedInstanceState.getBoolean(STATE_NAVIGATION_DONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_show_map, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.v(LOG_TAG, "onCreateView without args...");
        } else {
            if (args.containsKey(ShowMapActivity.EXTRA_REVIEWS_URI)) {
                mReviewsUri = args.getParcelable(ShowMapActivity.EXTRA_REVIEWS_URI);
            }
            if (args.containsKey(ShowMapActivity.EXTRA_REVIEWS_SORT_ORDER)) {
                mReviewsSortOrder = args.getString(ShowMapActivity.EXTRA_REVIEWS_SORT_ORDER);
            }
            if (args.containsKey(ShowMapActivity.EXTRA_PRODUCERS_URI)) {
                mProducersUri = args.getParcelable(ShowMapActivity.EXTRA_PRODUCERS_URI);
            }
            if (args.containsKey(ShowMapActivity.EXTRA_PRODUCERS_SORT_ORDER)) {
                mProducersSortOrder = args.getString(ShowMapActivity.EXTRA_PRODUCERS_SORT_ORDER);
            }
        }

        mInfoView = (TextView) mRootView.findViewById(R.id.map_info);
        // src: http://stackoverflow.com/questions/1748977/making-textview-scrollable-in-android
        mInfoView.setMovementMethod(new ScrollingMovementMethod());
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MAP_INFO)) {
            mInfoView.setText(savedInstanceState.getString(STATE_MAP_INFO));
        }

        createToolbar(mRootView);
        updateToolbar();

        ((ShowMapActivity) getActivity()).scheduleStartPostponedTransition(mRootView);

        //src: http://stackoverflow.com/questions/15525111/getsupportfragmentmanager-findfragmentbyid-returns-null
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mapFragment.getMapAsync(this);
//            Log.v(LOG_TAG, "onCreateView, MapFragment found & set...");
        }

        startPopulateMap();

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: make MarkerInfo parcelable... (later)
        if (mInfoView != null) {
            outState.putString(STATE_MAP_INFO, mInfoView.getText().toString());
        }
        outState.putBoolean(STATE_NAVIGATION_DONE, mAlreadyLoaded);
        super.onSaveInstanceState(outState);
    }

    private void startPopulateMap() {
        Log.v(LOG_TAG, "startPopulateMap, mReviewsUri=" + mReviewsUri+ ", mReviewsSortOrder=" + mReviewsSortOrder);
        if (mReviewsUri != null) {
            new PopulateMapTask(getActivity(),
                    QueryColumns.MapFragment.Reviews.COLUMNS,
                    QueryColumns.MapFragment.Reviews.COL_REVIEW_LOCATION,
                    mReviewsSortOrder, this).execute(mReviewsUri);
        }
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume, hashCode=" + this.hashCode() + ", " + "");
        // loaders don't help that much:
        // instead: use async task to: query, reverse-geocode and add markers...
        // getLoaderManager().initLoader(MAP_REVIEWS_LOADER, null, this);
        super.onResume();
    }

    public void setMapType(int mapType) {
        mMapType = mapType;
        if (mMapReady) {
            mMap.setMapType(mMapType);
        }
    }

    private void createToolbar(View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                Log.e(LOG_TAG, "createToolbar - no supportActionBar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);
        } else {
            Log.v(LOG_TAG, "createToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void updateToolbar() {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            TextView titleView = (TextView) mRootView.findViewById(R.id.action_bar_title);
            titleView.setText(
                    getString(R.string.title_show_map));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapReady = true;
        mMap = googleMap;
        Log.v(LOG_TAG, "onMapReady, mapType; " + mMap.getMapType() + ", hashCode=" + this.hashCode() + ", " + "googleMap = [" + googleMap + "]");
        if (mMapType >= 0) {
            mMap.setMapType(mMapType);
        }
        if (mMarkers != null && !mMarkers.isEmpty()) {  //add all already populated markers

            addAllMarkersToMap();
        }
    }

    private void addAllMarkersToMap() {
        for (PopulateMapTask.MarkerInfo marker : mMarkers.values()) {
            mMap.addMarker(marker.getMarkerOptions());  //no content description available for Marker
        }
        mMap.setContentDescription(getActivity().getString(R.string.a11y_map_populated, mMarkers.size()));
    }

    private void moveToFirstEntry() {
        if (mMarkers != null && !mMarkers.isEmpty() && mMapReady) {
            Map.Entry<LatLng, PopulateMapTask.MarkerInfo> first = mMarkers.entrySet().iterator().next();
            Toast.makeText(
                    getActivity(),
                    getString(R.string.msg_map_navigate_to, first.getValue().location),
                    Toast.LENGTH_LONG).show();
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLng(first.getKey()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    private void fillInfoText() {
        List<String> rows = new ArrayList<>();
        rows.add(getString(R.string.info_map_heading, mMarkers.size()));
        for (PopulateMapTask.MarkerInfo marker : mMarkers.values()) {
            rows.add(marker.getTitle() + ":");
            rows.addAll(marker.reviews.values());
            rows.add("");
        }

        mInfoView.setText(Utils.joinMax("\n", rows, 50));
    }

    @Override
    public void onMapPopulated(Map<LatLng, PopulateMapTask.MarkerInfo> markers, String message) {
        Log.v(LOG_TAG, "onMapPopulated, hashCode=" + this.hashCode() + ", " + "markers = [" + markers + "], message = [" + message + "]");

        // just if activity is still alive
        if (getActivity() == null) {
            return;
        }

        if (!mAlreadyLoaded) {  // small workaround...
            Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
        }

        if (markers != null) {
            mMarkers = markers;
            if (!mAlreadyLoaded){
                moveToFirstEntry();
                mAlreadyLoaded = true;

            }
            if (mMapReady) {
                addAllMarkersToMap();
            }

            fillInfoText();
        }

    }
}
