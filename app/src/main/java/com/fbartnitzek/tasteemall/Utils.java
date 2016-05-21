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
import android.preference.PreferenceManager;

import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Drink;

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

    private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat filePrefixFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static final String GEOCODE_ME = "geocode_me";
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

    static public boolean isNetworkUnavailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }


    public static String joinMax(CharSequence delimiter, Iterable tokens, int max) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        int i = 0;
        for (Object token: tokens) {
            if (i > max) {
                sb.append(delimiter);
                sb.append("...");
                return sb.toString();
            } else {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(token);
                ++i;
            }
        }
        return sb.toString();
    }

    public static String getCurrentLocalIso8601Time() {
        return iso8601Format.format(new java.util.Date());
    }

    public static String getCurrentLocalTimePrefix() {
        return filePrefixFormat.format(new java.util.Date());
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

//        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date;
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

    public static String formatLocationForGeocoder(Location currentLocation) {
        return GEOCODE_ME + LAT_PREFIX + String.valueOf(currentLocation.getLatitude())
                + LONG_PREFIX + String.valueOf(currentLocation.getLongitude());
    }

    public static String formatAddress(Address address) {
        // Fetch the address lines using getAddressLine,
        // join them, and send them to the thread.
        String currentAddress = "";

        for(int i = address.getMaxAddressLineIndex() -1 ; i >= 0; --i) {
            currentAddress += address.getAddressLine(i) + ", ";  //seems to be quite good
        }
        currentAddress = currentAddress.substring(0, currentAddress.length() - 2); //65432 FFM, some street nr
        return currentAddress;
    }

    public static boolean checkGeocodeAddressFormat(String formattedAddress) {
        return formattedAddress != null
                && formattedAddress.matches(GEOCODE_ME + LAT_PREFIX + D_DOT_D + LONG_PREFIX + D_DOT_D);
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

    public static double getLatitude(String location) throws NumberFormatException {
        if (location != null && location.contains(LAT_PREFIX) && location.contains(LONG_PREFIX)) {
            String lat = location.substring(
                    GEOCODE_ME.length() + LAT_PREFIX.length(), location.indexOf(LONG_PREFIX));
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
}
