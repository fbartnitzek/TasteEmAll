package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.DrinkEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

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

public class DatabaseProvider extends ContentProvider {

    private static DatabaseHelper mHelper;
    private static final String LOG_TAG = DatabaseProvider.class.getName();

//    private static final int LOCATIONS = 100;

    private static final int PRODUCERS = 200;

    private static final int DRINKS = 300;
//    private static final int USERS = 400;
    private static final int REVIEWS = 500;

    private final UriMatcher mUriMatcher = buildUriMatcher();
//    private static final SQLiteQueryBuilder sBreweryByNameQueryBuilder;

    static {
//        sBreweryByNameQueryBuilder = new SQLiteQueryBuilder();
//        sBreweryByNameQueryBuilder.setTables(
//                ProducerEntry.TABLE_NAME + " INNER JOIN " +
//                        LocationEntry.TABLE_NAME +
//                        " ON " + ProducerEntry.TABLE_NAME + "." + Producer.LOCATION_ID +
//                        " = " + LocationEntry.TABLE_NAME + "." + Location.LOCATION_ID);
    }

    private static final String sBreweryLocationSelection = //both seem to work
//            ProducerEntry.TABLE_NAME + "." + Producer.NAME + " LIKE ?";
            ProducerEntry.TABLE_NAME + "." + Producer.NAME + " LIKE '%' || ? || '%'";

    private UriMatcher buildUriMatcher() {
        Log.v(LOG_TAG, "buildUriMatcher, " + "");
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        // all locations
//        matcher.addURI(authority, DatabaseContract.PATH_LOCATION, LOCATIONS);

        // all breweries
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER, PRODUCERS);
            // needed for empty string ...
//        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_WITH_LOCATION + "/" , PRODUCERS_WITH_LOCATION_BY_NAME);
//        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_WITH_LOCATION+ "/*", PRODUCERS_WITH_LOCATION_BY_NAME);
        //TODO: all breweries in certain location - even better in area (center, radius)

        // all beers
        matcher.addURI(authority, DatabaseContract.PATH_DRINK, DRINKS);
        // TODO: all beers of certain brewery, of breweries in certain location / area

        // all users
//        matcher.addURI(authority, DatabaseContract.PATH_USER, USERS);
//         TODO: all users in certain area (, with certain reviewed beers)

        // all reviews
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW, REVIEWS);
        // TODO: lots of stuff...

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Log.v(LOG_TAG, "onCreate, " + "");
        mHelper = new DatabaseHelper(getContext());
        return true; // successfully loaded
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(LOG_TAG, "query, " + "uri = [" + uri + "], projection = [" + projection + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "], sortOrder = [" + sortOrder + "]");
        Cursor cursor;
        final SQLiteDatabase db = mHelper.getReadableDatabase();
//        final int match = mUriMatcher.match(uri);
        switch (mUriMatcher.match(uri)) {
            case PRODUCERS:
                Log.v(LOG_TAG, "query - PRODUCERS, " + "uri = [" + uri + "], projection = [" + projection + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "], sortOrder = [" + sortOrder + "]");
                cursor = db.query(ProducerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
//            case PRODUCERS_WITH_LOCATION_BY_NAME:
//                String pattern = ProducerEntry.getSearchString(uri);
//                String[] mySelectionArgs = {pattern + "%"};
//                Log.v(LOG_TAG, "query - PRODUCERS_WITH_LOCATION_BY_NAME, " + pattern + ", uri = [" + uri + "], projection = [" + projection + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "], sortOrder = [" + sortOrder + "]");
//
//                cursor = sBreweryByNameQueryBuilder.query(mHelper.getReadableDatabase(),
//                        projection,
//                        sBreweryLocationSelection,
//                        mySelectionArgs,
//                        null,
//                        null,
//                        sortOrder);
//
//                break;
            case DRINKS:
                cursor = db.query(DrinkEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REVIEWS:
                cursor = db.query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            // TODO: more complicated stuff later...
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.v(LOG_TAG, "getType, " + "uri = [" + uri + "]");
//        final int match = mUriMatcher.match(uri);
        switch (mUriMatcher.match(uri)) {
            case PRODUCERS:
                return ProducerEntry.CONTENT_TYPE;
            case DRINKS:
                return DrinkEntry.CONTENT_TYPE;
            case REVIEWS:
                return ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "insert, " + "uri = [" + uri + "], values = [" + values + "]");
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        Uri returnUri;
        switch (mUriMatcher.match(uri)) {
            case PRODUCERS: {
                long id = db.insert(ProducerEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ProducerEntry.buildUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case DRINKS: {
                long id = db.insert(DrinkEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DrinkEntry.buildUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEWS: {
                long id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ReviewEntry.buildUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.v(LOG_TAG, "bulkInsert, " + "uri = [" + uri + "], values = [" + values + "]");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int returnCount = 0;
        switch (mUriMatcher.match(uri)) {
            case PRODUCERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(ProducerEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case DRINKS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(DrinkEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(ReviewEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, "delete, " + "uri = [" + uri + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int deletedRows;
        if (null == selection) selection = "1";
        switch (mUriMatcher.match(uri)) {
            case PRODUCERS:
                deletedRows = db.delete(ProducerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DRINKS:
                deletedRows = db.delete(DrinkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                deletedRows = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (deletedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, "update, " + "uri = [" + uri + "], values = [" + values + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int impactedRows;
        switch (mUriMatcher.match(uri)) {
            case PRODUCERS:
                impactedRows = db.update(ProducerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DRINKS:
                impactedRows = db.update(DrinkEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEWS:
                impactedRows = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (impactedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return impactedRows;
    }
}
