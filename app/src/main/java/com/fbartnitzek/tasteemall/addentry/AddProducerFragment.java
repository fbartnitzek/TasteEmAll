package com.fbartnitzek.tasteemall.addentry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import android.widget.EditText;
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
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 */
public class AddProducerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    private static final int EDIT_PRODUCER_LOADER_ID = 12345;
    private static final String STATE_PRODUCER_NAME = "STATE_PRODUCER_NAME";
    private static final String STATE_LOCATION = "STATE_LOCATION";
    private static final String STATE_PRODUCER_WEBSITE = "STATE_PRODUCER_WEBSITE";
    private static final String STATE_PRODUCER_DESCRIPTION = "STATE_PRODUCER_DESCRIPTION";
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";
    private static final String STATE_PRODUCER_ID = "STATE_PRODUCER_ID";

    private static final String STATE_GEOCODING_RUNNING = "STATE_GEOCODING_RUNNING";

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 32456;
    private static final String STATE_LOCATION_PARCELABLE = "STATE_LOCATION_PARCELABLE";
    private static final String STATE_LAST_LOCATION = "STATE_LAST_LOCATION";
    private static final String STATE_ORIGINAL_LOCATION_INPUT = "STATE_ORIGINAL_LOCATION_INPUT";
    private static final java.lang.String STATE_NETWORK_ERROR_SHOWN = "STATE_NETWORK_ERROR_SHOWN";

    private static View mRootView;
    private static EditText mEditProducerName;
    private static EditText mEditLocation;
    private static EditText mEditProducerWebsite;
    private static EditText mEditProducerDescription;

    private LocationArrayAdapter mLocationArrayAdapter;
    private static ListView mListView;
    private static GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private Marker mCurrentMarker = null;
    private Address[] mLocationAddresses;
    private int mPosition = ListView.INVALID_POSITION;


    private String mProducerName;

    private static final String LOG_TAG = AddProducerFragment.class.getName();
    private Uri mContentUri = null;
    private String mProducerId = null;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
//    private boolean mGeocodingRunning = false;
    private LocationParcelable mLocationParcelable;
    private String mLocationInput;
    private String mOriginalLocationInput = null;
    private boolean mNetworkErrorShown = false;

    public AddProducerFragment() {
        // Required empty public constructor
        mProducerName = "";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        mLocationArrayAdapter = new LocationArrayAdapter(getActivity());
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CONTENT_URI)) {
                mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
            }
            if (savedInstanceState.containsKey(STATE_PRODUCER_ID)) {
                mProducerId = savedInstanceState.getString(STATE_PRODUCER_ID);
            }

            if (savedInstanceState.containsKey(STATE_LOCATION_PARCELABLE)) {
                mLocationParcelable = savedInstanceState.getParcelable(STATE_LOCATION_PARCELABLE);
            }

            if (savedInstanceState.containsKey(STATE_ORIGINAL_LOCATION_INPUT)) {
                mOriginalLocationInput = savedInstanceState.getString(STATE_ORIGINAL_LOCATION_INPUT);
            }

            mNetworkErrorShown = savedInstanceState.getBoolean(STATE_NETWORK_ERROR_SHOWN, false);

//            mGeocodingRunning = savedInstanceState.getBoolean(STATE_GEOCODING_RUNNING, false);
        }
        setRetainInstance(true);
        mResultReceiver = new AddressResultReceiver(new Handler());

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_producer, container, false);

        mEditProducerName = (EditText) mRootView.findViewById(R.id.producer_name);
//        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "producerName = " + mProducerName);
        mEditProducerName.setText(mProducerName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditProducerName.setTransitionName(getString(R.string.shared_transition_add_producer_name));
        }

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        // usually hide it first
        hideMap();
        if (mMapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mMapFragment.getMapAsync(this);
        }

        mEditLocation = (EditText) mRootView.findViewById(R.id.producer_location);
        mEditLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    hideMap();
                    mLocationParcelable = null;
                    mLocationInput = s.toString();;
                    startGeocodeService(s.toString());
                }
            }
        });

        mRootView.findViewById(R.id.location_here_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "v = [" + v + "]");
                getCurrentLocation();
            }
        });

        mEditProducerWebsite = (EditText) mRootView.findViewById(R.id.producer_website);
        mEditProducerDescription = (EditText) mRootView.findViewById(R.id.producer_description);

        if (savedInstanceState != null) {
            mEditProducerName.setText(savedInstanceState.getString(STATE_PRODUCER_NAME));
            mEditProducerWebsite.setText(savedInstanceState.getString(STATE_PRODUCER_WEBSITE));
            mEditProducerDescription.setText(savedInstanceState.getString(STATE_PRODUCER_DESCRIPTION));

            if (savedInstanceState.containsKey(STATE_LOCATION)) {
                mEditLocation.setText(savedInstanceState.getString(STATE_LOCATION));
            }

            if (savedInstanceState.containsKey(STATE_LAST_LOCATION)) {
                mLastLocation = savedInstanceState.getParcelable(STATE_LAST_LOCATION);
            }
        }

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
//                if (getActivity().getCurrentFocus() != mListView) {
//                    return;
//                }

                mPosition = position;
                Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");

                showMap();
                focusOnMap();
                Address address = mLocationArrayAdapter.getItem(mPosition);
                mLocationParcelable = Utils.getLocationFromAddress(address, mLocationInput, null);
                updateAndMoveToMarker();
            }
        });

        createToolbar();

        if (mContentUri == null) {
            resumeActivityEnterTransition();    // from add
        }

        return mRootView;
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
//                Log.v(LOG_TAG, "createToolbar - no supportActionBar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }

            supportActionBar.setDisplayHomeAsUpEnabled(true);  //false: not visible anymore
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableProducer = getString(Utils.getReadableProducerNameId(getActivity(), drinkType));
            if (mContentUri != null) {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_producer_activity_preview,
                                readableProducer));
            } else {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_drink_activity,
                                readableProducer));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void updateToolbar(String producerName) {
//        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                    getString(R.string.title_edit_producer_activity,
                            producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: other states...
        outState.putString(STATE_PRODUCER_NAME, mEditProducerName.getText().toString().trim().trim());
        outState.putString(STATE_LOCATION, mEditLocation.getText().toString().trim());
        outState.putString(STATE_PRODUCER_WEBSITE, mEditProducerWebsite.getText().toString().trim());
        outState.putString(STATE_PRODUCER_DESCRIPTION, mEditProducerDescription.getText().toString().trim());
        outState.putBoolean(STATE_NETWORK_ERROR_SHOWN, mNetworkErrorShown);

        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }
        if (mProducerId != null) {
            outState.putString(STATE_PRODUCER_ID, mProducerId);
        }
        if (mOriginalLocationInput  != null) {
            outState.putString(STATE_ORIGINAL_LOCATION_INPUT, mOriginalLocationInput);
        }
        if (mLocationParcelable != null) {
            outState.putParcelable(STATE_LOCATION_PARCELABLE, mLocationParcelable);
        }

//        outState.putBoolean(STATE_GEOCODING_RUNNING, mGeocodingRunning);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        if (mContentUri != null) {
            getLoaderManager().initLoader(EDIT_PRODUCER_LOADER_ID, null, this);
        }

        super.onActivityCreated(savedInstanceState);
    }


    // save

    void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");
        mProducerName = mEditProducerName.getText().toString().trim();
        mLocationInput = mEditLocation.getText().toString().trim();

        //validate
        if (mProducerName.length() == 0) {
            Snackbar.make(mRootView, R.string.msg_enter_producer_name, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (mLocationInput.length() == 0) {
            Snackbar.make(mRootView, R.string.msg_enter_producer_location, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mContentUri != null) { //update

            updateData();   // location exists

        } else { // insert

            insertData();
        }
    }

    private void insertData() {
        Log.v(LOG_TAG, "insertData, hashCode=" + this.hashCode() + ", " + "");

        if (mLocationParcelable != null) {  // address shown in map, use it
            Log.v(LOG_TAG, "insertData with parcelable, formattedAddress=" + mLocationParcelable.getFormattedAddress());
        } else {
            if (mLastLocation != null) {    // latLng without geocoded address: use latLng with GEOCODE_ME
                mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation, null);
                Log.v(LOG_TAG, "insertData with latLng, latLng=" + mLocationParcelable.getInput());
            } else {                        // save input-string with GEOCODE_ME
                mLocationParcelable = Utils.createLocationStubFromInput(
                        mEditLocation.getText().toString(), null);
                Log.v(LOG_TAG, "insertData with inputString, input=" + mLocationParcelable.getInput());
            }
        }

        // use parcelable
        insertProducer();
    }


    private void insertProducer() {
        Log.v(LOG_TAG, "insertProducer, hashCode=" + this.hashCode() + ", " + "");

        if (mLocationParcelable == null) {
            Log.e(LOG_TAG, "insertProducer without locationParcelable!!!");
            return;
        }

        new InsertEntryTask(
                getActivity(), DatabaseContract.ProducerEntry.CONTENT_URI, mRootView, mProducerName)
                .execute(DatabaseHelper.buildProducerValues(
                        Utils.calcProducerId(mProducerName, mLocationParcelable.getInput()),
                        mProducerName,
                        mEditProducerDescription.getText().toString().trim(),
                        mEditProducerWebsite.getText().toString().trim(),
                        mLocationParcelable.getInput(),
                        mLocationParcelable.getLatitude(),
                        mLocationParcelable.getLongitude(),
                        mLocationParcelable.getCountry(),
                        mLocationParcelable.getFormattedAddress()
                ));
    }


    public String getProducerName() {
        return mProducerName;
    }

    public void setProducerName(String producerName) {
        this.mProducerName = producerName;
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
            Toast.makeText(AddProducerFragment.this.getActivity(), R.string.msg_no_location_access, Toast.LENGTH_SHORT).show();

            return;
        } else {
            Log.v(LOG_TAG, "getCurrentLocation - with permission");
        }

        if (CustomApplication.isGoogleApiClientConnected()) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(CustomApplication.getGoogleApiClient());
            if (mLastLocation != null) {
                Log.v(LOG_TAG, "getCurrentLocation - startGeocodeServiceByLatLng");

//                startGeocodeServiceByLatLng();
                startGeocodeService(null);
                return;
            }
        }
        Toast.makeText(AddProducerFragment.this.getActivity(), R.string.msg_no_location_provided, Toast.LENGTH_SHORT).show();

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



    // TODO: refactor with AddReviewFragment - geocoder usage...
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
                        mLocationArrayAdapter.clear();
                        mLocationArrayAdapter.addAll(mLocationAddresses);
                        mPosition = 0;
                        simulateClick();
                    }

                } else if (resultData.containsKey(GeocodeIntentService.RESULT_ADDRESSES_KEY)) {  // by text

                    mLocationAddresses = Utils.castParcelableArray(Address.class,
                            resultData.getParcelableArray(GeocodeIntentService.RESULT_ADDRESSES_KEY));
                    mLocationArrayAdapter.clear();
                    mLocationArrayAdapter.addAll(mLocationAddresses);
                    if (mLocationAddresses != null && mLocationAddresses.length == 1) {
                        mPosition = 0;
                        simulateClick();
                    }

                } else {
                    Log.e(LOG_TAG, "onReceiveResult - SUCCESS without address - should never happen...");
                }

            } else if (getActivity() == null) { // fast enough on back button - useless result

//                mGeocodingRunning = false;

                return;

            } else {  // somehow failed

                if (mLastLocation != null) {    //queried by here
                    // geocode later
                    if (Utils.isNetworkUnavailable(getActivity())) {
                        Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
//                        mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation, null);
                        updateLocationText();
                    } else if (GeocodeIntentService.FAILURE_SERVICE_NOT_AVAILABLE == resultCode) {
                        Toast.makeText(getActivity(), R.string.msg_service_not_available, Toast.LENGTH_LONG).show();
//                        mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation, null);
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



    // edit - use loaders

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, mContentUri=" + mContentUri + ", hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mContentUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mContentUri,
                    QueryColumns.ProducerFragment.ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...
            int producer_Id = data.getInt(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER__ID);
            String name = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_NAME);
            mEditProducerName.setText(name);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mEditProducerName.setTransitionName(
                        getString(R.string.shared_transition_producer_producer) + producer_Id);
            }

            mOriginalLocationInput = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_INPUT);
            String formatted = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_FORMATTED_ADDRESS);
            mLocationParcelable = new LocationParcelable(
                    LocationParcelable.INVALID_ID,
                    data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_COUNTRY),
                    "",
                    data.getDouble(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_LATITUDE),
                    data.getDouble(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_LONGITUDE),
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

            String website = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_WEBSITE);
            mEditProducerWebsite.setText(website);
            String description = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_DESCRIPTION);
            mEditProducerDescription.setText(description);
            mProducerId = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_ID);

            updateToolbar(name);

            resumeActivityEnterTransition();    // from edit

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");

    }

    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((AddProducerActivity) getActivity()).scheduleStartPostponedTransition(mEditProducerName);
        }
    }

    // save for edit (update)

    private void updateData() {
        Log.v(LOG_TAG, "updateData, hashCode=" + this.hashCode() + ", " + "");
        Uri singleProducerUri = Utils.calcSingleProducerUri(mContentUri);
        if (mLocationParcelable == null) {
            Toast.makeText(getActivity(), "no locationParcelable - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        } else if (mOriginalLocationInput == null) {
            Toast.makeText(getActivity(), "no originalLocationInput - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.v(LOG_TAG, "updateData, locationParcelable=" + mLocationParcelable.toString());

        new UpdateEntryTask(getActivity(), singleProducerUri, mProducerName, mRootView)
                .execute(DatabaseHelper.buildProducerUpdateValues(
                        mProducerName,
                        mEditProducerDescription.getText().toString().trim(),
                        mEditProducerWebsite.getText().toString().trim(),
                        mOriginalLocationInput,
                        mLocationParcelable.getLatitude(),
                        mLocationParcelable.getLongitude(),
                        mLocationParcelable.getCountry(),
                        mLocationParcelable.getFormattedAddress()));
    }
}
