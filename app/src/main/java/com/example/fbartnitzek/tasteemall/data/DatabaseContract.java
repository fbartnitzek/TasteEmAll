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
    public static final String PATH_DRINK_BY_NAME = "drink_by_name";
    public static final String PATH_DRINK = "drink";
    public static final String PATH_DRINK_WITH_PRODUCER_BY_NAME = "drink_with_producer_by_name";
    public static final String PATH_REVIEW = "review";

//    //URI data
    public static final String CONTENT_AUTHORITY = "fbartnitzek.tasteemall";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class ProducerEntry implements BaseColumns {
        public static final String TABLE_NAME = "producer";
        public static final String PATH_PATTERN = "pattern";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCER;

        public static Uri buildUri(long id) {
//            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriWithName(String searchString) {
//            Log.v(LOG_TAG, "buildUriWithName, " + "searchString = [" + searchString + "]");
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCER_BY_NAME).
                    appendPath(searchString).build();
//            return CONTENT_URI.buildUpon().
////                    appendQueryParameter("pattern", searchString).build();
//                    appendPath(PATH_PATTERN).
//                    appendPath(searchString).build();
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

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_DRINK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_DRINK;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_WITH_PRODUCER_BY_NAME).
                    appendPath(searchString).build();
        }

        public static Uri buildUriDrinkOnlyWithName(String searchString) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRINK_BY_NAME).
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

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;


        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }

    public static int getIdFromUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }

}
