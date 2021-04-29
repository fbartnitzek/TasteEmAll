package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;

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

public class UpdateEntryTask extends AsyncTask<ContentValues, Void, Uri> {

    private static final String LOG_TAG = UpdateEntryTask.class.getName();
    private final Activity mActivity;
    private final Uri mContentUri;
    private final String mEntryName;
    private final View mRootView;

    public UpdateEntryTask(Activity mActivity, Uri mContentUri, String mEntryName, View mRootView) {
        this.mActivity = mActivity;
        this.mContentUri = mContentUri;
        this.mEntryName = mEntryName;
        this.mRootView = mRootView;
    }

    @Override
    protected Uri doInBackground(ContentValues... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + Arrays.toString(params) + "]");

        if (params.length == 0) {
            return null;
        }

        int rows = mActivity.getContentResolver().update(
                mContentUri,
                params[0],
                null, null);

        if (rows < 1) {
            return null;
        } else {
            return mContentUri;
        }

    }

    @Override
    protected void onPostExecute(Uri uri) {
        Log.v(LOG_TAG, "onPostExecute, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "]");
        if (uri != null) {
            Intent output = new Intent();
            output.setData(uri);
            mActivity.setResult(Activity.RESULT_OK, output);
            mActivity.finish();
        } else {
            Snackbar.make(mRootView, "Updating entry " + mEntryName + " didn't work...",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }
}
