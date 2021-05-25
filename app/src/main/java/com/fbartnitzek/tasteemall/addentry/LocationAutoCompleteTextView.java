package com.fbartnitzek.tasteemall.addentry;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

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

public class LocationAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private boolean mShowDropDown = false;

    private static final String LOG_TAG = LocationAutoCompleteTextView.class.getName();

    public LocationAutoCompleteTextView(Context context) {
        super(context);
    }

    public LocationAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean enoughToFilter() {
        Log.v(LOG_TAG, "enoughToFilter, mShowDropDown=" + mShowDropDown);
        return mShowDropDown || super.enoughToFilter();
    }

    // base-src: http://stackoverflow.com/questions/11284368/autocompletetextview-force-to-show-all-items

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
//            performFiltering(getText(), 0);
            if (getText().length() > 0) {   //never reached...
                Log.v(LOG_TAG, "onFocusChanged, reset mShowDropDown:" + mShowDropDown);
                mShowDropDown = false;
            }
            if (mShowDropDown){
                showDropDown();
            }
        }
    }

    public void setShowDropDown(boolean mShowDropDown) {
        Log.v(LOG_TAG, "setShowDropDown, hashCode=" + this.hashCode() + ", " + "mShowDropDown = [" + mShowDropDown + "]");
        this.mShowDropDown = mShowDropDown;
    }

}
