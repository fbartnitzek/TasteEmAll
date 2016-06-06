package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Review;

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

public class GeocodeReviewsTask extends AsyncTask<Void, Void, String> {

    private final Activity mActivity;
    private final GeocodeReviewsUpdatedHandler mUpdatedHandler;
    private static final String LOG_TAG = GeocodeReviewsTask.class.getName();

    public interface GeocodeReviewsUpdatedHandler {
        void onUpdatedReviews(String msg);
    }

    public GeocodeReviewsTask(Activity mActivity, GeocodeReviewsUpdatedHandler mUpdatedHandler) {
        Log.v(LOG_TAG, "GeocodeReviewsTask, hashCode=" + this.hashCode() + ", " + "mActivity = [" + mActivity + "], mUpdatedHandler = [" + mUpdatedHandler + "]");
        this.mActivity = mActivity;
        this.mUpdatedHandler = mUpdatedHandler;
    }

    @Override
    protected String doInBackground(Void... params) {
        Cursor cursor = mActivity.getContentResolver().query(
                DatabaseContract.ReviewEntry.buildGeocodingUri(),
                QueryColumns.MainFragment.GeocodingQuery.COLUMNS,
                null, null, null);

        if (cursor == null) {
            Log.e(LOG_TAG, "doInBackground - no cursor found..., hashCode=" + this.hashCode() + "]");
            return mActivity.getString(R.string.no_geocode_cursor_reivews);
        } else if (cursor.getCount() == 0) {
            Log.v(LOG_TAG, "doInBackground - no entries to geocode, hashCode=" + this.hashCode() + "]");
            return mActivity.getString(R.string.no_geocode_reviews);
        }

        long id;
        String location;
        double latitude;
        double longitude;
        int converted = 0;
        int failed = 0;
        int rows;

        Log.v(LOG_TAG, "doInBackground, starting geocoder...");
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        
        while (cursor.moveToNext()) {
            id = cursor.getLong(QueryColumns.MainFragment.GeocodingQuery.COL_REVIEW__ID);
            location = cursor.getString(QueryColumns.MainFragment.GeocodingQuery.COL_LOCATION);

            try {
                latitude = Utils.getLatitude(location);
                longitude = Utils.getLongitude(location);
                Log.v(LOG_TAG, "doInBackground lat:" + latitude + ", long: " + longitude);

                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses == null || addresses.size() < 1) {
                    failed++;
                } else {
                    location = Utils.formatAddress(addresses.get(0));
                    Log.v(LOG_TAG, "doInBackground location:" + location);
                    ContentValues cv = new ContentValues();
                    cv.put(Review.LOCATION_ID, location);   //TODO
                    rows = mActivity.getContentResolver().update(
                            DatabaseContract.ReviewEntry.buildUri(id),
                            cv, null, null);
                    if (rows == 1) {
                        converted++;
                        Log.v(LOG_TAG, "doInBackground, converted!");
                    } else {
                        failed++;
                        Log.v(LOG_TAG, "doInBackground, failed...");
                    }
                }

            } catch (NumberFormatException e) {
                Log.v(LOG_TAG, "doInBackground - NumberFormatException");

                failed++;
            } catch (IOException e) {
                // IO not available - stop
                Log.v(LOG_TAG, "doInBackground - IOException");
                cursor.close();
                return mActivity.getString(R.string.msg_mass_geocoder_unreachable, cursor.getCount());
            }
        }
        cursor.close();


        String string = mActivity.getString(R.string.msg_mass_geocoder_result, converted, failed);
        Log.v(LOG_TAG, "doInBackground, msg: " + string);
        return string;
    }

    @Override
    protected void onPostExecute(String string) {
        Log.v(LOG_TAG, "onPostExecute, hashCode=" + this.hashCode() + ", " + "string = [" + string + "]");
        if (string != null) {
            mUpdatedHandler.onUpdatedReviews(string);
        }
    }

}
