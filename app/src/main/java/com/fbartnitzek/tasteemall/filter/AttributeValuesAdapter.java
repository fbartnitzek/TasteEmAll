package com.fbartnitzek.tasteemall.filter;

import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;

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

public class AttributeValuesAdapter extends RecyclerView.Adapter<AttributeValuesAdapter.ViewHolder>{

    private Cursor mCursor;

    private final AttributeValuesAdapterClickHandler mClickHandler;

    public AttributeValuesAdapter(AttributeValuesAdapterClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public interface AttributeValuesAdapterClickHandler {
        void onAttributeClick(String s, int adapterPosition);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attribute_value_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String value = mCursor.getString(0);
        holder.value.setText(value);
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

        final TextView value;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.value = (TextView) itemView.findViewById(R.id.list_item_value);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onAttributeClick(value.getText().toString(), getAdapterPosition());
        }
    }
}
