package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
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

public class InsertLocationTask extends AsyncTask<ContentValues, Void, Uri> {

    private static final String LOG_TAG = InsertLocationTask.class.getName();
    private final Activity mActivity;
    private final View mRootView;
    private final String mEntryName;
    private final InsertLocationHandler mInsertHandler;
    private String mLocationId;

    public interface InsertLocationHandler {
        void onInsertedLocation(Uri uri, String mEntryName, String locationId);
    }

    public InsertLocationTask(Activity mActivity, View rootView, String entryName, InsertLocationHandler mInsertHandler) {
        this.mInsertHandler = mInsertHandler;
        Log.v(LOG_TAG, "InsertEntryTask, hashCode=" + this.hashCode() + ", " + "mActivity = [" + mActivity + "], rootView = [" + rootView + "], entryName = [" + entryName + "]");
        this.mActivity = mActivity;
        this.mRootView = rootView;
        this.mEntryName = entryName;

    }

    @Override
    protected Uri doInBackground(ContentValues... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + Arrays.toString(params) + "]");
        if (params.length == 0) {
            return null;
        }

        Uri insertUri = mActivity.getContentResolver().insert(DatabaseContract.LocationEntry.CONTENT_URI, params[0]);
        Cursor cursor = mActivity.getContentResolver().query(
                insertUri, QueryColumns.LocationPart.CompletionQuery.COLUMNS, null, null, null);
        if (cursor == null || cursor.getCount() < 1) {
            Log.e(LOG_TAG, "doInBackground - INSERT FAILED!!, hashCode=" + this.hashCode() + ", " + "params = [" + params + "]");
            return null;
        }
        if (cursor.moveToFirst()) {
            mLocationId = cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
        }
        cursor.close();

        return insertUri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        Log.v(LOG_TAG, "onPostExecute, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "]");

        if (uri != null) {
            TaskUtils.updateWidgets(mActivity);

            if (mInsertHandler != null) {
                mInsertHandler.onInsertedLocation(uri, mEntryName, mLocationId);
            }
        } else {
            Snackbar.make(mRootView,
                    mActivity.getString(R.string.msg_creating_new_entry_did_not_work, mEntryName),
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }


}
