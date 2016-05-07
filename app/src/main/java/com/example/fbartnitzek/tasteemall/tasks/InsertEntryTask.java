package com.example.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

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

public class InsertEntryTask extends AsyncTask<ContentValues, Void, Uri> {

    private static final String LOG_TAG = InsertEntryTask.class.getName();
    private Activity mActivity;
    private View mRootView;
    private String mEntryName;
    private Uri mContentUri;

    public InsertEntryTask(Activity mActivity, Uri contentUri, View rootView, String entryName) {
        Log.v(LOG_TAG, "InsertEntryTask, hashCode=" + this.hashCode() + ", " + "mActivity = [" + mActivity + "], rootView = [" + rootView + "], entryName = [" + entryName + "]");
        this.mActivity = mActivity;
        this.mRootView = rootView;
        this.mEntryName = entryName;
        this.mContentUri = contentUri;
    }

    @Override
    protected Uri doInBackground(ContentValues... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + params + "]");
        if (params.length == 0) {
            return null;
        }

        return mActivity.getContentResolver().insert(
                mContentUri,
                params[0]
        );
    }

    @Override
    protected void onPostExecute(Uri uri) {
        Log.v(LOG_TAG, "onPostExecute, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "]");

        if (uri != null) {
            Intent output = new Intent();
            output.setData(uri);    //always returns single-uri
            mActivity.setResult(Activity.RESULT_OK, output);
            mActivity.finish();
        } else {
            Snackbar.make(mRootView, "Creating new entry " + mEntryName + " didn't work...",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }

    }
}
