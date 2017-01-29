package com.fbartnitzek.tasteemall.addentry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.CustomApplication;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.location.GeocodeIntentService;
import com.fbartnitzek.tasteemall.location.LocationArrayAdapter;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.fbartnitzek.tasteemall.ui.OnTouchHideKeyboardListener;
import com.google.android.gms.location.LocationServices;
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

public class AddLocationFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    private static final String LOG_TAG = AddLocationFragment.class.getName();
    private static final int EDIT_LOCATION_LOADER_ID = 5432;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 54323;

    private static final String STATE_LOCATION_PARCELABLE = "STATE_LOCATION_PARCELABLE";
    private static final String STATE_LOCATION_INPUT = "STATE_LOCATION_INPUT";
    private static final String STATE_LOCATION_DESCRIPTION = "STATE_LOCATION_DESCRIPTION";
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";

    private View mRootView;
//    private EditText mEditLocation;
    private LocationAutoCompleteTextView mEditLocation;
    private AutoCompleteTextView mEditLocationDescription;
//    private EditText mEditLocationDescription;

    private LocationArrayAdapter mLocationListAdapter;
    private CompletionLocationAdapter mLocationAdapter;
    private CompletionLocationDescriptionAdapter mLocationDescriptionAdapter;
    private ListView mListView;
    private static GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private Marker mCurrentMarker = null;
    private Address[] mLocationAddresses;
    private int mPosition = ListView.INVALID_POSITION;
    private LocationParcelable mLocationParcelable;
    private Location mLastLocation;
    private boolean mNetworkErrorShown = false;

    private AddressResultReceiver mResultReceiver;
    private String mLocationInput;
    private Uri mContentUri;
    private String mOriginalLocationInput;
    private String mLocationDescription;
    private long mLocation_Id;
    private String mLocationId;

    // TODO: when used from startGeocode: switch to external app and switch back will reload values from db...
    // quite complicated to restore in the right way -> later...


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CONTENT_URI)) {
            mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
        }
        mLocationListAdapter = new LocationArrayAdapter(getActivity());

        setRetainInstance(true);
        mResultReceiver = new AddressResultReceiver(new Handler());

        super.onCreate(savedInstanceState);
    }

    // TODO: query known locations in AddLocationActivity and reuse them!

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_location, container, false);

        //        mEditLocationDescription = (EditText) mRootView.findViewById(R.id.location_description);
        mEditLocationDescription = (AutoCompleteTextView) mRootView.findViewById(R.id.location_description);
        mEditLocationDescription.setThreshold(1);
        mLocationDescriptionAdapter = new CompletionLocationDescriptionAdapter(getActivity());
        mEditLocationDescription.setAdapter(mLocationDescriptionAdapter);

        mEditLocationDescription.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                mLocation_Id = c.getLong(QueryColumns.LocationPart.CompletionQuery.COL__ID);
                mLocationId = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
                Log.v(LOG_TAG, "editDescription.onItemClick Cursor with locationId=" + mLocationId);

                if (mEditLocation.getText().toString().isEmpty()) {
                    String location = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_FORMATTED_ADDRESS);
                    if (location != null && !location.isEmpty()) {
                        mEditLocation.setText(location);    // TODO: no reloading - should work
                    }
                }
            }
        });

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        hideMap();
        if (mMapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mMapFragment.getMapAsync(this);
        }

        mEditLocation = (LocationAutoCompleteTextView) mRootView.findViewById(R.id.location_location);
        mEditLocation.setThreshold(1);
        mLocationAdapter = new CompletionLocationAdapter(getActivity(), true);
        mEditLocation.setAdapter(mLocationAdapter);
//        mEditLocation = (EditText) mRootView.findViewById(R.id.location_location);
        mEditLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: for now: just show it
                if (s.length() > 1) {
                    hideMap();
                    mLocationParcelable = null;
                    mLocationInput = s.toString();
                    startGeocodeService(s.toString());
                }
            }
        });

        mEditLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                mLocation_Id = c.getLong(QueryColumns.LocationPart.CompletionQuery.COL__ID);
                mLocationId = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
                Log.v(LOG_TAG, "editLocation.onItemClick Cursor with locationId=" + mLocationId);

                if (mEditLocationDescription.getText().toString().isEmpty()) {
                    String description = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_DESCRIPTION);
                    if (description != null && !description.isEmpty()) {
                        mEditLocationDescription.setText(description);
                        mEditLocationDescription.setEnabled(false);
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_LOCATION_INPUT)) {
                mLocationInput = savedInstanceState.getString(STATE_LOCATION_INPUT);
            }
            if (savedInstanceState.containsKey(STATE_LOCATION_DESCRIPTION)) {
                mLocationDescription = savedInstanceState.getString(STATE_LOCATION_DESCRIPTION);
            }
        }

        if (mLocationInput != null) {
            mEditLocation.setText(mLocationInput);
        }
        if (mLocationDescription != null) {
            mEditLocationDescription.setText(mLocationDescription);
        }

        mRootView.findViewById(R.id.location_here_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "here.onClick, hashCode=" + this.hashCode() + ", " + "v = [" + v + "]");
                getCurrentLocation();
            }
        });


        mListView = (ListView) mRootView.findViewById(R.id.listview_locations);
        mListView.setAdapter(mLocationListAdapter);

        if (mLocationAddresses != null && mLocationAddresses.length > 0) {
            mLocationListAdapter.addAll(mLocationAddresses);

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
                Log.v(LOG_TAG, "listview.onItemClick, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");

                showMap();
                focusOnMap();

                Address address = mLocationListAdapter.getItem(mPosition);
                mLocationParcelable = Utils.getLocationFromAddress(address, mLocationInput, null);

                updateAndMoveToMarker();

                //TODO: if other...? remove mLocationId/_Id
                //TODO if addresses not equal, but nearby (same town, latLng, street ...?): ask, else remove
                // at first: same text
                if (!mLocationInput.equals(mEditLocation.getText().toString())) {
                    Log.v(LOG_TAG, "listview.onItemClick, resetting mLocationId");
                    mLocationId = null;
                } else {
                    Log.v(LOG_TAG, "listview.onItemClick, same text - keep mLocationId");
                }

            }
        });

        createToolbar();

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_LOCATION_INPUT, mEditLocation.getText().toString());
        outState.putString(STATE_LOCATION_DESCRIPTION, mEditLocationDescription.getText().toString());
        if (mLocationParcelable != null) {
            outState.putParcelable(STATE_LOCATION_PARCELABLE, mLocationParcelable);
        }
        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }
        super.onSaveInstanceState(outState);
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                return;
            }

            supportActionBar.setDisplayHomeAsUpEnabled(true);  //false: not visible anymore
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(R.string.title_search_location);

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }


    public void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");

        mLocationInput = mEditLocation.getText().toString().trim();

        // validate ...
        if (mLocationInput.length() == 0 ) {
            Snackbar.make(mRootView, R.string.msg_enter_location, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mContentUri != null) { //update

            updateData();   // location exists

        } else { // insert

            Log.v(LOG_TAG, "saveData with mLocationId=" + mLocationId);
            if (mLocationId != null) {
                finish();
            } else {
                insertData();
            }


        }
    }

    private void finish() {
        Log.v(LOG_TAG, "finish, hashCode=" + this.hashCode() + ", " + "");
        Intent output = new Intent();
        output.setData(DatabaseContract.LocationEntry.buildUri(mLocation_Id));
        getActivity().setResult(Activity.RESULT_OK, output);
        getActivity().finish();
    }

    private void insertData() {
        Log.v(LOG_TAG, "insertData, hashCode=" + this.hashCode() + ", " + "");

        // TODO: check if location already exists...

        if (mLocationParcelable == null) {
            if (mLastLocation != null) {    // latLng without geocoded address: use latLng with GEOCODE_ME
                // TODO: needed? already with locationParcelable
                mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation,
                        mEditLocationDescription.getText().toString());
                Log.v(LOG_TAG, "insertData with latLng, latLng=" + mLocationParcelable.getInput());
            } else {                        // save input-string with GEOCODE_ME
                mLocationParcelable = Utils.createLocationStubFromInput(
                        mEditLocation.getText().toString(), mEditLocationDescription.getText().toString());
                Log.v(LOG_TAG, "insertData with inputString, input=" + mLocationParcelable.getInput());
            }
        }

        // insert Location
        new InsertEntryTask(
                getActivity(), DatabaseContract.LocationEntry.CONTENT_URI, mRootView, mLocationInput)
                .execute(DatabaseHelper.buildLocationValues(
                        Utils.calcLocationId(mLocationParcelable.getInput()),
                        mLocationParcelable.getInput(),
                        mLocationParcelable.getLatitude(),
                        mLocationParcelable.getLongitude(),
                        mLocationParcelable.getCountry(),
                        mLocationParcelable.getFormattedAddress(),
                        mEditLocationDescription.getText().toString()
                ));
    }

    private void updateData() {

        Log.v(LOG_TAG, "updateData, hashCode=" + this.hashCode() + ", " + "");
        Uri locationUri = Utils.calcSingleLocationUri(mContentUri);
        if (mLocationParcelable == null) {
            Toast.makeText(getActivity(), "no locationParcelable - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        } else if (mOriginalLocationInput == null) {
            Toast.makeText(getActivity(), "no originalLocationInput - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.v(LOG_TAG, "updateData, locationParcelable=" + mLocationParcelable.toString());

        new UpdateEntryTask(getActivity(), locationUri, mLocationParcelable.getFormattedAddress(), mRootView)
                .execute(DatabaseHelper.buildLocationUpdateValues(
                        mLocationParcelable.getLatitude(),
                        mLocationParcelable.getLongitude(),
                        mLocationParcelable.getCountry(),
                        mLocationParcelable.getFormattedAddress(),
                        mEditLocationDescription.getText().toString()));
    }

    private void simulateClick() {
        Log.v(LOG_TAG, "simulateClick, hashCode=" + this.hashCode() + ", mPosition=" + mPosition);
        mListView.performItemClick(
                mListView.getAdapter().getView(mPosition, null, null),
                mPosition,
                mListView.getAdapter().getItemId(mPosition));
    }


    // permissions

    private void handlePermission() {
        // src: FilePickerFragment
        // https://developer.android.com/training/permissions/requesting.html
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_LOCATION_PERMISSION_CODE == requestCode && permissions.length > 0) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0] ||
                    PackageManager.PERMISSION_GRANTED == grantResults[grantResults.length - 1]) { // at least one allowed
//                Log.v(LOG_TAG, "onRequestPermissionsResult - permission granted, try again!");
                getCurrentLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // map parts

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateAndMoveToMarker();
    }

    private void hideMap() {
        if (mMapFragment != null && mMapFragment.getView() != null) {
            mMapFragment.getView().setVisibility(View.INVISIBLE);
        }

    }

    private void showMap() {
        if (mMapFragment != null && mMapFragment.getView() != null) {
            mMapFragment.getView().setVisibility(View.VISIBLE);
        }
    }

    private void focusOnMap() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "focusOnMap-run, hashCode=" + this.hashCode() + ", " + "");

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

    private void updateAndMoveToMarker() {
        if (mMap != null && mLocationParcelable != null) {
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            LatLng latLng = new LatLng(mLocationParcelable.getLatitude(), mLocationParcelable.getLongitude());
            mCurrentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(mLocationParcelable.getCountry())
                    .snippet(mLocationParcelable.getFormattedAddress())
                    .draggable(false));
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }


    public void setInput(String input) {
        this.mLocationInput = input;
    }

    public void setDescription(String description) {
        this.mLocationDescription = description;
    }

    public void setContentUri(Uri contentUri) {
        this.mContentUri = contentUri;
    }


    private void updateLocationText() {
        Log.v(LOG_TAG, "updateLocationText - mLastLocation: " + mLastLocation + ", hashCode=" + this.hashCode() + ", " + "");
        if (mEditLocation != null) {
            if (mLocationParcelable != null) {
                if (mLocationParcelable.getFormattedAddress() != null) {
                    mEditLocation.setText(mLocationParcelable.getFormattedAddress());
                }
            }   // no updates if no parcelable location...

            if (mLocationParcelable == null && mLastLocation != null) { // geocoder not reachable
                Log.v(LOG_TAG, "updateLocationText, call geocoder later - happens ever? hashCode=" + this.hashCode() + ", " + "");
                mEditLocation.setText(R.string.call_geocoder_later);
            }
        }
    }


    // GoogleApi-lastLocation

    private void getCurrentLocation() {
        Log.v(LOG_TAG, "getCurrentLocation, hashCode=" + this.hashCode() + ", " + "");
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            handlePermission(); //request permissions - may help

            Log.v(LOG_TAG, "getCurrentLocation - no permission");
            Toast.makeText(AddLocationFragment.this.getActivity(), R.string.msg_no_location_access, Toast.LENGTH_SHORT).show();

            return;
        } else {
            Log.v(LOG_TAG, "getCurrentLocation - with permission");
        }

        if (CustomApplication.isGoogleApiClientConnected()) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(CustomApplication.getGoogleApiClient());
            if (mLastLocation != null) {
                Log.v(LOG_TAG, "getCurrentLocation - startGeocodeServiceByLatLng, mLastLocation=" + mLastLocation);

                startGeocodeService(null);
                return;
            }
        }
        Toast.makeText(AddLocationFragment.this.getActivity(), R.string.msg_no_location_provided, Toast.LENGTH_SHORT).show();

    }

// Geocoder

    private void startGeocodeService(String address) {
        Log.v(LOG_TAG, "startGeocodeService, address=" + address + ", lastLocation=" + mLastLocation);

        if (Utils.isNetworkUnavailable(getActivity())){
            if (!mNetworkErrorShown) {
                Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
                mNetworkErrorShown = true;
            }

            if (address == null && mLastLocation != null) {
                // bugfix 2
                mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation,
                        mEditLocationDescription.getText().toString());
                updateLocationText(); // now might use mLastLocation
            }
            return;
        }

        if (mResultReceiver == null) {
            Log.e(LOG_TAG, "startGeocodeService - no resultReceiver found!, hashCode=" + this.hashCode() + ", " + "");
            return;
        }
        Intent intent = new Intent(this.getActivity(), GeocodeIntentService.class);
        intent.putExtra(GeocodeIntentService.RECEIVER, mResultReceiver);
        if (address != null) {
            intent.putExtra(GeocodeIntentService.EXTRA_LOCATION_TEXT, address);
        } else if (mLastLocation != null) {
            intent.putExtra(GeocodeIntentService.EXTRA_LOCATION_LATLNG, mLastLocation);
        }

        getActivity().startService(intent);
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
            if (getActivity() == null) {    // if changes happen while saving - ignore them...
                return;
            }
            Log.v(LOG_TAG, "onReceiveResult, hashCode=" + this.hashCode() + ", " + "resultCode = [" + resultCode + "], resultData = [" + resultData + "]");
            // Display the address string or an error message sent from the intent service.

            if (GeocodeIntentService.SUCCESS_RESULT == resultCode) {
                // already optimized address before geocoder was called, new address

                if (resultData.containsKey(GeocodeIntentService.RESULT_ADDRESS_KEY)) {  // by latLng
                    Address address = resultData.getParcelable(GeocodeIntentService.RESULT_ADDRESS_KEY);

                    if (address == null) {  // TODO: better error handling
                        Log.e(LOG_TAG, "onReceiveResult: address result = NULL!!!");
                    } else {
                        mLocationInput = Utils.getLocationInput(address.getLatitude(), address.getLongitude());
                        mEditLocation.setText(mLocationInput);

                        mLocationAddresses = new Address[]{address};
                        mLocationListAdapter.clear();
                        mLocationListAdapter.addAll(mLocationAddresses);
                        mPosition = 0;
                        simulateClick();
                    }

                } else if (resultData.containsKey(GeocodeIntentService.RESULT_ADDRESSES_KEY)) {  // by text

                    mLocationAddresses = Utils.castParcelableArray(Address.class,
                            resultData.getParcelableArray(GeocodeIntentService.RESULT_ADDRESSES_KEY));
                    mLocationListAdapter.clear();
                    mLocationListAdapter.addAll(mLocationAddresses);
                    if (mLocationAddresses != null && mLocationAddresses.length == 1) {
                        mPosition = 0;
                        simulateClick();
                    }

                } else {
                    Log.e(LOG_TAG, "onReceiveResult - SUCCESS without address - should never happen...");
                }

            } else if (getActivity() == null) { // fast enough on back button - useless result

                return;

            } else {  // somehow failed

                if (mLastLocation != null) {    //queried by here
                    // geocode later
                    if (Utils.isNetworkUnavailable(getActivity())) {
                        Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
                        updateLocationText();
                    } else if (GeocodeIntentService.FAILURE_SERVICE_NOT_AVAILABLE == resultCode) {
                        Toast.makeText(getActivity(), R.string.msg_service_not_available, Toast.LENGTH_LONG).show();
                        updateLocationText();
                    }
                } else {
                    int toastRes;
                    switch (resultCode) {
                        case GeocodeIntentService.FAILURE_INVALID_LAT_LONG_USED:
                            toastRes = R.string.msg_invalid_lat_long;
                            break;
                        case GeocodeIntentService.FAILURE_NO_LOCATION_DATA_PROVIDED:
                            toastRes = R.string.msg_no_location_provided;
                            break;
                        default:
                            return;
                        // ignore that one - TODO: just show it for "complete search" - how to detect...?
//                        case GeocodeIntentService.FAILURE_NO_RESULT_FOUND:
//                            toastRes = R.string.msg_no_address_found;
//                            break;
//                        default:
//                            toastRes = R.string.msg_no_location_generic;
                    }

                    Toast.makeText(getActivity(), toastRes, Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    @Override
    public void onResume() {    //TODO: onActivityCreated or onResume for loader.init?
        if (mContentUri != null) {
            Log.v(LOG_TAG, "onResume with contentUri - edit, hashCode=" + this.hashCode() + ", " + "");
            getLoaderManager().initLoader(EDIT_LOCATION_LOADER_ID, null, this);
        } else {
            Log.v(LOG_TAG, "onResume without contentUri - add, hashCode=" + this.hashCode() + ", " + "");
        }
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

        if (mContentUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mContentUri,
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
            // variables not really needed - optimize later...
            int location_Id = data.getInt(QueryColumns.LocationFragment.ShowQuery.COL__ID);
            String desc = data.getString(QueryColumns.LocationFragment.ShowQuery.COL_DESCRIPTION);
            mEditLocationDescription.setText(desc);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mEditProducerName.setTransitionName(
//                        getString(R.string.shared_transition_producer_producer) + producer_Id);
//            }

            mOriginalLocationInput = data.getString(QueryColumns.LocationFragment.ShowQuery.COL_INPUT);
            String formatted = data.getString(QueryColumns.LocationFragment.ShowQuery.COL_FORMATTED_ADDRESS);
            mLocationParcelable = new LocationParcelable(
                    LocationParcelable.INVALID_ID,
                    data.getString(QueryColumns.LocationFragment.ShowQuery.COL_COUNTRY),
                    "",
                    data.getDouble(QueryColumns.LocationFragment.ShowQuery.COL_LATITUDE),
                    data.getDouble(QueryColumns.LocationFragment.ShowQuery.COL_LONGITUDE),
                    mOriginalLocationInput,
                    formatted,
                    null);

            if (!Utils.isNetworkUnavailable(getActivity()) && DatabaseContract.LocationEntry.GEOCODE_ME.equals(formatted)) {
                // geocode
                mEditLocation.setText(mOriginalLocationInput);
            } else {
                updateLocationText();
            }

            if (Utils.isValidLocation(mLocationParcelable)) {
                showMap();
                updateAndMoveToMarker();
            } else {
                hideMap();
            }

//            updateToolbar(name);
//            resumeActivityEnterTransition();    // from edit

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
