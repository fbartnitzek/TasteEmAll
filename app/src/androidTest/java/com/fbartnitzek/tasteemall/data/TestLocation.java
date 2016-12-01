package com.fbartnitzek.tasteemall.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.fbartnitzek.tasteemall.Utils;

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

public class TestLocation extends AndroidTestCase {

    private static final String LOG_TAG = TestLocation.class.getName();

    private final Double[] mainAddress = new Double[]     {49.992236,   8.660902};
    private final Double[] nearbyAddress1 = new Double[]  {49.9922541,  8.6597822};
    private final Double[] nearbyAddress2 = new Double[]  {49.9903763,  8.6589285};
    private final Double[] nearbyAddress3 = new Double[]  {49.990729,   8.660845};
    private final Double[] otherAddress = new Double[]    {49.9934195,  8.6557552};
    private final Double[] leipzigAddress = new Double[]  {51.3257686,  12.3861969};

    private void deleteAllRecordsThroughDb() {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DatabaseContract.ReviewEntry.TABLE_NAME, null, null);
        db.delete(DatabaseContract.UserEntry.TABLE_NAME, null, null);
        db.delete(DatabaseContract.DrinkEntry.TABLE_NAME, null, null);
        db.delete(DatabaseContract.ProducerEntry.TABLE_NAME, null, null);
        db.delete(DatabaseContract.LocationEntry.TABLE_NAME, null, null);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsThroughDb();
    }

    @Override
    protected void tearDown() throws Exception {
        deleteAllRecordsThroughDb();
        super.tearDown();
    }

    public void testLocationQueries() {

        TestUtils.TestContentObserver tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DatabaseContract.LocationEntry.CONTENT_URI, true, tco);

        // main location
        Uri insertUri = mContext.getContentResolver().insert(DatabaseContract.LocationEntry.CONTENT_URI,
                TestUtils.createMainAddress());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor cursor = mContext.getContentResolver().query(DatabaseContract.LocationEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing location after insert", cursor.getCount() == 1);
        cursor.close();

        // location Leipzig
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DatabaseContract.LocationEntry.CONTENT_URI, true, tco);
        insertUri = mContext.getContentResolver().insert(DatabaseContract.LocationEntry.CONTENT_URI,
                TestUtils.createLocationLeipzig());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(DatabaseContract.LocationEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing location after insert", cursor.getCount() == 2);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                DatabaseContract.LocationEntry.buildUriWithLatLng(
                        nearbyAddress1[0], nearbyAddress1[1]),
                null, null, null, null); // sort by distance?
        assertTrue("to many nearby addresses", cursor.getCount() == 1);
        cursor.close();


        cursor = mContext.getContentResolver().query(
                DatabaseContract.LocationEntry.buildUriWithLatLng(
                        otherAddress[0],otherAddress[1]),
                null, null, null, null); // sort by distance?
        assertTrue("should be a new location itself...", cursor.getCount() == 0);
        cursor.close();

    }

    public void testCalcDistance() {
        double d = Utils.calcDistance(mainAddress, nearbyAddress1);
        double md = Utils.calcManhattenDistance(mainAddress, nearbyAddress1);
        assertTrue(d < DatabaseContract.LocationEntry.DISTANCE_SQUARE_THRESHOLD);
        Log.d(LOG_TAG, "distance main - nearby1: " + d + " manhatten: " + md);

        d = Utils.calcDistance(mainAddress, nearbyAddress2);
        md = Utils.calcManhattenDistance(mainAddress, nearbyAddress2);
        assertTrue(d < DatabaseContract.LocationEntry.DISTANCE_SQUARE_THRESHOLD);
        Log.d(LOG_TAG, "distance main - nearby2: "  + d + " manhatten: " + md);

        d = Utils.calcDistance(mainAddress, nearbyAddress3);
        md = Utils.calcManhattenDistance(mainAddress, nearbyAddress3);
        assertTrue(d < DatabaseContract.LocationEntry.DISTANCE_SQUARE_THRESHOLD);
        Log.d(LOG_TAG, "distance main - nearby3: "  + d + " manhatten: " + md);

        d = Utils.calcDistance(mainAddress, otherAddress);
        md = Utils.calcManhattenDistance(mainAddress, otherAddress);
        assertTrue(d > DatabaseContract.LocationEntry.DISTANCE_SQUARE_THRESHOLD);
        Log.d(LOG_TAG, "distance main - otherAddress: "  + d + " manhatten: " + md);

        d = Utils.calcDistance(mainAddress, leipzigAddress);
        md = Utils.calcManhattenDistance(mainAddress, leipzigAddress);
        assertTrue(d > DatabaseContract.LocationEntry.DISTANCE_SQUARE_THRESHOLD);
        Log.d(LOG_TAG, "distance main - leipzigAddress "  + d + " manhatten: " + md);
//        distance main - nearby1: 1.2542796499992039E-6 manhatten: 0.0011017000000013155
//        distance main - nearby2: 7.353186339989622E-6 manhatten: 0.0038331999999972055
//        distance main - nearby3: 2.2742979999897326E-6 manhatten: 0.0015639999999965681
//        distance main - otherAddress: 2.7890222490011392E-5 manhatten: 0.003963299999997005
//        distance main - leipzigAddress 15.656131287228781 manhatten: -5.058827500000005
    }
}
