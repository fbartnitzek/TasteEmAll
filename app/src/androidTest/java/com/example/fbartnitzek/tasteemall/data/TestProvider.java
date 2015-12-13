package com.example.fbartnitzek.tasteemall.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.BeerEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.BreweryEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.LocationEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.UserEntry;

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

public class TestProvider extends AndroidTestCase {

    private void deleteAllRecordsThroughProvider() {
        mContext.getContentResolver().delete(ReviewEntry.CONTENT_URI, null, null);
        Cursor cursor = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
        mContext.getContentResolver().delete(UserEntry.CONTENT_URI, null, null);
        cursor = mContext.getContentResolver().query(UserEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from User table during delete", 0, cursor.getCount());
        cursor.close();
        mContext.getContentResolver().delete(BeerEntry.CONTENT_URI, null, null);
        cursor = mContext.getContentResolver().query(BeerEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from Beer table during delete", 0, cursor.getCount());
        cursor.close();
        mContext.getContentResolver().delete(BreweryEntry.CONTENT_URI, null, null);
        cursor = mContext.getContentResolver().query(BreweryEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from Brewery table during delete", 0, cursor.getCount());
        cursor.close();
        mContext.getContentResolver().delete(LocationEntry.CONTENT_URI, null, null);
        cursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    private void deleteAllRecordsThroughDb() {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(ReviewEntry.TABLE_NAME, null, null);
        db.delete(UserEntry.TABLE_NAME, null, null);
        db.delete(BeerEntry.TABLE_NAME, null, null);
        db.delete(BreweryEntry.TABLE_NAME, null, null);
        db.delete(LocationEntry.TABLE_NAME, null, null);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsThroughDb();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // DatabaseProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                DatabaseProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: DatabaseProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + DatabaseContract.CONTENT_AUTHORITY,
                    providerInfo.authority, DatabaseContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: DatabaseProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testInserReadProvider() {

        TestUtils.TestContentObserver tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);

        // location
        Uri insertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI,
                TestUtils.createLocationLeipzig());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor cursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing location after insert", cursor.getCount() == 1);
//        TestUtils.printAllCursorEntries(cursor, " 1 location should be inserted");
        cursor.close();

        // brewery
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(BreweryEntry.CONTENT_URI, true, tco);

        insertUri = mContext.getContentResolver().insert(BreweryEntry.CONTENT_URI,
                TestUtils.createBreweryBayrischerBahnhof());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(BreweryEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing brewery after insert", cursor.getCount() > 0);
//        TestUtils.printAllCursorEntries(cursor, "1 brewery should be inserted");
        cursor.close();

        // beer
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(BeerEntry.CONTENT_URI, true, tco);

        insertUri = mContext.getContentResolver().insert(BeerEntry.CONTENT_URI,
                TestUtils.createBeerGose());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(BeerEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing beer after insert", cursor.getCount() > 0);
//        TestUtils.printAllCursorEntries(cursor, "1 beer should be inserted");
        cursor.close();

        // user
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(UserEntry.CONTENT_URI, true, tco);

        insertUri = mContext.getContentResolver().insert(UserEntry.CONTENT_URI,
                TestUtils.createUserFrank());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(UserEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing user after insert", cursor.getCount() > 0);
//        TestUtils.printAllCursorEntries(cursor, "1 user should be inserted");
        cursor.close();

        // review
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        insertUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI,
                TestUtils.createReview1());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing review after insert", cursor.getCount() > 0);
//        TestUtils.printAllCursorEntries(cursor, "1 review should be inserted");
        cursor.close();

        // bulk insert reviews
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);
        int insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI,
                TestUtils.createBulkReviews(9));
        assertTrue("not enough reviews were inserted", insertCount == 9);
        tco.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI, null, null, null, null);
        assertTrue("missing reviews after bulk insert", cursor.getCount() > 9);
        cursor.close();

    }



}
