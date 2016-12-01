package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.net.Uri;

import com.fbartnitzek.tasteemall.data.DatabaseContract;

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


public class CompletionLocationDescriptionAdapter extends CompletionLocationBaseAdapter {

    public CompletionLocationDescriptionAdapter(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public Uri buildUri(String constraint) {
        return DatabaseContract.LocationEntry.buildUriWithDescriptionPattern(constraint);
    }
}
