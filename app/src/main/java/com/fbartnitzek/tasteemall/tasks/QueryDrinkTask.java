package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.QueryColumns;

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

public class QueryDrinkTask extends AsyncTask<Uri, Void, Object[]>{
    private final Activity mActivity;
    private final QueryDrinkFoundHandler mFoundHandler;

    private static final String LOG_TAG = QueryDrinkTask.class.getName();

    public interface QueryDrinkFoundHandler {
        void onFoundDrink(int drink_Id, String drinkName, String drinkId, String producerName);
    }

    public QueryDrinkTask(Activity mActivity, QueryDrinkFoundHandler mFoundHandler) {
        this.mActivity = mActivity;
        this.mFoundHandler = mFoundHandler;
    }

    @Override
    protected Object[] doInBackground(Uri... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", "
                + "params = [" + Arrays.toString(params) + "]");
        if (params.length == 0) {
            return null;
        }

        Uri uri = Utils.calcDrinkIncludingProducerUri(params[0]);

        Cursor cursor = mActivity.getContentResolver().query(
                uri, QueryColumns.ReviewFragment.DrinkCompletionQuery.COLUMNS, null, null, null);

        Object[] objects;
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            objects = new Object[]{
                    cursor.getInt(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_DRINK__ID),
                    cursor.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_DRINK_NAME),
                    cursor.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_DRINK_ID),
                    cursor.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_PRODUCER_NAME)};
        } else {
            objects = null;
        }
        cursor.close();

        return objects;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        if (objects != null) {
            mFoundHandler.onFoundDrink((int) objects[0], (String) objects[1],
                    (String) objects[2], (String) objects[3]);
        }
    }
}
