package com.fbartnitzek.tasteemall;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


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

public class ShowMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String LOG_TAG = ShowMapFragment.class.getName();
    GoogleMap mMap;
    boolean mMapReady = false;
    private Uri mUri;
    private View mRootView;
    private int mMapType = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_show_map, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.v(LOG_TAG, "onCreateView without args...");
        } else {
            // TODO: something with args...
        }

        createToolbar(mRootView, LOG_TAG);
        updateToolbar();

        //src: http://stackoverflow.com/questions/15525111/getsupportfragmentmanager-findfragmentbyid-returns-null
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mapFragment.getMapAsync(this);
            Log.v(LOG_TAG, "onCreateView, MapFragment found & set...");
        }

        return mRootView;
    }

    public void setMapType(int mapType) {
        mMapType = mapType;
        if (mMapReady) {
            mMap.setMapType(mMapType);
        }
    }

    void createToolbar(View rootView, String LOG_TAG) {
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

    void updateToolbar() {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
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
        Toast.makeText(ShowMapFragment.this.getActivity(), "map is ready", Toast.LENGTH_SHORT).show();
    }
}
