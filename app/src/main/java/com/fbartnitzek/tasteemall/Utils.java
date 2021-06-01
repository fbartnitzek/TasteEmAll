package com.fbartnitzek.tasteemall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.location.AddressData;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static final String LOG_TAG = Utils.class.getName();
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

    private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat filePrefixFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//    public static final String GEOCODE_ME = "geocode_me";
    private static final String LAT_PREFIX = "_lat_";
    private static final String LONG_PREFIX = "_long_";
    private static final String D_DOT_D = "\\d+\\.\\d+";

    // TODO: might use some hash-function later...
    public static String calcProducerId(String producerName, String location) {
        return "producer_" + producerName + "_;location_" + location;
    }

    public static String calcDrinkId(String drinkName, String producerId) {
        return producerId + "_;drink_" + drinkName;
    }

    public static String calcReviewId(String userId, String mDrinkId, String date) {
        return "review_" + userId + "_;_date_" + date + "_;_" + mDrinkId;
    }

    public static String calcUserId(String userName) {
        return "user_" + userName;
    }

    public static String calcLocationId(String locationValue) {
        String s = locationValue.replace(" ", "");
        s = s.replace(",", "");
        if (s.length() > 50) {
            s = s.substring(0, 49);
        }
        return "location_" + s;
    }

    public static String getLocationInput(double latitude, double longitude) {
        return latitude + "_" + longitude;
    }

    public static Uri calcSingleDrinkUri(Uri uri) {    //if called f.e. with drink_with_producer-id...
        if (uri != null) {
            return DatabaseContract.DrinkEntry.buildUri(DatabaseContract.getIdFromUri(uri));
        } else {
            return null;
        }
    }

    public static Uri calcDrinkIncludingProducerUri(Uri uri) {    //if called with drink-id...
        if (uri != null) {
            return DatabaseContract.DrinkEntry.buildUriIncludingProducer(DatabaseContract.getIdFromUri(uri));
        } else {
            return null;
        }
    }

    public static Uri calcProducerUriIncludingDrink(Uri uri) {
        if (uri != null) {
            return DatabaseContract.ProducerEntry.buildUriIncLocation(DatabaseContract.getIdFromUri(uri));
        } else {
            return null;
        }
    }

    public static Uri calcJoinedReviewUri(Uri uri) {
        if (uri != null) {
            return DatabaseContract.ReviewEntry.buildUriForShowReview(DatabaseContract.getIdFromUri(uri));
        } else {
            return null;
        }
    }

    public static Uri calcSingleReviewUri(Uri uri) {
        if (uri != null) {
            return DatabaseContract.ReviewEntry.buildUri(DatabaseContract.getIdFromUri(uri));
        } else {
            return null;
        }
    }

    public static Uri calcSingleProducerUri(Uri uri) {    //if called f.e. with drink_with_producer-id...
        if (uri != null) {
            return DatabaseContract.ProducerEntry.buildUri(DatabaseContract.getIdFromUri(uri));
        } else {
            return null;
        }
    }

    public static Uri calcSingleLocationUri(Uri mContentUri) {
        if (mContentUri!= null) {
            return DatabaseContract.LocationEntry.buildUri(DatabaseContract.getIdFromUri(mContentUri));
        } else {
            return null;
        }
    }

    static public boolean isNetworkUnavailable(Context context) {
        // todo: https://github.com/android/connectivity-samples/tree/main/NetworkConnect
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isGeocodeMeLatLong(AddressData address) {
        return address != null
                && DatabaseContract.LocationEntry.GEOCODE_ME.equals(address.getFormatted())
                && Utils.isValidLatLong(address.getLatitude(), address.getLongitude());
    }


//    public static String joinMax(CharSequence delimiter, Iterable tokens, int max) {
//        StringBuilder sb = new StringBuilder();
//        boolean firstTime = true;
//        int i = 0;
//        for (Object token: tokens) {
//            if (i > max) {
//                sb.append(delimiter);
//                sb.append("...");
//                return sb.toString();
//            } else {
//                if (firstTime) {
//                    firstTime = false;
//                } else {
//                    sb.append(delimiter);
//                }
//                sb.append(token);
//                ++i;
//            }
//        }
//        return sb.toString();
//    }

    public static String getCurrentLocalIso8601Time() {
        return iso8601Format.format(new java.util.Date());
    }

    public static String getIso8601Time(Date date) {
        return iso8601Format.format(date);
    }

    public static String getCurrentLocalTimePrefix() {
        return filePrefixFormat.format(new java.util.Date());
    }

//    public static String formatDateTime(Context context, String timeToFormat) {
//
//        String finalDateTime = "";
//
////        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        Date date;
//        if (timeToFormat != null) {
//            try {
//                date = iso8601Format.parse(timeToFormat);
//            } catch (ParseException e) {
//                date = null;
//            }
//
//            if (date != null) {
//                long when = date.getTime();
//                int flags = 0;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
//                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
//
//                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
//                        when + TimeZone.getDefault().getOffset(when), flags);
//            }
//        }
//        return finalDateTime;
//    }

    public static String getFormattedDate(Date date, String formatString) {
        if (date != null) {
            return new SimpleDateFormat(formatString).format(date);
        } else {
            return null;
        }
    }
    
    public static Date getDate(String isoTime) {
        if (isoTime == null) {
            return null;
        }
        Date date;
        try {
            date = iso8601Format.parse(isoTime);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }


    public static String getUserNameFromSharedPrefs(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(
                    context.getString(R.string.pref_key_user_name),
                    context.getString(R.string.pref_value_user_name_empty));

    }


    public static int getDrinkTypeIndexFromSharedPrefs(Context context, boolean isFilter) {
        String drinkType = getDrinkTypeFromSharedPrefs(context, isFilter);
        return getDrinkTypeId(context, drinkType);
    }

    public static String getDrinkTypeFromSharedPrefs(Context context, boolean isFilter) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (isFilter) {
            return prefs.getString(
                    context.getString(R.string.pref_key_type),
                    context.getString(R.string.drink_key_all));
        } else {    // no all
            String drinkType = prefs.getString(
                    context.getString(R.string.pref_key_type),
                    context.getString(R.string.drink_key_generic));
            if (Drink.TYPE_ALL.equals(drinkType)){
                return context.getString(R.string.drink_key_generic);
            } else {
                return drinkType;
            }
        }
    }

    public static void setSharedPrefsDrinkType(Context context, String drinkType) {
        // context.getResources().getStringArray(R.array.pref_rating_values);
        // check if valid key?
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(context.getString(R.string.pref_key_type), drinkType);
        spe.apply();
    }

    public static int getDrinkTypeId(Context context, String drinkTypeKey) {
        int drinkTypeId = context.getResources().getIdentifier(
                "drink_key_" + drinkTypeKey, "string", context.getPackageName());
        if (drinkTypeId == 0) {
            return R.string.drink_key_generic;
        } else {
            return drinkTypeId;
        }
    }

    public static int getEntityNameId(Context context, String entity) {
        if (entity != null && context != null) {
            return context.getResources().getIdentifier("entity_" + entity, "string", context.getPackageName());
        }
        return -1;
    }

    public static int getAttributeNameId(Context context, String attribute) {
        if (attribute != null && context != null) {
            return context.getResources().getIdentifier("attribute_" + attribute, "string", context.getPackageName());
        }
        return -1;
    }

    public static int getReadableDrinkNameId(Context context, int drinkTypeId) {
        return getReadableDrinkNameId(context, context.getResources().getString(drinkTypeId));
    }

    public static int getReadableDrinkNameId(Context context, String drinkType) {
        if (drinkType != null) {
            int showDrinkId = context.getResources().getIdentifier(
                    "drink_show_" + drinkType, "string", context.getPackageName());
            if (showDrinkId != 0) {
                return showDrinkId;
            }
        }
        return R.string.drink_show_generic;
    }

    public static int getReadableProducerNameId(Context context, int drinkTypeId) {
        return getReadableProducerNameId(context, context.getResources().getString(drinkTypeId));
    }

    public static int getReadableProducerNameId(Context context, String drinkType) {
        if (drinkType != null) {
            int showProducerId = context.getResources().getIdentifier(
                    "producer_show_" + drinkType, "string", context.getPackageName());
            if (showProducerId != 0) {
                return showProducerId;
            }
        }
        return R.string.producer_show_generic;
    }

//    public static String formatLocationForGeocoder(Location currentLocation) {
//        return DatabaseContract.LocationEntry.GEOCODE_ME + LAT_PREFIX + String.valueOf(currentLocation.getLatitude())
//                + LONG_PREFIX + String.valueOf(currentLocation.getLongitude());
//    }

    public static String formatAddress(Address address) {
        // somehow the Address-format changed in the last 4-5years to only 1 line ...
        // https://developer.android.com/reference/android/location/Address.html#getMaxAddressLineIndex()

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = address.getMaxAddressLineIndex() ; i >= 0; --i) {
            stringBuilder.append(address.getAddressLine(i)).append(", ");  //seems to be quite good
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() -2);
        }

        String formatted;
        if (stringBuilder.length() > 0) {
            formatted = stringBuilder.toString();
        } else {    //bugfix for Sri Lanka :-p
            formatted = address.getCountryName();
        }

        Log.v(LOG_TAG, "formatAddress, formatted: " + formatted + ", address: " + address);
        return formatted;
    }

    public static boolean checkGeocodeAddressFormat(String formattedAddress) {
        return formattedAddress != null
                && formattedAddress.matches(DatabaseContract.LocationEntry.GEOCODE_ME + LAT_PREFIX + D_DOT_D + LONG_PREFIX + D_DOT_D);
    }

    public static boolean checkTimeFormat(String timeFormat) {
        if (timeFormat != null) {
            Date date = getDate(timeFormat);
            if (date != null) {
                String newTime = iso8601Format.format(date);
                if (timeFormat.equals(newTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static LocationParcelable getLocationFromAddress(Address address, String locationInput, String description) {
        Log.v(LOG_TAG, "getLocationFromAddress, address: " + address + ", locationInput: " + locationInput);
        return new LocationParcelable(
                LocationParcelable.INVALID_ID,
                address.getCountryName(),
                calcLocationId(locationInput),
                address.getLatitude(),
                address.getLongitude(),
                locationInput,
                formatAddress(address),
                description);
    }

    public static LocationParcelable getLocationFromAddressData(AddressData address, String description) {
        Log.v(LOG_TAG, "getLocationFromAddress, address: " + address);
        return new LocationParcelable(
                LocationParcelable.INVALID_ID,
                address.getCountryName(),
                calcLocationId(address.getOrigInput()),
                address.getLatitude(),
                address.getLongitude(),
                address.getOrigInput(),
                address.getFormatted(),
                description);
    }

    public static LocationParcelable getLocationStubFromLastLocation(Location mLastLocation, String description) {
        String locationInput = getLocationInput(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        return new LocationParcelable(
                LocationParcelable.INVALID_ID,
                null,
                calcLocationId(locationInput),
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                locationInput,
                DatabaseContract.LocationEntry.GEOCODE_ME,
                description
        );
    }

    public static LocationParcelable createLocationStubFromInput(String inputString, String description) {

        return new LocationParcelable(
                LocationParcelable.INVALID_ID,
                null,
                calcLocationId(inputString),
                DatabaseContract.LocationEntry.INVALID_LAT_LNG,
                DatabaseContract.LocationEntry.INVALID_LAT_LNG,
                inputString,
                DatabaseContract.LocationEntry.GEOCODE_ME,
                description
        );
    }

    public static boolean isValidLatLong(double lat, double lon) {
        return lat >= -90.0 && lat <= 90.0 && lon >= -180.0 && lon <= 180.0;
    }

    public static boolean isValidLocation(LocationParcelable location) {
//        private static final String LOCATIONS_GEOCODE_VALID_LATLNG =
//                com.fbartnitzek.tasteemall.data.pojo.Location.FORMATTED_ADDRESS + " LIKE '" + DatabaseContract.LocationEntry.GEOCODE_ME + "' AND "
//                        + com.fbartnitzek.tasteemall.data.pojo.Location.LATITUDE + " <= 90.0 AND " + com.fbartnitzek.tasteemall.data.pojo.Location.LATITUDE + " >= -90.0 AND "
//                        + com.fbartnitzek.tasteemall.data.pojo.Location.LONGITUDE+ " <= 180.0 AND " + com.fbartnitzek.tasteemall.data.pojo.Location.LONGITUDE + " >= -180.0";
        return location != null && isValidLatLong(location.getLatitude(), location.getLongitude());
    }

    public static double getLatitude(String location) throws NumberFormatException {
        if (location != null && location.contains(LAT_PREFIX) && location.contains(LONG_PREFIX)) {
            String lat = location.substring(
                    DatabaseContract.LocationEntry.GEOCODE_ME.length() + LAT_PREFIX.length(), location.indexOf(LONG_PREFIX));
            return Double.parseDouble(lat);
        }
        throw new NumberFormatException();
    }

    public static double getLongitude(String location) throws NumberFormatException {
        if (location != null && location.contains(LAT_PREFIX) && location.contains(LONG_PREFIX)) {
            String longitude = location.substring(location.indexOf(LONG_PREFIX) + LONG_PREFIX.length());
            return Double.parseDouble(longitude);
        }
        throw new NumberFormatException();
    }

    public static void openInBrowser(String website, Activity activity) {
        if (website == null || website.length() == 0) {
            return;
        }
        if (!website.startsWith("http://") && !website.startsWith("https://")) {
            website = "http://" + website;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(website));
        if (intent.resolveActivity(activity.getPackageManager()) != null){
            activity.startActivity(intent);
        }
    }

    public static double calcDistance(Double[] mainAddress, Double[] otherAddress) {
        double dx = mainAddress[0] - otherAddress[0];
        double dy = mainAddress[1] - otherAddress[1];
        return dx * dx + dy * dy;
    }

    public static double calcManhattenDistance(Double[] mainAddress, Double[] otherAddress) {
        double dx = mainAddress[0] - otherAddress[0];
        double dy = mainAddress[1] - otherAddress[1];
        return Math.abs(dx) + Math.abs(dy);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> T[] castParcelableArray(Class<T> clazz, Parcelable[] parcelables) {
        final int length = parcelables.length;
        final T[] customClasses = (T[]) Array.newInstance(clazz, length);
        for (int i = 0; i < length; i++) {
            customClasses[i] = (T) parcelables[i];
        }
        return customClasses;
    }
}
