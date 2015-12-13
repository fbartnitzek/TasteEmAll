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

        public static final String LOCATION_ID = "location_id";
        public static final String NAME = "location_name";
        public static final String COUNTRY = "location_country";
        public static final String LOCATION_LONGITUDE = "location_longitude";
        public static final String LOCATION_LATITUDE = "location_latitude";
        public static final String POSTAL_CODE = "location_postal_code";

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

        public static final String BREWERY_ID = "brewery_id";
        public static final String NAME = "brewery_name";
        public static final String INTRODUCED = "brewery_introduced"; //wikipedia-style for absolute age of a brewery
        public static final String WEBSITE = "brewery_website";

        public static final String LOCATION_ID = "brewery_location_id";

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

        public static final String BEER_ID = "beer_id";
        public static final String NAME = "beer_name";
        public static final String ABV = "beer_abv"; //alcohol by volume
        // https://de.wikipedia.org/wiki/Stammw%C3%BCrze#Grad_Plato
        public static final String DEGREES_PLATO = "beer_plato"; //formula between plato and Stammwuerze exists
        public static final String STAMMWUERZE = "beer_stammwuerze";// but confusing - for now 2 optional values...
        // Stammwuerze / EBC / plato / SRM ...
        //https://en.wikipedia.org/wiki/Beer_measurement
        public static final String STYLE = "beer_style";
        public static final String IBU = "beer_ibu";    // international bitterness unit
        public static final String BREWERY_ID = "beer_brewery_id";

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

        public static final String USER_ID = "user_id";
        public static final String LOGIN = "user_login";
        public static final String NAME = "user_name";
        public static final String EMAIL = "user_eamil";
        public static final String HOME_LOCATION_ID = "user_home_location_id";

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

        public static final String REVIEW_ID = "review_id";
        public static final String RATING = "review_rating";
        public static final String DESCRIPTION = "review_description";
        public static final String LOOK = "review_look";    // bottle-design, color, foam, ...
        public static final String SMELL = "review_smell";  // ipa :-)
        public static final String TASTE = "review_taste";  // taste = antrunk, frische, abgang - bitter/malzig/...
        public static final String TIMESTAMP = "review_timestamp";

        public static final String USER_ID = "review_user_id";
        public static final String BEER_ID = "review_beer_id";
        public static final String LOCATION_ID = "review_location_id";

        public static Uri buildUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }

}
