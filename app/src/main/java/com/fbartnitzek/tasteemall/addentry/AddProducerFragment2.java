package com.fbartnitzek.tasteemall.addentry;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.fbartnitzek.tasteemall.CustomApplication;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.location.AddressData;
import com.fbartnitzek.tasteemall.location.GeocodeWorker;
import com.fbartnitzek.tasteemall.location.LocationDataArrayAdapter;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.fbartnitzek.tasteemall.data.QueryColumns.ProducerFragment.ShowQuery;

public class AddProducerFragment2 extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    private static final int EDIT_PRODUCER_LOADER_ID = 12345;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 32456;
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";
    private static final String STATE_ADDRESS = "STATE_ADDRESS";
    private static final String STATE_ORIGINAL_LOCATION_INPUT = "STATE_ORIGINAL_LOCATION_INPUT";
    private static final String STATE_NETWORK_ERROR_SHOWN = "STATE_NETWORK_ERROR_SHOWN";
    private static final String STATE_LAST_LOCATION = "STATE_LAST_LOCATION";
    private static final String STATE_PRODUCER_NAME = "STATE_PRODUCER_NAME";
    private static final String STATE_PRODUCER_WEBSITE = "STATE_PRODUCER_WEBSITE";
    private static final String STATE_PRODUCER_DESCRIPTION = "STATE_PRODUCER_DESCRIPTION";
    private static final String STATE_LOCATION = "STATE_LOCATION";

    private static final String LOG_TAG = AddProducerFragment2.class.getName();
    private String producerName;
    private Uri contentUri;

    private View rootView;
    private EditText editProducerName;
    private EditText editLocation;
    private EditText editProducerWebsite;
    private EditText editProducerDescription;
    private SupportMapFragment mapFragment;
    private static GoogleMap map;

    private String originalLocationInput;

    // todo: both needed?
    private boolean networkErrorShown = false;

    private LocationDataArrayAdapter locationDataArrayAdapter;
    private Location lastLocation;
    private ListView listView;
    private int position;
    private AddressData address;
    private LocationParcelable locationParcelable;
    private AddressData[] addresses;
    private Marker currentMarker;
    private int ignoreEdit = 0;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        locationDataArrayAdapter = new LocationDataArrayAdapter(getActivity());
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    // todo: test rotation: rotation in show before edit breaks activity-mContentUri
    // todo: remove locationParcelable?
    // todo: other location-fragments...

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_CONTENT_URI)) {
            contentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
        }
        if (savedInstanceState.containsKey(STATE_ADDRESS)) {
            address = savedInstanceState.getParcelable(STATE_ADDRESS);
        }
        if (savedInstanceState.containsKey(STATE_ORIGINAL_LOCATION_INPUT)) {
            originalLocationInput = savedInstanceState.getString(STATE_ORIGINAL_LOCATION_INPUT);
        }
        networkErrorShown = savedInstanceState.getBoolean(STATE_NETWORK_ERROR_SHOWN, false);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        rootView = inflater.inflate(R.layout.fragment_add_producer, container, false);
        editProducerName = rootView.findViewById(R.id.producer_name);
        editProducerName.setTransitionName(getString(R.string.shared_transition_add_producer_name));
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        // usually hide it first
        hideMap();
        if (mapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mapFragment.getMapAsync(this);
        }

        editLocation = rootView.findViewById(R.id.producer_location);
        editLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (ignoreEdit > 0) {
                    ignoreEdit--;
                } else {
                    // todo: somehow not every change, only every ms
                    if (s.length() > 1) {
                        hideMap();
                        startGeocodeWorker(s.toString());
                    }
                }
            }
        });

        rootView.findViewById(R.id.location_here_button).setOnClickListener(v -> getCurrentLocation());
        editProducerWebsite = rootView.findViewById(R.id.producer_website);
        editProducerDescription = rootView.findViewById(R.id.producer_description);

        if (savedInstanceState != null) {
            editProducerName.setText(savedInstanceState.getString(STATE_PRODUCER_NAME));
            editProducerWebsite.setText(savedInstanceState.getString(STATE_PRODUCER_WEBSITE));
            editProducerDescription.setText(savedInstanceState.getString(STATE_PRODUCER_DESCRIPTION));
            if (savedInstanceState.containsKey(STATE_LOCATION)) {
                editLocation.setText(savedInstanceState.getString(STATE_LOCATION));
            }
            if (savedInstanceState.containsKey(STATE_LAST_LOCATION)) {
                lastLocation = savedInstanceState.getParcelable(STATE_LAST_LOCATION);
            }
        }

        listView = rootView.findViewById(R.id.listview_locations);
        listView.setAdapter(locationDataArrayAdapter);
        if (addresses != null && addresses.length > 0) {
            locationDataArrayAdapter.addAll(addresses);

            if (ListView.INVALID_POSITION != position){
                listView.smoothScrollToPosition(position);
                listView.setItemChecked(position, true);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                position = pos;
                Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");

                showMap();
                focusOnMap();
                address = locationDataArrayAdapter.getItem(pos);
                Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "address = [" + address + "]");
                updateAndMoveToMarker();
            }
        });

        createToolbar();
        if (contentUri == null) {
            resumeActivityEnterTransition();    // from add
        }
        return rootView;
    }


    private void getCurrentLocation() {
        Log.v(LOG_TAG, "getCurrentLocation, hashCode=" + this.hashCode() + ", " + "");
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            handlePermission(); //request permissions - may help

            Log.v(LOG_TAG, "getCurrentLocation - no permission");
            Toast.makeText(AddProducerFragment2.this.getActivity(),
                    R.string.msg_no_location_access, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.v(LOG_TAG, "getCurrentLocation - with permission");
        }

        if (CustomApplication.isGoogleApiClientConnected()) {
            LocationServices.getFusedLocationProviderClient(getActivity())
                    .getLastLocation()
                    .addOnSuccessListener(location -> {
                        lastLocation = location;
                        Log.v(LOG_TAG, "getCurrentLocation - startGeocodeServiceByLatLng");
                        startGeocodeWorker(null);
                        hideKeyboard();
                    })
                    .addOnFailureListener(e -> Toast.makeText(
                            AddProducerFragment2.this.getActivity(),
                            R.string.msg_last_location_not_found, Toast.LENGTH_SHORT).show());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity())
                .getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(
                    currentFocus.getWindowToken(),
                    0); //InputMethodManager.HIDE_NOT_ALWAYS also possible
        }
    }

    private void startGeocodeWorker(String input) {
        if (getActivity() == null || Utils.isNetworkUnavailable(getActivity())) {
            if (!networkErrorShown) {
                Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
                networkErrorShown = true;
            }
            return;
        }

        Data.Builder dataBuilder = new Data.Builder();
        if (isGeocodeMeLatLong(address)) {
            dataBuilder.putDouble(GeocodeWorker.INPUT_LATITUDE, address.getLatitude());
            dataBuilder.putDouble(GeocodeWorker.INPUT_LONGITUDE, address.getLongitude());
        } else if (input != null) {
            dataBuilder.putString(GeocodeWorker.INPUT_TEXT, input);
        } else if (lastLocation != null) {
            dataBuilder.putDouble(GeocodeWorker.INPUT_LATITUDE, lastLocation.getLatitude());
            dataBuilder.putDouble(GeocodeWorker.INPUT_LONGITUDE, lastLocation.getLongitude());
        }
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(GeocodeWorker.class)
                .setInputData(dataBuilder.build())
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();
        WorkManager
                .getInstance(Objects.requireNonNull(getContext()))
                .enqueue(workRequest);

        WorkManager
                .getInstance(Objects.requireNonNull(getContext()))
                .getWorkInfoByIdLiveData(workRequest.getId())
                .observe(getViewLifecycleOwner(), info -> {
                    if (info != null && info.getState().isFinished()) {
                        if (info.getState() == WorkInfo.State.SUCCEEDED) {
                            Data outData = info.getOutputData();
                            Log.d(LOG_TAG, "worker finished with "
                                    + outData.getKeyValueMap().size()
                                    + " results");
                            if (outData.getKeyValueMap().size() > 4) {
                                updateAddressesFromWorkerOutput(outData);
                            }
                        } else {
                            Toast.makeText(getActivity(),
                                    "GeocodeJob Failed: " + info.getOutputData().getString(GeocodeWorker.OUTPUT_ERROR),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isGeocodeMeLatLong(AddressData address) {
        return address != null
                && DatabaseContract.LocationEntry.GEOCODE_ME.equals(address.getFormatted())
                && Utils.isValidLatLong(address.getLatitude(), address.getLongitude());
    }

    private void updateAddressesFromWorkerOutput(Data outData) {
        int len = (outData.getKeyValueMap().size() - 1) / 5;
        AddressData[] tmpAddresses = new AddressData[len];
        Log.d(LOG_TAG, len + " entries found");
        String origInput = outData.getString(GeocodeWorker.ORIG_INPUT);
        for (int i = 0; i < len; i++){
            String country = outData.getString(i + GeocodeWorker.COUNTRY);
            String countryCode = outData.getString(i + GeocodeWorker.COUNTRY_CODE);
            String formatted = outData.getString(i + GeocodeWorker.FORMATTED);
            double latitude = outData.getDouble(i + GeocodeWorker.LATITUDE, GeocodeWorker.ILLEGAL_LAT_LONG);
            double longitude = outData.getDouble(i + GeocodeWorker.LONGITUDE, GeocodeWorker.ILLEGAL_LAT_LONG);
            tmpAddresses[i] = new AddressData(latitude, longitude, countryCode, country,
                    formatted, origInput);
        }
        Log.d(LOG_TAG, "switching addresses with " + Arrays.toString(tmpAddresses));
        addresses = tmpAddresses;
        locationDataArrayAdapter.clear();
        locationDataArrayAdapter.addAll(addresses);

        position = 0;
        address = addresses[position];

        // modify editlocation for latLong, but skip mod
        if (outData.getBoolean(GeocodeWorker.GEOCODE_TYPE_LAT_LONG, false)) {
            ignoreEdit = 1;
            editLocation.setText(address.getFormatted());
        }
        updateAndMoveToMarker();
    }

    private void handlePermission() {
        // src: FilePickerFragment
        // https://developer.android.com/training/permissions/requesting.html
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }


    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Objects.requireNonNull(activity).setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                Log.v(LOG_TAG, "createToolbar - no supportActionBar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }

            supportActionBar.setDisplayHomeAsUpEnabled(true);  //false: not visible anymore
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableProducer = getString(Utils.getReadableProducerNameId(
                    getActivity(), drinkType));
            if (contentUri != null) {
                ((TextView) rootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_producer_activity_preview,
                                readableProducer));
            } else {
                ((TextView) rootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_drink_activity,
                                readableProducer));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void updateToolbar(String producerName) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            ((TextView) rootView.findViewById(R.id.action_bar_title))
                    .setText(getString(R.string.title_edit_producer_activity, producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        }
    }

    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");
        ((AddProducerActivity) Objects.requireNonNull(getActivity()))
                .scheduleStartPostponedTransition(editProducerName);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    // map parts

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        map = googleMap;
        updateAndMoveToMarker();
    }

    private void hideMap() {
        if (mapFragment != null && mapFragment.getView() != null) {
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
    }

    private void showMap() {
        if (mapFragment != null && mapFragment.getView() != null) {
            mapFragment.getView().setVisibility(View.VISIBLE);
        }
    }

    private void focusOnMap() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "focusOnMap-run, hashCode=" + this.hashCode() + ", " + "");

                NestedScrollView scrollView = Objects.requireNonNull(getActivity())
                        .findViewById(R.id.nested_scrollView);
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
        showMap();
        Log.v(LOG_TAG, "updateAndMoveToMarker, map=[" + map + "], address=[" + address + "]");
        if (map != null && address != null) {
            if (currentMarker != null) {
                Log.v(LOG_TAG, "remove marker");
                currentMarker.remove();
            }
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            Log.v(LOG_TAG, "add marker for " + address.getFormatted() + " on " + latLng.toString());
            currentMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(address.getCountryName())
                    .snippet(address.getFormatted())
                    .draggable(false));

            Log.v(LOG_TAG, "move cam and zoom");

            map.animateCamera(
                    CameraUpdateFactory
                            .newCameraPosition(new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(9)
                                    .build()),
                    2000, null);
        } else {
            Log.v(LOG_TAG, "map or address not ready");
        }
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setContentUri(Uri contentUri) {
        Log.v(LOG_TAG, "set contentUri to " + contentUri);
        this.contentUri = contentUri;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (contentUri != null) {
            LoaderManager.getInstance(this).initLoader(EDIT_PRODUCER_LOADER_ID, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, contentUri=" + contentUri + ", hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (contentUri != null && getActivity() != null) {
            return new CursorLoader(
                    getActivity(),
                    contentUri,
                    ShowQuery.COLUMNS,
                    null,
                    null,
                    null
            );
        } else {
            Log.wtf(LOG_TAG, "could not create loader!, contentUri=" + contentUri);
            // todo: what would be appropriate?
            return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...

            String name = data.getString(ShowQuery.COL_PRODUCER_NAME);
            editProducerName.setText(name);
            editProducerName.setTransitionName(
                    getString(R.string.shared_transition_producer_producer)
                            + data.getInt(ShowQuery.COL_PRODUCER__ID));

            originalLocationInput = data.getString(ShowQuery.COL_PRODUCER_INPUT);
            String formatted = data.getString(ShowQuery.COL_PRODUCER_FORMATTED_ADDRESS);
            String countryName = data.getString(ShowQuery.COL_PRODUCER_COUNTRY);
            Optional<String> countryCode = Optional.empty();
            if (countryName != null) {
                Log.v(LOG_TAG, "countryName of loaded entry: " + countryName);
                countryCode = Arrays.stream(Locale.getISOCountries())
                        .filter(code -> countryName.equals(new Locale("", code).getDisplayCountry()))
                        .findFirst();
            } else {
                Log.v(LOG_TAG, "countryName of loaded entry is null!");
            }

            address = new AddressData(
                    data.getDouble(ShowQuery.COL_PRODUCER_LATITUDE),
                    data.getDouble(ShowQuery.COL_PRODUCER_LONGITUDE),
                    countryCode.orElse("NA"),
                    countryName,
                    data.getString(ShowQuery.COL_PRODUCER_FORMATTED_ADDRESS),
                    data.getString(ShowQuery.COL_PRODUCER_INPUT)
            );

            if (!Utils.isNetworkUnavailable(Objects.requireNonNull(getActivity()))
                    && DatabaseContract.LocationEntry.GEOCODE_ME.equals(formatted)) {
                // geocode
                editLocation.setText(originalLocationInput);
            } else {
                ignoreEdit = 1;
                editLocation.setText(address.getFormatted());
            }

            if (Utils.isValidLatLong(address.getLatitude(), address.getLongitude())) {
                updateAndMoveToMarker();
            } else {
                hideMap();
            }

            String website = data.getString(ShowQuery.COL_PRODUCER_WEBSITE);
            editProducerWebsite.setText(website);
            String description = data.getString(ShowQuery.COL_PRODUCER_DESCRIPTION);
            editProducerDescription.setText(description);

            updateToolbar(name);
            resumeActivityEnterTransition();    // from edit
        }
    }

    public void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");
        producerName = editProducerName.getText().toString().trim();

        //validate
        if (producerName.length() == 0) {
            Snackbar.make(rootView, R.string.msg_enter_producer_name, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (contentUri != null) { //update
            updateData();   // location exists
        } else { // insert
            createLocationParcelable();
            insertData();
        }
    }

    private void createLocationParcelable() {
        if (address == null) {
            // use stubs
            if (lastLocation != null) {    // latLng without geocoded address: use latLng with GEOCODE_ME
                locationParcelable = Utils.getLocationStubFromLastLocation(lastLocation, null);
                Log.v(LOG_TAG, "insertData with latLng, latLng=" + locationParcelable.getInput());
            } else {                        // save input-string with GEOCODE_ME
                locationParcelable = Utils.createLocationStubFromInput(
                        editLocation.getText().toString(), null);
                Log.v(LOG_TAG, "insertData with inputString, input=" + locationParcelable.getInput());
            }
        } else {
            locationParcelable = Utils.getLocationFromAddressData(address, null);
        }
    }

    private void insertData() {
        Log.v(LOG_TAG, "insertProducer, hashCode=" + this.hashCode() + ", " + "");
        if (locationParcelable == null) {
            Log.e(LOG_TAG, "insertProducer without locationParcelable!!!");
            return;
        }
        if (locationParcelable.getInput() == null) {
            Snackbar.make(rootView, R.string.msg_enter_producer_location, Snackbar.LENGTH_SHORT).show();
            return;
        }
        new InsertEntryTask(
                getActivity(), DatabaseContract.ProducerEntry.CONTENT_URI, rootView, producerName)
                .execute(DatabaseHelper.buildProducerValues(
                        Utils.calcProducerId(producerName, locationParcelable.getInput()),
                        producerName,
                        editProducerDescription.getText().toString().trim(),
                        editProducerWebsite.getText().toString().trim(),
                        locationParcelable.getInput(),
                        locationParcelable.getLatitude(),
                        locationParcelable.getLongitude(),
                        locationParcelable.getCountry(),
                        locationParcelable.getFormattedAddress()
                ));
    }

    private void updateData() {
        Log.v(LOG_TAG, "updateData, hashCode=" + this.hashCode() + ", " + "");
        Uri singleProducerUri = Utils.calcSingleProducerUri(contentUri);
        if (address == null) {
            Toast.makeText(getActivity(), "no locationParcelable - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        } else if (originalLocationInput == null) {
            Toast.makeText(getActivity(), "no originalLocationInput - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.v(LOG_TAG, "updateData, address=" + address.toString());
        new UpdateEntryTask(getActivity(), singleProducerUri, producerName, rootView)
                .execute(DatabaseHelper.buildProducerUpdateValues(
                        producerName,
                        editProducerDescription.getText().toString().trim(),
                        editProducerWebsite.getText().toString().trim(),
                        originalLocationInput,
                        address.getLatitude(),
                        address.getLongitude(),
                        address.getCountryName(),
                        address.getFormatted()));
    }
}
