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

    // TODO: remove unused...
    public static class MainFragment {

        public static class ProducerQuery{

            public static final String[] COLUMNS = {
                    ProducerEntry.TABLE_NAME + "." +  ProducerEntry._ID,
                    Producer.NAME,
                    Producer.DESCRIPTION,
                    Producer.LOCATION_ID};  //TODO

            public static final int COL_QUERY_PRODUCER__ID = 0;
            public static final int COL_QUERY_PRODUCER_NAME = 1;
            public static final int COL_QUERY_PRODUCER_DESCRIPTION = 2;
            public static final int COL_QUERY_PRODUCER_LOCATION = 3;
        }

        public static class DrinkWithProducerQuery {
            public static final String[] COLUMNS = {
                    DA + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.PRODUCER_ID,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.SPECIFICS,
                    DA + "." + Drink.STYLE,
                    PA + "." + Producer.NAME,
                    PA + "." + Producer.LOCATION_ID};  //TODO

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_QUERY_DRINK_PRODUCER_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_QUERY_DRINK_SPECIFICS= 4;
            public static final int COL_DRINK_STYLE = 5;
            public static final int COL_PRODUCER_NAME = 6;
            public static final int COL_QUERY_DRINK_PRODUCER_LOCATION= 7;
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

        public static class GeocodingQuery {
            public static final String[] COLUMNS = {
                    ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID,
                    Review.LOCATION_ID  //TODO
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_LOCATION = 1;
        }
    }

    public static class ReviewFragment {

        public static class DrinkCompletionQuery {

            public static final String[] COLUMNS = {
                    DA + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.DRINK_ID,
                    DA + "." + Drink.TYPE,
                    PA + "." + Producer.NAME};

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_DRINK_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_PRODUCER_NAME = 4;
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
                    RA + "." + Review.LOCATION_ID, //TODO
                    RA + "." + Review.RECOMMENDED_SIDES,
                    UA + "." + User.NAME,
                    DA + "." + DrinkEntry.ALIAS + "." + DrinkEntry._ID,
                    DA + "." + Drink.NAME,
                    DA + "." + Drink.TYPE,
                    DA + "." + Drink.STYLE,
                    DA + "." + Drink.SPECIFICS,
                    DA + "." + Drink.INGREDIENTS,
                    PA + "." + ProducerEntry.ALIAS + "." + ProducerEntry._ID,
                    PA + "." + Producer.NAME,
                    PA + "." + Producer.LOCATION_ID,   //TODO
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_RATING = 1;
            public static final int COL_REVIEW_DESCRIPTION = 2;
            public static final int COL_REVIEW_READABLE_DATE = 3;
            public static final int COL_REVIEW_LOCATION = 4;
            public static final int COL_REVIEW_RECOMMENDED_SIDES = 5;
            public static final int COL_USER_NAME = 6;
            public static final int COL_DRINK__ID = 7;
            public static final int COL_DRINK_NAME = 8;
            public static final int COL_DRINK_TYPE = 9;
            public static final int COL_DRINK_STYLE = 10;
            public static final int COL_DRINK_SPECIFICS = 11;
            public static final int COL_DRINK_INGREDIENTS = 12;
            public static final int COL_PRODUCER__ID = 13;
            public static final int COL_PRODUCER_NAME = 14;
            public static final int COL_PRODUCER_LOCATION = 15;
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

            public static final int COL_REVIEW_LOCATION = 8;

            public static final int COL_DRINK_ID = 9;
            public static final int COL_DRINK_NAME = 10;
            public static final int COL_DRINK_TYPE = 11;

            public static final int COL_PRODUCER_NAME = 12;
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
                    PA + "." + Producer.LOCATION_ID};  //TODO

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
            public static final int COL_PRODUCER_LOCATION = 10;
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
                    PA + "." + Producer.NAME,
                    PA + "." + Producer.LOCATION_ID};  //TODO

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_DRINK_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_DRINK_SPECIFICS = 4;
            public static final int COL_DRINK_STYLE = 5;
            public static final int COL_DRINK_INGREDIENTS = 6;
            public static final int COL_PRODUCER_ID = 7;
            public static final int COL_PRODUCER_NAME = 8;
            public static final int COL_PRODUCER_LOCATION = 9;
        }


        public static class ProducerCompletionQuery {

            public static final String[] COLUMNS = {
                    DatabaseContract.ProducerEntry.TABLE_NAME + "." +  DatabaseContract.ProducerEntry._ID,
                    Producer.NAME,
                    Producer.LOCATION_ID,   //TODO
                    Producer.PRODUCER_ID};

            public static final int COL_PRODUCER__ID = 0;
            public static final int COL_PRODUCER_NAME = 1;
            public static final int COL_PRODUCER_LOCATION = 2;
            public static final int COL_PRODUCER_ID = 3;
        }

    }

    public static class ProducerFragment {

        public static final String[] DETAIL_COLUMNS = {
                DatabaseContract.ProducerEntry.TABLE_NAME + "." + DatabaseContract.ProducerEntry._ID,
                Producer.PRODUCER_ID,
                Producer.NAME,
                Producer.DESCRIPTION,
                Producer.WEBSITE,
                Producer.LOCATION_ID    //TODO
        };

        public static final int COL_PRODUCER__ID = 0;
        public static final int COL_PRODUCER_ID = 1;
        public static final int COL_PRODUCER_NAME = 2;
        public static final int COL_PRODUCER_DESCRIPTION = 3;
        public static final int COL_PRODUCER_WEBSITE = 4;
        public static final int COL_PRODUCER_LOCATION = 5;
    }


    public static class ExportAndImport {
        public static class ReviewColumns {
            public static final String[] COLUMNS = {
                    Review.REVIEW_ID,
                    Review.USER_ID, //TODO
                    Review.READABLE_DATE,
                    Review.LOCATION_ID, //TODO
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
                    Producer.LOCATION_ID,
                    Producer.DESCRIPTION,
                    Producer.WEBSITE
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

    public static class MapFragment {

        public static class Reviews {

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
                    PA + "." + Producer.LOCATION_ID,   //TODO
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
            public static final int COL_PRODUCER_LOCATION = 12;
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
