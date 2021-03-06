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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.fbartnitzek.tasteemall.data.DatabaseContract.DrinkEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.LocationEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry;
import com.fbartnitzek.tasteemall.data.DatabaseContract.UserEntry;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.data.pojo.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.fbartnitzek.tasteemall.data.DatabaseContract.ReviewEntry.getPathString;

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

    private static final String RA = ReviewEntry.ALIAS;   //ReviewAlias
    private static final String UA = UserEntry.ALIAS;   //UserAlias
    private static final String DA = DrinkEntry.ALIAS;   //DrinkAlias
    private static final String PA = ProducerEntry.ALIAS;   //ProducerAlias
    private static final String LAR = LocationEntry.ALIAS_REVIEW;   //LocationAlias

    private static final String LOCATION_NOT_NULL = " AND " + LAR + "." + Location.LOCATION_ID
            + " IS NOT NULL AND " + LAR + "." + Location.LOCATION_ID + " != ''";;
    private static DatabaseHelper mHelper;
    private static final String LOG_TAG = DatabaseProvider.class.getName();

    private static final int LOCATIONS = 100;
    private static final int LOCATIONS_BY_PATTERN = 101;
    private static final int LOCATION_BY_ID = 102;
    private static final int LOCATIONS_BY_LATLNG = 103;
    private static final int LOCATIONS_BY_DESCRIPTION_PATTERN = 104;
    private static final int LOCATIONS_BY_BOTH_PATTERNS = 106;
    private static final int GEOCODE_VALID_LATLNG_LOCATIONS = 110;
    private static final int GEOCODE_TEXT_LATLNG_LOCATIONS = 111;

    private static final int PRODUCERS = 200;
    private static final int PRODUCER_BY_ID = 202;
    private static final int PRODUCERS_WITH_LOCATION_BY_ID = 203;
    private static final int PRODUCERS_WITH_LOCATION_BY_PATTERN = 204;
    private static final int GEOCODE_VALID_LATLNG_PRODUCER_LOCATIONS = 210;
    private static final int GEOCODE_TEXT_PRODUCER_LOCATIONS = 211;

    private static final int DRINKS = 300;
//    private static final int DRINKS_BY_NAME = 301;
    private static final int DRINK_BY_ID = 302;
    private static final int DRINKS_WITH_PRODUCER_BY_NAME = 310;
    private static final int DRINKS_WITH_PRODUCER_BY_ID = 311;
    private static final int DRINKS_WITH_PRODUCER_AND_LOCATION_BY_ID = 312;
    private static final int DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE = 320;

    private static final int USERS = 400;
    private static final int USERS_BY_NAME = 401;
    private static final int USER_BY_ID = 402;

    private static final int REVIEWS = 500;
    private static final int REVIEW_BY_ID = 503;
    private static final int REVIEW_WITH_ALL_BY_ID = 510;
    private static final int REVIEWS_WITH_ALL_BY_NAME_AND_TYPE = 520;
    private static final int REVIEW_LOCATIONS_WITH_ALL_BY_NAME_AND_TYPE = 5201;
    private static final int REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE = 5202;
//    private static final int REVIEWS_GEOCODE = 530;

    private static final int JSON = 600;


    private final UriMatcher mUriMatcher = buildUriMatcher();


    private static final String LOCATIONS_GEOCODE_VALID_LATLNG =
            Location.FORMATTED_ADDRESS + " LIKE '" + LocationEntry.GEOCODE_ME + "' AND "
                    + Location.LATITUDE + " <= 90.0 AND " + Location.LATITUDE + " >= -90.0 AND "
                    + Location.LONGITUDE+ " <= 180.0 AND " + Location.LONGITUDE + " >= -180.0";

//    private static final String REVIEW_LOCATIONS_VALID_LATLNG =
//            LAR + "." + Location.LATITUDE + " <= 90.0 AND " + LAR + "." + Location.LATITUDE + " >= -90.0 AND " +
//            LAR + "." + Location.LONGITUDE + " <= 180.0 AND " + LAR + "." + Location.LONGITUDE + " >= -180.0";

    public static final String PRODUCERS_GEOCODE_VALID_LATLNG =
            Producer.FORMATTED_ADDRESS + " LIKE '" + LocationEntry.GEOCODE_ME + "' AND "
                    + Producer.LATITUDE + " <= 90.0 AND " + Producer.LATITUDE + " >= -90.0 AND "
                    + Producer.LONGITUDE+ " <= 180.0 AND " + Producer.LONGITUDE + " >= -180.0";

    public static final String LOCATIONS_GEOCODE_TEXT =
            Location.FORMATTED_ADDRESS + " LIKE '" + LocationEntry.GEOCODE_ME + "' AND ("
                    + Location.LATITUDE + " > 90.0 OR " + Location.LATITUDE + " < -90.0 OR "
                    + Location.LONGITUDE+ " > 180.0 OR " + Location.LONGITUDE + " < -180.0)";

    public static final String PRODUCERS_GEOCODE_TEXT =
            Producer.FORMATTED_ADDRESS + " LIKE '" + LocationEntry.GEOCODE_ME + "' AND ("
                    + Producer.LATITUDE + " > 90.0 OR " + Producer.LATITUDE + " < -90.0 OR "
                    + Producer.LONGITUDE+ " > 180.0 OR " + Producer.LONGITUDE + " < -180.0)";




//    private static final String PRODUCERS_BY_NAME_OR_LOCATION_SELECTION =
//                PA + "." + Producer.NAME + " LIKE ? OR "
//                    + LAP + "." + Location.FORMATTED_ADDRESS + " LIKE ? OR "
//                    + LAP + "." + Location.COUNTRY + " LIKE ?";
    private static final String PRODUCERS_BY_NAME_OR_LOCATION_SELECTION =
        Producer.NAME + " LIKE ? OR "
                + Producer.FORMATTED_ADDRESS + " LIKE ? OR "
                + Producer.COUNTRY + " LIKE ?";

    private static final String DRINKS_OR_PRODUCERS_BY_NAME_SELECTION =
            DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?";

    private static final String DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION =
            "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?)" +
                    " AND " + DA + "." + Drink.TYPE + " = ?";

    private static final String REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION =
            DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?";

    private static final String REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION =
            "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?)" +
                    " AND " + DA + "." + Drink.TYPE + " = ?";

    private static final String REVIEWS_OF_LOCATION_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION =
            "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?) AND "
            + LAR + "." + LocationEntry._ID + " = ?" ;

    private static final String REVIEWS_OF_LOCATION_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION =
            "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?)" +
                    " AND " + DA + "." + Drink.TYPE + " = ? AND " + LAR + "." + LocationEntry._ID + " = ?";

    private UriMatcher buildUriMatcher() {
        Log.v(LOG_TAG, "buildUriMatcher, " + "");
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        // all locations
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION, LOCATIONS);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION + "/#", LOCATION_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_PATTERN + "/", LOCATIONS_BY_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_PATTERN + "/*", LOCATIONS_BY_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_DESCRIPTION_PATTERN + "/", LOCATIONS_BY_DESCRIPTION_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_DESCRIPTION_PATTERN + "/*", LOCATIONS_BY_DESCRIPTION_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_PATTERN_OR_DESCRIPTION + "/", LOCATIONS_BY_BOTH_PATTERNS);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_PATTERN_OR_DESCRIPTION + "/*", LOCATIONS_BY_BOTH_PATTERNS);

        // all variations if double is without dot
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_LATLNG + "/#/#", LOCATIONS_BY_LATLNG);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_LATLNG + "/*/#", LOCATIONS_BY_LATLNG);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_LATLNG + "/#/*", LOCATIONS_BY_LATLNG);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_BY_LATLNG + "/*/*", LOCATIONS_BY_LATLNG);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_LATLNG_GEOCODE, GEOCODE_VALID_LATLNG_LOCATIONS);
        matcher.addURI(authority, DatabaseContract.PATH_LOCATION_TEXT_GEOCODE, GEOCODE_TEXT_LATLNG_LOCATIONS);

        // all producers
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER, PRODUCERS);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER + "/#", PRODUCER_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_WITH_LOCATION + "/#", PRODUCERS_WITH_LOCATION_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_WITH_LOCATION_BY_PATTERN + "/", PRODUCERS_WITH_LOCATION_BY_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCER_WITH_LOCATION_BY_PATTERN + "/*", PRODUCERS_WITH_LOCATION_BY_PATTERN);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCERS_LATLNG_GEOCODE, GEOCODE_VALID_LATLNG_PRODUCER_LOCATIONS);
        matcher.addURI(authority, DatabaseContract.PATH_PRODUCERS_TEXT_GEOCODE, GEOCODE_TEXT_PRODUCER_LOCATIONS);

        //TODO: all breweries in certain location - even better in area (center, radius)

        // all drinks
        matcher.addURI(authority, DatabaseContract.PATH_DRINK, DRINKS);
//        matcher.addURI(authority, DatabaseContract.PATH_DRINK_BY_NAME + "/", DRINKS_BY_NAME);
//        matcher.addURI(authority, DatabaseContract.PATH_DRINK_BY_NAME + "/*", DRINKS_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME + "/", DRINKS_WITH_PRODUCER_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME + "/*", DRINKS_WITH_PRODUCER_BY_NAME);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE + "/*/", DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE + "/*/*", DRINKS_WITH_PRODUCER_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK + "/#", DRINK_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER + "/#", DRINKS_WITH_PRODUCER_BY_ID);
        matcher.addURI(authority, DatabaseContract.PATH_DRINK_WITH_PRODUCER_AND_LOCATION + "/#", DRINKS_WITH_PRODUCER_AND_LOCATION_BY_ID);

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
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_LOCATION_WITH_ALL_BY_NAME_AND_TYPE + "/*/", REVIEW_LOCATIONS_WITH_ALL_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_LOCATION_WITH_ALL_BY_NAME_AND_TYPE + "/*/*", REVIEW_LOCATIONS_WITH_ALL_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE + "/*/#/", REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE + "/*/*/#", REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE);
//        matcher.addURI(authority, DatabaseContract.PATH_REVIEW_GEOCODE_LOCATION, REVIEWS_GEOCODE);

        matcher.addURI(authority, DatabaseContract.PATH_JSON + "/*", JSON);

        return matcher;
    }


    private static final String LAT = Location.LATITUDE;
    private static final String LNG = Location.LONGITUDE;

    private static String filterLatLng(double lat, double lng, double filter) {
        // manhattan distance
        return LAT + " >= " + (lat - filter) + " AND " + LAT + " <= " + (lat + filter)
                + " AND " + LNG + " >= " + (lng - filter) + " AND " + LNG + " <= " + (lng + filter)
                // 2d euclidean distance - good enough
                + " AND "
                + "(" + LAT + " - " + lat + ") * (" + LAT + " - " + lat + ") + "
                + "(" + LNG + " - " + lng + ") * (" + LNG + " - " + lng + ") "
                + " <= " + LocationEntry.DISTANCE_SQUARE_THRESHOLD;
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
            case LOCATIONS_BY_PATTERN:
                pattern = LocationEntry.getSearchString(uri);
                mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", "%" + pattern + "%"};
                cursor = db.query(LocationEntry.TABLE_NAME, projection,
                        Location.FORMATTED_ADDRESS + " LIKE ? OR " + Location.INPUT + " LIKE ? OR " + Location.COUNTRY + " LIKE ?",
                        mySelectionArgs,
                        null, null, sortOrder);
                break;
            case LOCATIONS_BY_DESCRIPTION_PATTERN:
                pattern = LocationEntry.getSearchString(uri);
                mySelectionArgs = new String[]{"%" + pattern + "%"};
                cursor = db.query(LocationEntry.TABLE_NAME, projection,
                        Location.DESCRIPTION+ " LIKE ?",
                        mySelectionArgs,
                        null, null, sortOrder);

                break;
            case LOCATIONS_BY_BOTH_PATTERNS:
                pattern = LocationEntry.getSearchString(uri);
                mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", "%" + pattern + "%", "%" + pattern + "%"};
                cursor = db.query(LocationEntry.TABLE_NAME, projection,
                        Location.FORMATTED_ADDRESS + " LIKE ? OR " + Location.INPUT + " LIKE ? OR " + Location.COUNTRY + " LIKE ? OR " + Location.DESCRIPTION + " LIKE ?",
                        mySelectionArgs,
                        null, null, sortOrder);
                break;
            case LOCATION_BY_ID:
                cursor = db.query(LocationEntry.TABLE_NAME, projection,
                        LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;
            case LOCATIONS_BY_LATLNG:
                double latitude = LocationEntry.getLatitude(uri);
                double longitude = LocationEntry.getLongitude(uri);
                cursor = db.query(LocationEntry.TABLE_NAME, projection,
                        filterLatLng(latitude, longitude, LocationEntry.DISTANCE_PRE_FILTER_LAT_LNG),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case GEOCODE_VALID_LATLNG_LOCATIONS:
                cursor = db.query(LocationEntry.TABLE_NAME, projection, LOCATIONS_GEOCODE_VALID_LATLNG,
                        selectionArgs, null, null, sortOrder);
                break;
            case GEOCODE_TEXT_LATLNG_LOCATIONS:
                cursor = db.query(LocationEntry.TABLE_NAME, projection, LOCATIONS_GEOCODE_TEXT,
                        selectionArgs, null, null, sortOrder);
                break;

            case PRODUCERS:
                cursor = db.query(ProducerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCER_BY_ID:
//                cursor = sProducersWithLocationQueryBuilder.query(db, projection,
//                        PA + "." + ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
//                        selectionArgs, null, null, sortOrder);
                cursor = db.query(ProducerEntry.TABLE_NAME, projection,
                        ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;

            case PRODUCERS_WITH_LOCATION_BY_ID:
//                cursor = sProducersWithLocationQueryBuilder.query(db, projection,
//                PA + "." + ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                cursor = db.query(ProducerEntry.TABLE_NAME, projection,
                        ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
                break;

            case PRODUCERS_WITH_LOCATION_BY_PATTERN:
                pattern = ProducerEntry.getSearchString(uri);
                mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", "%" + pattern + "%"};
                Log.v(LOG_TAG, "query, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "], pattern= [" + pattern+ "]");
//                cursor = sProducersWithLocationQueryBuilder.query(db, projection,
                cursor = db.query(ProducerEntry.TABLE_NAME, projection,
                        PRODUCERS_BY_NAME_OR_LOCATION_SELECTION,
                        mySelectionArgs, null, null, sortOrder);
                break;

            case GEOCODE_VALID_LATLNG_PRODUCER_LOCATIONS:
                cursor = db.query(ProducerEntry.TABLE_NAME, projection, PRODUCERS_GEOCODE_VALID_LATLNG,
                        selectionArgs, null, null, sortOrder);
                break;
            case GEOCODE_TEXT_PRODUCER_LOCATIONS:
                cursor = db.query(ProducerEntry.TABLE_NAME, projection, PRODUCERS_GEOCODE_TEXT,
                        selectionArgs, null, null, sortOrder);
                break;

            case DRINKS:
                cursor = db.query(DrinkEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
//            case DRINKS_BY_NAME:
//                pattern = DrinkEntry.getSearchString(uri, false);
//                mySelectionArgs = new String[]{"%" + pattern + "%"};
//                cursor = db.query(DrinkEntry.TABLE_NAME, projection,
//                        DrinkEntry.TABLE_NAME + "." + Drink.NAME + " LIKE '%' || ? || '%'",
//                        mySelectionArgs, null, null, sortOrder);
//                break;
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
            case DRINKS_WITH_PRODUCER_AND_LOCATION_BY_ID:
                cursor = sDrinksWithProducersAndLocationQueryBuilder.query(db,
                        projection, DA + "." + DrinkEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs, null, null, sortOrder);
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
            case REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE:

                String locationId;
                drinkType = ReviewEntry.getPathString(uri, 1);
                if (uri.getPathSegments().size() > 3) {
                    pattern = getPathString(uri, 2);
                    locationId = ReviewEntry.getPathString(uri, 3);

                } else {
                    pattern = "";
                    locationId = ReviewEntry.getPathString(uri, 2);
                }

                String mySortOrder = ReviewEntry.ALIAS + "." + Review.READABLE_DATE + " DESC";
                if (Drink.TYPE_ALL.equals(drinkType)) {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", locationId};

                    cursor = sReviewWithAllQueryBuilder.query(db,
                            projection, REVIEWS_OF_LOCATION_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION,
                            mySelectionArgs, null, null, mySortOrder);

                    Log.v(LOG_TAG, "query, ReviewsOfLocations without type, selection = [" + REVIEWS_OF_LOCATION_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION + "], selectionArgs = [" + Arrays.toString(mySelectionArgs) + "], sortOrder = [" + mySortOrder+ "]");
                } else {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", drinkType, locationId};

                    cursor = sReviewWithAllQueryBuilder.query(db,
                            projection, REVIEWS_OF_LOCATION_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION,
                            mySelectionArgs, null, null, mySortOrder);
                    Log.v(LOG_TAG, "query, ReviewsOfLocations with type, selection = [" + REVIEWS_OF_LOCATION_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION + "], selectionArgs = [" + Arrays.toString(mySelectionArgs) + "], sortOrder = [" + mySortOrder + "]");
                }

                break;
            case REVIEW_LOCATIONS_WITH_ALL_BY_NAME_AND_TYPE:
                // all other patterns all also needed...

                pattern = ReviewEntry.getSearchString(uri, true);
                drinkType = ReviewEntry.getDrinkType(uri);
                mySortOrder = LocationEntry.ALIAS_REVIEW + "." + Location.FORMATTED_ADDRESS + " ASC";

                if (Drink.TYPE_ALL.equals(drinkType)) {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%"};
//                    sReviewWithAllQueryBuilder.setDistinct(true);
                    cursor = sReviewWithAllQueryBuilder.query(db,
                            projection, "(" + REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION + ")" + LOCATION_NOT_NULL,
                            mySelectionArgs, null, null, mySortOrder);
                    Log.v(LOG_TAG, "query, Reviews without type, selection = [" + "(" + REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION + ")" + LOCATION_NOT_NULL + "], selectionArgs = [" + Arrays.toString(mySelectionArgs) + "], sortOrder = [" + mySortOrder+ "]");
                } else {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", drinkType};
                    cursor = sReviewWithAllQueryBuilder.query(db,
                            projection, "(" + REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION + ")" + LOCATION_NOT_NULL,
                            mySelectionArgs, null, null, mySortOrder);
                    Log.v(LOG_TAG, "query, Reviews with type, selection = [" + "(" + REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION + ")" + LOCATION_NOT_NULL + "], selectionArgs = [" + Arrays.toString(mySelectionArgs) + "], sortOrder = [" + mySortOrder + "]");
                }

                break;
            case REVIEWS_WITH_ALL_BY_NAME_AND_TYPE:
                pattern = ReviewEntry.getSearchString(uri, true);
                drinkType = ReviewEntry.getDrinkType(uri);
                if (Drink.TYPE_ALL.equals(drinkType)) {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%"};
                    cursor = sReviewWithMostQueryBuilder.query(db,
                            projection, REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_SELECTION, mySelectionArgs, null, null, sortOrder);
                } else {
                    mySelectionArgs = new String[]{"%" + pattern + "%", "%" + pattern + "%", drinkType};
                    cursor = sReviewWithMostQueryBuilder.query(db,
                            projection, REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION, mySelectionArgs, null, null, sortOrder);
                }
                break;
//            case REVIEWS_GEOCODE:
//                cursor = db.query(ReviewEntry.TABLE_NAME, projection,
//                        //TODO!!!
//                        Review.LOCATION_ID + " LIKE '" + LocationEntry.GEOCODE_ME + "%'",
//                        selectionArgs, null, null, sortOrder);
//                break;


            case JSON:  // WORKS :-D
                String json = DatabaseContract.getJson(uri);
                // f.e. json={"review":{"date":{"GT":"2016-12-01+00:00:00"}}}
                Log.v(LOG_TAG, "query JSON, json=" + json + ", hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "], projection = [" + projection + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "], sortOrder = [" + sortOrder + "]");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String rootEntity = jsonObject.keys().next();   // just 1 rootElement
                    if (!DatabaseContract.ALIASES.containsKey(rootEntity)) {
                        throw new RuntimeException("not yet implemented json entity `" + rootEntity + "'");
                    }

                    String prefix = DatabaseContract.TABLE_NAMES.get(rootEntity) + " " + DatabaseContract.ALIASES.get(rootEntity);

                    StringBuilder customSelection = new StringBuilder();
                    List<String> customSelectionArgs = new ArrayList<>();
                    Set<String> joins = parseJsonEntity(jsonObject, rootEntity, customSelection, customSelectionArgs, " AND ");
                    Log.v(LOG_TAG, "query, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "], joins=" + joins);

                    // enrich joins if needed through projection
                    joins.addAll(joinsFromProjection(projection, rootEntity));
                    Log.v(LOG_TAG, "query after projection-enrichment, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "], joins=" + joins);

                    SQLiteQueryBuilder jsonQueryBuilder = new SQLiteQueryBuilder();
                    jsonQueryBuilder.setTables(
                            prefix + (joins.isEmpty() ? "" : " " + TextUtils.join(" ", joins)));

                    Log.v(LOG_TAG, "query JSON, customSelection=" + customSelection.toString() + ", customArgs=" + customSelectionArgs.toString());

                    cursor = jsonQueryBuilder.query(db, projection,
                            customSelection.toString(),
                            customSelectionArgs.toArray(new String[customSelectionArgs.size()]),
                            null, null, sortOrder);

                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "query JSON, error:" + e.getMessage());
                    throw new UnsupportedOperationException("invalid json structure: " + json);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    private static String decode(String s) throws UnsupportedEncodingException {
        // decode attribute-values - see DC.encodeValue() ...
        return URLDecoder.decode(s, DatabaseContract.UTF8);
    }

    private Set<String> parseJsonEntity(JSONObject jsonParent, String entityName, StringBuilder customSelection,
                                        List<String> customSelectionArgs, String compare) throws JSONException, UnsupportedEncodingException {

        JSONObject entity = jsonParent.getJSONObject(entityName);
        Log.v(LOG_TAG, "parseJsonEntity, hashCode=" + this.hashCode() + ", " + "jsonParent = [" + jsonParent + "], entityName = [" + entityName + "], customSelection = [" + customSelection + "], customSelectionArgs = [" + customSelectionArgs + "]");

        final String alias = DatabaseContract.ALIASES.get(entityName);
        if (alias == null) {
            throw new RuntimeException("unknown entity '" + entityName + "'");
        }

        Iterator<String> keys = entity.keys();
        Set<String> joins = new LinkedHashSet<>();

        while (keys.hasNext()) {
            String attributeName = keys.next();
            if (DatabaseContract.ATTRIBUTES.get(entityName).contains(attributeName)) {
                JSONObject attribute = entity.getJSONObject(attributeName);
                String operation = attribute.keys().next(); //just 1 operation
                if (customSelection.length() > 0) {
//                    customSelection.append(" AND ");
                    customSelection.append(compare);    // mostly AND, sometimes OR
                }
                switch (operation) {
                    case DatabaseContract.Operations.GT:
                        customSelection.append(alias).append(".").append(attributeName).append(" > ?");
                        customSelectionArgs.add(decode(attribute.getString(operation)));
                        break;
                    case DatabaseContract.Operations.GTE:
                        customSelection.append(alias).append(".").append(attributeName).append(" >= ?");
                        customSelectionArgs.add(decode(attribute.getString(operation)));
                        break;
                    case DatabaseContract.Operations.LT:
                        customSelection.append(alias).append(".").append(attributeName).append(" < ?");
                        customSelectionArgs.add(decode(attribute.getString(operation)));
                        break;
                    case DatabaseContract.Operations.LTE:
                        customSelection.append(alias).append(".").append(attributeName).append(" <= ?");
                        customSelectionArgs.add(decode(attribute.getString(operation)));
                        break;
                    case DatabaseContract.Operations.BETWEEN: // between: [1,5]
                        customSelection.append(alias).append(".").append(attributeName).append(" >= ? AND ")
                                .append(alias).append(".").append(attributeName).append(" <= ?");
                        customSelectionArgs.add(decode(attribute.getJSONArray(operation).getString(0)));
                        customSelectionArgs.add(decode(attribute.getJSONArray(operation).getString(1)));
                        break;
                    case DatabaseContract.Operations.CONTAINS:
                        customSelection.append(alias).append(".").append(attributeName).append(" LIKE ?");
                        customSelectionArgs.add("%" + decode(attribute.getString(operation)) + "%");
                        break;
                    case DatabaseContract.Operations.IS:    // {"IS", ["a","b","c"]}
                        Object something = attribute.get(operation);
                        if (something instanceof JSONArray) {
                            JSONArray array = attribute.getJSONArray(operation);
                            if (array.length() > 0) {
                                customSelection.append("(");

                                for (int i = 0; i < array.length(); ++i) {
                                    if (i > 0) {
                                        customSelection.append(" OR ");
                                    }
                                    customSelection.append(alias).append(".").append(attributeName).append(" LIKE ?");
//                                Log.v(LOG_TAG, "parseJsonEntity, arg" + i + ": '" + array.get(i) + "', decoded: '"+decode((String) array.get(i))+"'" );
                                    customSelectionArgs.add(decode((String) array.get(i)));
                                }

                                customSelection.append(")");
                            }
                        } else {    // cast it to string
                            customSelection.append(alias).append(".").append(attributeName).append(" LIKE ?");
                            customSelectionArgs.add(decode((String) something));
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("unsupported operation '" + operation + "' in json structure: " + entity);
                }

            } else if (DatabaseContract.ASSOCIATIONS.get(entityName).contains(attributeName)) { // f.e. drink of review

                // special handling for NULL (no review location given)
                if (attributeName.equals(Location.ENTITY)
                        && entity.getJSONObject(Location.ENTITY).has(DatabaseContract.Operations.NULL)) {
                    if (customSelection.length() > 0) {
                        customSelection.append(compare);    // mostly AND, sometimes OR
                    }
                    customSelection.append(alias).append(".").append(Review.LOCATION_ID).append(" IS NULL");
                } else {

                    String joinBuilder = (attributeName.equals(Location.ENTITY) ? "LEFT JOIN " : "INNER JOIN ") +
                            DatabaseContract.TABLE_NAMES.get(attributeName) +
                            " " + DatabaseContract.ALIASES.get(attributeName) +
                            " ON " +
                            DatabaseContract.ALIASES.get(entityName) + "." +
                            DatabaseContract.FOREIGN_KEYS.get(entityName).get(attributeName) +
                            " = " +
                            DatabaseContract.ALIASES.get(attributeName) + "." +
                            DatabaseContract.PRIMARY_KEYS.get(attributeName);
                    joins.add(joinBuilder);

//                Log.v(LOG_TAG, "parseJsonEntity, entity = [" + entityName + "], child = [" + attributeName + "], preJoin= [" + joinBuilder.toString()+ "]");
                    Set<String> childJoins = parseJsonEntity(entity, attributeName, customSelection, customSelectionArgs, compare);
//                Log.v(LOG_TAG, "parseJsonEntity, entity = [" + entityName + "], child = [" + attributeName + "], childJoin= [" + TextUtils.join(" ", childJoins) + "]");
                    joins.addAll(childJoins);
                }

            } else if (DatabaseContract.OR.equals(attributeName)) {
                JSONObject rootEntity = entity.getJSONObject(DatabaseContract.OR);

                if (customSelection.length() > 0) {
                    customSelection.append(compare);
                }
                customSelection.append("(");
                StringBuilder orStringBuilder = new StringBuilder();
                Set<String> orJoins = parseJsonEntity(rootEntity, rootEntity.keys().next(), orStringBuilder, customSelectionArgs, " OR ");
                joins.addAll(orJoins);
                customSelection.append(orStringBuilder);
                customSelection.append(")");
            }
        }

//        Log.v(LOG_TAG, "parseJsonEntity, entityName = [" + entityName + "], allJoins= [" + TextUtils.join(" ", joins) + "]");
        return joins;
    }

    private Set<String> joinsFromProjection(String[] projection, String rootEntity) {
        List<String> attributes = Arrays.asList(projection);
        Set<String> joins = new LinkedHashSet<>();

        for (String attribute : attributes) {
            if (attribute.indexOf('.') > 0) {
                String alias = attribute.substring(0, attribute.indexOf('.'));
                if (rootEntity.equals(Review.ENTITY)) {
                    switch (alias) {
                        case LocationEntry.ALIAS_REVIEW:
                            joins.add("LEFT JOIN " + LocationEntry.TABLE_NAME + " " + LAR + " ON " + RA + "." + Review.LOCATION_ID + " = " + LAR + "." + Location.LOCATION_ID);
                            break;
                        case UserEntry.ALIAS:
                            joins.add("INNER JOIN " + UserEntry.TABLE_NAME + " " + UA + " ON " + RA + "." + Review.USER_ID + " = " + UA + "." + User.USER_ID);
                            break;
                        case ProducerEntry.ALIAS:   // join for producer and drink
                            joins.add("INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON " + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID);
                        case DrinkEntry.ALIAS:
                            joins.add("INNER JOIN " + DrinkEntry.TABLE_NAME + " " + DA + " ON " + RA + "." + Review.DRINK_ID + " = " + DA + "." + Drink.DRINK_ID);
                            break;
                    }
                } else if (rootEntity.equals(Drink.ENTITY) && alias.equals(ProducerEntry.ALIAS)) {
                    joins.add("INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON " + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID);
                }
            }
        }

//        Log.v(LOG_TAG, "joinsFromProjection, hashCode=" + this.hashCode() + ", joins = " + TextUtils.join(" ", joins));
        return joins;
    }

    private static final SQLiteQueryBuilder sDrinksWithProducersQueryBuilder;
    static {
        sDrinksWithProducersQueryBuilder = new SQLiteQueryBuilder();
        sDrinksWithProducersQueryBuilder.setTables(
                DrinkEntry.TABLE_NAME + " " + DA +
                        " INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON "
                        + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID);
    }

    public static final SQLiteQueryBuilder sDrinksWithProducersAndLocationQueryBuilder;
    static {
        sDrinksWithProducersAndLocationQueryBuilder = new SQLiteQueryBuilder();
        sDrinksWithProducersAndLocationQueryBuilder.setTables(
                DrinkEntry.TABLE_NAME + " " + DA
                        + " INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON "
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
                        + " LEFT JOIN " + LocationEntry.TABLE_NAME + " " + LAR + " ON "
                        + RA + "." + Review.LOCATION_ID + " = " + LAR + "." + Location.LOCATION_ID
                        + " INNER JOIN " + DrinkEntry.TABLE_NAME + " " + DA + " ON "
                        + RA + "." + Review.DRINK_ID + " = " + DA + "." + Drink.DRINK_ID
                        + " INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON "
                        + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID
        );
    }

    private static final SQLiteQueryBuilder sReviewWithMostQueryBuilder;
    static {
        sReviewWithMostQueryBuilder= new SQLiteQueryBuilder();
        sReviewWithMostQueryBuilder.setTables(
                ReviewEntry.TABLE_NAME + " " + RA
                        + " INNER JOIN " + UserEntry.TABLE_NAME + " " + UA + " ON "
                        + RA + "." + Review.USER_ID + " = " + UA + "." + User.USER_ID
                        + " INNER JOIN " + DrinkEntry.TABLE_NAME + " " + DA + " ON "
                        + RA + "." + Review.DRINK_ID + " = " + DA + "." + Drink.DRINK_ID
                        + " INNER JOIN " + ProducerEntry.TABLE_NAME + " " + PA + " ON "
                        + DA + "." + Drink.PRODUCER_ID + " = " + PA + "." + Producer.PRODUCER_ID
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
                        ProducerEntry.TABLE_NAME + "." + ProducerEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
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
            case GEOCODE_VALID_LATLNG_LOCATIONS:
                return LocationEntry.CONTENT_TYPE;
            case PRODUCERS:
                return ProducerEntry.CONTENT_TYPE;
            case PRODUCERS_WITH_LOCATION_BY_PATTERN:
                return ProducerEntry.CONTENT_TYPE;
            case PRODUCER_BY_ID:
                return ProducerEntry.CONTENT_ITEM_TYPE;
            case DRINKS:
                return DrinkEntry.CONTENT_TYPE;
//            case DRINKS_BY_NAME:
//                return DrinkEntry.CONTENT_TYPE;
            case DRINK_BY_ID:
                return DrinkEntry.CONTENT_ITEM_TYPE;
            case DRINKS_WITH_PRODUCER_BY_NAME:
                return DrinkEntry.CONTENT_TYPE;
            case DRINKS_WITH_PRODUCER_AND_LOCATION_BY_ID:
                return DrinkEntry.CONTENT_ITEM_TYPE;
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
//            case REVIEWS_GEOCODE:
//                return ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
