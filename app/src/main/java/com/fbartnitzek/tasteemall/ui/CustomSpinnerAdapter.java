package com.fbartnitzek.tasteemall.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> data;
    private final int spinnerLayout;
    private final int a11yStringId;
    private final LayoutInflater inflater;
//    private static final String LOG_TAG = CustomSpinnerAdapter.class.getName();

    public CustomSpinnerAdapter(Context context, ArrayList<String> objects, int spinnerLayout, int a11yStringId) {
        super(context, spinnerLayout, objects);
//        Log.v(LOG_TAG, "CustomSpinnerAdapter, hashCode=" + this.hashCode() + ", " + "context = [" + context + "], objects = [" + objects + "], spinnerLayout = [" + spinnerLayout + "], a11yStringId = [" + a11yStringId + "]");
        this.spinnerLayout = spinnerLayout;
        this.context = context;
        data = objects;
        this.a11yStringId = a11yStringId;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.v(LOG_TAG, "getView, hashCode=" + this.hashCode() + ", " + "position = [" + position + "], convertView = [" + convertView + "], parent = [" + parent + "]");
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row;
        // reuse view
        if (convertView == null) {
            row = inflater.inflate(spinnerLayout, parent, false);
        } else {
            row = convertView;
        }

        TextView tvType = row.findViewById(R.id.tvEntry);
        String text = data.get(position);
        tvType.setText(text);
        tvType.setContentDescription(context.getString(a11yStringId, text));
        return row;
    }


}
