package com.fbartnitzek.tasteemall.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;

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
    public Resources res;
    private final LayoutInflater inflater;


    public CustomSpinnerAdapter(Context context, ArrayList<String> objects, int spinnerLayout) {
        super(context, spinnerLayout, objects);
        this.spinnerLayout = spinnerLayout;
        this.context = context;
        data = objects;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(spinnerLayout, parent, false);
        TextView tvType = (TextView) row.findViewById(R.id.tvEntry);
        tvType.setText(data.get(position));
        return row;
    }


}
