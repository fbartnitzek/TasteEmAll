package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

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

    public static final String PATH_PRODUCER = "producer";
    public static final String PATH_PRODUCER_BY_NAME = "producer_by_name";
    public static final String PATH_PRODUCER_BY_PATTERN = "producer_by_pattern";
    public static final String PATH_DRINK_BY_NAME = "drink_by_name";
    public static final String PATH_DRINK = "drink";
    public static final String PATH_DRINK_WITH_PRODUCER = "drink_with_producer";
    public static final String PATH_DRINK_WITH_PRODUCER_BY_NAME = "drink_with_producer_by_name";
    public static final String PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE = "drink_with_producer_by_name_and_type";

    public static final String PATH_REVIEW = "review";
    public static final String PATH_REVIEW_WITH_ALL = "review_with_all";
    public static final String PATH_REVIEW_WITH_ALL_BY_NAME_AND_TYPE = "review_with_all_by_name_and_type";
    public static final String PATH_REVIEW_GEOCODE_LOCATION = "review_geocode_locations";

//    //URI data
    public static final String CONTENT_AUTHORITY = "fbartnitzek.tasteemall";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


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



        public static Uri buildUriWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER_BY_NAME).
                    appendPath(searchString).build();
        }
        public static Uri buildUriWithPattern(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER_BY_PATTERN).
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

        public static Uri buildUriIncludingProducer(long id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER)
                    .appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER_BY_NAME).
                    appendPath(searchString).build();
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

        public static Uri buildUriWithNameAndType(String searchString, String drinkType) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER_BY_NAME_AND_TYPE)
                    .appendPath(drinkType)
                    .appendPath(searchString)
                    .build();
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

    public static int getIdFromUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }

}
