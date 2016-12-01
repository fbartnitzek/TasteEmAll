package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;

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

public class QueryNearbyLocationsTask extends AsyncTask<double[], Void, LocationParcelable[]> {

    private final Activity mActivity;
    private final QueryNearbyLocationHandler mFoundHandler;

    private static final String LOG_TAG = QueryNearbyLocationsTask.class.getName();

    public interface QueryNearbyLocationHandler {
//        void onNearbyLocationsFound(Uri locationUri, String locationId, String text);
        void onNearbyLocationsFound(LocationParcelable[] locations);
        void onNearbyLocationNotFound();
    }

    public QueryNearbyLocationsTask(Activity mActivity, QueryNearbyLocationHandler mFoundHandler) {
        this.mActivity = mActivity;
        this.mFoundHandler = mFoundHandler;
    }

    @Override
    protected LocationParcelable[] doInBackground(double[] ... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + params + "]");
        if (params.length == 0 || params[0] == null || params[0].length != 2) {
            return null;
        }

        Cursor cursor = mActivity.getContentResolver().query(
                DatabaseContract.LocationEntry.buildUriWithLatLng(params[0][0], params[0][1]),
                QueryColumns.LocationPart.CompletionQuery.COLUMNS, null, null, null);

//        String [] strings = null;
        // if list - return all - try to fill adapter...
        if (cursor != null) {
            LocationParcelable[] locations = new LocationParcelable[cursor.getCount()];

            try {
                int i = 0;
                while (cursor.moveToNext()) {
                    LocationParcelable entry = new LocationParcelable(
                            cursor.getInt(QueryColumns.LocationPart.CompletionQuery.COL__ID),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_COUNTRY),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID),
                            cursor.getDouble(QueryColumns.LocationPart.CompletionQuery.COL_LATITUDE),
                            cursor.getDouble(QueryColumns.LocationPart.CompletionQuery.COL_LONGITUDE),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_INPUT),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_FORMATTED_ADDRESS),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_DESCRIPTION));
//                    // uri is no contentValue...
                    locations[i++] = entry;
                }
            } finally {
                cursor.close();
            }

//            if (cursor.moveToFirst()) {     // for now: returns first or nothing
//                int location_Id = cursor.getInt(QueryColumns.LocationPart.CompletionQuery.COL__ID);
//                String locationId = cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
//                String formatted = cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_FORMATTED_ADDRESS);
//                String input = cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_INPUT);
//
////                Log.v(LOG_TAG, "doInBackground - found, locationId=" + locationId + ", " + "formatted= [" + formatted + "], input = [" + input + "]");
//
//                strings = new String[]{
//                        Integer.toString(location_Id),
//                        locationId,
//                        (formatted != null && !formatted.isEmpty()) ? formatted : input
//                };
//            }
//            cursor.close();
            return locations;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(LocationParcelable[] locations) {
        Log.v(LOG_TAG, "onPostExecute, hashCode=" + this.hashCode() + ", " + "locations = [" + locations + "]");
        if (locations!= null && locations.length > 0) {
            mFoundHandler.onNearbyLocationsFound(locations);
        } else {
            mFoundHandler.onNearbyLocationNotFound();
        }
    }


}
