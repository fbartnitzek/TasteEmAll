package com.fbartnitzek.tasteemall.location;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.google.android.gms.maps.model.LatLng;

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


public class ReviewLocationAdapter extends RecyclerView.Adapter<ReviewLocationAdapter.ViewHolder>{

    private final Context mContext;
    private Cursor mCursor;
    private static final String LOG_TAG = ReviewLocationAdapter.class.getName();

    private final ReviewLocationAdapterClickHandler mClickHandler;

    public interface ReviewLocationAdapterClickHandler {
        void onClick(String reviewLocationId, ViewHolder viewHolder, LatLng latLng, String formatted, String description);
    }

    public ReviewLocationAdapter(Context mContext, ReviewLocationAdapterClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_location_recycler, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String country = mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_COUNTRY);
        String formatted = mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_FORMATTED);
        String description = mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_DESCRIPTION);
//        Log.v(LOG_TAG, "onBindViewHolder, country=" + country + ", " + "formatted = [" + formatted + "], position = [" + position + "]");
        if (mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_REVIEW_LOCATION_ID) == null) {
            holder.formattedView.setText(R.string.no_review_location_given);
        } else {
            holder.formattedView.setText(formatted);
        }

        holder.countryView.setText(country);
        holder.descriptionView.setText(description);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView countryView;
        final TextView formattedView;
        final TextView descriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.countryView = (TextView) itemView.findViewById(R.id.list_item_location_country);
            this.formattedView = (TextView) itemView.findViewById(R.id.list_item_location_formatted);
            this.descriptionView = (TextView) itemView.findViewById(R.id.list_item_location_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
//            Log.v(LOG_TAG, "onClick, getAdapterPosition=" + getAdapterPosition());
            String locationId = mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_REVIEW_LOCATION_ID);
            if (locationId == null) {   // workaround for null (empty location)
                mClickHandler.onClick(null, this, null, null, null);
            } else {
                double lat = mCursor.getDouble(QueryColumns.MapFragment.ReviewLocations.COL_REVIEW_LOCATION_LAT);
                double lon = mCursor.getDouble(QueryColumns.MapFragment.ReviewLocations.COL_REVIEW_LOCATION_LON);
                String formatted = mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_FORMATTED);
                String description = mCursor.getString(QueryColumns.MapFragment.ReviewLocations.COL_DESCRIPTION);
                mClickHandler.onClick(locationId, this, new LatLng(lat, lon), formatted, description);
            }
        }
    }

}
