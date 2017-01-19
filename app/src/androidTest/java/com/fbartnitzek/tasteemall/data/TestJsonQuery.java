package com.fbartnitzek.tasteemall.data;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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


public class TestJsonQuery extends AndroidTestCase {

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

    public void testJsonQueries() {
        // TODO: some inserts for all entries (at least 2 per class)


        // TODO: provider: json instead of reviews_json => first json-object is review

        // TODO: easier and complex json queries, to check that provider "always" works correctly
    }
}
