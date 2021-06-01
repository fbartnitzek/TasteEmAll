package com.fbartnitzek.tasteemall.addentry;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.fbartnitzek.tasteemall.CustomApplication;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.location.AddressData;
import com.fbartnitzek.tasteemall.location.GeocodeWorker;
import com.fbartnitzek.tasteemall.location.LocationDataArrayAdapter;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public abstract class GeocoderFragment extends Fragment implements
        OnMapReadyCallback, TextWatcher, AdapterView.OnItemClickListener {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 32456;
    private static final String LOG_TAG = GeocoderFragment.class.getName();
    private static final String GEOCODE_WORKER = "GeocoderFragment_GeocodeWorker";
    private static final String STATE_ADDRESS = "STATE_ADDRESS";
    private static final String STATE_LAST_LOCATION = "STATE_LAST_LOCATION";
    private static final String STATE_NETWORK_ERROR_SHOWN = "STATE_NETWORK_ERROR_SHOWN";

    View rootView;
    TextView editLocation;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    AddressData address;
    private AddressData[] addresses;
    private LocationDataArrayAdapter locationDataArrayAdapter;
    private Location lastLocation;
    private Marker currentMarker;
    private boolean networkErrorShown = false;
    private int position;
    private int ignoreEdit = 0;

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        if (lastLocation != null) {
            outState.putParcelable(STATE_LAST_LOCATION, lastLocation);
        }
        if (address != null) {
            outState.putParcelable(STATE_ADDRESS, address);
        }
        outState.putBoolean(STATE_NETWORK_ERROR_SHOWN, networkErrorShown);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_ADDRESS)) {
                address = savedInstanceState.getParcelable(STATE_ADDRESS);
            }
            if (savedInstanceState.containsKey(STATE_LAST_LOCATION)) {
                lastLocation = savedInstanceState.getParcelable(STATE_LAST_LOCATION);
            }
            networkErrorShown = savedInstanceState.getBoolean(STATE_NETWORK_ERROR_SHOWN, false);
        }
        locationDataArrayAdapter = new LocationDataArrayAdapter(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // usually hide it first
        hideMap();
        if (mapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mapFragment.getMapAsync(this);
        }

        ListView listView = rootView.findViewById(R.id.listview_locations);
        listView.setAdapter(locationDataArrayAdapter);
        if (addresses != null && addresses.length > 0) {
            locationDataArrayAdapter.addAll(addresses);
            if (ListView.INVALID_POSITION != position){
                listView.smoothScrollToPosition(position);
                listView.setItemChecked(position, true);
            }
        }

        listView.setOnItemClickListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @NotNull
    Optional<String> calcCountryCode(String countryName) {
        Optional<String> countryCode = Optional.empty();
        if (countryName != null) {
            Log.v(LOG_TAG, "countryName of loaded entry: " + countryName);
            countryCode = Arrays.stream(Locale.getISOCountries())
                    .filter(code -> countryName.equals(new Locale("", code).getDisplayCountry()))
                    .findFirst();
        } else {
            Log.v(LOG_TAG, "countryName of loaded entry is null!");
        }
        return countryCode;
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
        if (Utils.isGeocodeMeLatLong(address)) {
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
                .enqueueUniqueWork(GEOCODE_WORKER, ExistingWorkPolicy.REPLACE, workRequest);

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


    void skipNextGeocoding(){
        ignoreEdit = 1;
    }

    LocationParcelable createLocationParcelable() {
        LocationParcelable parcelable;
        if (address == null) {
            // use stubs
            if (lastLocation != null) {    // latLng without geocoded address: use latLng with GEOCODE_ME
                parcelable = Utils.getLocationStubFromLastLocation(lastLocation, null);
                Log.v(LOG_TAG, "insertData with latLng, latLng=" + parcelable.getInput());
            } else {                        // save input-string with GEOCODE_ME
                parcelable = Utils.createLocationStubFromInput(
                        editLocation.getText().toString(),
                        null);
                Log.v(LOG_TAG, "insertData with inputString, input=" + parcelable.getInput());
            }
        } else {
            parcelable = Utils.getLocationFromAddressData(address, null);
        }
        return parcelable;
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

        // modify editLocation for latLong, but skip mod
        if (outData.getBoolean(GeocodeWorker.GEOCODE_TYPE_LAT_LONG, false)) {
            ignoreEdit = 1;
            editLocation.setText(address.getFormatted());
        }
        updateAndMoveToMarker();
    }

    void showValidPosition(){
        if (Utils.isValidLatLong(address.getLatitude(), address.getLongitude())) {
            updateAndMoveToMarker();
        } else {
            hideMap();
        }
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

    private void updateAndMoveToMarker() {
        showMap();
        Log.v(LOG_TAG, "updateAndMoveToMarker, map=[" + map + "], address=[" + address + "]");
        if (map != null && address != null) {
            // todo: weird marker behaviour on updates
            //  but is not parcelable, has no id, but survives...
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

    void getCurrentLocation() {
        Log.v(LOG_TAG, "getCurrentLocation, hashCode=" + this.hashCode() + ", " + "");
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            handlePermission(); //request permissions - may help
            Log.v(LOG_TAG, "getCurrentLocation - no permission");
            Toast.makeText(GeocoderFragment.this.getActivity(),
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
                            GeocoderFragment.this.getActivity(),
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

    private void handlePermission() {
        // src: FilePickerFragment
        // https://developer.android.com/training/permissions/requesting.html
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        map = googleMap;
        updateAndMoveToMarker();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        position = pos;
        Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        showMap();
        focusOnMap();
        address = locationDataArrayAdapter.getItem(pos);
        Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "address = [" + address + "]");
        updateAndMoveToMarker();
        resetLocationId();
    }

    abstract void resetLocationId();

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
}
