package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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

    private static final String LOG_TAG = TestUtils.class.getName();

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

    public static ContentValues[] createBulkReviews(int n) {
        ContentValues[] cvs = new ContentValues[n];
        for (int i = 0; i < n; ++i) {
            cvs[i] = DatabaseHelper.buildReviewValues(
                    "bulk_" + i, String.valueOf(i), "desc_" + i,
                    "look_"+i, "smell_" + i, "taste_"+i,
                    String.valueOf(System.currentTimeMillis()),
                    "1", "1", "");
        }
        return cvs;
    }

    public static void printCurrentCursorEntry(Cursor cursor) {
        String result = "";
        for (int i = 0 ; i < cursor.getColumnCount() ; ++i) {
            result += i + ": " + cursor.getString(i) + ", ";
        }
        Log.v(LOG_TAG, "printCurrentCursorEntry, " + "content= [" + result+ "]");
    }

    public static void printAllCursorEntries(Cursor teamCursor, String msg) {
        Log.v(LOG_TAG, "printAllCursorEntries, " + "msg = [" + msg + "]");
        while (teamCursor.moveToNext()){
            printCurrentCursorEntry(teamCursor);
        }
    }


    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
