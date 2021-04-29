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


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;

    public interface UserAdapterClickHandler {

        void onClick(String userName, Uri contentUri, RecyclerView.ViewHolder viewHolder);
    }
    private final UserAdapterClickHandler mClickHandler;

    public UserAdapter(UserAdapterClickHandler mClickHandler, Context mContext) {
        this.mClickHandler = mClickHandler;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_user_completion, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException(mContext.getString(R.string.error_not_bound_to_recycler_view));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.userNameView.setText(mCursor.getString(QueryColumns.MainFragment.UserQuery.COL_NAME));
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
        public final TextView userNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.userNameView = (TextView) itemView.findViewById(R.id.list_item_user_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            int userId = mCursor.getInt(QueryColumns.MainFragment.UserQuery.COL__ID);
            Uri contentUri = DatabaseContract.UserEntry.buildUri(userId);
            mClickHandler.onClick(
                    mCursor.getString(QueryColumns.MainFragment.UserQuery.COL_NAME),
                    contentUri, this);
        }
    }
}
