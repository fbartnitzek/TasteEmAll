package com.fbartnitzek.tasteemall.location;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.google.android.gms.maps.model.LatLng;



/**
 * Copyright 2017.  Frank Bartnitzek
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

public class ProducerLocationAdapter extends RecyclerView.Adapter<ProducerLocationAdapter.ViewHolder>{

    private Cursor mCursor;
    private final ProducerLocationAdapterClickHandler mClickHandler;


    interface ProducerLocationAdapterClickHandler {
        void onClick(String producerId, ViewHolder viewHolder, LatLng latLng, String formatted, String name);
    }


    public ProducerLocationAdapter(ProducerLocationAdapterClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_producer_location_recycler, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String name = mCursor.getString(QueryColumns.MapFragment.ProducerLocations.COL_NAME);
        String country = mCursor.getString(QueryColumns.MapFragment.ProducerLocations.COL_COUNTRY);
        String formatted = mCursor.getString(QueryColumns.MapFragment.ProducerLocations.COL_FORMATTED);
        holder.producerName.setText(name);
        holder.country.setText(country);
        holder.formatted.setText(formatted);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView producerName;
        final TextView country;
        final TextView formatted;

        public ViewHolder(View itemView) {
            super(itemView);
            this.producerName = (TextView) itemView.findViewById(R.id.list_item_producer_name);
            this.country = (TextView) itemView.findViewById(R.id.list_item_producer_country);
            this.formatted = (TextView) itemView.findViewById(R.id.list_item_producer_location);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            String producerId = mCursor.getString(QueryColumns.MapFragment.ProducerLocations.COL_PRODUCER_ID);
            double lat = mCursor.getDouble(QueryColumns.MapFragment.ProducerLocations.COL_PRODUCER_LOCATION_LAT);
            double lon = mCursor.getDouble(QueryColumns.MapFragment.ProducerLocations.COL_PRODUCER_LOCATION_LON);
            String formatted = mCursor.getString(QueryColumns.MapFragment.ProducerLocations.COL_FORMATTED);
            String name = mCursor.getString(QueryColumns.MapFragment.ProducerLocations.COL_NAME);
            mClickHandler.onClick(producerId, this, new LatLng(lat, lon), formatted, name);
        }
    }
}

