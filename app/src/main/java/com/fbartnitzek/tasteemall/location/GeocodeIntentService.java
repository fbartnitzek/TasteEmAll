package com.fbartnitzek.tasteemall.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

public class GeocodeIntentService extends IntentService {

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_NO_LOCATION_DATA_PROVIDED = 1;  // TODO: other msg?
    public static final int FAILURE_SERVICE_NOT_AVAILABLE = 2;
    public static final int FAILURE_INVALID_LAT_LONG_USED = 3;
    public static final int FAILURE_NO_RESULT_FOUND = 4;

    private static final String PACKAGE_NAME = GeocodeIntentService.class.getPackage().getName();

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_ADDRESS_KEY = PACKAGE_NAME + ".RESULT_ADDRESS_KEY";
    public static final String RESULT_ADDRESSES_KEY = PACKAGE_NAME + ".RESULT_ADDRESSES_KEY";
    public static final String RESULT_ADDRESSES_INPUT = PACKAGE_NAME + ".RESULT_ADDRESSES_INPUT";
    public static final String RESULT_MSG_KEY = PACKAGE_NAME + ".RESULT_MSG_KEY";

    public static final String EXTRA_LOCATION_LATLNG = PACKAGE_NAME + ".EXTRA_LOCATION_LATLNG";
    public static final String EXTRA_LOCATION_TEXT = PACKAGE_NAME + ".EXTRA_LOCATION_TEXT";


    private ResultReceiver mReceiver = null;
    private static final String LOG_TAG = GeocodeIntentService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GeocodeIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage;

        if (intent.hasExtra(RECEIVER)) {
            mReceiver = intent.getParcelableExtra(RECEIVER);
        }

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(LOG_TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // geocoding: formattedAddress => LatLng
        // reverse geocoding: LatLng => formattedAddress
        boolean reverseGeocoding = intent.hasExtra(EXTRA_LOCATION_LATLNG);

        if (!reverseGeocoding && !intent.hasExtra(EXTRA_LOCATION_TEXT)) {
            errorMessage = getString(R.string.msg_no_location_provided);
            Log.wtf(LOG_TAG, errorMessage);
            deliverResultToReceiver(FAILURE_NO_LOCATION_DATA_PROVIDED, errorMessage);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Addresses found using the Geocoder.
        List<Address> addresses;

        Location location = null;
        String locationName = null;
        try {
            if (reverseGeocoding) {
                // Using getFromLocation() returns an array of Addresses for the area immediately
                // surrounding the given latitude and longitude. The results are a best guess and are
                // not guaranteed to be accurate.
                location = intent.getParcelableExtra(EXTRA_LOCATION_LATLNG);
                Log.v(LOG_TAG, "onHandleIntent reverseGeocoding - lat: " + location.getLatitude() + ", long: " + location.getLongitude() + ", hashCode=" + this.hashCode() + ", " + "intent = [" + intent + "]");
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1); // for LatLong, we want just the single address.
            } else {
                locationName = intent.getStringExtra(EXTRA_LOCATION_TEXT);
                Log.v(LOG_TAG, "onHandleIntent geocoding - locationName: " + locationName + ", hashCode=" + this.hashCode() + ", " + "intent = [" + intent + "]");
                addresses = geocoder.getFromLocationName(locationName, 10); //without borders, anywhere
            }

        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Log.w(LOG_TAG, errorMessage, ioException);
            deliverResultToReceiver(FAILURE_SERVICE_NOT_AVAILABLE, errorMessage);
            return;
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.w(LOG_TAG, errorMessage + ". " + printLocationInput(location, locationName),
                    illegalArgumentException);
            deliverResultToReceiver(FAILURE_INVALID_LAT_LONG_USED, errorMessage);
            return;
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            errorMessage = getString(R.string.no_address_found);
            Log.w(LOG_TAG, errorMessage);
            deliverResultToReceiver(FAILURE_NO_RESULT_FOUND, errorMessage);
        } else {
            Log.v(LOG_TAG, "onHandleIntent with " + addresses.size() + " results");
            if (reverseGeocoding) { // just 1
                deliverResultToReceiver(SUCCESS_RESULT, addresses.get(0));
            } else {    // all
                Address[] addressesArray = addresses.toArray(new Address[addresses.size()]);
                deliverResultsToReceiver(SUCCESS_RESULT, addressesArray, locationName);
            }

        }
    }

    private String printLocationInput(Location location, String locationName) {
        if (location != null) {
            return "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude();
        } else {
            return "LocationName = " + locationName;
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_MSG_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverResultToReceiver(int resultCode, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_ADDRESS_KEY, address);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverResultsToReceiver(int resultCode, Address[] addresses, String input) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(RESULT_ADDRESSES_KEY, addresses);
        bundle.putString(RESULT_ADDRESSES_INPUT, input);
        mReceiver.send(resultCode, bundle);
    }
}
