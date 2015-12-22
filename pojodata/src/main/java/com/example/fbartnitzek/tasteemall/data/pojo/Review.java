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
    public static final String LOOK = "review_look";    // bottle-design, color, foam, ...
    public static final String SMELL = "review_smell";  // ipa :-)
    public static final String TASTE = "review_taste";  // taste = antrunk, frische, abgang - bitter/malzig/...
    public static final String TIMESTAMP = "review_timestamp";

    public static final String USER_ID = "review_user_id";
    public static final String BEER_ID = "review_beer_id";
    public static final String LOCATION_ID = "review_location_id";


    private String reviewId;
    private String rating;
    private String description;
    private String look;
    private String smell;
    private String taste;
    private String timestamp;
    private String userId;
    private String beerId;
    private String locationId;

    public Review(Beer beer, String description, Location location, String look, String rating, String reviewId, String smell, String taste, String timestamp, User user) {
        this.beerId = beer.getBeerId();
        this.description = description;
        this.locationId = location == null ? null : location.getLocationId();
        this.look = look;
        this.rating = rating;
        this.reviewId = reviewId;
        this.smell = smell;
        this.taste = taste;
        this.timestamp = timestamp;
        this.userId = user.getUserId();
    }

    @Override
    public String toString() {
        return "Review{" +
                "beerId=" + beerId +
                ", reviewId='" + reviewId + '\'' +
                ", rating='" + rating + '\'' +
                ", description='" + description + '\'' +
                ", look='" + look + '\'' +
                ", smell='" + smell + '\'' +
                ", taste='" + taste + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", userId=" + userId +
                ", locationId=" + locationId +
                '}';
    }

    public String getBeerId() {
        return beerId;
    }

    public void setBeerId(String beerId) {
        this.beerId = beerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLook() {
        return look;
    }

    public void setLook(String look) {
        this.look = look;
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

    public String getSmell() {
        return smell;
    }

    public void setSmell(String smell) {
        this.smell = smell;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
