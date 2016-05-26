package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
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

public class InsertEntryTask extends AsyncTask<ContentValues, Void, Uri> {

    private static final String LOG_TAG = InsertEntryTask.class.getName();
    private final Activity mActivity;
    private final View mRootView;
    private final String mEntryName;
    private final Uri mContentUri;

    public InsertEntryTask(Activity mActivity, Uri contentUri, View rootView, String entryName) {
        Log.v(LOG_TAG, "InsertEntryTask, hashCode=" + this.hashCode() + ", " + "mActivity = [" + mActivity + "], rootView = [" + rootView + "], entryName = [" + entryName + "]");
        this.mActivity = mActivity;
        this.mRootView = rootView;
        this.mEntryName = entryName;
        this.mContentUri = contentUri;
    }

    @Override
    protected Uri doInBackground(ContentValues... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + Arrays.toString(params) + "]");
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
            TaskUtils.updateWidgets(mActivity);

            /* notes on shared element transition for adding:
             * - in theory for the same behaviour as in ShowEntities, the parent id is needed
             * - but: it is not existing at the moment
             * - solution/workaround: use the field without id - so it's
             *     producer.name instead of drink.producer.drinkId
             */
            // TODO: still one problem - finish in addReview:
            // no modification of bundle in Fragment/Activity to transitionName with id possible...

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
