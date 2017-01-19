package com.fbartnitzek.tasteemall.location;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
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

public class ReviewOfLocationAdapter extends RecyclerView.Adapter<ReviewOfLocationAdapter.ViewHolder>{

    private final Context mContext;
    private Cursor mCursor;
    private final ReviewAdapterClickHandler mClickHandler;

    public interface ReviewAdapterClickHandler {
        void onClick(Uri contentUri, ViewHolder viewHolder);
    }

    public ReviewOfLocationAdapter(ReviewAdapterClickHandler mClickHandler, Context context) {
        this.mClickHandler = mClickHandler;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_review_recycler, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException(mContext.getString(R.string.error_not_bound_to_recycler_view));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String drinkName = mCursor.getString(QueryColumns.MapFragment.ReviewsSubQuery.COL_DRINK_NAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int id = mCursor.getInt(QueryColumns.MapFragment.ReviewsSubQuery.COL_REVIEW__ID);
            holder.producerNameView.setTransitionName(mContext.getString(R.string.shared_transition_review_producer) + id);
            holder.drinkNameView.setTransitionName(mContext.getString(R.string.shared_transition_review_drink) + id);
        }

        holder.drinkNameView.setText(drinkName);
        holder.drinkNameView.setContentDescription(mContext.getString(R.string.a11y_drink_name, drinkName));

        String producerName = mCursor.getString(QueryColumns.MapFragment.ReviewsSubQuery.COL_PRODUCER_NAME);
        holder.producerNameView.setText(producerName);
        holder.producerNameView.setContentDescription(mContext.getString(R.string.a11y_producer_name, producerName));

        String drinkType = mCursor.getString(QueryColumns.MapFragment.ReviewsSubQuery.COL_DRINK_TYPE);
        holder.drinkTypeView.setText(drinkType);
        holder.drinkTypeView.setContentDescription(mContext.getString(R.string.a11y_drink_type,
                mContext.getString(Utils.getReadableDrinkNameId(mContext, drinkType))));

        String reviewRating = mCursor.getString(QueryColumns.MapFragment.ReviewsSubQuery.COL_REVIEW_RATING);
        holder.reviewRatingView.setText(reviewRating);
        holder.reviewRatingView.setContentDescription(mContext.getString(R.string.a11y_review_rating, reviewRating));

        String readableDate = mCursor.getString(QueryColumns.MapFragment.ReviewsSubQuery.COL_REVIEW_READABLE_DATE);
        holder.reviewDateView.setText(readableDate);
        holder.reviewDateView.setContentDescription(mContext.getString(R.string.a11y_review_date, readableDate));
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
        public final TextView reviewRatingView;
        public final TextView reviewDateView;

        public ViewHolder(View view) {
            super(view);
            this.drinkNameView = (TextView) view.findViewById(R.id.list_item_drink_name);
            this.producerNameView = (TextView) view.findViewById(R.id.list_item_producer_name);
            this.drinkTypeView = (TextView) view.findViewById(R.id.list_item_drink_type);
            this.reviewRatingView = (TextView) view.findViewById(R.id.list_item_review_rating);
            this.reviewDateView = (TextView) view.findViewById(R.id.list_item_review_date);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            int reviewId = mCursor.getInt(QueryColumns.MapFragment.ReviewsSubQuery.COL_REVIEW__ID);
            Uri contentUri = DatabaseContract.ReviewEntry.buildUriForShowReview(reviewId);
            mClickHandler.onClick(contentUri,this);

        }
    }
}
