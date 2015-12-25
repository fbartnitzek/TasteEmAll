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
    public static final String LOCALITY = "location_locality";
    public static final String COUNTRY = "location_country";
    public static final String POSTAL_CODE = "location_postal_code";
    public static final String STREET = "location_street";
    public static final String LONGITUDE = "location_longitude";
    public static final String LATITUDE = "location_latitude";
    public static final String FORMATTED_ADDRESS = "location_formatted_address";

    private String locationId;
    private String locality;
    private String country;
    private String postalCode;
    private String street;
    private String longitude;
    private String latitude;
    private String formattedAddress;

    public Location(String country, String latitude, String locationId, String longitude, String postalCode, String locality, String street,
                    String formattedAddress) {
        this.country = country;
        this.latitude = latitude;
        this.locationId = locationId;
        this.longitude = longitude;
        this.postalCode = postalCode;
        this.locality = locality;
        this.street = street;
        this.formattedAddress = formattedAddress;
    }

    @Override
    public String toString() {
        return "Location{" +
                "country='" + country + '\'' +
                ", locationId='" + locationId + '\'' +
                ", locality='" + locality+ '\'' +
                ", postalCode='" + postalCode+ '\'' +
                ", street='" + street + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                '}';
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
