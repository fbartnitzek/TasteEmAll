package com.fbartnitzek.tasteemall.location;

import android.app.Activity;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;

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

public class LocationArrayAdapter extends ArrayAdapter<Address> {

    private final Activity mActivity;
    private static final String LOG_TAG = LocationArrayAdapter.class.getName();

    public LocationArrayAdapter(Activity activity) {
        super(activity, R.layout.list_item_location_list);
        this.mActivity = activity;
    }

    private static class ViewHolder {
        TextView formattedAddressView;
        TextView countryView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.v(LOG_TAG, "getView, hashCode=" + this.hashCode() + ", " + "position = [" + position + "], convertView = [" + convertView + "], parent = [" + parent + "]");
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_location_list, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.countryView = (TextView) rowView.findViewById(R.id.list_item_location_country);
            viewHolder.formattedAddressView = (TextView) rowView.findViewById(R.id.list_item_location_formatted);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        Address address = this.getItem(position);

        viewHolder.countryView.setText(address.getCountryCode());
        viewHolder.formattedAddressView.setText(Utils.formatAddress(address));

        return rowView;
    }
}
