package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;

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

public class ValidateUserTask extends AsyncTask<String, Void, String>{

    private final ValidateUserHandler mValidationHandler;
    private final Activity mActivity;

    public ValidateUserTask(ValidateUserHandler mValidationHandler, Activity mActivity) {
        this.mValidationHandler = mValidationHandler;
        this.mActivity = mActivity;
    }

    public interface ValidateUserHandler {
        void onUserValidated(String userId);
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0 || params[0].length() == 0) {
            return null;
        }

        Cursor cursor = TaskUtils.queryWithExactUserName(mActivity, params[0]);

        if (cursor != null) {
            if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                String id = cursor.getString(QueryColumns.ReviewFragment.UserQuery.COL_USER_ID);
                cursor.close();
                return id;
            }
            cursor.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String userId) {
        mValidationHandler.onUserValidated(userId);
    }
}
