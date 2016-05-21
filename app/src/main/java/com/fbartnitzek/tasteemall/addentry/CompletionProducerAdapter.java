package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.tasks.QueryColumns;

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

class CompletionProducerAdapter extends SimpleCursorAdapter {

    private final CompletionProducerAdapterSelectHandler mSelectHandler;
    private final Activity mActivity;
    private static final String LOG_TAG = CompletionProducerAdapter.class.getName();

    interface CompletionProducerAdapterSelectHandler {
        void onSelectedProducer(int producer_Id, String producerName, String producerId);
    }

    public CompletionProducerAdapter(Activity activity,
                                     CompletionProducerAdapterSelectHandler updateHandler) {
        super(activity,
                R.layout.list_item_producer_completion,
                null,
                new String[]{Producer.NAME, Producer.LOCATION},
                new int[]{R.id.list_item_producer_name, R.id.list_item_location_name},
                0);
        mActivity = activity;
        mSelectHandler = updateHandler;
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == R.id.list_item_location_name) {
            v.setText(mActivity.getString(R.string.completion_subentry_formatting, text));
            v.setContentDescription(mActivity.getString(R.string.a11y_producer_location, text));
        } else if (v.getId() == R.id.list_item_producer_name) {
            v.setText(text);
            v.setContentDescription(mActivity.getString(R.string.a11y_producer_name, text));
        } else {
            super.setViewText(v, text);
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
//        Log.v(LOG_TAG, "runQueryOnBackgroundThread, hashCode=" + this.hashCode() + ", " + "constraint = [" + constraint + "]");

//        Uri uri = DatabaseContract.ProducerEntry.buildUriWithName(String.valueOf(constraint));
        return mActivity.getContentResolver().query(
                DatabaseContract.ProducerEntry.CONTENT_URI,
                QueryColumns.DrinkFragment.PRODUCER_QUERY_COLUMNS,
                Producer.NAME + " LIKE ? ",
                new String[]{ "%" + constraint + "%"},
                null);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
//        Log.v(LOG_TAG, "convertToString, hashCode=" + this.hashCode() + ", " + "cursor = [" + cursor + "]");
        String producerName = cursor.getString(QueryColumns.DrinkFragment.COL_QUERY_PRODUCER_NAME);
        String producerId = cursor.getString(QueryColumns.DrinkFragment.COL_QUERY_PRODUCER_ID);
        int producer_Id = cursor.getInt(QueryColumns.DrinkFragment.COL_QUERY_PRODUCER__ID);
        mSelectHandler.onSelectedProducer(producer_Id, producerName, producerId);
        return producerName;
    }
}
