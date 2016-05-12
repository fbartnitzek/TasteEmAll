package com.example.fbartnitzek.tasteemall.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.os.ResultReceiver;

import com.example.fbartnitzek.tasteemall.R;

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

public class GeocodeAddressIntentService extends IntentService {

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_NO_LOCATION_DATA_PROVIDED = 1;
    public static final int FAILURE_SERVICE_NOT_AVAILABLE = 2;
    public static final int FAILURE_INVALID_LAT_LONG_USED = 3;
    public static final int FAILURE_NO_RESULT_FOUND = 4;

    public static final String PACKAGE_NAME = GeocodeAddressIntentService.class.getPackage().getName();

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_ADDRESS_KEY = PACKAGE_NAME + ".RESULT_ADDRESS_KEY";
    public static final String RESULT_MSG_KEY = PACKAGE_NAME + ".RESULT_MSG_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    protected ResultReceiver mReceiver = null;
    private static final String LOG_TAG = GeocodeAddressIntentService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GeocodeAddressIntentService() {
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

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.toast_no_location_provided);
            Log.wtf(LOG_TAG, errorMessage);
            deliverResultToReceiver(FAILURE_NO_LOCATION_DATA_PROVIDED, errorMessage);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating the failure. If an address is found, it is returned

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            Log.v(LOG_TAG, "onHandleIntent - lat: " + location.getLatitude() + ", long: " + location.getLongitude() + ", hashCode=" + this.hashCode() + ", " + "intent = [" + intent + "]");
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1); // for LatLong, we want just the single address.
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(LOG_TAG, errorMessage, ioException);
            deliverResultToReceiver(FAILURE_SERVICE_NOT_AVAILABLE, errorMessage);
            return;
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(LOG_TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
            deliverResultToReceiver(FAILURE_INVALID_LAT_LONG_USED, errorMessage);
            return;
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            errorMessage = getString(R.string.no_address_found);
            Log.e(LOG_TAG, errorMessage);
            deliverResultToReceiver(FAILURE_NO_RESULT_FOUND, errorMessage);
        } else {
            Address address = addresses.get(0);
            Log.i(LOG_TAG, getString(R.string.address_found));
            deliverResultToReceiver(SUCCESS_RESULT, address);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_MSG_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    // TODO: for Geocoding by name: return multiple addresses...
    private void deliverResultToReceiver(int resultCode, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_ADDRESS_KEY, address);
        mReceiver.send(resultCode, bundle);
    }
}
