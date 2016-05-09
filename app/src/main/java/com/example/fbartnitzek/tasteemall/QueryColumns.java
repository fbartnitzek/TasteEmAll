package com.example.fbartnitzek.tasteemall;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;
import com.example.fbartnitzek.tasteemall.data.pojo.Review;

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
                    DatabaseContract.ProducerEntry.TABLE_NAME + "."
                            +  DatabaseContract.ProducerEntry._ID,
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
                    DatabaseContract.DrinkEntry.TABLE_NAME + "."
                            +  DatabaseContract.DrinkEntry._ID,
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
                    DatabaseContract.ReviewEntry.ALIAS + "."
                            +  DatabaseContract.ReviewEntry._ID,
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
    }

    public static class ReviewFragment {
        //TODO: more refactoring
        public static final String[] DRINK_QUERY_COLUMNS = {
                DatabaseContract.DrinkEntry.TABLE_NAME + "." +  DatabaseContract.DrinkEntry._ID,
                Drink.NAME,
                Drink.DRINK_ID,
                Drink.TYPE,
                Drink.PRODUCER_ID,
                Producer.NAME,
                Producer.LOCATION,
                Producer.PRODUCER_ID};
        // drink.producerId and producer.producerId might be useless - TODO: test later

        public static final int COL_QUERY_DRINK__ID = 0;
        public static final int COL_QUERY_DRINK_NAME = 1;
        public static final int COL_QUERY_DRINK_ID = 2;
        static final int COL_QUERY_DRINK_TYPE = 3;
        static final int COL_QUERY_PRODUCER_ID = 4;
        public static final int COL_QUERY_PRODUCER_NAME = 5;
        static final int COL_QUERY_PRODUCER_LOCATION = 6;
    }

    public static class DrinkFragment {

        static final String[] DETAIL_COLUMNS = {
                DatabaseContract.DrinkEntry.TABLE_NAME + "." + DatabaseContract.DrinkEntry._ID,  // without the CursorAdapter doesn't work
                Drink.NAME,
                Drink.DRINK_ID,
                Drink.PRODUCER_ID,
                Drink.TYPE,
                Drink.SPECIFICS,
                Drink.STYLE,
                Drink.INGREDIENTS,
                Producer.NAME,
                Producer.LOCATION};

        static final int COL_QUERY_DRINK__ID = 0;
        static final int COL_QUERY_DRINK_NAME = 1;
        static final int COL_QUERY_DRINK_ID = 2;
        static final int COL_QUERY_DRINK_PRODUCER_ID = 3;
        static final int COL_QUERY_DRINK_TYPE = 4;
        static final int COL_QUERY_DRINK_SPECIFICS = 5;
        static final int COL_QUERY_DRINK_STYLE = 6;
        static final int COL_QUERY_DRINK_INGREDIENTS = 7;
        static final int COL_QUERY_DRINK_PRODUCER_NAME = 8;
        static final int COL_QUERY_DRINK_PRODUCER_LOCATION = 9;


        public static final String[] PRODUCER_QUERY_COLUMNS = {
                DatabaseContract.ProducerEntry.TABLE_NAME + "." +  DatabaseContract.ProducerEntry._ID,
                Producer.NAME,
                Producer.LOCATION,
                Producer.PRODUCER_ID};

        public static final int COL_QUERY_PRODUCER__ID = 0;
        public static final int COL_QUERY_PRODUCER_NAME = 1;
        static final int COL_QUERY_PRODUCER_LOCATION = 2;
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

        static final int COL_PRODUCER__ID = 0;
        static final int COL_PRODUCER_ID = 1;
        static final int COL_PRODUCER_NAME = 2;
        public static final int COL_PRODUCER_DESCRIPTION = 3;
        static final int COL_PRODUCER_WEBSITE = 4;
        static final int COL_PRODUCER_LOCATION = 5;
    }


}
