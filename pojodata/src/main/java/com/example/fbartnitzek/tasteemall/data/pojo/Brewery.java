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

public class Brewery {
    // TODO: better generalization for all sorts of drinks (beer, wine, whisky, ...) - producer?

    public static final String BREWERY_ID = "brewery_id";
    public static final String NAME = "brewery_name";
    public static final String INTRODUCED = "brewery_introduced"; //wikipedia-style for absolute age of a brewery
    public static final String WEBSITE = "brewery_website";

    public static final String LOCATION_ID = "brewery_location_id";

    private String breweryId;
    private String name;
    private String introduced;
    private String website;
    private String locationId;

    public Brewery(String breweryId, String introduced, Location location, String name, String website) {
        this.breweryId = breweryId;
        this.introduced = introduced;
        this.locationId = location == null ? null : location.getLocationId();
        this.name = name;
        this.website = website;
    }

    @Override
    public String toString() {
        return "Brewery{" +
                "breweryId='" + breweryId + '\'' +
                ", name='" + name + '\'' +
                ", introduced='" + introduced + '\'' +
                ", website='" + website + '\'' +
                ", locationId=" + locationId +
                '}';
    }

    public String getBreweryId() {
        return breweryId;
    }

    public void setBreweryId(String breweryId) {
        this.breweryId = breweryId;
    }

    public String getIntroduced() {
        return introduced;
    }

    public void setIntroduced(String introduced) {
        this.introduced = introduced;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
