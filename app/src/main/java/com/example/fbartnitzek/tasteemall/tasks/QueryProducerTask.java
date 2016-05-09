package com.example.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fbartnitzek.tasteemall.QueryColumns;

import java.util.Arrays;

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

public class QueryProducerTask extends AsyncTask<Uri, Void, Object[]>{
    private final Activity mActivity;
    private final QueryProducerFoundHandler mFoundHandler;

    private static final String LOG_TAG = QueryProducerTask.class.getName();

    public interface QueryProducerFoundHandler {
        void onFoundProducer(int producer_id, String producerName, String producerId);
    }

    public QueryProducerTask(Activity mActivity, QueryProducerFoundHandler mFoundHandler) {
        this.mActivity = mActivity;
        this.mFoundHandler = mFoundHandler;
    }

    @Override
    protected Object[] doInBackground(Uri... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + Arrays.toString(params) + "]");
        if (params.length == 0) {
            return null;
        }

        Cursor cursor = mActivity.getContentResolver().query(
                params[0], QueryColumns.DrinkFragment.PRODUCER_QUERY_COLUMNS, null, null, null);

        Object[] objects;
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            objects = new Object[]{
                    cursor.getInt(QueryColumns.DrinkFragment.COL_QUERY_PRODUCER__ID),
                    cursor.getString(QueryColumns.DrinkFragment.COL_QUERY_PRODUCER_NAME),
                    cursor.getString(QueryColumns.DrinkFragment.COL_QUERY_PRODUCER_ID)};
        } else {
            objects = null;
        }
        cursor.close();

        return objects;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        if (objects != null) {
            mFoundHandler.onFoundProducer((int) objects[0], (String) objects[1], (String) objects[2]);
        }
    }
}
