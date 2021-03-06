package com.fbartnitzek.tasteemall.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.fbartnitzek.tasteemall.data.DatabaseContract.DrinkEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.LocationEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.UserEntry;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Review;

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

@SuppressWarnings("ALL")
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
        mContext.getContentResolver().delete(DrinkEntry.CONTENT_URI, null, null);
        cursor = mContext.getContentResolver().query(DrinkEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from Drink table during delete", 0, cursor.getCount());
        cursor.close();
        mContext.getContentResolver().delete(ProducerEntry.CONTENT_URI, null, null);
        cursor = mContext.getContentResolver().query(ProducerEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals("Error: Records not deleted from Producer table during delete", 0, cursor.getCount());
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
        db.delete(DrinkEntry.TABLE_NAME, null, null);
        db.delete(ProducerEntry.TABLE_NAME, null, null);
        db.delete(LocationEntry.TABLE_NAME, null, null);
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

    public void testInsertReadProvider() {

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
        cursor.close();


        // producer
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProducerEntry.CONTENT_URI, true, tco);

        insertUri = mContext.getContentResolver().insert(ProducerEntry.CONTENT_URI,
                TestUtils.createBreweryBayrischerBahnhof());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(ProducerEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing brewery after insert", cursor.getCount() > 0);
        cursor.close();

        // drink
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DrinkEntry.CONTENT_URI, true, tco);

        Uri uriGose = mContext.getContentResolver().insert(DrinkEntry.CONTENT_URI,
                TestUtils.createBeerGose());
        assertTrue(uriGose != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(DrinkEntry.CONTENT_URI,
                null, null, null, null);

        assertTrue("missing beer after insert", cursor.getCount() > 0);
        cursor.close();


        // joined drink + producer
        cursor = mContext.getContentResolver().query(DrinkEntry.buildUriWithName(""),
                null, null, null, null);
        assertTrue("joined drink query failed", cursor.getCount() > 0
                && cursor.getColumnCount() == TestUtils.createBeerGose().size()
                + TestUtils.createBreweryBayrischerBahnhof().size() + 2);   //all attributes + id each
        cursor.close();

        // other beer
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DrinkEntry.CONTENT_URI, true, tco);
        insertUri = mContext.getContentResolver().insert(DrinkEntry.CONTENT_URI,
                TestUtils.createBeerSchwarzbier());
        assertTrue(insertUri != null);
        mContext.getContentResolver().unregisterContentObserver(tco);
        cursor = mContext.getContentResolver().query(insertUri, null, null, null, null);
        assertTrue("missing beer after second insert - by id", cursor.getCount() == 1);
        cursor.close();


        // and location, distillery and whisky
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);
        insertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI,
                TestUtils.createLocationIslay());
        assertTrue("location islay not inserted", insertUri != null);
        mContext.getContentResolver().unregisterContentObserver(tco);
        cursor = mContext.getContentResolver().query(insertUri, null, null, null, null);
        assertTrue("query of location failed...", cursor.getCount() == 1);
        cursor.close();

        // distillery
        Uri uriLaphroaig = mContext.getContentResolver().insert(ProducerEntry.CONTENT_URI,
                TestUtils.createDistilleryLaphroaig());
        assertTrue(uriLaphroaig != null);

        cursor = mContext.getContentResolver().query(ProducerEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing distillery after second insert", cursor.getCount() > 1);
        cursor.close();

        // whisky
        insertUri = mContext.getContentResolver().insert(DrinkEntry.CONTENT_URI,
                TestUtils.createWhiskyLaphroaig());
        assertTrue(insertUri != null);

        cursor = mContext.getContentResolver().query(DrinkEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue("missing whisky after third insert", cursor.getCount() > 2);
        cursor.close();

        // query with type...
        cursor = mContext.getContentResolver().query(DrinkEntry.buildUriWithNameAndType("", "beer"),
                null, null, null, null);
        assertTrue("query with type failed", cursor.getCount() > 0);
        cursor.close();

//        // validate location of producer
//        cursor = mContext.getContentResolver().query(uriLaphroaig, QueryColumns.ProducerFragment.ShowQuery.COLUMNS, null, null, null);
//        assertTrue("query of producer failed...", cursor.getCount() == 1);
//        cursor.moveToFirst();
////        int location_Id = cursor.getInt(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_LOCATION__ID);
////        assertTrue("no location_id found for producer", location_Id > 0);
//        cursor.close();

//        cursor = mContext.getContentResolver().query(DatabaseContract.LocationEntry.buildUri(location_Id),
//                null, null, null, null);
//        assertTrue("query for location of producer by location_id failed", cursor.getCount() == 1);
//        cursor.close();

        // update distillery
        int rows = mContext.getContentResolver().update(uriLaphroaig,
                TestUtils.updateDistilleryLaphroaig(), null, null);
        assertTrue("update of distillery failed", rows == 1);
        cursor = mContext.getContentResolver().query(uriLaphroaig,
                QueryColumns.ProducerFragment.ShowQuery.COLUMNS, null, null, null);
        assertTrue("query after update of distillery failed", cursor.getCount() == 1);
        cursor.moveToFirst();
        String prodDescription = cursor.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_DESCRIPTION);
        assertTrue("update of distillery-description failed", TestUtils.NEW_PRODUCER_LAPHROAIG_DESCRIPTION.equals(prodDescription));
        cursor.close();

        // update beer
        rows = mContext.getContentResolver().update(uriGose,
                TestUtils.updateBeerGose(), null, null);
        assertTrue("update of beer failed", rows == 1);
        final String[] DETAIL_COLUMNS = {
                DatabaseContract.DrinkEntry.TABLE_NAME + "." + DatabaseContract.DrinkEntry._ID,
                Drink.NAME,
                Drink.DRINK_ID,
                Drink.INGREDIENTS};
        cursor = mContext.getContentResolver().query(uriGose,
                DETAIL_COLUMNS, null, null, null);
        assertTrue("query after update of distillery failed", cursor.getCount() == 1);
        cursor.moveToFirst();
        String drinkIngredients = cursor.getString(3);
        assertTrue("update of beer-ingredients failed",
                TestUtils.NEW_BEER_GOSE_INGREDIENTS.equals(drinkIngredients));
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
        assertTrue("missing review after insert", cursor.getCount() == 1);
        cursor.close();

        //query review with all...
        cursor = mContext.getContentResolver().query(ReviewEntry.buildUriForShowReview(1),
                null, null, null, null);
        assertTrue("joined query on review did not work", cursor.getCount() > 0);
        cursor.close();

        // complicated joined review-query
        cursor = mContext.getContentResolver().query(
                ReviewEntry.buildUriForShowReviewWithPatternAndType("z", Drink.TYPE_ALL),
                null, null, null, null);
        assertTrue("joined query on review ALL containing z", cursor.getCount() == 0);
        cursor.close();
        cursor = mContext.getContentResolver().query(
                ReviewEntry.buildUriForShowReviewWithPatternAndType("se", "beer"),
                null, null, null, null);
        assertTrue("joined query on review Beer containing se", cursor.getCount() == 1);
        cursor.close();
        cursor = mContext.getContentResolver().query(
                ReviewEntry.buildUriForShowReviewWithPatternAndType("se", "wine"),
                null, null, null, null);
        assertTrue("joined query on review Wine containing se", cursor.getCount() == 0);
        cursor.close();
        cursor = mContext.getContentResolver().query(
                ReviewEntry.buildUriForShowReviewWithPatternAndType("hof", "wine"),
                null, null, null, null);
        assertTrue("joined query on review Wine containing hof", cursor.getCount() == 0);
        cursor.close();
        cursor = mContext.getContentResolver().query(
                ReviewEntry.buildUriForShowReviewWithPatternAndType("hof", "beer"),
                null, null, null, null);
        assertTrue("joined query on review Beer containing hof", cursor.getCount() > 0);
        cursor.close();

        // bulk insert reviews
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(UserEntry.CONTENT_URI, true, tco);
        int insertCount = mContext.getContentResolver().bulkInsert(UserEntry.CONTENT_URI,
                TestUtils.createBulkUsers(9));
        assertTrue("not enough users were inserted", insertCount == 9);
        tco.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(UserEntry.CONTENT_URI, null, null, null, null);
        assertTrue("missing users after bulk insert", cursor.getCount() == 10);


        // bulk insert users
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);
        insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI,
                TestUtils.createBulkReviews(9));
        assertTrue("not enough reviews were inserted", insertCount == 9);
        tco.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI, null, null, null, null);
        assertTrue("missing reviews after bulk insert", cursor.getCount() > 9);
        cursor.close();

        // insert review without location
        tco = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        insertUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI,
                TestUtils.createReview2());
        assertTrue(insertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        //query review with all...
        cursor = mContext.getContentResolver().query(insertUri, null, null, null, null);
        assertTrue("joined query on review did not work", cursor.getCount() == 1);
        cursor.moveToFirst();
        String noLocation = cursor.getString(cursor.getColumnIndex(Review.LOCATION_ID));
        assertTrue("location not null: " + noLocation, noLocation == null);
        cursor.close();
    }

}
