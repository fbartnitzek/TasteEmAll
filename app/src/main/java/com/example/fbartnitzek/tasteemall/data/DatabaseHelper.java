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
import com.example.fbartnitzek.tasteemall.data.pojo.Beer;
import com.example.fbartnitzek.tasteemall.data.pojo.Brewery;
import com.example.fbartnitzek.tasteemall.data.pojo.Location;
import com.example.fbartnitzek.tasteemall.data.pojo.Review;
import com.example.fbartnitzek.tasteemall.data.pojo.User;

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
                + Location.LOCATION_ID + " TEXT NOT NULL,"
                + Location.LOCALITY + " TEXT NOT NULL,"
                + Location.COUNTRY + " TEXT NOT NULL,"
                + Location.POSTAL_CODE + " TEXT,"
                + Location.STREET + " TEXT,"
                + Location.LONGITUDE + " TEXT,"
                + Location.LATITUDE + " TEXT,"
                + Location.FORMATTED_ADDRESS + " TEXT,"
                //primary key
                + "UNIQUE (" + Location.LOCATION_ID + ") ON CONFLICT REPLACE" //obviously wrong...
                + " );";
        db.execSQL(createLocationTable);

        final String createBreweryTable = "CREATE TABLE " + BreweryEntry.TABLE_NAME + " ("
                + BreweryEntry._ID + " INTEGER PRIMARY KEY,"
                + Brewery.BREWERY_ID + " TEXT NOT NULL,"
                + Brewery.NAME + " TEXT NOT NULL, "
                + Brewery.INTRODUCED + " TEXT,"
                + Brewery.WEBSITE + " TEXT,"
                + Brewery.LOCATION_ID + " TEXT NOT NULL,"

                + "FOREIGN KEY (" + Brewery.LOCATION_ID + ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + Location.LOCATION_ID + "),"

                + "UNIQUE (" + Brewery.BREWERY_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createBreweryTable);

        final String createBeerTable = "CREATE TABLE " + BeerEntry.TABLE_NAME + " ("
                + BeerEntry._ID + " INTEGER PRIMARY KEY,"
                + Beer.BEER_ID + " TEXT NOT NULL,"
                + Beer.NAME + " TEXT NOT NULL,"
                + Beer.ABV + " TEXT,"
                + Beer.DEGREES_PLATO + " TEXT,"
                + Beer.STAMMWUERZE + " TEXT,"
                + Beer.STYLE + " TEXT NOT NULL,"
                + Beer.IBU + " TEXT,"
                + Beer.BREWERY_ID + " TEXT NOT NULL,"

                + "FOREIGN KEY (" + Beer.BREWERY_ID + ") REFERENCES "
                + BreweryEntry.TABLE_NAME + " (" + Brewery.BREWERY_ID + "),"

                + "UNIQUE (" + Beer.BEER_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createBeerTable);

        final String createUserTable = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
                + UserEntry._ID + " INTEGER PRIMARY KEY,"
                + User.USER_ID + " TEXT NOT NULL,"
                + User.LOGIN + " TEXT NOT NULL,"
                + User.NAME + " TEXT,"
                + User.EMAIL + " TEXT NOT NULL,"
                + User.HOME_LOCATION_ID + " TEXT,"

                + "FOREIGN KEY (" + User.HOME_LOCATION_ID + ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + Location.LOCATION_ID + "),"

                + "UNIQUE (" + User.USER_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createUserTable);

        final String createReviewTable = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ("
                + ReviewEntry._ID + " INTEGER PRIMARY KEY,"
                + Review.REVIEW_ID + " TEXT NOT NULL,"
                + Review.RATING + " TEXT NOT NULL,"
                + Review.DESCRIPTION + " TEXT,"
                + Review.LOOK + " TEXT,"
                + Review.SMELL + " TEXT,"
                + Review.TASTE + " TEXT,"
                + Review.TIMESTAMP + " TEXT NOT NULL,"
                + Review.USER_ID + " TEXT NOT NULL,"
                + Review.BEER_ID + " TEXT NOT NULL,"
                + Review.LOCATION_ID + " TEXT,"

                + "FOREIGN KEY (" + Review.USER_ID + ") REFERENCES "
                + UserEntry.TABLE_NAME + " (" + User.USER_ID + "),"
                + "FOREIGN KEY (" + Review.BEER_ID + ") REFERENCES "
                + BeerEntry.TABLE_NAME + " (" + Beer.BEER_ID + "),"
                + "FOREIGN KEY (" + Review.LOCATION_ID + ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + Location.LOCATION_ID + "),"

                + "UNIQUE (" + Review.REVIEW_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createReviewTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("db upgrade not yet supported...");
    }

    public static ContentValues buildLocationValues(String locationId, String locality,
                                                    String country, String plz, String street,
                                                    String longitude, String latitude,
                                                    String formattedAddress) {
        ContentValues cv = new ContentValues();
        cv.put(Location.LOCATION_ID, locationId);
        cv.put(Location.LOCALITY, locality);
        cv.put(Location.COUNTRY, country);
        cv.put(Location.POSTAL_CODE, plz);
        cv.put(Location.STREET, street);
        cv.put(Location.LONGITUDE, longitude);
        cv.put(Location.LATITUDE, latitude);
        cv.put(Location.FORMATTED_ADDRESS, formattedAddress);
        return cv;
    }

    public static ContentValues buildBreweryValues(String breweryId, String name,
                                                   String introduced, String website,
                                                   String locationId) {
        ContentValues cv = new ContentValues();
        cv.put(Brewery.BREWERY_ID, breweryId);
        cv.put(Brewery.NAME, name);
        cv.put(Brewery.INTRODUCED, introduced);
        cv.put(Brewery.WEBSITE, website);
        cv.put(Brewery.LOCATION_ID, locationId);
        return cv;
    }

    public static ContentValues buildBeerValues(String beerId, String name, String abv,
                                                String degreesPlato, String stammwuerze,
                                                String style, String ibu,
                                                String breweryId) {
        ContentValues cv = new ContentValues();
        cv.put(Beer.BEER_ID, beerId);
        cv.put(Beer.NAME, name);
        cv.put(Beer.ABV, abv);
        cv.put(Beer.DEGREES_PLATO, degreesPlato);
        cv.put(Beer.STAMMWUERZE, stammwuerze);
        cv.put(Beer.STYLE, style);
        cv.put(Beer.IBU, ibu);
        cv.put(Beer.BREWERY_ID, breweryId);
        return cv;
    }

    public static ContentValues buildUserValues(String userId, String login, String name,
                                                String email, String homeLocationId) {
        ContentValues cv = new ContentValues();
        cv.put(User.USER_ID, userId);
        cv.put(User.LOGIN, login);
        cv.put(User.NAME, name);
        cv.put(User.EMAIL, email);
        cv.put(User.HOME_LOCATION_ID, homeLocationId);
        return cv;
    }

    public static ContentValues buildReviewValues(String reviewId, String rating, String description,
                                                  String look, String smell, String taste,
                                                  String timestamp, String userId, String beerId,
                                                  String locationId) {
        ContentValues cv = new ContentValues();
        cv.put(Review.REVIEW_ID, reviewId);
        cv.put(Review.RATING, rating);
        cv.put(Review.DESCRIPTION, description);
        cv.put(Review.LOOK, look);
        cv.put(Review.SMELL, smell);
        cv.put(Review.TASTE, taste);
        cv.put(Review.TIMESTAMP, timestamp);
        cv.put(Review.USER_ID, userId);
        cv.put(Review.BEER_ID, beerId);
        cv.put(Review.LOCATION_ID, locationId);
        return cv;
    }
}
