package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.DrinkEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;
import com.example.fbartnitzek.tasteemall.data.pojo.Review;

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

//        final String createLocationTable = "CREATE TABLE " + LocationEntry.TABLE_NAME + " ("
//                + LocationEntry._ID + " INTEGER PRIMARY KEY,"
//                + Location.LOCATION_ID + " TEXT NOT NULL,"
//                + Location.LOCALITY + " TEXT NOT NULL,"
//                + Location.COUNTRY + " TEXT NOT NULL,"
//                + Location.POSTAL_CODE + " TEXT,"
//                + Location.STREET + " TEXT,"
//                + Location.LONGITUDE + " TEXT,"
//                + Location.LATITUDE + " TEXT,"
//                + Location.FORMATTED_ADDRESS + " TEXT,"
//                //primary key
//                + "UNIQUE (" + Location.LOCATION_ID + ") ON CONFLICT REPLACE" //obviously wrong...
//                + " );";
//        db.execSQL(createLocationTable);

        final String createProducerTable = "CREATE TABLE " + DatabaseContract.ProducerEntry.TABLE_NAME + " ("
                + DatabaseContract.ProducerEntry._ID + " INTEGER PRIMARY KEY,"
                + Producer.PRODUCER_ID + " TEXT NOT NULL,"
                + Producer.NAME + " TEXT NOT NULL, "
                + Producer.DESCRIPTION + " TEXT,"
                + Producer.WEBSITE + " TEXT,"
                + Producer.LOCATION + " TEXT NOT NULL,"

                + "UNIQUE (" + Producer.PRODUCER_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createProducerTable);

        final String createBeerTable = "CREATE TABLE " + DrinkEntry.TABLE_NAME + " ("
                + DrinkEntry._ID + " INTEGER PRIMARY KEY,"
                + Drink.DRINK_ID + " TEXT NOT NULL,"
                + Drink.NAME + " TEXT NOT NULL,"
                + Drink.SPECIFICS + " TEXT,"
                + Drink.STYLE + " TEXT,"
                + Drink.TYPE + " TEXT NOT NULL,"
                + Drink.PRODUCER_ID + " TEXT NOT NULL,"

                + "FOREIGN KEY (" + Drink.PRODUCER_ID + ") REFERENCES "
                + ProducerEntry.TABLE_NAME + " (" + Producer.PRODUCER_ID + "),"

                + "UNIQUE (" + Drink.DRINK_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createBeerTable);

//        final String createUserTable = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
//                + UserEntry._ID + " INTEGER PRIMARY KEY,"
//                + User.USER_ID + " TEXT NOT NULL,"
//                + User.LOGIN + " TEXT NOT NULL,"
//                + User.NAME + " TEXT,"
//                + User.EMAIL + " TEXT NOT NULL,"
//                + User.HOME_LOCATION_ID + " TEXT,"
//
//                + "FOREIGN KEY (" + User.HOME_LOCATION_ID + ") REFERENCES "
//                + LocationEntry.TABLE_NAME + " (" + Location.LOCATION_ID + "),"
//
//                + "UNIQUE (" + User.USER_ID + ") ON CONFLICT REPLACE"
//                + " );";
//        db.execSQL(createUserTable);

        final String createReviewTable = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ("
                + ReviewEntry._ID + " INTEGER PRIMARY KEY,"
                + Review.REVIEW_ID + " TEXT NOT NULL,"
                + Review.RATING + " TEXT NOT NULL,"
                + Review.DESCRIPTION + " TEXT,"
                + Review.DATE + " TEXT NOT NULL,"

                + Review.DRINK_ID + " TEXT NOT NULL,"
                + Review.LOCATION + " TEXT,"
                // TODO: USER!

                + "FOREIGN KEY (" + Review.DRINK_ID + ") REFERENCES "
                + DrinkEntry.TABLE_NAME + " (" + Drink.DRINK_ID + "),"

                + "UNIQUE (" + Review.REVIEW_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createReviewTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("db upgrade not yet supported...");
    }

//    public static ContentValues buildLocationValues(String locationId, String locality,
//                                                    String country, String plz, String street,
//                                                    String longitude, String latitude,
//                                                    String formattedAddress) {
//        ContentValues cv = new ContentValues();
//        cv.put(Location.LOCATION_ID, locationId);
//        cv.put(Location.LOCALITY, locality);
//        cv.put(Location.COUNTRY, country);
//        cv.put(Location.POSTAL_CODE, plz);
//        cv.put(Location.STREET, street);
//        cv.put(Location.LONGITUDE, longitude);
//        cv.put(Location.LATITUDE, latitude);
//        cv.put(Location.FORMATTED_ADDRESS, formattedAddress);
//        return cv;
//    }

    public static ContentValues buildProducerValues(String producerId, String name,
                                                    String description, String website,
                                                    String location) {
        ContentValues cv = new ContentValues();
        cv.put(Producer.PRODUCER_ID, producerId);
        cv.put(Producer.NAME, name);
        cv.put(Producer.DESCRIPTION, description);
        cv.put(Producer.WEBSITE, website);
        cv.put(Producer.LOCATION, location);
        return cv;
    }

    public static ContentValues buildDrinkValues(String drinkId, String name, String specifics,
                                                String style, String type,
                                                String producerId) {
        ContentValues cv = new ContentValues();
        cv.put(Drink.DRINK_ID, drinkId);
        cv.put(Drink.NAME, name);
        cv.put(Drink.SPECIFICS, specifics);
        cv.put(Drink.STYLE, style);
        cv.put(Drink.TYPE, type);
        cv.put(Drink.PRODUCER_ID, producerId);
        return cv;
    }

//    public static ContentValues buildUserValues(String userId, String login, String name,
//                                                String email, String homeLocationId) {
//        ContentValues cv = new ContentValues();
//        cv.put(User.USER_ID, userId);
//        cv.put(User.LOGIN, login);
//        cv.put(User.NAME, name);
//        cv.put(User.EMAIL, email);
//        cv.put(User.HOME_LOCATION_ID, homeLocationId);
//        return cv;
//    }

    public static ContentValues buildReviewValues(String reviewId, String rating, String description,
                                                  String date, String drinkId,
                                                  String location) {
        ContentValues cv = new ContentValues();
        cv.put(Review.REVIEW_ID, reviewId);
        cv.put(Review.RATING, rating);
        cv.put(Review.DESCRIPTION, description);
        cv.put(Review.DATE, date);
        cv.put(Review.DRINK_ID, drinkId);
        cv.put(Review.LOCATION, location);
        return cv;
    }
}
