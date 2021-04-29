package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Producer;

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

    private final Activity mActivity;
    private static final String LOG_TAG = CompletionProducerAdapter.class.getName();

    public CompletionProducerAdapter(Activity activity) {

        super(activity,
                R.layout.list_item_producer_completion,
                null,
                new String[] {Producer.NAME, Producer.FORMATTED_ADDRESS},
                new int[]{R.id.list_item_producer_name, R.id.list_item_location_name},
                0);
//        Log.v(LOG_TAG, "CompletionProducerAdapter, hashCode=" + this.hashCode() + ", " + "activity = [" + activity + "], updateHandler = [" + updateHandler + "]");
        Log.v(LOG_TAG, "CompletionProducerAdapter, hashCode=" + this.hashCode() + ", " + "activity = [" + activity + "]");
        mActivity = activity;
    }

    @Override
    public void setViewText(TextView v, String text) {
        Log.v(LOG_TAG, "setViewText, hashCode=" + this.hashCode() + ", " + "v = [" + v + "], text = [" + text + "]");
        if (v.getId() == R.id.list_item_location_name) {
            v.setText(mActivity.getString(R.string.completion_subentry_formatting, text));
            v.setContentDescription(mActivity.getString(R.string.a11y_producer_location, text));
        } else if (v.getId() == R.id.list_item_producer_name) {
            v.setText(text);
            v.setContentDescription(mActivity.getString(R.string.a11y_producer_name, text));
        }
    }

    // no override possible for getColumnIndex: // Hack according to bug 903852
    // BUT cause of the exception was a missing column - confusing ...

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Log.v(LOG_TAG, "runQueryOnBackgroundThread, hashCode=" + this.hashCode() + ", " + "constraint = [" + constraint + "]");

        if (constraint == null) {
            return null;
        }

        return mActivity.getContentResolver().query(
                DatabaseContract.ProducerEntry.buildUriIncLocationWithPattern(String.valueOf(constraint)),
                QueryColumns.DrinkFragment.ProducerCompletionQuery.COLUMNS,
                null, null, null);

    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(QueryColumns.DrinkFragment.ProducerCompletionQuery.COL_PRODUCER_NAME);
    }
}
