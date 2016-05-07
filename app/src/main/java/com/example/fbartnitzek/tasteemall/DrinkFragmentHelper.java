package com.example.fbartnitzek.tasteemall;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

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

class DrinkFragmentHelper {

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


    static final String[] PRODUCER_QUERY_COLUMNS = {
            DatabaseContract.ProducerEntry.TABLE_NAME + "." +  DatabaseContract.ProducerEntry._ID,
            Producer.NAME,
            Producer.LOCATION,
            Producer.PRODUCER_ID};

    static final int COL_QUERY_PRODUCER__ID = 0;
    static final int COL_QUERY_PRODUCER_NAME = 1;
    static final int COL_QUERY_PRODUCER_LOCATION = 2;
    static final int COL_QUERY_PRODUCER_ID = 3;
}
