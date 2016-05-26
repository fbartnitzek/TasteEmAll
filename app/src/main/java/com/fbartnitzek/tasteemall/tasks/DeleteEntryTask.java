package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class DeleteEntryTask extends AsyncTask<Uri, Void, String> {

    private static final String LOG_TAG = DeleteEntryTask.class.getName();

    private final Activity mActivity;
    private final String mNameColumn;
//    private final DeleteEntryHandler mDeleteHandler;

    public DeleteEntryTask(Activity mActivity, String nameColumn) {
        this.mActivity = mActivity;
//        this.mDeleteHandler = mDeleteHandler;
        mNameColumn = nameColumn;
    }

//    public interface DeleteEntryHandler {
//        void onDeletedEntry(String message);
//    }

    @Override
    protected String doInBackground(Uri... params) {
        if (params.length == 0) {
            return null;
        }
        // params[0] = deleteUri
        // params[1] = checkUri

        // first: initial check if entry exists (and for the name)
        Cursor cursor = mActivity.getContentResolver().query(params[0],
                new String[]{mNameColumn}, null, null, null);
        if (cursor == null) {
            return "self-check for entry failed!";
        }
        String name;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
            cursor.close();
        } else {
            cursor.close();
            return "entry does not exist already before deletion...";
        }

        if (params.length > 1) {
            // TODO: create matching uri ...!
            cursor = mActivity.getContentResolver().query(params[1], null, null, null, null);
            if (cursor == null) {
                return "check for foreign key usage failed for " + name + "!";
            }
            if (cursor.getCount() > 1) {
                cursor.close();
                return "entry is still used as a foreign key!";
            }
            cursor.close();
        }

        mActivity.getContentResolver().delete(params[0], null, null);

        // check if delete worked
        cursor = mActivity.getContentResolver().query(params[0], null, null, null, null);
        if (cursor == null) {
            return "deleting " + name + " failed - no cursor...";
        }
        if (cursor.getCount() == 0) {
            cursor.close();
            return "deleted " + name;
        } else {
            cursor.close();
            return "not deleted " + name;
        }

    }

    @Override
    protected void onPostExecute(String s) {
        TaskUtils.updateWidgets(mActivity);
        Toast.makeText(mActivity, s, Toast.LENGTH_LONG).show();
        mActivity.finish();
    }
}
