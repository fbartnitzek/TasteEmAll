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

public class ProducerAdapter extends RecyclerView.Adapter<ProducerAdapter.ViewHolder>{

//    private static final String LOG_TAG = ProducerAdapter.class.getName();
    private final Context mContext;

    private Cursor mCursor;
    private final ProducerAdapterClickHandler mClickHandler;

    public ProducerAdapter(ProducerAdapterClickHandler ch, Context context) {
        mClickHandler = ch;
        mContext = context;
    }


    public interface ProducerAdapterClickHandler {
        void onClick(String producerName, Uri contentUri, ViewHolder viewHolder);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_producer_recycler, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        String producerName = mCursor.getString(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER_NAME);

        int id = mCursor.getInt(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER__ID);
        viewHolder.nameView.setTransitionName(mContext.getString(R.string.shared_transition_producer_producer) + id);

        viewHolder.nameView.setText(producerName);
        viewHolder.nameView.setContentDescription(mContext.getString(R.string.a11y_producer_name, producerName));

        //TODO later with flag: https://github.com/hjnilsson/country-flags
        String producerLocation = mCursor.getString(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER_LOCATION);
        viewHolder.locationView.setText(producerLocation);
        viewHolder.locationView.setContentDescription(mContext.getString(R.string.a11y_producer_location, producerLocation));

        String producerDescription = mCursor.getString(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER_DESCRIPTION);
        viewHolder.descriptionView.setText(producerDescription);
        viewHolder.descriptionView.setContentDescription(mContext.getString(R.string.a11y_producer_description, producerDescription));

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
        public final TextView nameView;
        public final TextView descriptionView;
        // --Commented out by Inspection (07.05.16 22:46):public final TextView websiteView;
        public final TextView locationView;

        public ViewHolder(View view) {
            super(view);
            this.nameView = view.findViewById(R.id.list_item_producer_name);
            this.descriptionView = view.findViewById(R.id.list_item_producer_description);
//            this.websiteView = (TextView) view.findViewById(R.id.list_item_producer_website);;
            this.locationView = view.findViewById(R.id.list_item_location_name);

            // as a start - just click for whole producer
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
//            Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
            int producerId = mCursor.getInt(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER__ID);
            Uri contentUri = DatabaseContract.ProducerEntry.buildUri(producerId);
//            Log.v(LOG_TAG, "onClick, producerId=" + producerId + ", contentUri= " +  contentUri + ", hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
            mClickHandler.onClick(
                    mCursor.getString(QueryColumns.MainFragment.ProducerQuery.COL_QUERY_PRODUCER_NAME),
                    contentUri,
                    this
            );
        }
    }
}
