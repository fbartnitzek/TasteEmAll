package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

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

class CompletionTextViewAdapter extends SimpleCursorAdapter {

    private final CompletionAdapterUpdateHandler mUpdateHandler;
    private final Activity mActivity;

    interface CompletionAdapterUpdateHandler {
        void onUpdate(String entryName, String entryId, int entry_Id);
    }

    public CompletionTextViewAdapter(Activity activity, int layout, String[] from, int[] to,
                                     CompletionAdapterUpdateHandler updateHandler) {
        super(activity, layout, null, from, to, 0);
        mActivity = activity;
        mUpdateHandler = updateHandler;
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == R.id.list_item_producer_location) {
            v.setText(mActivity.getString(
                    R.string.location_completion_postfix, text));
        } else {
            super.setViewText(v, text);
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return mActivity.getContentResolver().query(
                DatabaseContract.ProducerEntry.CONTENT_URI,
                DrinkFragmentHelper.PRODUCER_QUERY_COLUMNS,
                Producer.NAME + " LIKE ? ",
                new String[]{ "%" + constraint + "%"},
                null);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        String entryName = cursor.getString(DrinkFragmentHelper.COL_QUERY_PRODUCER_NAME);
        String entryId = cursor.getString(DrinkFragmentHelper.COL_QUERY_PRODUCER_ID);
        int entry_Id = cursor.getInt(DrinkFragmentHelper.COL_QUERY_PRODUCER__ID);
        mUpdateHandler.onUpdate(entryName, entryId, entry_Id);
        return entryName;
    }
}
