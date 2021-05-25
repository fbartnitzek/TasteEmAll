package com.fbartnitzek.tasteemall.location;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbartnitzek.tasteemall.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

/**
 * Copyright 2017.  Frank Bartnitzek
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


public abstract class ShowBaseMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String LOG_TAG = ShowBaseMapFragment.class.getName();
    protected GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    protected int mMapType = -1;
    protected View mRootView;
    protected MarkerOptions mMarkerOptions;
    protected Marker mCurrentMarker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getTabLayout(), container, false);

        //src: http://stackoverflow.com/questions/15525111/getsupportfragmentmanager-findfragmentbyid-returns-null
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mMapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            Log.v(LOG_TAG, "onCreateView - calling getMapAsync, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            mMapFragment.getMapAsync(this);
        }

        return mRootView;
    }

    protected abstract int getTabLayout();

    public void setMapType(int mapType) {
        Log.v(LOG_TAG, "setMapType, hashCode=" + this.hashCode() + ", " + "mapType = [" + mapType + "]");
        mMapType = mapType;
        if (mMap != null) {
            Log.v(LOG_TAG, "setMapType, hashCode=" + this.hashCode() + ", " + "mapType = [" + mapType + "]");
            mMap.setMapType(mMapType);
        }
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        Log.v(LOG_TAG, "onMapReady, hashCode=" + this.hashCode() + ", " + "googleMap = [" + googleMap + "]");
        mMap = googleMap;
        Log.v(LOG_TAG, "onMapReady, mapType: " + mMap.getMapType());
        if (mMapType >= 0) {
            mMap.setMapType(mMapType);
        }

        tryUpdatingMarker();
    }

    protected void showMap() {
        if (mMapFragment != null && mMapFragment.getView() != null) {
            mMapFragment.getView().setVisibility(View.VISIBLE);
        }
    }

    protected void hideMap() {
        if (mMapFragment != null && mMapFragment.getView() != null) {
            mMapFragment.getView().setVisibility(View.INVISIBLE);
        }
    }

    protected void tryUpdatingMarker() {
        Log.v(LOG_TAG, "tryUpdatingMarker, mMap=" + mMap + ", mMarkerOptions=" + mMarkerOptions);
        if (mMap != null && mMarkerOptions != null) {
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            mCurrentMarker = mMap.addMarker(mMarkerOptions);

            Log.v(LOG_TAG, "tryUpdatingMarker - moving to marker at " + mMarkerOptions.getPosition());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mMarkerOptions.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

            // scroll down
            NestedScrollView nsv = mRootView.findViewById(R.id.nested_scrollView);
            nsv.fullScroll(View.FOCUS_DOWN);
        }
    }
}
