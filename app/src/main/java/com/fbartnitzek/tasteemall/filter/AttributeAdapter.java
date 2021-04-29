package com.fbartnitzek.tasteemall.filter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;

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


public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.ViewHolder>{

    final List<String> mAttributeList;
    private final AttributeAdapterHandler mClickHandler;
    private final Context mContext;
    private final ArrayList<Integer> mSelected;

    public interface AttributeAdapterHandler {
        void onClick(String attributeName, int adapterPosition);
//        void onAllLoaded();
    }

    public AttributeAdapter(List<String> attributeList, AttributeAdapterHandler clickHandler, Context context, ArrayList<Integer> mSelected) {
        this.mAttributeList = attributeList;
        this.mClickHandler = clickHandler;
        this.mContext = context;
        this.mSelected = mSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_filter_attribute, parent, false);
        v.setFocusable(true);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.attributeNameView.setText(
                mContext.getString(Utils.getAttributeNameId(mContext, mAttributeList.get(position))));
        if (mSelected.contains(position)) {
            holder.attributeNameView.setBackgroundColor(Color.GREEN);
        }
//        if (mAttributeList.size() == position + 1) {
//            mClickHandler.onAllLoaded();
//        }
    }

    @Override
    public int getItemCount() {
        return mAttributeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView attributeNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.attributeNameView = (TextView) itemView.findViewById(R.id.list_item_attribute_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mAttributeList.get(getAdapterPosition()), getAdapterPosition());
        }
    }
}
