package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

public class PopulateMapTask extends AsyncTask<Uri, Void, Map<LatLng, PopulateMapTask.MarkerInfo>> {

    private static final String LOG_TAG = PopulateMapTask.class.getName();
    private final Activity mActivity;
    private final int mLocationColumn;
    private final String[] mColumns;
    private final String mSortOrder;
    private final PopulateMapHandler mMapHandler;
    private String mMessage;
    private final SimpleDateFormat mShortFormat;


    public interface PopulateMapHandler {
        void onMapPopulated(Map<LatLng, MarkerInfo> markers, String message);
    }

    public PopulateMapTask(Activity activity, String[] columns, int locationColumn,
                           String sortOrder, PopulateMapHandler mapHandler) {
        mActivity = activity;
        mLocationColumn = locationColumn;
        mColumns = columns;
        mSortOrder = sortOrder;
        mMapHandler = mapHandler;
        mShortFormat = new SimpleDateFormat(mActivity.getString(R.string.short_format));
    }


    @Override
    protected Map<LatLng, PopulateMapTask.MarkerInfo> doInBackground(Uri... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + params + "]");
        if (params.length == 0 || params[0] == null) {
            mMessage = mActivity.getString(R.string.msg_invalid_selection_for_map);
            return null;
        }
        Uri uri = params[0];
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());

        Cursor cursor = mActivity.getContentResolver().query(
                uri, mColumns, null, null, mSortOrder);

        if (cursor != null) {
            Map<LatLng, MarkerInfo> markers = new HashMap<>();
            try {
                int failed = 0;
                int success = 0;
                while (cursor.moveToNext()) {   //every attribute is string
                    String location = cursor.getString(mLocationColumn);

                    List<Address> addresses = geocoder.getFromLocationName(location, 1);

                    if (addresses == null || addresses.size() < 1) {
                        failed++;
                        Log.v(LOG_TAG, "doInBackground, location=" + location + " failed");
                    } else {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        MarkerInfo markerInfo;
                        long id = cursor.getLong(
                                QueryColumns.MapFragment.Reviews.COL_REVIEW__ID);
                        if (markers.containsKey(latLng)) {
                            markerInfo = markers.get(latLng);
                            markerInfo.reviews.put(id, createReviewText(cursor));
                            Log.v(LOG_TAG, "doInBackground, location=" + location + " updated");

                        } else {
                            markerInfo = new MarkerInfo();
                            markerInfo.latLng = latLng;
                            markerInfo.location = address.getLocality() + ", " + address.getThoroughfare();
                            markerInfo.reviews.put(id, createReviewText(cursor));
                            Log.v(LOG_TAG, "doInBackground, location=" + location + " added");
                        }
                        markers.put(latLng, markerInfo);

                        success++;
                    }
                }

                for (MarkerInfo marker : markers.values()) {
                    Log.v(LOG_TAG, "doInBackground, initMarker:" + marker.location + " - "
                            + marker.reviews.size() + "Reviews");
                    marker.initMarkerOptions();
                }

                mMessage = mActivity.getString(R.string.toast_populated_map, success, failed);
            } catch (IOException e) {
                mMessage = mActivity.getString(R.string.toast_mass_geocoder_unreachable);
                return null;
            }
            cursor.close();
            return markers;

        } else {
            mMessage =  mActivity.getString(R.string.toast_map_no_cursor);
            return null;
        }

    }

    @Override
    protected void onPostExecute(Map<LatLng, MarkerInfo> latLngMarkerInfoMap) {
        mMapHandler.onMapPopulated(latLngMarkerInfoMap, mMessage);
    }

    private String createReviewText(Cursor cursor) {

        String drinkName = cursor.getString(QueryColumns.MapFragment.Reviews.COL_DRINK_NAME);
        String producerName = cursor.getString(QueryColumns.MapFragment.Reviews.COL_PRODUCER_NAME);
        String dateString = cursor.getString(QueryColumns.MapFragment.Reviews.COL_REVIEW_READABLE_DATE);
        String user = cursor.getString(QueryColumns.MapFragment.Reviews.COL_REVIEW_USER_NAME);

        Date date = Utils.getDate(dateString);

        return mActivity.getString(R.string.marker_review_snippet,
                user, mShortFormat.format(date), producerName, drinkName);
    }


    // TODO: msg with % ...
//
//    @Override
//    protected void onProgressUpdate(MarkerInfo... values) {
//        Log.v(LOG_TAG, "onProgressUpdate, values.length=" + values.length + ", hashCode=" + this.hashCode() + ", " + "values = [" + values + "]");
//        mFragment.addMarker(values[0]);
//    }

    public class MarkerInfo {
        public LatLng latLng;
        public String location;
        private String title;
        public Map<Long, String> reviews = new HashMap<>();
        MarkerOptions markerOptions;

        public String getTitle() {
            return title;
        }

        void initMarkerOptions() {
            title = mActivity.getString(R.string.marker_title, reviews.size(), location);
            markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(Utils.joinMax("\n", reviews.values(), 10))
                    .draggable(false);
        }

        public MarkerOptions getMarkerOptions() {
            return markerOptions;
        }
    }


}
