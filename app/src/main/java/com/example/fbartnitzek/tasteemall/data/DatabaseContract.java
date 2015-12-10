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
//    public static final String PATH_SCORE_WITH_TEAMS = "scores_with_teams";

    //URI data
    public static final String CONTENT_AUTHORITY = "fbartnitzek.tasteemall";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Locations
    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";
//        public static final String ALIAS_HOME = " team_home";
//        public static final String ALIAS_AWAY = " team_away";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_LOCATION;

        // id string for query, f.e. http://api.football-data.org/alpha/teams/556
        public static final String LOCATION_ID_COL = "location_id";
        public static final String NAME_COL = "location_name";
        public static final String COUNTRY_COL = "location_country";
        public static final String LOCATION_LONGITUDE = "location_longitude";
        public static final String LOCATION_LATITUDE = "location_latitude";
        public static final String POSTAL_CODE = "location_postal_code";

//        public static Uri buildTeamUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
    }

    //TODO Breweries
//    public static final class

}
