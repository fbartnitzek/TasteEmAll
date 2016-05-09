package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
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

public class CompletionDrinkAdapter extends SimpleCursorAdapter {

    private final CompletionDrinkAdapterSelectionHandler mSelectHandler;
    private final Activity mActivity;

    private static final String LOG_TAG = CompletionDrinkAdapter.class.getName();

    interface CompletionDrinkAdapterSelectionHandler {
        void onSelectedDrink(int drink_Id, String drinkName, String drinkId, String producerName);
    }

    public CompletionDrinkAdapter(Activity activity,
                                  CompletionDrinkAdapterSelectionHandler selectHandler) {
        super(activity,
                R.layout.list_item_drink_completion,
                null,
                new String[]{Drink.NAME, Drink.TYPE, Producer.NAME},
                new int[]{R.id.list_item_drink_name, R.id.list_item_drink_type,
                        R.id.list_item_producer_name},
                0);
        mActivity = activity;
        mSelectHandler = selectHandler;
    }

    @Override
    public void setViewText(TextView v, String text) {
        Log.v(LOG_TAG, "setViewText, hashCode=" + this.hashCode() + ", " + "v = [" + v + "], text = [" + text + "]");
        switch (v.getId()) {
            case R.id.list_item_producer_name:
                v.setText(mActivity.getString(
                        R.string.completion_subentry_formatting, text));
                break;
            case R.id.list_item_drink_type:
                v.setText(mActivity.getString(
                        R.string.completion_prefix_formatting, text));
                break;
            default:
                super.setViewText(v, text);
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        // uri with name for drink or producer... (currently just drink)
        Log.v(LOG_TAG, "runQueryOnBackgroundThread, hashCode=" + this.hashCode() + ", " + "constraint = [" + constraint + "]");
        return mActivity.getContentResolver().query(
                DatabaseContract.DrinkEntry.buildUriWithName(String.valueOf(constraint)),
                QueryColumns.ReviewFragment.DRINK_QUERY_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        Log.v(LOG_TAG, "convertToString, hashCode=" + this.hashCode() + ", " + "cursor = [" + cursor + "]");
        String drinkName = cursor.getString(QueryColumns.ReviewFragment.COL_QUERY_DRINK_NAME);
        String drinkId = cursor.getString(QueryColumns.ReviewFragment.COL_QUERY_DRINK_ID);
        int drink_Id = cursor.getInt(QueryColumns.ReviewFragment.COL_QUERY_DRINK__ID);
        String producerName = cursor.getString(QueryColumns.ReviewFragment.COL_QUERY_PRODUCER_NAME);
        mSelectHandler.onSelectedDrink(drink_Id, drinkName, drinkId, producerName);
        return drinkName;
    }
}
