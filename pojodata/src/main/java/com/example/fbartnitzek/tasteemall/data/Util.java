package com.example.fbartnitzek.tasteemall.data;


import com.example.fbartnitzek.tasteemall.data.pojo.Location;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;

import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

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

public class Util {

    public static String readApiKey() {
        Properties properties = new Properties();
        String path = System.getProperty("user.home") + "/.gradle/gradle.properties";
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = properties.getProperty("TasteEmAll_Geocode_Key");
        return unquote(value);
    }

    public static String unquote(String s) {
        if (s != null && (
                (s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\"")) )) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static Location queryLocation(String locationString) {
        // use google maps api
        // https://maps.googleapis.com/maps/api/geocode/json?address=Toronto%20%28Canada%29,%20275%20Yonge%20Street

        GeoApiContext context = new GeoApiContext().setApiKey(readApiKey());
        GeocodingResult[] results = new GeocodingResult[0];
        try {
            results = GeocodingApi.geocode(context, locationString).await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String formattedAddress = results[0].formattedAddress;
//        System.out.println(formattedAddress);
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        String route = null;
        String country= null;
        String postalCode = null;
        String locality = null;
        // country, street_number, route, postal_code, long, lat, locality
        // "location": {
        // "lat": ​43.6555326,
        // "lng": ​-79.3802037 },
        AddressComponent[] addressComponents = results[0].addressComponents;
        for (AddressComponent a: addressComponents){
            for (AddressComponentType type : a.types) {
                if ("postal_code".equals(type.name().toLowerCase())) {
                    postalCode = a.longName;
                } else if ("route".equals(type.name().toLowerCase())) {
                    route = a.longName;
                } else if ("country".equals(type.name().toLowerCase())) {
                    country = a.longName;
                } else if ("locality".equals(type.name().toLowerCase())) {
                    locality = a.longName;
                }
            }
        }

        return new Location(country, Double.toString(latitude), "location_" + locationString, Double.toString(longitude), postalCode, locality, route, formattedAddress);
    }

    public static String getRecord(CSVRecord record, String attribute) {
        try{
            return record.get(attribute);
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    public static String readTimestamp(String record) {
        // TODO ...
        return record;
    }

    public static <V> ArrayList<V> map2List(Map<String, V> map) {
        ArrayList<V> valuesList = new ArrayList<V>(map.values());
        return valuesList;
    }

}
