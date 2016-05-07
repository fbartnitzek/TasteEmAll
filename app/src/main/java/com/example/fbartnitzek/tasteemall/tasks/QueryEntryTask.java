package com.example.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

public class QueryEntryTask  extends AsyncTask<Uri, Void, Object[]>{
    private Activity mActivity;
    private String mColumnNameEntry_Id;
    private String mColumnNameEntryId;
    private String mColumnNameEntryName;
    private QueryEntryFoundHandler mFoundHandler;

    private static final String LOG_TAG = QueryEntryTask.class.getName();

    public interface QueryEntryFoundHandler {
        void onFound(int entry_Id, String entryId, String entryName);
    }

    public QueryEntryTask(Activity mActivity, String mColumnNameEntry_Id, String mColumnNameEntryId,
                          String mColumnNameEntryName, QueryEntryFoundHandler mFoundHandler) {
        this.mActivity = mActivity;
        this.mColumnNameEntry_Id = mColumnNameEntry_Id;
        this.mColumnNameEntryId = mColumnNameEntryId;
        this.mColumnNameEntryName = mColumnNameEntryName;
        this.mFoundHandler = mFoundHandler;
    }

    @Override
    protected Object[] doInBackground(Uri... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + params + "]");
        if (params.length == 0) {
            return null;
        }
        final String[] queryColumns = {mColumnNameEntry_Id, mColumnNameEntryId, mColumnNameEntryName};

        Cursor cursor = mActivity.getContentResolver().query(
                params[0], queryColumns, null, null, null);

        Object[] objects;
        if (cursor != null && cursor.moveToFirst()) {
            objects = new Object[]{cursor.getInt(0), cursor.getString(1), cursor.getString(2)};
        } else {
            objects = null;
        }
        cursor.close();

        return objects;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        if (objects != null) {
            mFoundHandler.onFound((int) objects[0], (String) objects[1], (String) objects[2]);
        }
    }
}
