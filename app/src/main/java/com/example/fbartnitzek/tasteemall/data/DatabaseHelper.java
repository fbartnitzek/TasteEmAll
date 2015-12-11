package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                + LocationEntry.LOCATION_ID + " TEXT NOT NULL,"
                + LocationEntry.NAME + " TEXT NOT NULL,"
                + LocationEntry.COUNTRY + " TEXT NOT NULL,"
                + LocationEntry.POSTAL_CODE + " TEXT,"
                + LocationEntry.LOCATION_LONGITUDE + " TEXT,"
                + LocationEntry.LOCATION_LATITUDE + " TEXT, "
                //primary key
                + "UNIQUE (" + LocationEntry.LOCATION_ID + ") ON CONFLICT REPLACE" //obviously wrong...
                + " );";
        db.execSQL(createLocationTable);

        final String createBreweryTable = "CREATE TABLE " + BreweryEntry.TABLE_NAME + " ("
                + BreweryEntry._ID + " INTEGER PRIMARY KEY,"
                + BreweryEntry.BREWERY_ID + " TEXT NOT NULL,"
                + BreweryEntry.NAME + " TEXT NOT NULL, "
                + BreweryEntry.INTRODUCED + " TEXT,"
                + BreweryEntry.WEBSITE + " TEXT,"
                + BreweryEntry.LOCATION_ID + " TEXT NOT NULL,"

                + "FOREIGN KEY (" + BreweryEntry.LOCATION_ID + ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + LocationEntry.LOCATION_ID + "),"

                + "UNIQUE (" + BreweryEntry.BREWERY_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createBreweryTable);

        final String createBeerTable = "CREATE TABLE " + BeerEntry.TABLE_NAME + " ("
                + BeerEntry._ID + " INTEGER PRIMARY KEY,"
                + BeerEntry.BEER_ID + " TEXT NOT NULL,"
                + BeerEntry.NAME + " TEXT NOT NULL,"
                + BeerEntry.ABV + " TEXT,"
                + BeerEntry.DEGREES_PLATO + " TEXT,"
                + BeerEntry.STAMMWUERZE+ " TEXT,"
                + BeerEntry.STYLE + " TEXT NOT NULL,"
                + BeerEntry.IBU + " TEXT,"
                + BeerEntry.BREWERY_ID + " TEXT NOT NULL,"

                + "FOREIGN KEY (" + BeerEntry.BREWERY_ID + ") REFERENCES "
                + BreweryEntry.TABLE_NAME + " (" + BreweryEntry.BREWERY_ID + "),"

                + "UNIQUE (" + BeerEntry.BEER_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createBeerTable);

        final String createUserTable = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
                + UserEntry._ID + " INTEGER PRIMARY KEY,"
                + UserEntry.USER_ID + " TEXT NOT NULL,"
                + UserEntry.LOGIN + " TEXT NOT NULL,"
                + UserEntry.NAME + " TEXT,"
                + UserEntry.EMAIL + " TEXT NOT NULL,"
                + UserEntry.HOME_LOCATION_ID + " TEXT,"

                + "FOREIGN KEY (" + UserEntry.HOME_LOCATION_ID + ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + LocationEntry.LOCATION_ID + "),"

                + "UNIQUE (" + UserEntry.USER_ID+ ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createUserTable);

        final String createReviewTable = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ("
                + ReviewEntry._ID + " INTEGER PRIMARY KEY,"
                + ReviewEntry.REVIEW_ID + " TEXT NOT NULL,"
                + ReviewEntry.RATING + " TEXT NOT NULL,"
                + ReviewEntry.DESCRIPTION + " TEXT,"
                + ReviewEntry.LOOK + " TEXT,"
                + ReviewEntry.SMELL + " TEXT,"
                + ReviewEntry.TASTE + " TEXT,"
                + ReviewEntry.TIMESTAMP + " TEXT NOT NULL,"
                + ReviewEntry.USER_ID + " TEXT NOT NULL,"
                + ReviewEntry.BEER_ID + " TEXT NOT NULL,"
                + ReviewEntry.LOCATION_ID + " TEXT,"

                + "FOREIGN KEY (" + ReviewEntry.USER_ID+ ") REFERENCES "
                + UserEntry.TABLE_NAME + " (" + UserEntry.USER_ID + "),"
                + "FOREIGN KEY (" + ReviewEntry.BEER_ID+ ") REFERENCES "
                + BeerEntry.TABLE_NAME + " (" + BeerEntry.BEER_ID+ "),"
                + "FOREIGN KEY (" + ReviewEntry.LOCATION_ID+ ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + LocationEntry.LOCATION_ID+ "),"

                + "UNIQUE (" + ReviewEntry.REVIEW_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createReviewTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("db upgrade not yet supported...");
    }

    public static ContentValues buildLocationValues(String locationId, String name,
                                                    String country, String plz,
                                                    String longitude, String latitude) {
        ContentValues cv = new ContentValues();
        cv.put(LocationEntry.LOCATION_ID, locationId);
        cv.put(LocationEntry.NAME, name);
        cv.put(LocationEntry.COUNTRY, country);
        cv.put(LocationEntry.POSTAL_CODE, plz);
        cv.put(LocationEntry.LOCATION_LONGITUDE, longitude);
        cv.put(LocationEntry.LOCATION_LATITUDE, latitude);
        return cv;
    }

    public static ContentValues buildBreweryValues(String breweryId, String name,
                                                   String introduced, String website,
                                                   String locationId) {
        ContentValues cv = new ContentValues();
        cv.put(BreweryEntry.BREWERY_ID, breweryId);
        cv.put(BreweryEntry.NAME, name);
        cv.put(BreweryEntry.INTRODUCED, introduced);
        cv.put(BreweryEntry.WEBSITE, website);
        cv.put(BreweryEntry.LOCATION_ID, locationId);
        return cv;
    }

    public static ContentValues buildBeerValues(String beerId, String name, String abv,
                                                String degreesPlato, String stammwuerze,
                                                String style, String ibu,
                                                String breweryId) {
        ContentValues cv = new ContentValues();
        cv.put(BeerEntry.BEER_ID, beerId);
        cv.put(BeerEntry.NAME, name);
        cv.put(BeerEntry.ABV, abv);
        cv.put(BeerEntry.DEGREES_PLATO, degreesPlato);
        cv.put(BeerEntry.STAMMWUERZE, stammwuerze);
        cv.put(BeerEntry.STYLE, style);
        cv.put(BeerEntry.IBU, ibu);
        cv.put(BeerEntry.BREWERY_ID, breweryId);
        return cv;
    }

    public static ContentValues buildUserValues(String userId, String login, String name,
                                                String email, String homeLocationId) {
        ContentValues cv = new ContentValues();
        cv.put(UserEntry.USER_ID, userId);
        cv.put(UserEntry.LOGIN, login);
        cv.put(UserEntry.NAME, name);
        cv.put(UserEntry.EMAIL, email);
        cv.put(UserEntry.HOME_LOCATION_ID, homeLocationId);
        return cv;
    }

    public static ContentValues buildReviewValues(String reviewId, String rating, String description,
                                                  String look, String smell, String taste,
                                                 String timestamp, String userId, String beerId,
                                                 String locationId) {
        ContentValues cv = new ContentValues();
        cv.put(ReviewEntry.REVIEW_ID, reviewId);
        cv.put(ReviewEntry.RATING, rating);
        cv.put(ReviewEntry.DESCRIPTION, description);
        cv.put(ReviewEntry.LOOK, look);
        cv.put(ReviewEntry.SMELL, smell);
        cv.put(ReviewEntry.TASTE, taste);
        cv.put(ReviewEntry.TIMESTAMP, timestamp);
        cv.put(ReviewEntry.USER_ID, userId);
        cv.put(ReviewEntry.BEER_ID, beerId);
        cv.put(ReviewEntry.LOCATION_ID, locationId);
        return cv;
    }
}
