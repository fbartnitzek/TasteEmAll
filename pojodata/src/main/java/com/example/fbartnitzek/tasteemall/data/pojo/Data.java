package com.example.fbartnitzek.tasteemall.data.pojo;

import java.util.List;

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

public class Data {
    List<Location> locations;
    List<Brewery> breweries;
    List<Beer> beers;
    List<Review> reviews;
    User user;

    public Data(List<Beer> beers, List<Brewery> breweries, List<Location> locations, List<Review> reviews, User user) {
        this.beers = beers;
        this.breweries = breweries;
        this.locations = locations;
        this.reviews = reviews;
        this.user = user;
    }

    public List<Beer> getBeers() {
        return beers;
    }

    public void setBeers(List<Beer> beers) {
        this.beers = beers;
    }

    public List<Brewery> getBreweries() {
        return breweries;
    }

    public void setBreweries(List<Brewery> breweries) {
        this.breweries = breweries;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
