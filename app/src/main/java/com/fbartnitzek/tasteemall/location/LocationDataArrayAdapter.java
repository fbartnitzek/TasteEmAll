package com.fbartnitzek.tasteemall.location;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;


public class LocationDataArrayAdapter extends ArrayAdapter<AddressData> {
    private static final String LOG_TAG = LocationDataArrayAdapter.class.getName();
    private final Activity activity;

    public LocationDataArrayAdapter(Activity activity) {
        super(activity, R.layout.list_item_location_list);
        this.activity = activity;
    }

    private static class ViewHolder {
        TextView formattedAddressView;
        TextView countryView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(LOG_TAG, "getView, hashCode=" + this.hashCode() + ", " + "position = [" + position + "], convertView = [" + convertView + "], parent = [" + parent + "]");
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_location_list, parent, false);
            Log.v(LOG_TAG, "guess rowView by id: " + rowView);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.countryView = rowView.findViewById(R.id.list_item_location_country);
            viewHolder.formattedAddressView = rowView.findViewById(R.id.list_item_location_formatted);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        AddressData address = this.getItem(position);

        viewHolder.countryView.setText(address.getCountryCode());
        viewHolder.formattedAddressView.setText(address.getFormatted());

        return rowView;
    }
}
