package com.fbartnitzek.tasteemall.filter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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

public class DateFilterDialogFragment extends AttributeFilterBaseDialogFragment{

//    private static final String LOG_TAG = DateFilterDialogFragment.class.getName();

    private EditText mToDate;
    private EditText mFromDate;
    private final SlideDateTimeListener toListener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            if (mToDate != null) {
                mToDate.setText(Utils.getIso8601Time(date));
            }
        }
    };
    private final SlideDateTimeListener fromListener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            if (mFromDate != null) {
                mFromDate.setText(Utils.getIso8601Time(date));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_date_dialog, container);

        mFromDate = view.findViewById(R.id.filter_from_date);
        mFromDate.setText(R.string.filter_date_unlimited);
        mFromDate.setOnClickListener(view1 -> startDatePicker(fromListener, 0, 0, 0));

        mToDate = view.findViewById(R.id.filter_to_date);
        mToDate.setText(R.string.filter_date_unlimited);
        mToDate.setOnClickListener(view12 -> startDatePicker(toListener, 23, 59, 59));

        setButtonListeners(view);
        return view;

    }

    private void startDatePicker(SlideDateTimeListener listener, int hour, int minute, int second) {

        // TODO: store / restore date...
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        new SlideDateTimePicker.Builder(Objects.requireNonNull(getActivity()).getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(calendar.getTime())
                .setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    @Override
    protected void onOkClicked() {
        String toDate = mToDate.getText().toString();
        String fromDate = mFromDate.getText().toString();
        String unlimited = getString(R.string.filter_date_unlimited);

        JSONObject filterJson = new JSONObject();
        try {
            if (unlimited.equals(toDate)){
                if (unlimited.equals(fromDate)) {   // both unlimited
                    sendFilterUpdate(null);
                    return;
                } else {    // t >= from
                    filterJson.put(DatabaseContract.Operations.GTE, DatabaseContract.encodeValue(fromDate));
                }
            } else {    // to = something
                if (unlimited.equals(fromDate)) {  // t <= to
                    filterJson.put(DatabaseContract.Operations.LTE, DatabaseContract.encodeValue(toDate));
                } else {    // t <= to, t >= from
                    JSONArray array = new JSONArray();
                    array.put(DatabaseContract.encodeValue(fromDate));
                    array.put(DatabaseContract.encodeValue(toDate));
                    filterJson.put(DatabaseContract.Operations.BETWEEN, array);
                }
            }

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sendFilterUpdate(filterJson);

    }
}
