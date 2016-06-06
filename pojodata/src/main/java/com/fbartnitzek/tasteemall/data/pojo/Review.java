package com.fbartnitzek.tasteemall.data.pojo;

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

public class Review {


    public static final String REVIEW_ID = "review_id";
    public static final String RATING = "review_rating";
    public static final String DESCRIPTION = "review_description";

    /**
     * notes on timestamps:
     * for reviews the readableDate is most important - as it's the time in the timezone when the user created the review on the given location
     * creation and updateDates are just for internal stuff and should be utc - later
     */
    public static final String READABLE_DATE = "review_readable_date";
//    public static final String CREATION_DATE = "review_creation_date";
//    public static final String UPDATE_DATE = "review_update_date";

    public static final String RECOMMENDED_SIDES = "review_recommended_sides";


    //    public static final String LOCATION = "review_location";
    public static final String LOCATION_ID = "review_location_id";

    //    public static final String USER_NAME = "review_user_name";
    public static final String USER_ID = "review_user_id";

    public static final String DRINK_ID = "review_drink_id";

}
