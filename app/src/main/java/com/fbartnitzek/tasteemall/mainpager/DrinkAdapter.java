package com.fbartnitzek.tasteemall.mainpager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;

import org.jetbrains.annotations.NotNull;

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
    private final Context mContext;
    private Cursor mCursor;

//    private static final String LOG_TAG = DrinkAdapter.class.getName();

    public interface DrinkAdapterClickHandler {
        void onClick(String drinkName, Uri contentUri, ViewHolder viewHolder);
    }

    private final DrinkAdapterClickHandler mClickHandler;


    public DrinkAdapter(DrinkAdapterClickHandler clickHandler, Context context) {
        this.mClickHandler = clickHandler;
        mContext = context;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_drink_recycler, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException(mContext.getString(R.string.error_not_bound_to_recycler_view));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);


        int id = mCursor.getInt(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_DRINK__ID);
        holder.drinkNameView.setTransitionName(mContext.getString(R.string.shared_transition_drink_drink) + id);
        holder.producerNameView.setTransitionName(mContext.getString(R.string.shared_transition_drink_producer) + id);

        String producerName = mCursor.getString(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_PRODUCER_NAME);
        holder.producerNameView.setText(producerName);
        holder.producerNameView.setContentDescription(mContext.getString(R.string.a11y_producer_name, producerName));

        String drinkName = mCursor.getString(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_DRINK_NAME);
        holder.drinkNameView.setText(drinkName);
        holder.drinkNameView.setContentDescription(mContext.getString(R.string.a11y_drink_name, drinkName));

        String drinkType = mCursor.getString(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_DRINK_TYPE);
        holder.drinkTypeView.setText(drinkType);
        holder.drinkTypeView.setContentDescription(mContext.getString(R.string.a11y_drink_type,
                mContext.getString(Utils.getReadableDrinkNameId(mContext, drinkType))));

        String drinkStyle = mCursor.getString(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_DRINK_STYLE);
        holder.drinkStyleView.setText(drinkStyle);
        if (drinkStyle == null || drinkStyle.length() == 0) {
            holder.drinkStyleView.setContentDescription(mContext.getString(R.string.a11y_no_drink_style));
        } else {
            holder.drinkStyleView.setContentDescription(mContext.getString(R.string.a11y_drink_style, drinkStyle));
        }

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
            this.producerNameView = view.findViewById(R.id.list_item_producer_name);
            this.drinkNameView = view.findViewById(R.id.list_item_drink_name);
            this.drinkTypeView = view.findViewById(R.id.list_item_drink_type);
            this.drinkStyleView = view.findViewById(R.id.list_item_drink_style);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            int drinkId = mCursor.getInt(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_DRINK__ID);
            Uri contentUri = DatabaseContract.DrinkEntry.buildUriIncludingProducer(drinkId);
            mClickHandler.onClick(
                    mCursor.getString(QueryColumns.MainFragment.DrinkWithProducerQuery.COL_DRINK_NAME),
                    contentUri,
                    this
            );
        }
    }

}
