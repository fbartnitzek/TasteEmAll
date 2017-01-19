package com.fbartnitzek.tasteemall.filter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;

import java.util.ArrayList;
import java.util.List;

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

public class AttributeValuesSelectAdapter extends RecyclerView.Adapter<AttributeValuesSelectAdapter.ViewHolder> {


    private Cursor mCursor;

    public AttributeValuesSelectAdapter() {
    }

    @Override
    public AttributeValuesSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attribute_value_select_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttributeValuesSelectAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.value.setText(mCursor.getString(0));
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        selectedItems.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    // src: http://www.grokkingandroid.com/statelistdrawables-for-recyclerview-selection/
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

//    public void clearSelections() {
//        selectedItems.clear();
//        notifyDataSetChanged();
//    }

//    public int getSelectedItemCount() {
//        return selectedItems.size();
//    }

    public List<String> getSelectedItems() {
        List<String> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            int pos = selectedItems.keyAt(i);
            mCursor.moveToPosition(pos);
            items.add(mCursor.getString(0));
        }
        return items;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView value;
        final CheckBox select;

        ViewHolder(View itemView) {
            super(itemView);
            this.value = (TextView) itemView.findViewById(R.id.list_item_value);
            this.select = (CheckBox) itemView.findViewById(R.id.list_item_select);
            // select from selectedItems
            this.select.setSelected(selectedItems.get(getAdapterPosition(), false));

            this.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedItems.get(getAdapterPosition(), false)) { //previously true
                        selectedItems.delete(getAdapterPosition());
                        select.setSelected(false);
                    } else {
                        selectedItems.put(getAdapterPosition(), true);
                        select.setSelected(true);
                    }
                }
            });
        }

    }
}

