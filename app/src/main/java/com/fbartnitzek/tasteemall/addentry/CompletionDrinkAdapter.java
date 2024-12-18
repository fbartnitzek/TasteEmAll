package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
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

public class CompletionDrinkAdapter extends SimpleCursorAdapter {

    private final Activity mActivity;

    public CompletionDrinkAdapter(Activity activity) {
        super(activity,
                R.layout.list_item_drink_completion,
                null,
                new String[]{Drink.NAME, Drink.TYPE, Producer.NAME},
                new int[]{R.id.list_item_drink_name, R.id.list_item_drink_type,
                        R.id.list_item_producer_name},  //quite useless now...
                0);
        mActivity = activity;
    }

    @Override
    public void setViewText(TextView v, String text) {
//        Log.v(LOG_TAG, "setViewText, hashCode=" + this.hashCode() + ", " + "v = [" + v + "], text = [" + text + "]");
        int id = v.getId();
        if (id == R.id.list_item_producer_name) {
            v.setText(mActivity.getString(R.string.completion_subentry_formatting, text));
            v.setContentDescription(mActivity.getString(R.string.a11y_producer_name, text));
        } else if (id == R.id.list_item_drink_name) {
            v.setText(text);
            v.setContentDescription(mActivity.getString(R.string.a11y_drink_name, text));
        } else if (id == R.id.list_item_drink_type) {
            v.setText(mActivity.getString(R.string.completion_prefix_formatting, text));
            v.setContentDescription(mActivity.getString(R.string.a11y_drink_type,
                    mActivity.getString(Utils.getReadableDrinkNameId(mActivity, text))));
        } else {
            super.setViewText(v, text);
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return mActivity.getContentResolver().query(
                DatabaseContract.DrinkEntry.buildUriWithName(String.valueOf(constraint)),
                QueryColumns.ReviewFragment.DrinkCompletionQuery.COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_DRINK_NAME);
    }
}
