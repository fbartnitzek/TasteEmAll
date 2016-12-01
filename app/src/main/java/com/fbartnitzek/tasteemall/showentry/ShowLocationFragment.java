package com.fbartnitzek.tasteemall.showentry;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

public class ShowLocationFragment extends ShowBaseFragment implements OnMapReadyCallback {

    private static final String LOG_TAG = ShowLocationFragment.class.getName();
    private static final int SHOW_LOCATION_LOADER_ID = 432;
    private View mRootView;
    private TextView mLocationCountryView;
    private TextView mLocationAddressView;
    private TextView mLocationDescriptionView;
    private TextView mLocationLatitudeView;
    private TextView mLocationLongitudeView;
    private TextView mLocationInputView;
    private TextView mLocationIdView;

    private static GoogleMap mMap;

    private LatLng mLatLng;
    private Uri mUri;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_show_location, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.w(LOG_TAG, "onCreateView without args - should never happen...");
        } else {
            if (args.containsKey(ShowLocationActivity.EXTRA_PRODUCER_URI)) {
                mUri = args.getParcelable(ShowLocationActivity.EXTRA_PRODUCER_URI);
                calcCompleteUri();
            }
        }

        mLocationInputView = (TextView) mRootView.findViewById(R.id.location_input);
        mLocationIdView = (TextView) mRootView.findViewById(R.id.location_id);
        mLocationCountryView = (TextView) mRootView.findViewById(R.id.location_country);
        mLocationAddressView = (TextView) mRootView.findViewById(R.id.location_address);
        mLocationDescriptionView = (TextView) mRootView.findViewById(R.id.location_description);
        mLocationLatitudeView = (TextView) mRootView.findViewById(R.id.location_latitude);
        mLocationLongitudeView = (TextView) mRootView.findViewById(R.id.location_longitude);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        createToolbar(mRootView, LOG_TAG);

        return mRootView;
    }

    @Override
    void calcCompleteUri() {
        mUri = DatabaseContract.LocationEntry.buildUri(DatabaseContract.getIdFromUri(mUri));
    }

    @Override
    void updateToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {

            String formatted = mLocationAddressView.getText().toString();
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(formatted);
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onResume() {
        getLoaderManager().initLoader(SHOW_LOCATION_LOADER_ID, null, this);
        super.onResume();
    }

    @Override
    void updateFragment(Uri uri) {
        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "]");
        mUri = uri;
        calcCompleteUri();
        getLoaderManager().restartLoader(SHOW_LOCATION_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    QueryColumns.LocationFragment.ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        } else {
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            mLocationAddressView.setText(data.getString(QueryColumns.LocationFragment.ShowQuery.COL_FORMATTED_ADDRESS));
            double lat = data.getDouble(QueryColumns.LocationFragment.ShowQuery.COL_LATITUDE);
            mLocationLongitudeView.setText(Double.toString(lat));
            double longitude = data.getDouble(QueryColumns.LocationFragment.ShowQuery.COL_LONGITUDE);
            mLocationLatitudeView.setText(Double.toString(longitude));
            mLocationDescriptionView.setText(data.getString(QueryColumns.LocationFragment.ShowQuery.COL_DESCRIPTION));
            mLocationInputView.setText(data.getString(QueryColumns.LocationFragment.ShowQuery.COL_INPUT));
            mLocationCountryView.setText(data.getString(QueryColumns.LocationFragment.ShowQuery.COL_COUNTRY));
            mLocationIdView.setText(data.getString(QueryColumns.LocationFragment.ShowQuery.COL_ID));

            mLatLng = new LatLng(lat, longitude);
            updateToolbar();

            updateAndMoveToMarker();
        }
    }

    private void updateAndMoveToMarker() {
        Log.v(LOG_TAG, "updateAndMoveToMarker, mMap=" + mMap + ", mLatLng=" + mLatLng);
        if (mMap != null && mLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .title(mLocationDescriptionView.getText().toString())
                    .snippet(mLocationAddressView.getText().toString())
                    .draggable(false));
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLng(mLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateAndMoveToMarker();
    }
}
