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

public class Location {
    public static final String LOCATION_ID = "location_id";
    public static final String NAME = "location_name";
    public static final String COUNTRY = "location_country";
    public static final String POSTAL_CODE = "location_postal_code";
    public static final String STREET = "location_street";
    public static final String LOCATION_LONGITUDE = "location_longitude";
    public static final String LOCATION_LATITUDE = "location_latitude";

    private String locationId;
    private String name;
    private String country;
    private String street;
    private String longitude;
    private String latitude;

    public Location(String country, String latitude, String locationId, String longitude, String name, String street) {
        this.country = country;
        this.latitude = latitude;
        this.locationId = locationId;
        this.longitude = longitude;
        this.name = name;
        this.street = street;
    }

    @Override
    public String toString() {
        return "Location{" +
                "country='" + country + '\'' +
                ", locationId='" + locationId + '\'' +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }
}
