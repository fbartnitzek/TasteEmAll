package com.example.fbartnitzek.tasteemall;

import android.content.ContentValues;

import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;

/**
 * Copyright 2015.  Frank Bartnitzek
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

public class TestUtils {

    public static ContentValues createLocationValues() {
        return DatabaseHelper.buildLocationValues("1", "Leipzig", "GERMANY", "04103", "", "");
    }
}
