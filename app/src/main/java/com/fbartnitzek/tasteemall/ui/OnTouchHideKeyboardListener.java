package com.fbartnitzek.tasteemall.ui;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * //src: http://stackoverflow.com/questions/13116784/how-to-hide-virtual-keyboard-on-touch-of-a-spinner
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

public class OnTouchHideKeyboardListener implements View.OnTouchListener {

    private final Fragment fragment;

    public OnTouchHideKeyboardListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(fragment.getActivity())
                .getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocus = fragment.getActivity().getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(
                    currentFocus.getWindowToken(),
                    0); //InputMethodManager.HIDE_NOT_ALWAYS also possible
        }
        return false;
    }
}
