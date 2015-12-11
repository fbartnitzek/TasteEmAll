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

    public static ContentValues createLocationLeipzig() {
        return DatabaseHelper.buildLocationValues("1", "Leipzig", "GERMANY", "04103", "", "");
    }

    public static ContentValues createBreweryBayrischerBahnhof() {
        return DatabaseHelper.buildBreweryValues("1", "Bayerischer Bahnhof", "2000",
                "http://www.bayerischer-bahnhof.de", "1");
    }

    public static ContentValues createBeerGose() {
        return DatabaseHelper.buildBeerValues("1", "Gose", "4.5", "", "10.8", "Gose", "", "1");
    }

    public static ContentValues createUserFrank() {
        return DatabaseHelper.buildUserValues("1", "fbartnitzek", "Frank Bartnitzek",
                "frank_bartnitzek@test.de", "");
    }

    public static ContentValues createReview1() {
        return DatabaseHelper.buildReviewValues("1", "++", "lecker", "obergärig", "",
                "leicht säuerlich", "20151211T160000", "1", "1", "");
    }
}
