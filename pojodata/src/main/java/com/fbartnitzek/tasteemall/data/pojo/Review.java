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
    public static final String READABLE_DATE = "review_readable_date";
//    public static final String CREATION_DATE = "review_creation_date";
//    public static final String UPDATE_DATE = "review_update_date";

    public static final String LOCATION = "review_location";
    public static final String RECOMMENDED_SIDES = "review_recommended_sides";

    public static final String USER_NAME = "review_user_name";
//    public static final String USER_ID = "review_user_id";

    public static final String DRINK_ID = "review_drink_id";



    private String reviewId;
    private String rating;
    private String description;
//    private String look;
//    private String smell;
//    private String taste;


    /**
     * notes on timestamps:
     * for reviews the readableDate is most important - as it's the time in the timezone when the user created the review on the given location
     * creation and updateDates are just for internal stuff and should be utc - later
     */
    private String readableDate;
//    private String creationDate;
//    private String updateDate;
    private String recommendedSides;
    private String userName;
//    private String userId;

    private String drinkId;
    private String location;


    public Review(String description, String drinkId, String location, String rating,
                  String readableDate, String recommendedSides, String reviewId, String userName) {
        this.description = description;
        this.drinkId = drinkId;
        this.location = location;
        this.rating = rating;
        this.readableDate = readableDate;
        this.recommendedSides = recommendedSides;
        this.reviewId = reviewId;
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReadableDate() {
        return readableDate;
    }

    public void setReadableDate(String readableDate) {
        this.readableDate = readableDate;
    }

    public String getRecommendedSides() {
        return recommendedSides;
    }

    public void setRecommendedSides(String recommendedSides) {
        this.recommendedSides = recommendedSides;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
