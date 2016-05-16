package com.fbartnitzek.tasteemall.tasks;

import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseContract.*;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;

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

    // TODO: remove unused...
    public static class MainFragment {

        public static class ProducerQuery{

            public static final String[] COLUMNS = {
                    ProducerEntry.TABLE_NAME + "." +  ProducerEntry._ID,
                    Producer.NAME,
                    Producer.DESCRIPTION,
                    Producer.LOCATION};

            public static final int COL_QUERY_PRODUCER__ID = 0;
            public static final int COL_QUERY_PRODUCER_NAME = 1;
            public static final int COL_QUERY_PRODUCER_DESCRIPTION = 2;
            public static final int COL_QUERY_PRODUCER_LOCATION = 3;
        }

        public static class DrinkWithProducerQuery {
            public static final String[] COLUMNS = {
                    DrinkEntry.TABLE_NAME + "." +  DrinkEntry._ID,
                    Drink.NAME,
                    Drink.PRODUCER_ID,
                    Drink.TYPE,
                    Drink.SPECIFICS,
                    Drink.STYLE,
                    Producer.NAME,
                    Producer.LOCATION};

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
                    ReviewEntry.ALIAS + "." +  ReviewEntry._ID,
                    Review.RATING,
                    Review.READABLE_DATE,
                    Drink.NAME,
                    Drink.TYPE,
                    Producer.NAME};

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
                    Review.LOCATION
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_LOCATION = 1;
        }
    }

    public static class ReviewFragment {

        public static class CompletionQuery {

            public static final String[] COLUMNS = {
                    DrinkEntry.TABLE_NAME + "." +  DrinkEntry._ID,
                    Drink.NAME,
                    Drink.DRINK_ID,
                    Drink.TYPE,
                    Producer.NAME};

            public static final int COL_DRINK__ID = 0;
            public static final int COL_DRINK_NAME = 1;
            public static final int COL_DRINK_ID = 2;
            public static final int COL_DRINK_TYPE = 3;
            public static final int COL_PRODUCER_NAME = 4;
        }


        public static class ShowQuery {

            public static final String[] COLUMNS = {
                    ReviewEntry.ALIAS + "." + ReviewEntry._ID,
                    Review.USER_NAME,
                    Review.RATING,
                    Review.DESCRIPTION,
                    Review.READABLE_DATE,
                    Review.LOCATION,
                    Review.RECOMMENDED_SIDES,
                    DrinkEntry.ALIAS + "." + DrinkEntry._ID,
                    Drink.NAME,
                    Drink.TYPE,
                    Drink.STYLE,
                    Drink.SPECIFICS,
                    Drink.INGREDIENTS,
                    ProducerEntry.ALIAS + "." + ProducerEntry._ID,
                    Producer.NAME,
                    Producer.LOCATION,
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_USER_NAME = 1;
            public static final int COL_REVIEW_RATING = 2;
            public static final int COL_REVIEW_DESCRIPTION = 3;
            public static final int COL_REVIEW_READABLE_DATE = 4;
            public static final int COL_REVIEW_LOCATION = 5;
            public static final int COL_REVIEW_RECOMMENDED_SIDES = 6;
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
                    ReviewEntry.ALIAS + "." + ReviewEntry._ID,
                    Review.REVIEW_ID,
                    Review.USER_NAME,
                    Review.RATING,
                    Review.DESCRIPTION,
                    Review.READABLE_DATE,
                    Review.LOCATION,
                    Review.RECOMMENDED_SIDES,
                    DrinkEntry.ALIAS + "." + DrinkEntry._ID,
                    Drink.DRINK_ID,
                    Drink.NAME,
                    Drink.TYPE,
                    Producer.NAME
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_ID = 1;
            public static final int COL_REVIEW_USER_NAME = 2;
            public static final int COL_REVIEW_RATING = 3;
            public static final int COL_REVIEW_DESCRIPTION = 4;
            public static final int COL_REVIEW_READABLE_DATE = 5;
            public static final int COL_REVIEW_LOCATION = 6;
            public static final int COL_REVIEW_RECOMMENDED_SIDES = 7;
            public static final int COL_DRINK__ID = 8;
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
                    DatabaseContract.DrinkEntry.TABLE_NAME + "." + DatabaseContract.DrinkEntry._ID,  // without the CursorAdapter doesn't work
                    Drink.NAME,
                    Drink.DRINK_ID,
                    Drink.TYPE,
                    Drink.SPECIFICS,
                    Drink.STYLE,
                    Drink.INGREDIENTS,
                    Producer.PRODUCER_ID,
                    Producer.NAME,
                    Producer.LOCATION};

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


        public static class EditQuery {

            public static final String[] COLUMNS = {
                    DatabaseContract.DrinkEntry.TABLE_NAME + "." + DatabaseContract.DrinkEntry._ID,  // without the CursorAdapter doesn't work
                    Drink.NAME,
                    Drink.DRINK_ID,
                    Drink.TYPE,
                    Drink.SPECIFICS,
                    Drink.STYLE,
                    Drink.INGREDIENTS,
                    Producer.PRODUCER_ID,
                    Producer.NAME,
                    Producer.LOCATION};

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

        public static final String[] PRODUCER_QUERY_COLUMNS = {
                DatabaseContract.ProducerEntry.TABLE_NAME + "." +  DatabaseContract.ProducerEntry._ID,
                Producer.NAME,
                Producer.LOCATION,
                Producer.PRODUCER_ID};

        public static final int COL_QUERY_PRODUCER__ID = 0;
        public static final int COL_QUERY_PRODUCER_NAME = 1;
        public static final int COL_QUERY_PRODUCER_LOCATION = 2;
        public static final int COL_QUERY_PRODUCER_ID = 3;
    }

    public static class ProducerFragment {

        public static final String[] DETAIL_COLUMNS = {
                DatabaseContract.ProducerEntry.TABLE_NAME + "." + DatabaseContract.ProducerEntry._ID,
                Producer.PRODUCER_ID,
                Producer.NAME,
                Producer.DESCRIPTION,
                Producer.WEBSITE,
                Producer.LOCATION
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
                    Review.USER_NAME,
                    Review.READABLE_DATE,
                    Review.LOCATION,
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
                    Producer.LOCATION,
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

        public static class Reviews {

            public static final String[] COLUMNS = {
                    ReviewEntry.ALIAS + "." + ReviewEntry._ID,
                    Review.USER_NAME,
                    Review.RATING,
                    Review.DESCRIPTION,
                    Review.READABLE_DATE,
                    Review.LOCATION,
                    DrinkEntry.ALIAS + "." + DrinkEntry._ID,
                    Drink.NAME,
                    Drink.TYPE,
                    Drink.STYLE,
                    ProducerEntry.ALIAS + "." + ProducerEntry._ID,
                    Producer.NAME,
                    Producer.LOCATION,
            };

            public static final int COL_REVIEW__ID = 0;
            public static final int COL_REVIEW_USER_NAME = 1;
            public static final int COL_REVIEW_RATING = 2;
            public static final int COL_REVIEW_DESCRIPTION = 3;
            public static final int COL_REVIEW_READABLE_DATE = 4;
            public static final int COL_REVIEW_LOCATION = 5;
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

        public static class ReviewQuery {

            public static final String[] COLUMNS = {
                    ReviewEntry._ID
            };
            public static final int COL_REVIEW__ID = 0;
        }
    }
}
