package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.User;
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

public class CompletionUserAdapter extends SimpleCursorAdapter {

    private final Activity mActivity;

    public CompletionUserAdapter(Activity activity) {
        super(activity,
                R.layout.list_item_user_completion,
                null,
                new String[]{User.NAME},
                new int[]{R.id.list_item_user_name},
                0);
        this.mActivity = activity;
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == R.id.list_item_user_name) {
            v.setText(text);
            v.setContentDescription(mActivity.getString(R.string.a11y_user_name, text));
        } else {
            super.setViewText(v, text);
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return mActivity.getContentResolver().query(
                DatabaseContract.UserEntry.buildUriWithName(String.valueOf(constraint)),
                QueryColumns.ReviewFragment.UserQuery.COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(QueryColumns.ReviewFragment.UserQuery.COL_USER_NAME);
    }
}
