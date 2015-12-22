package com.example.fbartnitzek.tasteemall.data;

import android.content.ContentResolver;
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

    public static final String PATH_LOCATION = "location";
    public static final String PATH_BREWERY = "brewery";
    public static final String PATH_BEER = "beer";
    public static final String PATH_USER = "user";
    public static final String PATH_REVIEW = "review";

//    public static final String PATH_SCORE_WITH_TEAMS = "scores_with_teams";

//    //URI data
    public static final String CONTENT_AUTHORITY = "fbartnitzek.tasteemall";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Locations
    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }

    public static final class BreweryEntry implements BaseColumns {
        public static final String TABLE_NAME = "brewery";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BREWERY).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWERY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWERY;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }

    public static final class BeerEntry implements BaseColumns {
        public static final String TABLE_NAME = "beer";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_BEER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_BEER;



        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }

    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
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

}
