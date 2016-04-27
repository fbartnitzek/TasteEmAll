package com.example.fbartnitzek.tasteemall;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;

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

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.ViewHolder> {
    private Cursor mCursor;

    private static final String LOG_TAG = DrinkAdapter.class.getName();

    public interface DrinkAdapterClickHandler {
        void onClick(String drinkName, Uri contentUri, ViewHolder viewHolder);
    }

    private final DrinkAdapterClickHandler mClickHandler;


    public DrinkAdapter(DrinkAdapterClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_drink_recycler, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        // TODO: joined query...
        holder.producerNameView.setText(
                mCursor.getString(MainFragment.COL_QUERY_DRINK_PRODUCER_ID));
        holder.drinkNameView.setText(
                mCursor.getString(MainFragment.COL_QUERY_DRINK_NAME));
        holder.drinkTypeView.setText(
                mCursor.getString(MainFragment.COL_QUERY_DRINK_TYPE));
        holder.drinkStyleView.setText(
                mCursor.getString(MainFragment.COL_QUERY_DRINK_STYLE));

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView producerNameView;
        public final TextView drinkNameView;
        public final TextView drinkTypeView;
        public final TextView drinkStyleView;

        public ViewHolder(View view) {
            super(view);
            this.producerNameView = (TextView) view.findViewById(R.id.list_item_producer_name);
            this.drinkNameView = (TextView) view.findViewById(R.id.list_item_drink_name);
            this.drinkTypeView = (TextView) view.findViewById(R.id.list_item_drink_type);
            this.drinkStyleView = (TextView) view.findViewById(R.id.list_item_drink_style);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            int drinkId = mCursor.getInt(MainFragment.COL_QUERY_DRINK__ID);
            Uri contentUri = DatabaseContract.DrinkEntry.buildUri(drinkId);
            mClickHandler.onClick(
                    mCursor.getString(MainFragment.COL_QUERY_DRINK_NAME),
                    contentUri,
                    this
            );
        }
    }

}
