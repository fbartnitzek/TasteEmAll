package com.example.fbartnitzek.tasteemall;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
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

public class ProducerFragmentHelper {

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
