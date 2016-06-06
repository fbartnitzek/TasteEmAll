package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;

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

public class QueryAndInsertUserTask extends AsyncTask<String, Void, String[]> {

    private final Activity mActivity;
    private final UserCreatedHandler mCreationHandler;
    private String mErrorMsg;

    public interface UserCreatedHandler {
        void onUserCreated(String userId, String userName);
    }

    public QueryAndInsertUserTask(Activity mActivity, UserCreatedHandler mCreationHandler) {
        this.mActivity = mActivity;
        this.mCreationHandler = mCreationHandler;
    }

    @Override
    protected String[] doInBackground(String... params) {
        if (params.length == 0 || params[0].length() == 0) {
            return null;
        }
        String userName = params[0];
        Cursor cursor = TaskUtils.queryWithExactUserName(mActivity, userName);

        if (cursor.getCount() > 0) {
            mErrorMsg = mActivity.getString(R.string.msg_user_already_exists, userName);
            cursor.close();
            return null;
        }
        cursor.close();

        Uri uri = mActivity.getContentResolver().insert(
                DatabaseContract.UserEntry.CONTENT_URI,
                DatabaseHelper.buildUserValues(
                        Utils.calcUserId(userName),
                        userName)
        );

        if (uri != null) {
            cursor = TaskUtils.queryWithExactUserName(mActivity, userName);
            if (cursor != null) {
                if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                    // all as expected
                    TaskUtils.updateWidgets(mActivity);
                    String id = cursor.getString(QueryColumns.ReviewFragment.UserQuery.COL_USER_ID);
                    cursor.close();
                    return new String[]{id, userName};
                }
                cursor.close();
            }
        }

        mErrorMsg = mActivity.getString(R.string.msg_creating_new_entry_did_not_work, userName);

        return null;
    }


    @Override
    protected void onPostExecute(String[] objects) {
        if (objects != null) {
            mCreationHandler.onUserCreated(objects[0], objects[1]);
        } else {
            if (mErrorMsg != null) {
                Toast.makeText(mActivity, mErrorMsg,Toast.LENGTH_LONG).show();
            }
        }
    }
}
