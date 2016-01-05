package com.example.fbartnitzek.tasteemall;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BreweryAdapter extends CursorAdapter{

    private static final String LOG_TAG = BreweryAdapter.class.getName();

    public BreweryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        Log.v(LOG_TAG, "BreweryAdapter, " + "context = [" + context + "], c = [" + c + "], flags = [" + flags + "]");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, "newView, " + "context = [" + context + "], cursor = [" + cursor + "], parent = [" + parent + "]");
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_brewery, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "bindView, " + "view = [" + view + "], context = [" + context + "], cursor = [" + cursor + "]");
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //DONE: pimp my cursor => query for brewery with joined location!
        String name = cursor.getString(MainFragment.COL_QUERY_BREWERY_NAME);
        String introduced = cursor.getString(MainFragment.COL_QUERY_BREWERY_INTRODUCED);

        String locality = cursor.getString(MainFragment.COL_QUERY_BREWERY_LOCALITY);
        String country = cursor.getString(MainFragment.COL_QUERY_BREWERY_COUNTRY);

        viewHolder.countryView.setText(country);    //TODO later a flag :-)
        viewHolder.localityView.setText(locality);
        viewHolder.introducedView.setText(introduced);
        viewHolder.nameView.setText(name);
    }

    public static class ViewHolder{
        public final TextView nameView;
        public final TextView introducedView;
        public final TextView countryView;
        public final TextView localityView;

        public ViewHolder(View view) {
            this.nameView = (TextView) view.findViewById(R.id.list_item_brewery_name);
            this.introducedView = (TextView) view.findViewById(R.id.list_item_brewery_introduced);
            this.countryView = (TextView) view.findViewById(R.id.list_item_brewery_country);;
            this.localityView = (TextView) view.findViewById(R.id.list_item_brewery_locality);
        }
    }
}
