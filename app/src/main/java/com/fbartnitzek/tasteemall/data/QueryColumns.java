package com.fbartnitzek.tasteemall.data;

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

/**
 * Copyright 2016.  Frank Bartnitzek
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

public class QueryColumns {

    private static final String RA = ReviewEntry.ALIAS;   //ReviewAlias
    private static final String UA = UserEntry.ALIAS;   //UserAlias
    private static final String DA = DrinkEntry.ALIAS;   //DrinkAlias
    private static final String PA = ProducerEntry.ALIAS;   //ProducerAlias
    private static final String LAR = LocationEntry.ALIAS_REVIEW;   //LocationAlias for ReviewsOld

    // TODO: remove unused...
    public static class MainFragment {

        public static class LocationQuery{

            public static final String[] COLUMNS = {
                    LocationEntry._ID,
                    Location.DESCRIPTION,
                    Location.FORMATTED_ADDRESS,
                    Location.COUNTRY};

            public static final int COL_QUERY_LOCATION__ID = 0;
            public static final int COL_QUERY_LOCATION_DESCRIPTION = 1;
            public static final int COL_QUERY_LOCATION_FORMATTED = 2;
            public static final int COL_QUERY_LOCATION_COUNTRY = 3;
        }

        public static class ProducerQuery{

            public static final String[] COLUMNS = {
                   ProducerEntry._ID,
                   Producer.NAME,
                   Producer.DESCRIPTION,
                   Producer.FORMATTED_ADDRESS,
                   Producer.COUNTRY};

            public static final int COL_QUERY_PRODUCER__ID = 0;
            public static final int COL_QUERY_PRODUCER_NAME = 1;
            public static final int COL_QUERY_PRODUCER_DESCRIPTION = 2;
            public static final int COL_QUERY_PRODUCER_LOCATION = 3;
            public static final int COL_QUERY_PRODUCER_COUNTRY = 4;
        }

        public static class DrinkWithProducerQuery {
            public static final String[] COLUMNS = {
                    DA + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.PRODUCER_ID,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.SPECIFICS,
                    DA + "." + Drink.STYLE,
                    PA + "." + Producer.NAME};

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_QUERY_DRINK_PRODUCER_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_QUERY_DRINK_SPECIFICS= 4;
            public static final int COL_DRINK_STYLE = 5;
            public static final int COL_PRODUCER_NAME = 6;
        }

        public static class ReviewAllQuery{
            public static final String[] COLUMNS = {
                    RA + "." + ReviewEntry._ID,
                    RA + "." + Review.RATING,
                    RA + "." + Review.READABLE_DATE,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,
                    PA + "." + Producer.NAME};

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_RATING = 1;
            public static final int COL_REVIEW_READABLE_DATE= 2;
            public static final int COL_DRINK_NAME = 3;
            public static final int COL_DRINK_TYPE = 4;
            public static final int COL_PRODUCER_NAME = 5;

        }

        public static class UserQuery {
            public static final String[] COLUMNS = {
                    UserEntry.TABLE_NAME + "." + UserEntry._ID,
                    User.USER_ID,
                    User.NAME
            };

            public static final int COL__ID = 0;
            public static final int COL_ID = COL__ID + 1;
            public static final int COL_NAME = COL_ID + 1;
        }

        public static class GeocodingQuery {
            public static final String[] COLUMNS = {
                    ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID,
                    Review.LOCATION_ID  //TODO
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_LOCATION = 1;
        }
    }

    public static class LocationFragment {
        public static class ShowQuery {
            public static final String[] COLUMNS = LocationPart.CompletionQuery.COLUMNS;

            public static final int COL__ID = 0;
            public static final int COL_ID = 1;
            public static final int COL_INPUT = 2;
            public static final int COL_FORMATTED_ADDRESS = 3;
            public static final int COL_COUNTRY = 4;
            public static final int COL_LATITUDE = 5;
            public static final int COL_LONGITUDE = 6;
            public static final int COL_DESCRIPTION= 7;
        }
    }

    public static class LocationPart {

        public static class CompletionQuery {

            public static final String[] COLUMNS = {
                    LocationEntry._ID,
                    Location.LOCATION_ID,
                    Location.INPUT,
                    Location.FORMATTED_ADDRESS,
                    Location.COUNTRY,
                    Location.LATITUDE,
                    Location.LONGITUDE,
                    Location.DESCRIPTION
            };

            public static final int COL__ID = 0;
            public static final int COL_ID = 1;
            public static final int COL_INPUT = 2;
            public static final int COL_FORMATTED_ADDRESS = 3;
            public static final int COL_COUNTRY = 4;
            public static final int COL_LATITUDE = 5;
            public static final int COL_LONGITUDE = 6;
            public static final int COL_DESCRIPTION= 7;
        }

//        public static class Geocoder {
//
//            public static final String[] COLUMNS = {
//                    LocationEntry.TABLE_NAME + "." + LocationEntry._ID,
//                    Location.LOCATION_ID,
//                    Location.INPUT,
//                    Location.FORMATTED_ADDRESS,
//                    Location.COUNTRY,
//                    Location.LATITUDE,
//                    Location.LONGITUDE
//            };
//
//            public static final int COL_LOCATION__ID = 0;
//            public static final int COL_LOCATION_ID = 1;
//            public static final int COL_LOCATION_INPUT = 2;
//            public static final int COL_LOCATION_FORMATTED_ADDRESS = 3;
//            public static final int COL_LOCATION_COUNTRY = 4;
//            public static final int COL_LOCATION_LATITUDE = 5;
//            public static final int COL_LOCATION_LONGITUDE = 6;
//        }
    }

    public static class ReviewFragment {

        public static class DrinkCompletionQuery {

            public static final String[] COLUMNS = {
                    DA + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.DRINK_ID,
                    DA + "." + Drink.TYPE,
                    PA + "." + Producer.NAME,
                    PA + "." + Producer.LATITUDE,
                    PA + "." + Producer.LONGITUDE,
                    PA + "." + Producer.COUNTRY,
                    PA + "." + Producer.INPUT,
                    PA + "." + Producer.FORMATTED_ADDRESS};

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_DRINK_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_PRODUCER_NAME = 4;
            public static final int COL_PRODUCER_LOCATION_LATITUDE = 5;
            public static final int COL_PRODUCER_LOCATION_LONGITUDE = 6;
            public static final int COL_PRODUCER_COUNTRY = 7;
            public static final int COL_PRODUCER_LOCATION_INPUT = 8;
            public static final int COL_PRODUCER_LOCATION_FORMATTED = 9;
        }

        public static class UserQuery {

            public static final String[] COLUMNS = {
                    UserEntry.TABLE_NAME + "." +  UserEntry._ID,
                    User.NAME,
                    User.USER_ID};

            public static final int COL_USER__ID = 0;
            public static final int COL_USER_NAME = 1;
            public static final int COL_USER_ID = 2;
        }


        public static class ShowQuery {

            public static final String[] COLUMNS = {
                    RA + "." + ReviewEntry._ID,
                    RA + "." + Review.RATING,
                    RA + "." + Review.DESCRIPTION,
                    RA + "." + Review.READABLE_DATE,
                    RA + "." + Review.RECOMMENDED_SIDES,

                    LAR + "." + LocationEntry._ID,
                    LAR + "." + Location.FORMATTED_ADDRESS,
                    LAR + "." + Location.DESCRIPTION,

                    UA + "." + User.NAME,

                    DA + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.STYLE,
                    DA + "." + Drink.SPECIFICS,
                    DA + "." + Drink.INGREDIENTS,

                    PA + "." + ProducerEntry._ID,
                    PA + "." + Producer.NAME,
                    PA + "." + Producer.FORMATTED_ADDRESS
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_RATING = 1;
            public static final int COL_REVIEW_DESCRIPTION = 2;
            public static final int COL_REVIEW_READABLE_DATE = 3;
            public static final int COL_REVIEW_RECOMMENDED_SIDES = 4;

            public static final int COL_REVIEW_LOCATION__ID = COL_REVIEW_RECOMMENDED_SIDES + 1;
            public static final int COL_REVIEW_LOCATION_FORMATTED = COL_REVIEW_LOCATION__ID + 1;
            public static final int COL_REVIEW_LOCATION_DESCRIPTION = COL_REVIEW_LOCATION_FORMATTED + 1;


            public static final int COL_USER_NAME = COL_REVIEW_LOCATION_DESCRIPTION + 1;
            public static final int COL_DRINK__ID = COL_USER_NAME + 1;
            public static final int COL_DRINK_NAME = COL_DRINK__ID + 1;
            public static final int COL_DRINK_TYPE = COL_DRINK_NAME + 1;
            public static final int COL_DRINK_STYLE = COL_DRINK_TYPE + 1;
            public static final int COL_DRINK_SPECIFICS = COL_DRINK_STYLE + 1;
            public static final int COL_DRINK_INGREDIENTS = COL_DRINK_SPECIFICS + 1;
            public static final int COL_PRODUCER__ID = COL_DRINK_INGREDIENTS + 1;
            public static final int COL_PRODUCER_NAME = COL_PRODUCER__ID + 1;
            public static final int COL_PRODUCER_LOCATION = COL_PRODUCER_NAME + 1;
        }

        public static class EditQuery {

            public static final String[] COLUMNS = {
                    RA + "." + ReviewEntry._ID,
                    RA + "." + Review.REVIEW_ID,
                    RA + "." + Review.RATING,
                    RA + "." + Review.DESCRIPTION,
                    RA + "." + Review.READABLE_DATE,
                    RA + "." + Review.RECOMMENDED_SIDES,

                    RA + "." + Review.USER_ID,
                    UA + "." + User.NAME,

                    RA + "." + Review.LOCATION_ID, //TODO
                    LAR + "." + Location.FORMATTED_ADDRESS,
                    LAR + "." + Location.LATITUDE,
                    LAR + "." + Location.LONGITUDE,
                    LAR + "." + Location.DESCRIPTION,

                    DA + "." + Drink.DRINK_ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,

                    PA + "." + Producer.NAME
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_ID = 1;
            public static final int COL_REVIEW_RATING = 2;
            public static final int COL_REVIEW_DESCRIPTION = 3;
            public static final int COL_REVIEW_READABLE_DATE = 4;
            public static final int COL_REVIEW_RECOMMENDED_SIDES = 5;

            public static final int COL_USER_ID = 6;
            public static final int COL_USER_NAME = 7;

            public static final int COL_REVIEW_LOCATION_ID = 8;
            public static final int COL_REVIEW_LOCATION_FORMATTED = 9;
            public static final int COL_REVIEW_LOCATION_LATITUDE = 10;
            public static final int COL_REVIEW_LOCATION_LONGITUDE = 11;
            public static final int COL_REVIEW_LOCATION_DESCRIPTION= 12;

            public static final int COL_DRINK_ID = 13;
            public static final int COL_DRINK_NAME = 14;
            public static final int COL_DRINK_TYPE = 15;

            public static final int COL_PRODUCER_NAME = 16;
        }

    }

    //TODO: more refactoring
    public static class DrinkFragment {

        public static class ShowQuery {

            public static final String[] COLUMNS = {
                    DA + "." + DrinkEntry._ID,  // without the CursorAdapter doesn't work
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.DRINK_ID,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.SPECIFICS,
                    DA + "." + Drink.STYLE,
                    DA + "." + Drink.INGREDIENTS,

                    PA + "." + ProducerEntry._ID,
                    PA + "." + Producer.PRODUCER_ID,
                    PA + "." + Producer.NAME,
                    PA + "." + Producer.COUNTRY,
                    PA + "." + Producer.FORMATTED_ADDRESS
            };

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_DRINK_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_DRINK_SPECIFICS = 4;
            public static final int COL_DRINK_STYLE = 5;
            public static final int COL_DRINK_INGREDIENTS = 6;
            public static final int COL_PRODUCER__ID = 7;
            public static final int COL_PRODUCER_ID = 8;
            public static final int COL_PRODUCER_NAME = 9;
            public static final int COL_PRODUCER_COUNTRY = 10;
            public static final int COL_PRODUCER_LOCATION = 11;
        }


        public static class EditQuery {

            public static final String[] COLUMNS = {
                    DA + "." + DrinkEntry._ID,  // without the CursorAdapter doesn't work
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.DRINK_ID,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.SPECIFICS,
                    DA + "." + Drink.STYLE,
                    DA + "." + Drink.INGREDIENTS,
                    PA + "." + Producer.PRODUCER_ID,
                    PA + "." + Producer.NAME};

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_DRINK_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_DRINK_SPECIFICS = 4;
            public static final int COL_DRINK_STYLE = 5;
            public static final int COL_DRINK_INGREDIENTS = 6;
            public static final int COL_PRODUCER_ID = 7;
            public static final int COL_PRODUCER_NAME = 8;
        }


        public static class ProducerCompletionQuery {

            public static final String[] COLUMNS = {
                    ProducerEntry._ID,
                    Producer.NAME,
                    Producer.PRODUCER_ID,
                    Producer.FORMATTED_ADDRESS,
                    Producer.COUNTRY
            };

            public static final int COL_PRODUCER__ID = 0;
            public static final int COL_PRODUCER_NAME = 1;
            public static final int COL_PRODUCER_ID = 2;
            public static final int COL_PRODUCER_LOCATION = 3;
            public static final int COL_PRODUCER_COUNTRY = 4;

        }

    }

    public static class ProducerFragment {

        public static class ShowQuery {

            public static final String[] COLUMNS = {
                    ProducerEntry._ID,  // without the CursorAdapter doesn't work
                    Producer.PRODUCER_ID,
                    Producer.NAME,
                    Producer.DESCRIPTION,
                    Producer.WEBSITE,

                    Producer.COUNTRY,
                    Producer.FORMATTED_ADDRESS,
                    Producer.LATITUDE,
                    Producer.LONGITUDE,
                    Producer.INPUT
            };

            public static final int COL_PRODUCER__ID = 0;
            public static final int COL_PRODUCER_ID = 1;
            public static final int COL_PRODUCER_NAME = 2;
            public static final int COL_PRODUCER_DESCRIPTION = 3;
            public static final int COL_PRODUCER_WEBSITE = 4;

            public static final int COL_PRODUCER_COUNTRY = 5;
            public static final int COL_PRODUCER_FORMATTED_ADDRESS = 6;
            public static final int COL_PRODUCER_LATITUDE = 7;
            public static final int COL_PRODUCER_LONGITUDE = 8;
            public static final int COL_PRODUCER_INPUT = 9;
        }
    }


    public static class Geocoder {

        public static class Producers {
            public static final String[] COLUMNS = {
                    ProducerEntry.TABLE_NAME + "." + ProducerEntry._ID,
                    Producer.PRODUCER_ID,
                    Producer.INPUT,
                    Producer.FORMATTED_ADDRESS,
                    Producer.COUNTRY,
                    Producer.LATITUDE,
                    Producer.LONGITUDE
            };

            public static final int COL_PRODUCER__ID = 0;
            public static final int COL_PRODUCER_ID = 1;
            public static final int COL_PRODUCER_INPUT = 2;
            public static final int COL_PRODUCER_FORMATTED_ADDRESS = 3;
            public static final int COL_PRODUCER_COUNTRY = 4;
            public static final int COL_PRODUCER_LATITUDE = 5;
            public static final int COL_PRODUCER_LONGITUDE = 6;
        }

        public static class Locations {
            public static final String[] COLUMNS = {
                    LocationEntry.TABLE_NAME + "." + LocationEntry._ID,
                    Location.LOCATION_ID,
                    Location.INPUT,
                    Location.FORMATTED_ADDRESS,
                    Location.COUNTRY,
                    Location.LATITUDE,
                    Location.LONGITUDE
            };

            public static final int COL_LOCATION__ID = 0;
            public static final int COL_LOCATION_ID = 1;
            public static final int COL_LOCATION_INPUT = 2;
            public static final int COL_LOCATION_FORMATTED_ADDRESS = 3;
            public static final int COL_LOCATION_COUNTRY = 4;
            public static final int COL_LOCATION_LATITUDE = 5;
            public static final int COL_LOCATION_LONGITUDE = 6;
        }
    }

    public static class ExportAndImport {
        public static class ReviewColumns {
            public static final String[] COLUMNS = {
                    Review.REVIEW_ID,
                    Review.USER_ID,
                    Review.READABLE_DATE,
                    Review.LOCATION_ID,
                    Review.RATING,
                    Review.DESCRIPTION,
                    Review.RECOMMENDED_SIDES,
                    Review.DRINK_ID
            };
        }

        public static class UserColumns {
            public static final String[] COLUMNS = {
                    User.USER_ID,
                    User.NAME
            };
        }

        public static class DrinkColumns {
            public static final String[] COLUMNS = {
                    Drink.DRINK_ID,
                    Drink.NAME,
                    Drink.SPECIFICS,
                    Drink.STYLE,
                    Drink.TYPE,
                    Drink.INGREDIENTS,
                    Drink.PRODUCER_ID
            };
        }

        public static class ProducerColumns {
            public static final String[] COLUMNS = {
                    Producer.PRODUCER_ID,
                    Producer.NAME,
                    Producer.DESCRIPTION,
                    Producer.WEBSITE,
                    Producer.INPUT,
                    Producer.COUNTRY,
                    Producer.FORMATTED_ADDRESS,
                    Producer.LATITUDE,
                    Producer.LONGITUDE
            };
        }

        public static class LocationColumns {
            public static final String[] COLUMNS = {
                    Location.LOCATION_ID,
                    Location.INPUT,
                    Location.LATITUDE,
                    Location.LONGITUDE,
                    Location.COUNTRY,
                    Location.FORMATTED_ADDRESS,
                    Location.DESCRIPTION
            };
        }
    }

    public static class ImportOldFormat {
        public static class ReviewColumns {
            public static final String[] COLUMNS = {
                    Review.REVIEW_ID,
                    "review_user_name",
                    Review.READABLE_DATE,
                    "review_location",
                    Review.RATING,
                    Review.DESCRIPTION,
                    Review.RECOMMENDED_SIDES,
                    Review.DRINK_ID
            };

            public static final int COL_REVIEW_ID = 0;
            public static final int COL_USER_NAME = 1;
            public static final int COL_READABLE_DATE = 2;
            public static final int COL_LOCATION = 3;
            public static final int COL_RATING = 4;
            public static final int COL_DESCRIPTION = 5;
            public static final int COL_RECOMMNEDED_SIDES = 6;
            public static final int COL_DRINK_ID = 7;


        }

        public static class DrinkColumns {
            public static final String[] COLUMNS = {
                    Drink.DRINK_ID,
                    Drink.NAME,
                    Drink.SPECIFICS,
                    Drink.STYLE,
                    Drink.TYPE,
                    Drink.INGREDIENTS,
                    Drink.PRODUCER_ID
            };

            public static final int COL_DRINK_ID = 0;
            public static final int COL_NAME = 1;
            public static final int COL_SPECIFICS = 2;
            public static final int COL_STYLE = 3;
            public static final int COL_TYPE = 4;
            public static final int COL_INGREDIENTS = 5;
            public static final int COL_PRODUCER_ID= 6;
        }

        public static class ProducerColumns {
            public static final String[] COLUMNS = {
                    Producer.PRODUCER_ID,
                    Producer.NAME,
                    "producer_location",
                    Producer.DESCRIPTION,
                    Producer.WEBSITE
            };

            public static final int COL_PRODUCER_ID = 0;
            public static final int COL_NAME= 1;
            public static final int COL_LOCATION = 2;
            public static final int COL_DESCRIPTION = 3;
            public static final int COL_WEBSITE = 4;
        }
    }

    public static class MapFragment {

        public static class ReviewLocations {
            public static final String[] COLUMNS = {
                    "DISTINCT " + LAR + "." + LocationEntry._ID,
//                    LAR + "." + LocationEntry._ID,
                    LAR + "." + Location.LOCATION_ID,
                    LAR + "." + Location.LATITUDE,
                    LAR + "." + Location.LONGITUDE,
                    LAR + "." + Location.COUNTRY,
                    LAR + "." + Location.FORMATTED_ADDRESS,
                    LAR + "." + Location.DESCRIPTION
            };


            public static final int COL_REVIEW_LOCATION__ID = 0;
            public static final int COL_REVIEW_LOCATION_ID = COL_REVIEW_LOCATION__ID + 1;
            public static final int COL_REVIEW_LOCATION_LAT = COL_REVIEW_LOCATION_ID + 1;
            public static final int COL_REVIEW_LOCATION_LON = COL_REVIEW_LOCATION_LAT + 1;
            public static final int COL_COUNTRY = COL_REVIEW_LOCATION_LON + 1;
            public static final int COL_FORMATTED = COL_COUNTRY + 1;
            public static final int COL_DESCRIPTION = COL_FORMATTED + 1;

        }

        public static class ReviewsOfLocationQuery{
            public static final String[] COLUMNS = {
                    RA + "." + ReviewEntry._ID,
                    RA + "." + Review.LOCATION_ID,
                    RA + "." + Review.RATING,
                    RA + "." + Review.READABLE_DATE,
                    LAR + "." + LocationEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,
                    PA + "." + Producer.NAME};

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_LOCATION_ID = COL_REVIEW__ID + 1;
            public static final int COL_REVIEW_RATING = COL_REVIEW_LOCATION_ID + 1;
            public static final int COL_REVIEW_READABLE_DATE = COL_REVIEW_RATING + 1;
            public static final int COL_LOCATION__ID = COL_REVIEW_READABLE_DATE + 1;
            public static final int COL_DRINK_NAME = COL_LOCATION__ID + 1;
            public static final int COL_DRINK_TYPE = COL_DRINK_NAME + 1;
            public static final int COL_PRODUCER_NAME = COL_DRINK_TYPE + 1;

        }

        public static class Reviews {

            public static final String[] COLUMNS = {
                    RA + "." + ReviewEntry._ID,
                    RA + "." + Review.RATING,
                    RA + "." + Review.DESCRIPTION,
                    RA + "." + Review.READABLE_DATE,

                    LAR + "." + Location.LATITUDE,
                    LAR + "." + Location.LONGITUDE,
                    LAR + "." + Location.COUNTRY,
                    LAR + "." + Location.FORMATTED_ADDRESS,

                    UA + "." + User.NAME,

                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.STYLE,

                    PA + "." + Producer.NAME,
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_RATING = 1;
            public static final int COL_REVIEW_DESCRIPTION = 2;
            public static final int COL_REVIEW_READABLE_DATE = 3;

            public static final int COL_REVIEW_LOCATION_LAT = COL_REVIEW_READABLE_DATE + 1;
            public static final int COL_REVIEW_LOCATION_LONG = COL_REVIEW_LOCATION_LAT + 1;
            public static final int COL_REVIEW_LOCATION_COUNTRY = COL_REVIEW_LOCATION_LONG + 1;
            public static final int COL_REVIEW_LOCATION_FORMATTED = COL_REVIEW_LOCATION_COUNTRY + 1;

            public static final int COL_USER_NAME = COL_REVIEW_LOCATION_FORMATTED +1;

            public static final int COL_DRINK_NAME = COL_USER_NAME +1;
            public static final int COL_DRINK_TYPE = COL_DRINK_NAME + 1;
            public static final int COL_DRINK_STYLE = COL_DRINK_TYPE + 1;

            public static final int COL_PRODUCER_NAME = COL_DRINK_STYLE + 1;
        }

        public static class ReviewsOld {

            public static final String[] COLUMNS = {
                    RA + "." + ReviewEntry._ID,
                    RA + "." + Review.RATING,
                    RA + "." + Review.DESCRIPTION,
                    RA + "." + Review.READABLE_DATE,
                    RA + "." + Review.LOCATION_ID, //TODO

                    UA + "." + User.NAME,

                    DA + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.STYLE,

                    PA + "." + ProducerEntry._ID,
                    PA + "." + Producer.NAME,
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_RATING = 1;
            public static final int COL_REVIEW_DESCRIPTION = 2;
            public static final int COL_REVIEW_READABLE_DATE = 3;
            public static final int COL_REVIEW_LOCATION = 4;

            public static final int COL_USER_NAME = 5;

            public static final int COL_DRINK__ID = 6;
            public static final int COL_DRINK_NAME = 7;
            public static final int COL_DRINK_TYPE = 8;
            public static final int COL_DRINK_STYLE = 9;

            public static final int COL_PRODUCER__ID = 10;
            public static final int COL_PRODUCER_NAME = 11;
        }
    }

    public static class Widget {

        public static class LocationQuery {
            public static final String[] COLUMNS = {
                    LocationEntry._ID,
            };
        }

        public static class ProviderQuery {

            public static final String[] COLUMNS = {
                    ProducerEntry._ID,
                    Producer.NAME
            };
            public static final int COL_PRODUCER__ID = 0;
            public static final int COL_NAME= 1;
        }

        public static class DrinkQuery {

            public static final String[] COLUMNS = {
                    DrinkEntry._ID,
                    Drink.NAME
            };
            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
        }

        public static class UserQuery {
            public static final String[] COLUMNS = {
                    UserEntry._ID
            };
        }

        public static class ReviewQuery {

            public static final String[] COLUMNS = {
                    ReviewEntry._ID
            };
            public static final int COL_REVIEW__ID = 0;
        }
    }
}
