package com.fbartnitzek.tasteemall.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract.DrinkEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.LocationEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.UserEntry;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.data.pojo.User;

import java.util.Arrays;

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

    private static final int LOCATIONS = 100;
    private static final int LOCATION_BY_ID = 102;

    private static final int PRODUCERS = 200;
    private static final int PRODUCERS_BY_NAME = 201;
    private static final int PRODUCER_BY_ID = 202;
    private static final int PRODUCERS_BY_PATTERN = 203;

    private static final int DRINKS = 300;
    private static final int DRINKS_BY_NAME = 301;
    private static final int DRINK_BY_ID = 302;
    private static final int DRINKS_WITH_PRODUCER_BY_NAME = 310;
    private static final int DRINKS_WITH_PRODUCER_BY_ID = 311;
    private static final int DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE = 320;

    private static final int USERS = 400;
    private static final int USERS_BY_NAME = 401;
    private static final int USER_BY_ID = 402;

    private static final int REVIEWS = 500;
    private static final int REVIEW_BY_ID = 503;
    private static final int REVIEW_WITH_ALL_BY_ID = 510;
    private static final int REVIEWS_WITH_ALL_BY_NAME_AND_TYPE = 520;
    private static final int REVIEWS_GEOCODE = 530;

    private final UriMatcher mUriMatcher = buildUriMatcher();

    private static final String RA = ReviewEntry.ALIAS;   //ReviewAlias
    private static final String UA = UserEntry.ALIAS;   //UserAlias
    private static final String DA = DrinkEntry.ALIAS;   //DrinkAlias
    private static final String PA = ProducerEntry.ALIAS;   //ProducerAlias
    private static final String LAP = LocationEntry.ALIAS_PRODUCER;   //LocationAlias
    private static final String LAR = LocationEntry.ALIAS_REVIEW;   //LocationAlias

    private static final String PRODUCERS_BY_NAME_SELECTION = //both seem to work
//            ProducerEntry.TABLE_NAME + "." + Producer.NAME + " LIKE ?";
            ProducerEntry.TABLE_NAME + "." + Producer.NAME + " LIKE '%' || ? || '%'";

    // TODO: adapt
    private static final String PRODUCERS_BY_NAME_OR_LOCATION_SELECTION =
                ProducerEntry.TABLE_NAME + "." + Producer.NAME + " LIKE ? OR "
                    + ProducerEntry.TABLE_NAME + "." + Producer.LOCATION_ID + " LIKE ?";

    private static final String DRINKS_OR_PRODUCERS_BY_NAME_SELECTION =
            DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?";

    private static final String REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION =
            DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?";

//    private static final String DRINKS_BY_NAME_AND_TYPE_SELECTION =
//            DrinkEntry.TABLE_NAME + "." + Drink.NAME + " LIKE ? AND "
//                    + DrinkEntry.TABLE_NAME + "." + Drink.TYPE + " = ?";

    private static final String DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION =
            "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?)" +
                    " AND " + DA + "." + Drink.TYPE + " = ?";

    private static final String REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION =
            "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?)" +
                    " AND " + DA + "." + Drink.TYPE + " = ?";

//    private static final String PRODUCER_BY_ID_SELECTION =
//            ProducerEntry.TABLE_NAME + "." + ProducerEntry._ID + " = ?";

    private UriMatcher buildUriMatcher() {
        Log.v(LOG_TAG, "buildUriMatcher, " + "");
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        // all locations
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION, LOCATIONS);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION + "/#", LOCATION_BY_ID);

        // all producers
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER, PRODUCERS);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER + "/#", PRODUCER_BY_ID);
        // needed for empty string ...
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_BY_NAME + "/", PRODUCERS_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_BY_NAME + "/*", PRODUCERS_BY_NAME);
        // producer.name and producer.location
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_BY_PATTERN + "/", PRODUCERS_BY_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_BY_PATTERN + "/*", PRODUCERS_BY_PATTERN);
        //TODO: all breweries in certain location - even better in area (center, radius)

        // all drinks
        matcher.addURI(authority, DatabaseContract.PATH_DRINK, DRINKS);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_BY_NAME + "/", DRINKS_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_BY_NAME + "/*", DRINKS_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME + "/", DRINKS_WITH_PRODUCER_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME + "/*", DRINKS_WITH_PRODUCER_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE + "/*/", DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE + "/*/*", DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK + "/#", DRINK_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER + "/#", DRINKS_WITH_PRODUCER_BY_ID);

        // TODO: all beers of certain brewery, of breweries in certain location / area

        // all users
        matcher.addURI(authority, DatabaseContract.PATH_USER, USERS);
        matcher.addURI(authority, DatabaseContract.PATH_USER + "/#", USER_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_USER_BY_NAME + "/", USERS_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_USER_BY_NAME + "/*", USERS_BY_NAME);


//         TODO: all users in certain area (, with certain reviewed beers)

        // all reviews
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW, REVIEWS);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW + "/#", REVIEW_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_WITH_ALL + "/#", REVIEW_WITH_ALL_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_WITH_ALL_BY_NAME_AND_TYPE + "/*/", REVIEWS_WITH_ALL_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_WITH_ALL_BY_NAME_AND_TYPE + "/*/*", REVIEWS_WITH_ALL_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_GEOCODE_LOCATION, REVIEWS_GEOCODE);

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
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(LOG_TAG, "query, " + "uri = [" + uri + "], projection = [" + Arrays.toString(projection) + "], selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "], sortOrder = [" + sortOrder + "]");
        Cursor cursor;
        final SQLiteDatabase db = mHelper.getReadableDatabase();

        String[] mySelectionArgs;
        String pattern;
        String drinkType;

        // TODO: optimize queries... (inline selectionArgs - might be better for sql-caching...)
        switch (mUriMatcher.match(uri)) {
            case LOCATIONS:
                cursor = db.query(LocationEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case LOCATION_BY_ID:
                cursor = db.query(LocationEntry.TABLE_NAME, projection,
                        LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case PRODUCERS:
                cursor = db.query(ProducerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCERS_BY_NAME:
                pattern = ProducerEntry.getSearchString(uri);
                mySelectionArgs = new String[]{"%" + pattern + "%"};
                cursor = db.query(ProducerEntry.TABLE_NAME, projection, PRODUCERS_BY_NAME_SELECTION,
                        mySelectionArgs, null, null, sortOrder);
                break;
            case PRODUCER_BY_ID:
                cursor = db.query(ProducerEntry.TABLE_NAME, projection,
                        ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case PRODUCERS_BY_PATTERN:
                pattern = ProducerEntry.getSearchString(uri);
                mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%"};
                cursor = db.query(ProducerEntry.TABLE_NAME, projection,
                        PRODUCERS_BY_NAME_OR_LOCATION_SELECTION,
                        mySelectionArgs, null, null, sortOrder);
                break;
            case DRINKS:
                cursor = db.query(DrinkEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DRINKS_BY_NAME:
                pattern = DrinkEntry.getSearchString(uri, false);
                mySelectionArgs = new String[]{"%" + pattern + "%"};
                cursor = db.query(DrinkEntry.TABLE_NAME, projection,
                        DrinkEntry.TABLE_NAME + "." + Drink.NAME + " LIKE '%' || ? || '%'",
                        mySelectionArgs, null, null, sortOrder);
                break;
            case DRINK_BY_ID:
                cursor = db.query(DrinkEntry.TABLE_NAME, projection,
                        DrinkEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case DRINKS_WITH_PRODUCER_BY_NAME:
                pattern = DrinkEntry.getSearchString(uri, false);
                mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%"};
                cursor = sDrinksWithProducersQueryBuilder.query(db,
                        projection, DRINKS_OR_PRODUCERS_BY_NAME_SELECTION, mySelectionArgs, null, null, sortOrder);
                break;
            case DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE:
                pattern = DrinkEntry.getSearchString(uri, true);
                drinkType = DrinkEntry.getDrinkType(uri);
                if (Drink.TYPE_ALL.equals(drinkType)) {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%"};
                    cursor = sDrinksWithProducersQueryBuilder.query(db,
                            projection, DRINKS_OR_PRODUCERS_BY_NAME_SELECTION, mySelectionArgs, null, null, sortOrder);
                } else {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", drinkType};
                    cursor = sDrinksWithProducersQueryBuilder.query(db,
                            projection, DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION, mySelectionArgs, null, null, sortOrder);
                }

                break;
            case DRINKS_WITH_PRODUCER_BY_ID:
                cursor = sDrinksWithProducersQueryBuilder.query(db,
                        projection, DA + "." + DrinkEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case USERS:
                cursor = db.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case USERS_BY_NAME:
                mySelectionArgs = new String[]{"%" + UserEntry.getSearchString(uri) + "%"};
                cursor = db.query(UserEntry.TABLE_NAME, projection,
                        UserEntry.TABLE_NAME + "." + User.NAME + " LIKE ?",
                        mySelectionArgs, null, null, sortOrder);
                break;
            case USER_BY_ID:
                cursor = db.query(UserEntry.TABLE_NAME, projection,
                        UserEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case REVIEWS:
                cursor = db.query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REVIEW_BY_ID:
                cursor = db.query(ReviewEntry.TABLE_NAME, projection,
                        ReviewEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case REVIEW_WITH_ALL_BY_ID:
                cursor = sReviewWithAllQueryBuilder.query(
                        db, projection,
                        RA + "." + ReviewEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case REVIEWS_WITH_ALL_BY_NAME_AND_TYPE: //TODO
                pattern = ReviewEntry.getSearchString(uri, true);
                drinkType = ReviewEntry.getDrinkType(uri);
                if (Drink.TYPE_ALL.equals(drinkType)) {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%"};
                    cursor = sReviewWithAllQueryBuilder.query(db,
                            projection, REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION, mySelectionArgs, null, null, sortOrder);
                } else {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", drinkType};
                    cursor = sReviewWithAllQueryBuilder.query(db,
                            projection, REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION, mySelectionArgs, null, null, sortOrder);
                }
                break;
            case REVIEWS_GEOCODE:
                cursor = db.query(ReviewEntry.TABLE_NAME, projection,
                        //TODO!!!
                        Review.LOCATION_ID + " LIKE '" + Utils.GEOCODE_ME + "%'",
                        selectionArgs, null, null, sortOrder);
                break;
            // TODO: special review-searches    - advanced search Fragment...
            //  ALL_COLUMNS, Selection- and SortOrder- Builder
            //  - CONTAINS_STRING_IN_DESCRIPTION
            //  - LOCATION (Country)
            //  - TIME (last holiday)
            //  - (other) USER
            // f.e. best IPA in last years Canada-vacation ;-)

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    private static final SQLiteQueryBuilder sDrinksWithProducersQueryBuilder;
    static {
        sDrinksWithProducersQueryBuilder = new SQLiteQueryBuilder();
        sDrinksWithProducersQueryBuilder.setTables(
                DrinkEntry.TABLE_NAME + " " + DA +
                        " INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON "
                        + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID);
    }

    private static final SQLiteQueryBuilder sReviewWithAllQueryBuilder;
    static {
        sReviewWithAllQueryBuilder = new SQLiteQueryBuilder();
        // JoinOfJoin: http://stackoverflow.com/questions/11105895/sqlite-left-outer-join-multiple-tables
        sReviewWithAllQueryBuilder.setTables(
                ReviewEntry.TABLE_NAME + " " + RA
                        + " INNER JOIN " + UserEntry.TABLE_NAME + " " + UA + " ON "
                        + RA + "." + Review.USER_ID + " = " + UA + "." + User.USER_ID
                        + " INNER JOIN " + DrinkEntry.TABLE_NAME + " " + DA + " ON "
                        + RA + "." + Review.DRINK_ID + " = " + DA + "." + Drink.DRINK_ID
                        + " INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON "
                        + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID
//                        + " INNER JOIN " + LocationEntry.TABLE_NAME + " " + LAP + " ON "
//                        + PA + "." + Producer.LOCATION_ID + " = " + LAP + "." + Location.LOCATION_ID
        );
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
//        Log.v(LOG_TAG, "insert, " + "uri = [" + uri + "], values = [" + values + "]");
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        Uri returnUri;
        switch (mUriMatcher.match(uri)) {
            case LOCATIONS: {
                long id = db.insert(LocationEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = LocationEntry.buildUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
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
            case USERS: {
                long id = db.insert(UserEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = UserEntry.buildUri(id);
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
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
//        Log.v(LOG_TAG, "bulkInsert, " + "uri = [" + uri + "], values = [" + Arrays.toString(values) + "]");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int returnCount = 0;
        switch (mUriMatcher.match(uri)) {
            case LOCATIONS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(LocationEntry.TABLE_NAME, null, value,
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
            case PRODUCERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        // insert or replace handling not needed (on conflict replace...)
//                        boolean replace = usesReplace(value);
                        // for more validation (later) use special tag-value
                        // src: https://www.buzzingandroid.com/2013/01/sqlite-insert-or-replace-through-contentprovider/

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
                        if (id != -1) { // TODO: seems to be no error = -1 if foreign key is missing...
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case USERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(UserEntry.TABLE_NAME, null, value,
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
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
//        Log.v(LOG_TAG, "delete, " + "uri = [" + uri + "], selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int deletedRows;
        if (null == selection) selection = "1";
        switch (mUriMatcher.match(uri)) {
            case LOCATIONS:
                deletedRows = db.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION_BY_ID:
                deletedRows = db.delete(LocationEntry.TABLE_NAME,
                        LocationEntry.TABLE_NAME + "." + LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case PRODUCERS:
                deletedRows = db.delete(ProducerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCER_BY_ID:
                deletedRows = db.delete(ProducerEntry.TABLE_NAME,
                        ProducerEntry.TABLE_NAME + "." + ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case DRINKS:
                deletedRows = db.delete(DrinkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DRINK_BY_ID:
                deletedRows = db.delete(DrinkEntry.TABLE_NAME,
                        DrinkEntry.TABLE_NAME + "." + DrinkEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case USERS:
                deletedRows = db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_BY_ID:
                deletedRows = db.delete(UserEntry.TABLE_NAME,
                        UserEntry.TABLE_NAME + "." + UserEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case REVIEWS:
                deletedRows = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW_BY_ID:
                deletedRows = db.delete(ReviewEntry.TABLE_NAME,
                        ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (deletedRows > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        Log.v(LOG_TAG, "update, " + "uri = [" + uri + "], values = [" + values + "], selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int impactedRows;
        switch (mUriMatcher.match(uri)) {
            case LOCATIONS:
                impactedRows = db.update(LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LOCATION_BY_ID:
                impactedRows = db.update(LocationEntry.TABLE_NAME, values,
                        LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case PRODUCERS:
                impactedRows = db.update(ProducerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCER_BY_ID:
                impactedRows = db.update(ProducerEntry.TABLE_NAME, values,
                        ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case DRINKS:
                impactedRows = db.update(DrinkEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DRINK_BY_ID:
                impactedRows = db.update(DrinkEntry.TABLE_NAME, values,
                        DrinkEntry.TABLE_NAME + "." + DrinkEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case USERS:
                impactedRows = db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_BY_ID:
                impactedRows = db.update(UserEntry.TABLE_NAME, values,
                        UserEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            case REVIEWS:
                impactedRows = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW_BY_ID:
                impactedRows = db.update(ReviewEntry.TABLE_NAME, values,
                        ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (impactedRows > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return impactedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
//        Log.v(LOG_TAG, "getType, " + "uri = [" + uri + "]");

        switch (mUriMatcher.match(uri)) {
            case LOCATIONS:
                return LocationEntry.CONTENT_TYPE;
            case LOCATION_BY_ID:
                return LocationEntry.CONTENT_ITEM_TYPE;
            case PRODUCERS:
                return ProducerEntry.CONTENT_TYPE;
            case PRODUCERS_BY_NAME:
                return ProducerEntry.CONTENT_TYPE;
            case PRODUCERS_BY_PATTERN:
                return ProducerEntry.CONTENT_TYPE;
            case PRODUCER_BY_ID:
                return ProducerEntry.CONTENT_ITEM_TYPE;
            case DRINKS:
                return DrinkEntry.CONTENT_TYPE;
            case DRINKS_BY_NAME:
                return DrinkEntry.CONTENT_TYPE;
            case DRINK_BY_ID:
                return DrinkEntry.CONTENT_ITEM_TYPE;
            case DRINKS_WITH_PRODUCER_BY_NAME:
                return DrinkEntry.CONTENT_TYPE;
            case DRINKS_WITH_PRODUCER_BY_ID:
                return DrinkEntry.CONTENT_ITEM_TYPE;
            case DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE:
                return DrinkEntry.CONTENT_TYPE;
            case USERS:
                return UserEntry.CONTENT_TYPE;
            case USER_BY_ID:
                return UserEntry.CONTENT_ITEM_TYPE;
            case USERS_BY_NAME:
                return UserEntry.CONTENT_TYPE;
            case REVIEWS:
                return ReviewEntry.CONTENT_TYPE;
            case REVIEW_BY_ID:
                return ReviewEntry.CONTENT_ITEM_TYPE;
            case REVIEW_WITH_ALL_BY_ID:
                return ReviewEntry.CONTENT_ITEM_TYPE;
            case REVIEWS_WITH_ALL_BY_NAME_AND_TYPE:
                return ReviewEntry.CONTENT_TYPE;
            case REVIEWS_GEOCODE:
                return ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
