package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

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

public class QueryLocationTask extends AsyncTask<Uri, Void, Object[]> {

    private final Activity mActivity;
    private final QueryLocationFoundHandler mFoundHandler;

    public interface QueryLocationFoundHandler {
        void onFoundLocation(int location_id, LocationParcelable locationParcelable);
    }

    public QueryLocationTask(Activity mActivity, QueryLocationFoundHandler mFoundHandler) {
        this.mActivity = mActivity;
        this.mFoundHandler = mFoundHandler;
    }


    @Override
    protected Object[] doInBackground(Uri... params) {
        if (params.length == 0) {
            return null;
        }

        Cursor cursor = mActivity.getContentResolver().query(
                params[0], QueryColumns.LocationPart.CompletionQuery.COLUMNS,
                null, null, null);

        Object[] objects;
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            objects = new Object[]{
                    cursor.getInt(QueryColumns.LocationPart.CompletionQuery.COL__ID),
                    new LocationParcelable(
                            cursor.getInt(QueryColumns.LocationPart.CompletionQuery.COL__ID),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_COUNTRY),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID),
                            cursor.getDouble(QueryColumns.LocationPart.CompletionQuery.COL_LATITUDE),
                            cursor.getDouble(QueryColumns.LocationPart.CompletionQuery.COL_LONGITUDE),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_INPUT),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_FORMATTED_ADDRESS),
                            cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_DESCRIPTION))
            };
        } else {
            objects = null;
        }
        cursor.close();

        return objects;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        if (objects != null) {
            mFoundHandler.onFoundLocation((int) objects[0], (LocationParcelable) objects[1]);
        }
    }
}
