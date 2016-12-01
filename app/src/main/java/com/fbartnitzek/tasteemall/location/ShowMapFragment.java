package com.fbartnitzek.tasteemall.location;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.showentry.ShowReviewActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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


public class ShowMapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback{

    private static final String LOG_TAG = ShowMapFragment.class.getName();
    private static final int REVIEW_LOCATIONS_LOADER_ID = 23452;
    private static final int REVIEWS_OF_LOCATION_LOADER_ID = 34563;
    private static GoogleMap mMap;
//    private boolean mMapReady = false;
    private Uri mReviewsUri;
    private String mReviewsSortOrder;
    private int mMapType = -1;
    private View mRootView;
    private ReviewLocationAdapter mReviewLocationAdapter;
    private ReviewOfLocationAdapter mReviewOfLocationAdapter;
    private TextView mHeadingLocations;
    private Marker mCurrentMarker;
    private MarkerOptions mMarkerOptions = null;
    private int mReviewLocation_Id;
    private String mLocationName;
    private TextView mHeadingReviewsOfLocation;
    private Uri mReviewsOfLocationUri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_show_map, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.w(LOG_TAG, "onCreateView without args...");
        } else {
            Log.v(LOG_TAG, "onCreateView with args: " + args);
            if (args.containsKey(ShowMapActivity.EXTRA_REVIEWS_URI)) {
                mReviewsUri = args.getParcelable(ShowMapActivity.EXTRA_REVIEWS_URI);
                mReviewsSortOrder = args.getString(ShowMapActivity.EXTRA_REVIEWS_SORT_ORDER);
                getLoaderManager().restartLoader(REVIEW_LOCATIONS_LOADER_ID, null, this);
            }
        }

        createToolbar();
        updateToolbar();

        mReviewLocationAdapter = new ReviewLocationAdapter(getActivity(),
                new ReviewLocationAdapter.ReviewLocationAdapterClickHandler() {
                    @Override
                    public void onClick(int reviewLocation_Id, ReviewLocationAdapter.ViewHolder viewHolder, LatLng latLng, String formatted, String description) {
                        addReviewLocationMarker(reviewLocation_Id, latLng, formatted, description);
                    }
        });
        mHeadingLocations = (TextView) mRootView.findViewById(R.id.heading_map_locations);
        mHeadingLocations.setText(R.string.label_list_map_locations_preview);
        RecyclerView locationRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_map_locations);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationRecyclerView.setAdapter(mReviewLocationAdapter);


        mHeadingReviewsOfLocation = (TextView) mRootView.findViewById(R.id.heading_map_sub_list);
        mHeadingReviewsOfLocation.setText(R.string.label_list_map_reviews_of_location_preview);
        mReviewOfLocationAdapter = new ReviewOfLocationAdapter(new ReviewOfLocationAdapter.ReviewAdapterClickHandler() {
            @Override
            public void onClick(Uri contentUri, ReviewOfLocationAdapter.ViewHolder vh) {
                Bundle bundle = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    bundle = ActivityOptions.makeSceneTransitionAnimation(
                            getActivity(),
                            new Pair<View, String>(vh.drinkNameView, vh.drinkNameView.getTransitionName()),
                            new Pair<View, String>(vh.producerNameView, vh.producerNameView.getTransitionName())
                    ).toBundle();
                }

                startActivity(
                        new Intent(getActivity(), ShowReviewActivity.class).setData(contentUri),
                        bundle);
            }
        }, getActivity());
        RecyclerView reviewsRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_map_sub_list);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsRecyclerView.setAdapter(mReviewOfLocationAdapter);

        Log.v(LOG_TAG, "onCreateView before MapFragment, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");

        //src: http://stackoverflow.com/questions/15525111/getsupportfragmentmanager-findfragmentbyid-returns-null
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            Log.v(LOG_TAG, "onCreateView - calling getMapAsync, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            mapFragment.getMapAsync(this);
        }

        return mRootView;
    }

    public void setMapType(int mapType) {
        mMapType = mapType;
        if (mMap != null) {
            mMap.setMapType(mMapType);
        }
    }

    private void addReviewLocationMarker(int reviewLocation_Id, LatLng latLng, String formatted, String description) {
        Log.v(LOG_TAG, "addReviewLocationMarker, hashCode=" + this.hashCode() + ", " + "reviewLocation_Id = [" + reviewLocation_Id + "], latLng = [" + latLng + "]");

        mReviewLocation_Id = reviewLocation_Id;

        if (latLng != null && Utils.isValidLatLong(latLng.latitude, latLng.longitude)) {
            mLocationName = description == null || description.isEmpty() ? formatted : description;
            mMarkerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(mLocationName)
                    .snippet(formatted)
                    .draggable(false);
            tryUpdatingMarker();
        }

        updateReviews();
    }

    private void updateReviews() {
        Log.v(LOG_TAG, "updateReviews, mReviewLocation_Id=" + mReviewLocation_Id);
        mReviewsOfLocationUri = DatabaseContract.ReviewEntry
                .getReviewsOfLocationUriFromMapUri(mReviewsUri, mReviewLocation_Id);
        getLoaderManager().restartLoader(REVIEWS_OF_LOCATION_LOADER_ID, null, this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO!
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case REVIEW_LOCATIONS_LOADER_ID:
                return new CursorLoader(getActivity(),
                        mReviewsUri, QueryColumns.MapFragment.ReviewLocations.COLUMNS,
                        null, null, null);
            case REVIEWS_OF_LOCATION_LOADER_ID:
                // TODO: use original sort order?
                return new CursorLoader(getActivity(), mReviewsOfLocationUri,
                        QueryColumns.MapFragment.ReviewsOfLocationQuery.COLUMNS, null, null, null);
            default:
                throw new RuntimeException("wrong loaderId in " + ShowMapFragment.class.getSimpleName());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
//        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        int count = data == null ? 0 : data.getCount();
        switch (loader.getId()) {
            case REVIEW_LOCATIONS_LOADER_ID:
                Log.v(LOG_TAG, "onLoadFinished - swapping " + count + " ReviewLocation");
                mReviewLocationAdapter.swapCursor(data);
                mHeadingLocations.setText(getString(R.string.label_list_map_locations, count));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((ShowMapActivity)getActivity()).scheduleStartPostponedTransition(mHeadingLocations);
                }
                break;
            case REVIEWS_OF_LOCATION_LOADER_ID:
                Log.v(LOG_TAG, "onLoadFinished - swapping " + count + " Reviews of Location");
                mReviewOfLocationAdapter.swapCursor(data);
                mHeadingReviewsOfLocation.setText(getString(R.string.label_list_map_reviews_of_location, count));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
        switch (loader.getId()) {
            case REVIEW_LOCATIONS_LOADER_ID:
                mReviewLocationAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(LOG_TAG, "onMapReady, hashCode=" + this.hashCode() + ", " + "googleMap = [" + googleMap + "]");
        mMap = googleMap;
        Log.v(LOG_TAG, "onMapReady, mapType: " + mMap.getMapType());
        if (mMapType >= 0) {
            mMap.setMapType(mMapType);
        }

        tryUpdatingMarker();
    }

    private void tryUpdatingMarker() {
        Log.v(LOG_TAG, "tryUpdatingMarker, mMap=" + mMap + ", mMarkerOptions=" + mMarkerOptions);
        if (mMap != null && mMarkerOptions != null) {
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            mCurrentMarker = mMap.addMarker(mMarkerOptions);

            Log.v(LOG_TAG, "tryUpdatingMarker - moving to marker at " + mMarkerOptions.getPosition());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mMarkerOptions.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    private void createToolbar() {
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
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
            if (mLocationName != null) {
                titleView.setText(getString(R.string.title_show_map_location, mLocationName));
            } else {
                titleView.setText(getString(R.string.title_show_map));
            }
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }
}


