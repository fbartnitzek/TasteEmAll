package com.fbartnitzek.tasteemall.mainpager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{

    private final Context mContext;

    private Cursor mCursor;
    private final LocationAdapterClickHandler mClickHandler;

    public LocationAdapter(LocationAdapterClickHandler ch, Context context) {
        mClickHandler = ch;
        mContext = context;
    }


    public interface LocationAdapterClickHandler {
        void onClick(String formatted, Uri contentUri, ViewHolder viewHolder);
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        String country = mCursor.getString(QueryColumns.MainFragment.LocationQuery.COL_QUERY_LOCATION_COUNTRY);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int id = mCursor.getInt(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER__ID);
//            viewHolder.nameView.setTransitionName(mContext.getString(R.string.shared_transition_producer_producer) + id);
//        }

        viewHolder.countryView.setText(country);
//        viewHolder.nameView.setContentDescription(mContext.getString(R.string.a11y_producer_name, producerName));

        //TODO later with flag: https://github.com/hjnilsson/country-flags
        String formatted = mCursor.getString(QueryColumns.MainFragment.LocationQuery.COL_QUERY_LOCATION_FORMATTED);
        viewHolder.formattedView.setText(formatted);
//        viewHolder.formattedView.setContentDescription(mContext.getString(R.string.a11y_producer_location, producerLocation));

        String description = mCursor.getString(QueryColumns.MainFragment.LocationQuery.COL_QUERY_LOCATION_DESCRIPTION);
        viewHolder.descriptionView.setText(description);
//        viewHolder.descriptionView.setContentDescription(mContext.getString(R.string.a11y_producer_description, description));

    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView descriptionView;
        public final TextView formattedView;
        public final TextView countryView;


        public ViewHolder(View view) {
            super(view);
            this.descriptionView = (TextView) view.findViewById(R.id.list_item_location_description);
            this.formattedView = (TextView) view.findViewById(R.id.list_item_location_formatted);
            this.countryView = (TextView) view.findViewById(R.id.list_item_location_country);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            int locationId = mCursor.getInt(QueryColumns.MainFragment.LocationQuery.COL_QUERY_LOCATION__ID);

            mClickHandler.onClick(
                    mCursor.getString(QueryColumns.MainFragment.LocationQuery.COL_QUERY_LOCATION_FORMATTED),
                    DatabaseContract.LocationEntry.buildUri(locationId),
                    this
            );
        }
    }
}
