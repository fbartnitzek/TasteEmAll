package com.fbartnitzek.tasteemall.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeocodeWorker extends Worker {

    public static final String INPUT_LATITUDE = "GEOCODE_INPUT_LATITUDE";
    public static final String INPUT_LONGITUDE = "GEOCODE_INPUT_LONGITUDE";
    public static final String INPUT_TEXT = "GEOCODE_INPUT_TEXT";
    public static final double ILLEGAL_LAT_LONG = 666.6;
    public static final String OUTPUT_ERROR = "GEOCODE_OUTPUT_ERROR";
    public static final String ORIG_INPUT = "GEOCODE_ORIG_INPUT";
    public static final String GEOCODE_TYPE_LAT_LONG = "GEOCODE_TYPE_LAT_LONG";
    public static final String FORMATTED = "_FORMATTED";
    public static final String COUNTRY_CODE = "_COUNTRY_CODE";
    public static final String COUNTRY = "_COUNTRY";
    public static final String LATITUDE = "_LATITUDE";
    public static final String LONGITUDE = "_LONGITUDE";
    private final double latitude;
    private final double longitude;
    private final String text;
    private final String origInput;
    private static final String LOG_TAG = GeocodeWorker.class.getName();

    public GeocodeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        text = workerParams.getInputData().getString(INPUT_TEXT);
        latitude = workerParams.getInputData().getDouble(INPUT_LATITUDE, ILLEGAL_LAT_LONG);
        longitude = workerParams.getInputData().getDouble(INPUT_LONGITUDE, ILLEGAL_LAT_LONG);
        if (text != null){
            origInput = text;
        } else {
            origInput = Utils.getLocationInput(latitude, longitude);;
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        if (text == null && (latitude == ILLEGAL_LAT_LONG || longitude == ILLEGAL_LAT_LONG)) {
            return calcFailedResult(R.string.msg_no_location_provided, null);
        }

        Geocoder geocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());

        List<Address> addresses;
        boolean isLatLong = false;
        if (text != null) {
            try {
                // prevented IllegalArgException via null-check
                addresses = geocoder.getFromLocationName(text, 10);
            } catch (IOException e) {
                return calcFailedResult(R.string.service_not_available, e);
            }
        } else {
            try {
                isLatLong = true;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                return calcFailedResult(R.string.service_not_available, e);
            } catch (IllegalArgumentException e) {
                return calcFailedResult(R.string.invalid_lat_long_used, e);
            }
        }

        if (addresses == null || addresses.isEmpty()) {
            return calcFailedResult(R.string.no_address_found, null);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(ORIG_INPUT, origInput);
        data.put(GEOCODE_TYPE_LAT_LONG, isLatLong);
        for (int i = 0; i < addresses.size(); i++) {
            putAddressData(data, addresses.get(i), i);
        }
        return Result.success(new Data.Builder().putAll(data).build());
    }

    private void putAddressData(Map<String, Object> data, Address address, int i) {
        data.put(i + FORMATTED, Utils.formatAddress(address));
        data.put(i + COUNTRY_CODE, address.getCountryCode());
        data.put(i + COUNTRY, address.getCountryName());
        data.put(i + LATITUDE, address.getLatitude());
        data.put(i + LONGITUDE, address.getLongitude());
    }

    private Result calcFailedResult(int stringId, Throwable t) {
        String errorMessage = getApplicationContext().getString(stringId);
        if (t != null){
            Log.w(LOG_TAG, errorMessage, t);
        } else {
            Log.w(LOG_TAG, errorMessage);
        }
        return Result.failure(new Data.Builder()
                .putString(OUTPUT_ERROR, errorMessage)
                .build());
    }
}
