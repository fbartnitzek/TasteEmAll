package com.example.fbartnitzek.tasteemall;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

public class Utils {
    // to confusing for now...
//    public static String calcDegreesPlato(double stammwuerze) {
//        //https://de.wikipedia.org/wiki/Stammw%C3%BCrze#Umrechnung_zwischen_Grad_Plato_und_Massendichte
//        return null;
//    }

//    public static ContentValues getContentValues(Location location) {
//        return DatabaseHelper.buildLocationValues(
//                location.getLocationId(), location.getLocality(), location.getCountry(),
//                location.getPostalCode(), location.getStreet(), location.getLongitude(),
//                location.getLatitude(), location.getFormattedAddress()
//        );
//    }

    static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // TODO: might use some hash-function later...
    public static String calcProducerId(String producerName) {
        return "producer_" + producerName;
    }

    public static String calcDrinkId(String drinkName, String producerId) {
        return producerId + "_;drink_" + drinkName;
    }

    public static String calcReviewId(String userName, String mDrinkId, String date) {
        return "review_user_" + userName + "_;_date_" + date + "_;_" + mDrinkId;
    }

    public static Uri calcSingleDrinkUri(Uri uri) {    //if called f.e. with drink_with_producer-id...
        if (uri != null) {
            int id = DatabaseContract.getIdFromUri(uri);
            return DatabaseContract.DrinkEntry.buildUri(id);
        } else {
            return null;
        }
    }

    public static Uri calcDrinkIncludingProducerUri(Uri uri) {    //if called with drink-id...
        if (uri != null) {
            int id = DatabaseContract.getIdFromUri(uri);
            return DatabaseContract.DrinkEntry.buildUriIncludingProducer(id);
        } else {
            return null;
        }
    }

    public static Uri calcJoinedReviewUri(Uri uri) {
        if (uri != null) {
            int id = DatabaseContract.getIdFromUri(uri);
            return DatabaseContract.ReviewEntry.buildUriForShowReview(id);
        } else {
            return null;
        }
    }

    public static Uri calcSingleReviewUri(Uri uri) {
        if (uri != null) {
            int id = DatabaseContract.getIdFromUri(uri);
            return DatabaseContract.ReviewEntry.buildUri(id);
        } else {
            return null;
        }
    }

    public static Uri calcSingleProducerUri(Uri uri) {    //if called f.e. with drink_with_producer-id...
        if (uri != null) {
            int id = DatabaseContract.getIdFromUri(uri);
            return DatabaseContract.ProducerEntry.buildUri(id);
        } else {
            return null;
        }
    }

    public static int getDrinkTypeIndexFromSharedPrefs(Context context, boolean isFilter) {
        // TODO: currently kind of wrong way around :-p
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String drinkType = prefs.getString(
                context.getString(R.string.pref_type_key),
                context.getString(R.string.pref_type_generic));
        int drinkIndex = getDrinkTypeIndex(context, drinkType);
        if (isFilter) {
            return drinkIndex;
        } else {
            return Drink.TYPE_ALL.equals(drinkType) ? R.string.drink_key_generic : drinkIndex;
        }
    }

    public static int getDrinkTypeIndex(Context context, String drinkType) {
        //TODO: generic solution - based only on string/array-values
        if (context.getString(R.string.drink_key_beer).equals(drinkType)) {
            return R.string.drink_key_beer;
        } else if (context.getString(R.string.drink_key_coffee).equals(drinkType)) {
            return R.string.drink_key_coffee;
        } else if (context.getString(R.string.drink_key_whisky).equals(drinkType)) {
            return R.string.drink_key_whisky;
        } else if (context.getString(R.string.drink_key_wine).equals(drinkType)){
            return R.string.drink_key_wine;
        } else {
            return R.string.drink_key_generic;
        }
    }

    public static String getDrinkTypeFromSharedPrefs(Context context, boolean isFilter) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String drinkType = prefs.getString(
                context.getString(R.string.pref_type_key),
                context.getString(R.string.pref_type_generic));
        if (isFilter) {
            return drinkType;
        } else {
            return Drink.TYPE_ALL.equals(drinkType) ? Drink.TYPE_GENERIC : drinkType;
        }
    }

    public static void setSharedPrefsDrinkType(Context context, String drinkType) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(context.getString(R.string.pref_type_key), drinkType);
        spe.apply();
    }

    public static ContentValues getContentValues(Producer producer) {
        return DatabaseHelper.buildProducerValues(
                producer.getProducerId(), producer.getName(), producer.getDescription(),
                producer.getWebsite(), producer.getLocation()
        );
    }

    public static String getCurrentLocalIso8601Time() {
        return iso8601Format.format(new java.util.Date());
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

//        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    public static String queryLocation(String locationString) {
        // use google maps api
        // https://maps.googleapis.com/maps/api/geocode/json?address=Toronto%20%28Canada%29,%20275%20Yonge%20Street

        GeoApiContext context = new GeoApiContext().setApiKey(BuildConfig.GOOGLE_MAPS_GEOCODE_KEY);
        GeocodingResult[] results = new GeocodingResult[0];
        try {
            results = GeocodingApi.geocode(context, locationString).await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results[0].formattedAddress;
//        String formattedAddress = results[0].formattedAddress;
//        System.out.println(formattedAddress);
//        double latitude = results[0].geometry.location.lat;
//        double longitude = results[0].geometry.location.lng;
//        String route = null;
//        String country= null;
//        String postalCode = null;
//        String locality = null;
//        // country, street_number, route, postal_code, long, lat, locality
//        // "location": {
//        // "lat": ​43.6555326,
//        // "lng": ​-79.3802037 },
//        AddressComponent[] addressComponents = results[0].addressComponents;
//        for (AddressComponent a: addressComponents){
//            for (AddressComponentType type : a.types) {
//                if ("postal_code".equals(type.name().toLowerCase())) {
//                    postalCode = a.longName;
//                } else if ("route".equals(type.name().toLowerCase())) {
//                    route = a.longName;
//                } else if ("country".equals(type.name().toLowerCase())) {
//                    country = a.longName;
//                } else if ("locality".equals(type.name().toLowerCase())) {
//                    locality = a.longName;
//                }
//            }
//        }
//
//        return DatabaseHelper.buildLocationValues("location_" + locationString, locality, country, postalCode,
//                route, Double.toString(longitude), Double.toString(latitude), formattedAddress);
////        return new Location(country, Double.toString(latitude), "location_" + locationString, Double.toString(longitude), postalCode, locality, route, formattedAddress);
    }

    public static int getDrinkName(int drinkType) {

        switch (drinkType) {
            case R.string.drink_key_beer:
                return R.string.drink_show_beer;
            case R.string.drink_key_coffee:
                return R.string.drink_show_coffee;
            case R.string.drink_key_whisky:
                return R.string.drink_show_whisky;
            case R.string.drink_key_wine:
                return R.string.drink_show_wine;
            default:
                return R.string.drink_show_generic;
        }
    }

    public static int getProducerName(int drinkType) {
        switch (drinkType) {
            case R.string.drink_key_beer:
                return R.string.producer_show_beer;
            case R.string.drink_key_coffee:
                return R.string.producer_show_coffee;
            case R.string.drink_key_whisky:
                return R.string.producer_show_whisky;
            case R.string.drink_key_wine:
                return R.string.producer_show_wine;
            default:
                return R.string.producer_show_generic;
        }
    }

}
