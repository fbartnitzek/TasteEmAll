package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
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


public abstract class CompletionLocationBaseAdapter extends CursorAdapter implements Filterable {

    private final Activity mActivity;
    private static final String LOG_TAG = CompletionLocationBaseAdapter.class.getName();

    public CompletionLocationBaseAdapter(Activity mActivity) {
        super(mActivity, null, 0);  //auto re-query???
        this.mActivity = mActivity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_location_completion, parent, false);
        CompletionLocationAdapter.ViewHolder vh = new CompletionLocationAdapter.ViewHolder(v);
        v.setTag(vh);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CompletionLocationAdapter.ViewHolder vh = (CompletionLocationAdapter.ViewHolder) view.getTag();
        vh.locationView.setText(cursorToString(cursor));
        vh.descriptionView.setText(cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_DESCRIPTION));
    }

    private CharSequence cursorToString(Cursor cursor) {
        String result = cursor.getString(
                QueryColumns.LocationPart.CompletionQuery.COL_FORMATTED_ADDRESS);
        if (result == null || result.length() == 0 || DatabaseContract.LocationEntry.GEOCODE_ME.equals(result)) {
            result = cursor.getString(QueryColumns.LocationPart.CompletionQuery.COL_INPUT);
        }
        return result;
    }

    @Override
    public void changeCursor(Cursor cursor) {
//        Log.v(LOG_TAG, "changeCursor, hashCode=" + this.hashCode() + ", " + "cursor = [" + cursor + "]");
        super.changeCursor(cursor);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
//        Log.v(LOG_TAG, "swapCursor, hashCode=" + this.hashCode() + ", " + "newCursor = [" + newCursor + "]");
        return super.swapCursor(newCursor);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {

        //        Log.v(LOG_TAG, "convertToString, hashCode=" + this.hashCode() + ", result=" + result + "cursor = [" + cursor + "]");
        return cursorToString(cursor);
    }

    public abstract Uri buildUri(String constraint);

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
//        Log.v(LOG_TAG, "runQueryOnBackgroundThread, hashCode=" + this.hashCode() + ", " + "constraint = [" + constraint + "]");

        Uri uri = buildUri(String.valueOf(constraint));
        Log.v(LOG_TAG, "runQueryOnBackgroundThread with uri, hashCode=" + this.hashCode() + ", " + "constraint = [" + constraint + "], uri=" + uri.toString());
        return mActivity.getContentResolver().query(
                uri,
                QueryColumns.LocationPart.CompletionQuery.COLUMNS,
                null,
                null,
                null);
    }

    static class ViewHolder  {
        final TextView locationView;
        final TextView descriptionView;
        ViewHolder(View view) {
            this.locationView = view.findViewById(R.id.list_item_location);
            this.descriptionView = view.findViewById(R.id.list_item_location_description);
        }
    }

}
