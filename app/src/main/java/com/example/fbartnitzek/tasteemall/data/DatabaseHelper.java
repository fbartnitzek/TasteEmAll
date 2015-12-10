package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.LocationEntry;

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

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TasteEmAll.db";
    private static final int DATABASE_VERSION = 1;

    private static final String LOG_TAG = DatabaseHelper.class.getName();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v(LOG_TAG, "DatabaseHelper, " + "context = [" + context + "]");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(LOG_TAG, "onCreate, " + "db = [" + db + "]");

        final String createLocationTable = "CREATE TABLE " + LocationEntry.TABLE_NAME + " ("
                + LocationEntry._ID + " INTEGER PRIMARY KEY,"
                + LocationEntry.LOCATION_ID_COL + " TEXT NOT NULL,"
                + LocationEntry.NAME_COL + " TEXT NOT NULL,"
                + LocationEntry.COUNTRY_COL + " TEXT NOT NULL,"
                + LocationEntry.POSTAL_CODE + " TEXT,"
                + LocationEntry.LOCATION_LONGITUDE + " TEXT,"
                + LocationEntry.LOCATION_LATITUDE + " TEXT, "
                // FOREIGN KEYs
                + "UNIQUE (" + LocationEntry.LOCATION_ID_COL + ") ON CONFLICT REPLACE" //obviously wrong...
                + " );";

        db.execSQL(createLocationTable);

        // TODO brweries
//        final String createBreweryTable =

        // TODO Beers

        // TODO Users

        // TODO Reviews
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("db upgrade not yet supported...");
    }

    public static ContentValues buildLocationValues(String locationId, String name,
                                                    String country, String plz,
                                                    String longitude, String latitude) {
        ContentValues cv = new ContentValues();
        cv.put(LocationEntry.LOCATION_ID_COL, locationId);
        cv.put(LocationEntry.NAME_COL, name);
        cv.put(LocationEntry.COUNTRY_COL, country);
        cv.put(LocationEntry.POSTAL_CODE, plz);
        cv.put(LocationEntry.LOCATION_LONGITUDE, longitude);
        cv.put(LocationEntry.LOCATION_LATITUDE, latitude);
        return cv;
    }
}
