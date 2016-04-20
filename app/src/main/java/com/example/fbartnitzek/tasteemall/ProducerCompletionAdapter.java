package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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

public class ProducerCompletionAdapter extends CursorAdapter{

    private final LayoutInflater mInflater;
    private final Activity mActivity;

    private static final String LOG_TAG = ProducerCompletionAdapter.class.getName();

    public ProducerCompletionAdapter(Activity activity, Cursor c, boolean autoRequery) {
        super(activity, c, autoRequery);
        Log.v(LOG_TAG, "ProducerCompletionAdapter, hashCode=" + this.hashCode() + ", " + "activity = [" + activity + "], c = [" + c + "], autoRequery = [" + autoRequery + "]");
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
//        mInflater = activity.getLayoutInflater();
        //alternatively:

    }

//    @Override
//    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
////        return super.runQueryOnBackgroundThread(constraint);
//        String filter = constraint == null ? "" : constraint.toString();
//        Log.v(LOG_TAG, "runQueryOnBackgroundThread, hashCode=" + this.hashCode() + ", " + "filter= [" + filter + "]" + ", " + "constraint = [" + constraint + "]");
////        Cursor c = mActivity.getContentResolver().query(<something using "filter">);
//        Cursor c = mActivity.getContentResolver().query(
//                DatabaseContract.ProducerEntry.CONTENT_URI,
//                AddDrinkFragment.PRODUCER_QUERY_COLUMNS,
//                DatabaseProvider.PRODUCERS_BY_NAME_SELECTION,
//                new String[]{filter + "%"},
//                DatabaseContract.ProducerEntry.TABLE_NAME + "." + Producer.NAME + " ASC");
//        if (c != null) {
//            c.getCount();        // Force data to be sent over right here
//        }
//        return c;
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // CursorAdapter reuses Views itself, without ViewHolder
        Log.v(LOG_TAG, "newView, hashCode=" + this.hashCode() + ", " + "context = [" + context + "], cursor = [" + cursor + "], parent = [" + parent + "]");
        return mInflater.inflate(R.layout.list_item_producer_recycler, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "bindView, hashCode=" + this.hashCode() + ", " + "view = [" + view + "], context = [" + context + "], cursor = [" + cursor + "]");
        TextView producerName = (TextView) view.findViewById(R.id.list_item_producer_name);
        producerName.setText(cursor.getShort(AddDrinkFragment.COL_QUERY_PRODUCER_NAME));
        TextView producerLocation = (TextView) view.findViewById(R.id.list_item_producer_location);
        producerLocation.setText(
                context.getString(R.string.location_completion_postfix,
                        cursor.getString(AddDrinkFragment.COL_QUERY_PRODUCER_LOCATION)));
    }

}
