package com.fbartnitzek.tasteemall.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.tasks.InsertLocationTask;
import com.fbartnitzek.tasteemall.ui.OnTouchHideKeyboardListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

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

public class ChooseLocationFragment extends Fragment implements SearchView.OnQueryTextListener, OnMapReadyCallback, InsertLocationTask.InsertLocationHandler {

    private static final String STATE_LOCATION_ADDRESSES = "STATE_LOCATION_ADDRESSES";
    private static final String STATE_LOCATION_INPUT = "STATE_LOCATION_INPUT";
    private static final String STATE_LOCATION_POSITION = "STATE_LOCATION_POSITION";
    private Address[] mLocationAddresses;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String LOG_TAG = ChooseLocationFragment.class.getName();
    private String mLocationInput;
    private static View mRootView;
    private static SearchView mLocationInputView;
    private LocationArrayAdapter mLocationArrayAdapter;
    private AddressResultReceiver mResultReceiver;
    private boolean mGeocodingRunning = false;
    private boolean mGeocodingDone = false;
    private ListView mListView;
    private GoogleMap mMap;
    private Marker mCurrentMarker = null;
    private NestedScrollView mScrollView;
    private SupportMapFragment mMapFragment;

    public ChooseLocationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        mLocationArrayAdapter = new LocationArrayAdapter(getActivity());
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_LOCATION_ADDRESSES)) {
                mLocationAddresses = Utils.castParcelableArray(Address.class,
                        savedInstanceState.getParcelableArray(STATE_LOCATION_ADDRESSES));
            }

            mLocationInput = savedInstanceState.getString(STATE_LOCATION_INPUT, "");
            mPosition = savedInstanceState.getInt(STATE_LOCATION_POSITION, ListView.INVALID_POSITION);
        }
        //        setHasOptionsMenu(true);
        setRetainInstance(true);
        mResultReceiver = new AddressResultReceiver(new Handler());

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mLocationAddresses != null) {
            outState.putParcelableArray(STATE_LOCATION_ADDRESSES, mLocationAddresses);
        }
        if (mLocationInput != null) {
            outState.putString(STATE_LOCATION_INPUT, mLocationInput);
        }
        outState.putInt(STATE_LOCATION_POSITION, mPosition);

        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_choose_location, container, false);

        mLocationInputView = (SearchView) mRootView.findViewById(R.id.location_input);
        mLocationInputView.setOnQueryTextListener(this);
        Log.v(LOG_TAG, "onCreateView with locationInput=" + this.mLocationInput + ", mLocationAddresses=" + Arrays.toString(mLocationAddresses));
        if (mLocationInput != null) {
            mLocationInputView.onActionViewExpanded();
            mLocationInputView.setQuery(mLocationInput, mLocationAddresses == null);
        }

//        mLocationsRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_locations);
//        mLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mLocationArrayAdapter = new LocationArrayAdapter(getActivity());
//        if (mLocationAddresses != null) {
//            mLocationArrayAdapter.bindData(mLocationAddresses);
//        }
//        mLocationsRecyclerView.setAdapter(mLocationArrayAdapter);

//        mScrollView = (NestedScrollView) mRootView.findViewById(R.id.scrollView);
//        if (mScrollView == null) {
//            Log.v(LOG_TAG, "onCreateView, scrollView == null");
//        }

        mListView = (ListView) mRootView.findViewById(R.id.listview_locations);
        mListView.setAdapter(mLocationArrayAdapter);

        if (mLocationAddresses != null && mLocationAddresses.length > 0) {
            mLocationArrayAdapter.addAll(mLocationAddresses);

            if (ListView.INVALID_POSITION != mPosition) {
                mListView.smoothScrollToPosition(mPosition);
                mListView.setItemChecked(mPosition, true);
            }
        }

        mListView.setOnTouchListener(new OnTouchHideKeyboardListener(this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
//                mListView.setItemChecked(mPosition, true);
                //TODO: callback...?
                focusOnMap();
                Address address = mLocationArrayAdapter.getItem(mPosition);
//                Toast.makeText(getActivity(), "selected: " + Utils.formatAddress(address), Toast.LENGTH_SHORT).show();
                updateAndMoveToMarker(address);
            }
        });

        createToolbar();
        updateToolbar();

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mMapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mMapFragment.getMapAsync(this);
//            Log.v(LOG_TAG, "onCreateView, MapFragment found & set...");
        }

        // TODO: react on save!!

        return mRootView;
    }

    //TODO: on save - contains ? - save placeholder

    private void focusOnMap() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "focusOnMap-run, hashCode=" + this.hashCode() + ", " + "");
                //TODO: scrolling not working!!!
                // map seldom override list... - somehow wrong...
                NestedScrollView scrollView = (NestedScrollView) getActivity().findViewById(R.id.nested_scrollView);

                if (scrollView != null) {
                    Log.v(LOG_TAG, "focusOnMap-run, hashCode=" + this.hashCode() + ", position=" + scrollView.getBottom() + "");
                    scrollView.smoothScrollTo(0, scrollView.getBottom());
                } else {
                    Log.v(LOG_TAG, "focusOnMap-run, scrollView == null");
                }
            }
        });
    }

    private void updateAndMoveToMarker(Address address) {
        if (mMap != null) {
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mCurrentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(address.getCountryName())
                    .snippet(Utils.formatAddress(address))
                    .draggable(false));
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLng(latLng));
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
            titleView.setText(
                    getString(R.string.title_choose_location));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    public void setmLocationAddresses(Address[] mLocationAddresses) {
        Log.v(LOG_TAG, "setmLocationAddresses, hashCode=" + this.hashCode() + ", " + "mLocationAddresses = [" + Arrays.toString(mLocationAddresses) + "]");
        this.mLocationAddresses = mLocationAddresses;
    }

    public void setLocationInput(String locationInput) {
        Log.v(LOG_TAG, "setLocationInput, hashCode=" + this.hashCode() + ", " + "locationInput = [" + locationInput + "]");
        this.mLocationInput = locationInput;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v(LOG_TAG, "onQueryTextSubmit, hashCode=" + this.hashCode() + ", " + "query = [" + query + "]");
        startGeocodeServiceByName(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v(LOG_TAG, "onQueryTextChange, hashCode=" + this.hashCode() + ", " + "newText = [" + newText + "]");
        startGeocodeServiceByName(newText);
        return false;
    }

    private void startGeocodeServiceByName(String address) {
        Log.v(LOG_TAG, "startGeocodeServiceByName, hashCode=" + this.hashCode() + ", " + "address = [" + address + "]");
        Intent intent = new Intent(this.getActivity(), GeocodeIntentService.class);
        if (mResultReceiver == null) {
            Log.e(LOG_TAG, "startGeocodeService - no resultReceiver found!, hashCode=" + this.hashCode() + ", " + "");
            return;
        }
        intent.putExtra(GeocodeIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(GeocodeIntentService.EXTRA_LOCATION_TEXT, address);
        getActivity().startService(intent);
        mGeocodingRunning = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mPosition != ListView.INVALID_POSITION) {
            Address address = mLocationArrayAdapter.getItem(mPosition);
            updateAndMoveToMarker(address);
        }
    }

    public void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", mMap=" + mMap + ", mPosition=" + mPosition);

        if (mMap != null && mLocationArrayAdapter != null && !mLocationArrayAdapter.isEmpty()
                && mPosition != ListView.INVALID_POSITION) {

            Address address = mLocationArrayAdapter.getItem(mPosition);
            new InsertLocationTask(
                    getActivity(), mRootView,
                    mLocationInput, this)
                    .execute(DatabaseHelper.buildLocationValues(
                            Utils.getLocationFromAddress(address, mLocationInput, "")
                    ));
        }
    }

    @Override
    public void onInsertedLocation(Uri uri, String mEntryName, String locationId) {
        Log.v(LOG_TAG, "onInsertedLocation, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "], mEntryName = [" + mEntryName + "], locationId = [" + locationId + "]");
        Intent output = new Intent();
        output.setData(uri);    //always returns single-uri
        getActivity().setResult(Activity.RESULT_OK, output);
        getActivity().finish();
    }


    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from GeocodeIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.v(LOG_TAG, "onReceiveResult, hashCode=" + this.hashCode() + ", " + "resultCode = [" + resultCode + "], resultData = [" + resultData + "]");
            // Display the address string or an error message sent from the intent service.
            mGeocodingDone = true;
            if (GeocodeIntentService.SUCCESS_RESULT == resultCode) {
                // already optimized address before geocoder was called, new address

                if (resultData.containsKey(GeocodeIntentService.RESULT_ADDRESS_KEY)) {  // by latLng
                    mLocationAddresses = new Address[]{
                            resultData.getParcelable(GeocodeIntentService.RESULT_ADDRESS_KEY)};

                } else if (resultData.containsKey(GeocodeIntentService.RESULT_ADDRESSES_KEY)) {  // by text

                    mLocationAddresses = Utils.castParcelableArray(Address.class,
                            resultData.getParcelableArray(GeocodeIntentService.RESULT_ADDRESSES_KEY));

                }
//                mLocationArrayAdapter.bindData(mLocationAddresses);    //TODO: fill
                mLocationArrayAdapter.clear();
                mLocationArrayAdapter.addAll(mLocationAddresses);

                // TODO: ideal = get old position, if changed: reselect
                if (mLocationAddresses.length == 1) {
                    mListView.setItemChecked(0, true);
                    updateAndMoveToMarker(mLocationAddresses[0]);
//                    mListView.setItemChecked(0, true);
                    // selected?
//                    mLocationsRecyclerView.findViewHolderForLayoutPosition(0).itemView.setSelected(true);
                } else {
//                    mLocationsRecyclerView.setSelected(false);
                }

            }   //ignore errors for now...

            mGeocodingRunning = false;

        }
    }
}
