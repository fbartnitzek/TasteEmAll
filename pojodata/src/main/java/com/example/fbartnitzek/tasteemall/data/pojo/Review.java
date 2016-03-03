package com.example.fbartnitzek.tasteemall.data.pojo;

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
    public static final String DATE = "review_date";
    public static final String LOCATION = "review_location";

    //TODO: more description, recommended sides, user?

    //    public static final String LOOK = "review_look";    // bottle-design, color, foam, ...
//    public static final String SMELL = "review_smell";  // ipa :-)
//    public static final String TASTE = "review_taste";  // taste = antrunk, frische, abgang - bitter/malzig/...

//    public static final String USER_ID = "review_user_id";
    public static final String DRINK_ID = "review_drink_id";



    private String reviewId;
    private String rating;
    private String description;
//    private String look;
//    private String smell;
//    private String taste;
    private String date;
//    private String userId;
    private String drinkId;
    private String location;

    public Review(Drink drink, String description, String location, String rating, String reviewId, String date) {
        this.drinkId = drink.getDrinkId();
        this.description = description;
        this.location = location;
        this.rating = rating;
        this.reviewId = reviewId;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Review{" +
                "date='" + date + '\'' +
                ", reviewId='" + reviewId + '\'' +
                ", rating='" + rating + '\'' +
                ", description='" + description + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
}
