package com.fbartnitzek.tasteemall.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;

import java.io.IOException;
import java.util.ArrayList;
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

public class GeocodeAllLocationsTask extends AsyncTask<Void, Void, Object[]>{

    private final GeocodeProducersUpdateHandler mUpdateHandler;
    private final Context mContext;

    private static final String LOG_TAG = GeocodeAllLocationsTask.class.getName();

    public GeocodeAllLocationsTask(Context context, GeocodeProducersUpdateHandler mUpdateHandler) {
        this.mUpdateHandler = mUpdateHandler;
        mContext = context;
    }

    public interface GeocodeProducersUpdateHandler {
        void onUpdatedLatLngLocations(String errorMsg,
                                      String producerLocationMsg, String reviewLocationMsg,
                                      ArrayList<Uri> producerUris, ArrayList<Uri> locationUris);
    }

    @Override
    protected Object[] doInBackground(Void... params) {


        String errorMessage = null;
        String reviewLocationMessage = null;
        String producerLocationMessage = null;
        ArrayList<Uri> producerLocations = null;
        ArrayList<Uri> reviewLocations = null;
        try {
            producerLocationMessage = geocodeProducerLocationsByPosition();
            reviewLocationMessage = geocodeReviewLocationsByPosition();
            producerLocations = queryProducerLocationsForTextGeocoder();
            reviewLocations = queryReviewLocationsForTextGeocoder();

        } catch (GeocoderException e) {
            errorMessage = e.getMessage();
        }

        return new Object[]{errorMessage, producerLocationMessage, reviewLocationMessage,
                producerLocations, reviewLocations};
    }

    private ArrayList<Uri> queryProducerLocationsForTextGeocoder() throws GeocoderException {
        Cursor cursor = null;
        ArrayList<Uri> list = new ArrayList<>();
        try {
            cursor = mContext.getContentResolver().query(
                    DatabaseContract.ProducerEntry.buildTextGeocodingUri(),
                    QueryColumns.Geocoder.Producers.COLUMNS,
                    null, null, null);

            if (cursor == null) {
                throw new GeocoderException("no cursor found...!");
            }

            while (cursor.moveToNext()) {
                list.add(DatabaseContract.ProducerEntry.buildUri(
                        cursor.getLong(QueryColumns.Geocoder.Producers.COL_PRODUCER__ID)));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private ArrayList<Uri> queryReviewLocationsForTextGeocoder() throws GeocoderException {
        Cursor cursor = null;
        ArrayList<Uri> list = new ArrayList<>();
        try {
            cursor = mContext.getContentResolver().query(
                    DatabaseContract.LocationEntry.buildTextGeocodingUri(),
                    QueryColumns.Geocoder.Locations.COLUMNS,
                    null, null, null);

            if (cursor == null) {
                throw new GeocoderException("no cursor found...!");
            }

            while (cursor.moveToNext()) {
                list.add(DatabaseContract.LocationEntry.buildUri(
                        cursor.getLong(QueryColumns.Geocoder.Locations.COL_LOCATION__ID)));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private String geocodeProducerLocationsByPosition() throws GeocoderException {
        Cursor cursor = mContext.getContentResolver().query(
                DatabaseContract.ProducerEntry.buildValidLatLngGeocodingUri(),
                QueryColumns.Geocoder.Producers.COLUMNS,
                null, null, null);

        if (cursor == null) {
            Log.e(LOG_TAG, "doInBackground - no cursor found..., hashCode=" + this.hashCode() + "]");
            throw new GeocoderException("no cursor found...!");
        }

        long id;
        String formattedAddress;
        double latitude;
        double longitude;
        int converted = 0;
        int failed = 0;
        int rows;

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        while (cursor.moveToNext()) {
            id = cursor.getLong(QueryColumns.Geocoder.Producers.COL_PRODUCER__ID);
            latitude = cursor.getDouble(QueryColumns.Geocoder.Producers.COL_PRODUCER_LATITUDE);
            longitude= cursor.getDouble(QueryColumns.Geocoder.Producers.COL_PRODUCER_LONGITUDE);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses == null || addresses.size() < 1) {
                    failed++;
                } else {
                    formattedAddress = Utils.formatAddress(addresses.get(0));
                    Log.v(LOG_TAG, "doInBackground location:" + formattedAddress);
                    ContentValues cv = new ContentValues();
                    cv.put(Producer.FORMATTED_ADDRESS, formattedAddress);
                    cv.put(Producer.COUNTRY, addresses.get(0).getCountryName());

                    rows = mContext.getContentResolver().update(
                            DatabaseContract.ProducerEntry.buildUri(id),
                            cv, null, null);
                    if (rows == 1) {
                        converted++;
                    } else {
                        failed++;
                    }
                }

            } catch (NumberFormatException e) {
                failed++;
            } catch (IOException e) { // IO not available - stop
                cursor.close();
                throw new GeocoderException(
                        mContext.getString(R.string.msg_mass_geocoder_unreachable,
                            cursor.getCount(),
                            mContext.getString(R.string.label_producer_locations),
                            converted, failed));
            }
        }
        cursor.close();

        if (converted + failed == 0) {
            return mContext.getString(R.string.msg_mass_geocoder_no_latlng_result_producer);
        } else {
            return mContext.getString(R.string.msg_mass_geocoder_valid_latlng_result_producer, converted, failed);
        }


    }

    private String geocodeReviewLocationsByPosition() throws GeocoderException {
        Cursor cursor = mContext.getContentResolver().query(
                DatabaseContract.LocationEntry.buildValidLatLngGeocodingUri(),
                QueryColumns.Geocoder.Locations.COLUMNS,
                null, null, null);

        if (cursor == null) {
            Log.e(LOG_TAG, "doInBackground - no cursor found..., hashCode=" + this.hashCode() + "]");
            throw new GeocoderException("no cursor found...!");
        }

        long id;
        String formattedAddress;
        double latitude;
        double longitude;
        int converted = 0;
        int failed = 0;
        int rows;

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        while (cursor.moveToNext()) {
            id = cursor.getLong(QueryColumns.Geocoder.Locations.COL_LOCATION__ID);
            latitude = cursor.getDouble(QueryColumns.Geocoder.Locations.COL_LOCATION_LATITUDE);
            longitude= cursor.getDouble(QueryColumns.Geocoder.Locations.COL_LOCATION_LONGITUDE);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses == null || addresses.size() < 1) {
                    failed++;
                } else {
                    formattedAddress = Utils.formatAddress(addresses.get(0));
                    Log.v(LOG_TAG, "doInBackground location:" + formattedAddress);
                    ContentValues cv = new ContentValues();
                    cv.put(Location.FORMATTED_ADDRESS, formattedAddress);
                    cv.put(Location.COUNTRY, addresses.get(0).getCountryName());

                    rows = mContext.getContentResolver().update(
                            DatabaseContract.LocationEntry.buildUri(id),
                            cv, null, null);
                    if (rows == 1) {
                        converted++;
                    } else {
                        failed++;
                    }
                }

            } catch (NumberFormatException e) {
                failed++;
            } catch (IOException e) { // IO not available - stop
                cursor.close();
                throw new GeocoderException(
                        mContext.getString(R.string.msg_mass_geocoder_unreachable,
                                cursor.getCount(),
                                mContext.getString(R.string.label_review_location),
                                converted, failed));
            }
        }
        cursor.close();

        if (converted + failed == 0) {
            return mContext.getString(R.string.msg_mass_geocoder_no_latlng_result_review);
        } else {
            return mContext.getString(R.string.msg_mass_geocoder_valid_latlng_result_review, converted, failed);
        }

    }

    private class GeocoderException extends Exception{
        public GeocoderException(String detailMessage) {
            super(detailMessage);
        }
    }


    @Override
    protected void onPostExecute(Object[] objects) {
        if (objects != null) {

            Log.v(LOG_TAG, "onPostExecute, hashCode=" + this.hashCode() + ", " + "errorMsg = [" + objects[0]
                    + "], producerLocationMsg=" + objects[1] + ", reviewLocationMsg=" + objects[2]);
            mUpdateHandler.onUpdatedLatLngLocations((String) objects[0], (String) objects[1],
                    (String) objects[2], (ArrayList<Uri>) objects[3], (ArrayList<Uri>) objects[4]);
        }
    }
}
