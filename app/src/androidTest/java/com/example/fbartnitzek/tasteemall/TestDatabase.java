package com.example.fbartnitzek.tasteemall;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
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

public class TestDatabase extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        deleteDb();
    }

    private void deleteDb() {
        mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    public void testLocationTable() {

        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //create content
        ContentValues locationValues = TestUtils.createLocationValues();

        // Insert ContentValues into database and get a row ID back
        long insertedRows = db.insert(DatabaseContract.LocationEntry.TABLE_NAME, null,
                locationValues);
        assertTrue(insertedRows > 0);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                DatabaseContract.LocationEntry.TABLE_NAME,
                null,   //select
                null,   //where keys
                null,   //where values
                null,   //group by
                null,   //having
                null);  //order by

        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        assertTrue("wrong entry count...? ", cursor.getCount() == 1);

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }
}
