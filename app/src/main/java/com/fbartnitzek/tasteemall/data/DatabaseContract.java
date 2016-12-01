package com.fbartnitzek.tasteemall.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

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

public class DatabaseContract {

    private static final String LOG_TAG = DatabaseContract.class.getName();

//    public static final String SQL_INSERT_OR_REPLACE = " sql insert or replace";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_LOCATION_BY_PATTERN = "location_by_pattern";
    public static final String PATH_LOCATION_BY_DESCRIPTION_PATTERN= "location_by_description_pattern";
    public static final String PATH_LOCATION_BY_PATTERN_OR_DESCRIPTION = "location_by_pattern_or_description";
    public static final String PATH_LOCATION_BY_LATLNG = "location_by_latlng";
    public static final String PATH_LOCATION_LATLNG_GEOCODE = "locations_geocode_latlng";
    public static final String PATH_LOCATION_TEXT_GEOCODE = "locations_geocode_text";


    public static final String PATH_PRODUCER = "producer";
    public static final String PATH_PRODUCER_WITH_LOCATION = "producer_with_location";
    public static final String PATH_PRODUCER_WITH_LOCATION_BY_PATTERN = "producer_with_location_by_pattern";
    public static final String PATH_PRODUCERS_LATLNG_GEOCODE = "producers_geocode_latlng";
    public static final String PATH_PRODUCERS_TEXT_GEOCODE = "producers_geocode_text";

    public static final String PATH_DRINK = "drink";
    public static final String PATH_DRINK_BY_NAME = "drink_by_name";
    public static final String PATH_DRINK_WITH_PRODUCER = "drink_with_producer";
    public static final String PATH_DRINK_WITH_PRODUCER_AND_LOCATION = "drink_with_producer_and_location";
    public static final String PATH_DRINK_WITH_PRODUCER_BY_NAME = "drink_with_producer_by_name";
    public static final String PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE = "drink_with_producer_by_name_and_type";

    public static final String PATH_USER = "user";
    public static final String PATH_USER_BY_NAME = "user_by_name";

    public static final String PATH_REVIEW = "review";
    public static final String PATH_REVIEW_WITH_ALL = "review_with_all";
    public static final String PATH_REVIEW_WITH_ALL_BY_NAME_AND_TYPE = "review_with_all_by_name_and_type";
    public static final String PATH_REVIEW_LOCATION_WITH_ALL_BY_NAME_AND_TYPE = "review_with_all_by_name_and_type_map_locations";
    public static final String PATH_REVIEWS_OF_LOCATION_WITH_ALL_BY_NAME_AND_TYPE = "review_with_all_by_name_and_type_map_reviews_of_location";
    public static final String PATH_REVIEW_GEOCODE_LOCATION = "review_geocode_locations";


//    //URI data
    public static final String CONTENT_AUTHORITY = "fbartnitzek.tasteemall";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "locations";
//        public static final String ALIAS_PRODUCER = "lp";
        public static final String ALIAS_REVIEW = "lr";
        public static final double DISTANCE_SQUARE_THRESHOLD = 1.0E-5;
        public static final double DISTANCE_PRE_FILTER_LAT_LNG = 0.01;
        public static final double INVALID_LAT_LNG = -1000;
        public static final String GEOCODE_ME = "geocode_me";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriWithPattern(String pattern) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_BY_PATTERN)
                    .appendPath(pattern).build();
        }

        public static Uri buildUriWithDescriptionPattern(String pattern) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_BY_DESCRIPTION_PATTERN)
                    .appendPath(pattern).build();
        }

        public static Uri buildUriWithPatternOrDescription(String pattern) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_BY_PATTERN_OR_DESCRIPTION)
                    .appendPath(pattern).build();
        }

        public static Uri buildUriWithLatLng(double lat, double lng) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_BY_LATLNG)
                    .appendPath(Double.toString(lat))
                    .appendPath(Double.toString(lng))
                    .build();
        }

        public static String getSearchString(Uri uri) {
            // may also be empty
            if (uri.getPathSegments().size()>1){
                return uri.getPathSegments().get(1);
            } else {
                return "";
            }
        }

        public static double getLatitude(Uri uri) {
            if (uri.getPathSegments().size() == 3){
                return Double.parseDouble(uri.getPathSegments().get(1));
            } else {
                return INVALID_LAT_LNG;
            }
        }

        public static double getLongitude(Uri uri) {
            if (uri.getPathSegments().size() == 3){
                return Double.parseDouble(uri.getPathSegments().get(2));
            } else {
                return INVALID_LAT_LNG;
            }
        }

        public static Uri buildValidLatLngGeocodingUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_LATLNG_GEOCODE).build();
        }

        public static Uri buildTextGeocodingUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_TEXT_GEOCODE).build();
        }
    }


    public static final class ProducerEntry implements BaseColumns {
        public static final String TABLE_NAME = "producer";
        public static final String ALIAS = "p";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCER;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriIncLocation(long id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER_WITH_LOCATION)
                    .appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriIncLocationWithPattern(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER_WITH_LOCATION_BY_PATTERN)
                    .appendPath(searchString).build();
        }

        public static String getSearchString(Uri uri) {
            // may also be empty
            if (uri.getPathSegments().size()>1){
                return uri.getPathSegments().get(1);
            } else {
                return "";
            }
        }

        public static Uri buildValidLatLngGeocodingUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCERS_LATLNG_GEOCODE).build();
        }

        public static Uri buildTextGeocodingUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCERS_TEXT_GEOCODE).build();
        }
    }

    public static final class DrinkEntry implements BaseColumns {
        public static final String TABLE_NAME = "drink";
        public static final String ALIAS = "d";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_DRINK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_DRINK;



        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriIncludingProducerAndLocation(long id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER_AND_LOCATION)
                    .appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriIncludingProducer(long id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER)
                    .appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER_BY_NAME).
                    appendPath(searchString).build();
        }

        public static Uri buildUriWithNameAndType(String searchString, String drinkType) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE)
                    .appendPath(drinkType)
                    .appendPath(searchString)
                    .build();
        }

        public static Uri buildUriDrinkOnlyWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_BY_NAME).
                    appendPath(searchString).build();
        }

        public static String getSearchString(Uri uri, boolean withDrink) {
            // may also be empty
            if (withDrink) {
                if (uri.getPathSegments().size()>2){
                    return uri.getPathSegments().get(2);
                } else {
                    return "";
                }
            } else {
                if (uri.getPathSegments().size()>1){
                    return uri.getPathSegments().get(uri.getPathSegments().size()-1);
                } else {
                    return "";
                }
            }
        }

        public static String getDrinkType(Uri uri) {
            // should never be empty - how might that work...?
            if (uri.getPathSegments().size()>1){
                return uri.getPathSegments().get(1);
            } else {
                return "";
            }
        }


    }

    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String ALIAS = "u";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_BY_NAME).
                    appendPath(searchString).build();
        }

        public static String getSearchString(Uri uri) {
            // may also be empty
            if (uri.getPathSegments().size()>1){
                return uri.getPathSegments().get(1);
            } else {
                return "";
            }
        }

    }


    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final String ALIAS = "r";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriForShowReview(int reviewId) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW_WITH_ALL)
                    .appendPath(Long.toString(reviewId)).build();
        }

        public static Uri buildUriForShowReviewWithPatternAndType(String pattern, String type) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW_WITH_ALL_BY_NAME_AND_TYPE)
                    .appendPath(type)
                    .appendPath(pattern)
                    .build();
        }

        public static Uri buildGeocodingUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW_GEOCODE_LOCATION).build();
        }

        public static String getPathString(Uri uri, int number) {
            if (uri != null && uri.getPathSegments().size() > number) {
                return uri.getPathSegments().get(number);
            }
            return "";

        }

        public static String getSearchString(Uri uri, boolean withDrink) {
            // may also be empty
            if (withDrink) {
                if (uri.getPathSegments().size()>2){
                    return uri.getPathSegments().get(2);
                } else {
                    return "";
                }
            } else {
                if (uri.getPathSegments().size()>1){
                    return uri.getPathSegments().get(uri.getPathSegments().size()-1);
                } else {
                    return "";
                }
            }
        }

        public static String getDrinkType(Uri uri) {
            // should never be empty - how might that work...?
            if (uri.getPathSegments().size()>1){
                return uri.getPathSegments().get(1);
            } else {
                return "";
            }
        }

        public static Uri getReviewLocationsUriFromMainFragmentReviewsUri(Uri src) {
            if (src != null) {
                String s = src.getPathSegments().get(0) + "_map_locations";

                Uri.Builder uriBuilder = BASE_CONTENT_URI.buildUpon().appendPath(s);
                for (int i = 1; i < src.getPathSegments().size(); ++i) {
                    uriBuilder.appendPath(src.getPathSegments().get(i));
                }

                Uri uri = uriBuilder.build();
                Log.v(LOG_TAG, "calcMainFragmentReviewsToReviewLocationsForMap, src = [" + src + "], uri=" + uri + "]");

                return uri;
            } else {
                return null;
            }
        }

        public static Uri getReviewsOfLocationUriFromMapUri(Uri src, int reviewLocation_Id) {
            if (src == null) {
                return null;
            }

            String s = src.getPathSegments().get(0);
            s = s.replace("_map_locations", "_map_reviews_of_location");
            Uri.Builder builder = BASE_CONTENT_URI.buildUpon().appendPath(s);
            for (int i = 1; i < src.getPathSegments().size(); ++i) {
                builder.appendPath(src.getPathSegments().get(i));
            }
            builder.appendPath(String.valueOf(reviewLocation_Id));

            Log.v(LOG_TAG, "getReviewsOfLocationUriFromMapUri, src= [" + src + "], reviewLocation_Id = ["
                    + reviewLocation_Id + "], uri = [" + builder.build() + "]");
            return builder.build();
        }
    }

    public static int getIdFromUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }

}
