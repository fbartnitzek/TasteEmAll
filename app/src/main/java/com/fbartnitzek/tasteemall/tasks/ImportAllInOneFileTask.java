package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.csv.CsvFileReader;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.data.pojo.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2017.  Frank Bartnitzek
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

public class ImportAllInOneFileTask extends ImportFileBaseTask {

    private final ImportAllInOneHandler mImportHandler;
    private boolean mErrorHappened = false;
    private static final String LOG_TAG = ImportAllInOneFileTask.class.getName();

    public interface ImportAllInOneHandler {
        void onImportAllInOneFinished(String message);
        void onImportAllInOneFailed(String message);
    }

    public ImportAllInOneFileTask(Activity activity, ImportAllInOneHandler mImportHandler) {
        super(activity);
        this.mImportHandler = mImportHandler;
    }

    @Override
    protected String doInBackground(Uri... uris) {
        if (uris.length == 0 || uris[0] == null) {
            return mActivity.getString(R.string.msg_on_import_files_chosen);
        }

        File file = createTempFile(uris[0]);
        if (file == null) {
            String msg = "temp file could not be created from uri: " + uris[0];
            Log.w(LOG_TAG, msg);
            mErrorHappened = true;
            return msg;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String message = null;
        mErrorHappened = false;
        db.beginTransaction();
        Cursor cursor = null;
        int row = 1;
        try {

            List<String> dataColumns = new ArrayList<>();
            List<List<String>> dataEntries = CsvFileReader.readCsvFileHeadingAndData(file, dataColumns);

            validateHeaders(dataColumns);
            int producerIdCol = dataColumns.indexOf(Producer.PRODUCER_ID);
            int producerNameCol = dataColumns.indexOf(Producer.NAME);
            int producerInputCol = dataColumns.indexOf(Producer.INPUT);
            List<String> producerAttributes = DatabaseContract.ATTRIBUTES.get(Producer.ENTITY);
            String[] prodAttrArray = producerAttributes.toArray(new String[producerAttributes.size()]);
            Map<String, String> addedProducers = new LinkedHashMap<>();

            int drinkIdCol = dataColumns.indexOf(Drink.DRINK_ID);
            int drinkNameCol = dataColumns.indexOf(Drink.NAME);
            List<String> drinkAttributes = DatabaseContract.ATTRIBUTES.get(Drink.ENTITY);
            String[] drinkAttrArray = drinkAttributes.toArray(new String[drinkAttributes.size()]);
            Set<String> addedDrinks = new LinkedHashSet<>();

            int userIdCol = dataColumns.indexOf(User.USER_ID);
            int userNameCol = dataColumns.indexOf(User.NAME);
            List<String> userAttributes = DatabaseContract.ATTRIBUTES.get(User.ENTITY);
            String[] userAttrArray = userAttributes.toArray(new String[userAttributes.size()]);
            Set<String> addedUsers = new LinkedHashSet<>();

            int locationIdCol = dataColumns.indexOf(Location.LOCATION_ID);
            int locationInputCol = dataColumns.indexOf(Location.INPUT);
            List<String> locationAttributes = DatabaseContract.ATTRIBUTES.get(Location.ENTITY);
            String[] locationAttrArray = locationAttributes.toArray(new String[locationAttributes.size()]);
            Set<String> addedLocations = new LinkedHashSet<>();

            int reviewDateCol = dataColumns.indexOf(Review.READABLE_DATE);
            int reviewRatingCol = dataColumns.indexOf(Review.RATING);
            List<String> reviewAttributes = DatabaseContract.ATTRIBUTES.get(Review.ENTITY);
            String[] reviewAttrArray = reviewAttributes.toArray(new String[reviewAttributes.size()]);

            List<String> ratings = Arrays.asList(mActivity.getResources().getStringArray(R.array.pref_rating_values));

            int iR = 0; int iL = 0; int iU = 0; int iD = 0; int iP = 0;
            int uL = 0; int uD = 0; int uP = 0;

            for (List<String> entry : dataEntries) {    // per line
                row++;

                Log.v(LOG_TAG, "doInBackground, parsing row " + row + ", entry: " + entry);
                // ignore empty lines
                if (isEmpty(entry)) {
                    continue;
                }


                // 1) update or add producer
                String producerId = null;

                // 1a) with producerId => try to update
                if (producerIdCol > 0 && entry.size() > producerIdCol && !entry.get(producerIdCol).isEmpty()) {
                    producerId = entry.get(producerIdCol);
//                    Log.v(LOG_TAG, "doInBackground, found producerId=" + producerId);
                    cursor = db.query(DatabaseContract.ProducerEntry.TABLE_NAME,
                            prodAttrArray,
                            Producer.PRODUCER_ID + " = ?",
                            new String[]{producerId}, null, null, null);
                    if (cursor.getCount() != 1){
                        cursor.close();
                        throw new ValidationException("producer in line " + row
                                + " was not found with producerId '" + producerId + "'!");
                    }

                    cursor.moveToFirst();
                    List<String> emptyAttributes = new ArrayList<>();
                    int i = -1;
//                    Log.v(LOG_TAG, "doInBackground, searching for empty attributes of producer: " + Arrays.toString(prodAttrArray));
                    for (String attrName : prodAttrArray) {
                        ++i;
                        if (Producer.LATITUDE.equals(attrName)){
                            if (!Utils.isValidLatLong(cursor.getDouble(i), 0L)){
                                emptyAttributes.add(attrName);
                            }
                        } else if (Producer.LONGITUDE.equals(attrName)) {
                            if (!Utils.isValidLatLong(0L, cursor.getDouble(i))){
                                emptyAttributes.add(attrName);
                            }

                        } else {
                            if (cursor.getString(i) == null || cursor.getString(i).isEmpty()) {
                                emptyAttributes.add(attrName);
                            }
                        }
                    }
                    cursor.close();

                    // get attributes from csv
//                    Log.v(LOG_TAG, "doInBackground, getNonEmptyCVs of producer, emptyAttributes = " + emptyAttributes + ", dataColumns = " + dataColumns);
                    ContentValues nonEmptyCVs = getContentValues(entry, dataColumns, emptyAttributes, true);
//                    Log.v(LOG_TAG, "doInBackground, after getNonEmptyCVs of producer, nonEmptyCVs = " + nonEmptyCVs);

                    if (nonEmptyCVs.size() > 0) {
                        Log.v(LOG_TAG, "doInBackground, updating producer in row " + row + ", nonEmptyCVs: " + nonEmptyCVs);
                        long updated = db.update(DatabaseContract.ProducerEntry.TABLE_NAME, nonEmptyCVs,
                                Producer.PRODUCER_ID + " = ?", new String[]{producerId});
                        if (updated != 1) {
                            throw new ValidationException("producer in line " + row
                                    + " could not be updated with '" + nonEmptyCVs + "'!");
                        }
                        uP++;
//                    } else {
//                        Log.v(LOG_TAG, "doInBackground, producer not updated");
                    }

                } else {

                    // 1b) search producer by name and location, prodId, add
                    if (entry.size() <= producerNameCol || entry.size() <= producerInputCol) {
                        throw new ValidationException("producer in line " + row
                                + " has no columns for either " + Producer.NAME + " or " + Producer.INPUT + "!");
                    }

                    String producerName = entry.get(producerNameCol);

                    if (addedProducers.containsKey(producerName)) {
                        producerId = addedProducers.get(producerName);
                    } else {
                        String producerInput = entry.get(producerInputCol);
                        producerId = Utils.calcProducerId(producerName, producerInput);
                        if (producerInput.isEmpty() || producerName.isEmpty()) {
                            throw new ValidationException("producer '" + producerName + "' in line " + row
                                    + " has no valid " + Producer.INPUT + " '" + producerInput + "'!");
                        }

                        // query db, just to be sure
                        cursor = db.query(DatabaseContract.ProducerEntry.TABLE_NAME,
                                prodAttrArray,
                                Producer.PRODUCER_ID + " = ? OR (" + Producer.NAME + " = ? AND " + Producer.INPUT + " = ?)",
                                new String[]{producerId, producerName, producerInput}, null, null, null);
                        if (cursor.getCount() != 0){
                            cursor.moveToFirst();
                            producerId = cursor.getString(0);
                            cursor.close();
                            throw new ValidationException("producer in line " + row
                                    + " was found with producerId '" + producerId + "' - use it in the csv!");
                        }
                        cursor.close();

                        ContentValues contentValues = getContentValues(entry, dataColumns, producerAttributes, false);
                        contentValues.put(Producer.PRODUCER_ID, producerId);
                        Log.v(LOG_TAG, "doInBackground, inserting producer in row " + row + ", contentValues: " + contentValues);
                        long inserted = db.insert(DatabaseContract.ProducerEntry.TABLE_NAME, null,
                                contentValues);

                        if (inserted > -1) {
                            addedProducers.put(producerName, producerId);
                        } else {
                            throw new ValidationException("producer in line " + row
                                    + " could not be inserted with '" + contentValues + "'!");
                        }
                        iP++;
                    }
                }


                // 2)  update or add drink
                String drinkId = null;

                if (drinkIdCol > 0 && entry.size() > drinkIdCol && !entry.get(drinkIdCol).isEmpty()) {   // update existing drink
                    drinkId = entry.get(drinkIdCol);
//                    Log.v(LOG_TAG, "doInBackground, found drinkId=" + drinkId);
                    cursor = db.query(DatabaseContract.DrinkEntry.TABLE_NAME, drinkAttrArray,
                            Drink.DRINK_ID + " = ?",
                            new String[]{drinkId}, null, null, null);
                    if (cursor.getCount() != 1) {
                        cursor.close();
                        throw new ValidationException("drink in line " + row
                                + " was not found with drinkId '" + drinkId + "'!");
                    }

                    cursor.moveToFirst();
                    List<String> emptyAttributes = new ArrayList<>();   // get empty cursor-attributes
                    for (int i=0; i < drinkAttrArray.length; ++i) {
                        if (cursor.getString(i) == null || cursor.getString(i).isEmpty()) {
                            emptyAttributes.add(drinkAttrArray[i]);
                        }
                    }
                    cursor.close();

                    ContentValues nonEmptyCVs = getContentValues(entry, dataColumns, emptyAttributes, true);
                    if (nonEmptyCVs.size() > 0) {
                        Log.v(LOG_TAG, "doInBackground, updating drink in row " + row + ", contentValues: " + nonEmptyCVs);
                        long updated = db.update(DatabaseContract.DrinkEntry.TABLE_NAME, nonEmptyCVs,
                                Drink.DRINK_ID + " = ?", new String[]{drinkId});
                        if (updated != 1) {
                            throw new ValidationException("drink in line " + row
                                    + " could not be updated with '" + nonEmptyCVs + "'!");
                        }
                        uD++;
                    }
                } else {    // add non-existing drink
                    if (entry.size() <= drinkNameCol) {
                        throw new ValidationException("drink in line " + row + " has no columns for " + Drink.NAME + "!");
                    }
                    String drinkName = entry.get(drinkNameCol);
//                    Log.v(LOG_TAG, "doInBackground, found drinkName=" + drinkName);
                    drinkId = Utils.calcDrinkId(drinkName, producerId);

                    if (!addedDrinks.contains(drinkId)) {
                        if (drinkName.isEmpty()) {
                            throw new ValidationException("drink in line " + row
                                    + " has no valid " + Drink.NAME + " '" + drinkName + "'!");
                        }

                        // query db, just to be sure
                        cursor = db.query(DatabaseContract.DrinkEntry.TABLE_NAME, drinkAttrArray,
                                "(" + Drink.PRODUCER_ID + " = ? AND " + Drink.NAME + " = ?) OR " + Drink.DRINK_ID + " = ?",
                                new String[]{producerId, drinkName, drinkId}, null, null, null);
                        if (cursor.getCount() != 0){
                            cursor.moveToFirst();
                            String string = cursor.getString(0);
                            cursor.close();
                            throw new ValidationException("drink in line " + row
                                    + " was found with drinkId '" + string + "' - use it in the csv!");
                        }
                        cursor.close();

                        ContentValues contentValues = getContentValues(entry, dataColumns, drinkAttributes, false);
                        contentValues.put(Drink.DRINK_ID, drinkId);
                        contentValues.put(Drink.PRODUCER_ID, producerId);
                        Log.v(LOG_TAG, "doInBackground, inserting drink in row " + row + ", contentValues: " + contentValues);
                        long inserted = db.insert(DatabaseContract.DrinkEntry.TABLE_NAME, null, contentValues);

                        if (inserted > -1) {
                            addedDrinks.add(drinkId);
                        } else {
                            throw new ValidationException("drink in line " + row
                                    + " could not be inserted with '" + contentValues + "'!");
                        }
                        iD++;
                    }
                }

                // 3) update or add user
                String userId = null;

                if (userIdCol > 0 && entry.size() > userIdCol && !entry.get(userIdCol).isEmpty()) {
                    userId = entry.get(userIdCol);
                    cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, userAttrArray,
                            User.USER_ID + " = ?", new String[]{userId}, null, null, null);
                    if (cursor.getCount() != 1) {
                        cursor.close();
                        throw new ValidationException("user in line " + row + " was not found with userId '" + userId + "'!");
                    }

                    cursor.close(); // nothing to update...
                } else {
                    if (entry.size() <= userNameCol) {
                        throw new ValidationException("user in line " + row
                                + " has no columns for " + User.NAME + "!");
                    }
                    String userName = entry.get(userNameCol);
                    userId = Utils.calcUserId(userName);

                    if (!addedUsers.contains(userId)) {
                        if (userName.isEmpty()) {
                            throw new ValidationException("user in line " + row + " has no valid " + User.NAME + " '" + userName + "'!");
                        }

                        // query db
                        cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, userAttrArray,
                                User.USER_ID + " = ? OR " + User.NAME + " = ?",
                                new String[]{userId, userName}, null, null, null);
                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            userId = cursor.getString(0);
                            cursor.close();
                            throw new ValidationException("user in line " + row
                                    + " was found with userId '" + userId + "' - use it in the csv!");
                        }
                        cursor.close();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(User.USER_ID, userId);
                        contentValues.put(User.NAME, userName);
                        Log.v(LOG_TAG, "doInBackground, inserting user in row " + row + ", contentValues: " + contentValues);
                        long inserted = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, contentValues);

                        if (inserted > -1) {
                            addedUsers.add(userId);
                        } else {
                            throw new ValidationException("user in line " + row + " could not be inserted with '" + contentValues + "'!");
                        }
                        iU++;
                    }
                }

                // 4) update or add or ignore location
                String locationId = null;
//                Log.v(LOG_TAG, "doInBackground, locationId:" + (locationIdCol > 0 && entry.size() > locationIdCol ? entry.get(locationIdCol) : "n/a"));
                if (locationIdCol > 0 && entry.size() > locationIdCol && !entry.get(locationIdCol).isEmpty()) {
                    locationId = entry.get(locationIdCol);
                    cursor = db.query(DatabaseContract.LocationEntry.TABLE_NAME, locationAttrArray,
                            Location.LOCATION_ID + " = ?", new String[]{locationId}, null, null, null);
                    if (cursor.getCount() != 1){
                        cursor.close();
                        throw new ValidationException("review location in line " + row
                                + " was not found with locationId '" + locationId + "'!");
                    }
//                    Log.v(LOG_TAG, "doInBackground, query location...");

                    cursor.moveToFirst();
                    List<String> emptyAttributes = new ArrayList<>();
                    int i = -1;
                    for (String attrName : locationAttrArray) {
                        ++i;
                        if (Location.LATITUDE.equals(attrName)){
                            if (!Utils.isValidLatLong(cursor.getDouble(i), 0L)){
                                emptyAttributes.add(attrName);
                            }
                        } else if (Location.LONGITUDE.equals(attrName)) {
                            if (!Utils.isValidLatLong(0L, cursor.getDouble(i))){
                                emptyAttributes.add(attrName);
                            }

                        } else {

                            if (cursor.getString(i) == null || cursor.getString(i).isEmpty()) {
                                emptyAttributes.add(attrName);
                            }
                        }
                    }
                    cursor.close();
//                    Log.v(LOG_TAG, "doInBackground, queried location" );

                    // get attributes from csv
                    ContentValues nonEmptyCVs = getContentValues(entry, dataColumns, emptyAttributes, true);

                    if (nonEmptyCVs.size() > 0) {
                        Log.v(LOG_TAG, "doInBackground, updating location in row " + row + ", contentValues: " + nonEmptyCVs);
                        long updated = db.update(DatabaseContract.LocationEntry.TABLE_NAME, nonEmptyCVs,
                                Location.LOCATION_ID + " = ?", new String[]{locationId});
                        if (updated != 1) {
                            throw new ValidationException("review location in line " + row
                                    + " could not be updated with '" + nonEmptyCVs + "'!");
                        }
                    }
                    uL++;
                } else if (locationInputCol < entry.size()){

                    String locationInput = entry.get(locationInputCol);
                    if (!locationInput.isEmpty()) { // add location

                        locationId = Utils.calcLocationId(locationInput);
                        if (!addedLocations.contains(locationId)) {
                            cursor = db.query(DatabaseContract.LocationEntry.TABLE_NAME, locationAttrArray,
                                    Location.LOCATION_ID + " = ? OR " + Location.INPUT + " = ?",
                                    new String[]{locationId, locationInput}, null, null, null);
                            if (cursor.getCount() != 0) {
                                cursor.moveToFirst();
                                locationId = cursor.getString(0);
                                cursor.close();
                                throw new ValidationException("review location in line " + row
                                        + " was found with locationId '" + locationId + "' - use it in the csv!");
                            }
                            cursor.close();

                            ContentValues contentValues = getContentValues(entry, dataColumns, locationAttributes, false);
                            contentValues.put(Location.LOCATION_ID, locationId);
                            Log.v(LOG_TAG, "doInBackground, inserting location in row " + row + ", contentValues: " + contentValues);
                            long inserted = db.insert(DatabaseContract.LocationEntry.TABLE_NAME, null, contentValues);

                            if (inserted > -1) {
                                addedLocations.add(locationId);
                            } else {
                                throw new ValidationException("location in line " + row
                                        + " could not be inserted with '" + contentValues + "'!");
                            }
                            iL++;
                        }


                    }   // else ignore

                }


                // 5) review - add it anytime
//                Log.v(LOG_TAG, "doInBackground, before review");

                if (reviewDateCol < 0 || entry.size() <= reviewDateCol || entry.get(reviewDateCol).isEmpty()
                        || Utils.getDate(entry.get(reviewDateCol)) == null) {

                    throw new ValidationException("review in line " + row
                            + " has no valid " + Review.READABLE_DATE + " '"
                            + (reviewDateCol > 0 ? entry.get(reviewDateCol) : "")
                            + "', it should look like '2016-05-13 00:36:00'");
                }

                if (reviewRatingCol < 0 || entry.size() <= reviewRatingCol || entry.get(reviewRatingCol).isEmpty()
                        || !ratings.contains(entry.get(reviewRatingCol))) {

                    throw new ValidationException("review in line " + row
                            + " has no valid " + Review.RATING + " '"
                            + (reviewRatingCol > 0 ? entry.get(reviewRatingCol) : "")
                            + "', it should be on of '" + ratings +"'");
                }

                String reviewId = Utils.calcReviewId(userId, drinkId, entry.get(reviewDateCol));

                // query db, just to be sure
                cursor = db.query(DatabaseContract.ReviewEntry.TABLE_NAME,
                        reviewAttrArray,
                        Review.REVIEW_ID + " = ?",
                        new String[]{reviewId}, null, null, null);
                if (cursor.getCount() != 0){
                    cursor.moveToFirst();
                    reviewId = cursor.getString(0);
                    cursor.close();
                    throw new ValidationException("review in line " + row
                            + " was found with same reviewId '" + reviewId + "' - you already imported this review from an csv!");
                }
                cursor.close();

                ContentValues contentValues = getContentValues(entry, dataColumns, reviewAttributes, false);
                contentValues.put(Review.REVIEW_ID, reviewId);
                contentValues.put(Review.USER_ID, userId);
                contentValues.put(Review.DRINK_ID, drinkId);
                if (locationId != null) {
                    contentValues.put(Review.LOCATION_ID, locationId);
                }

                Log.v(LOG_TAG, "doInBackground, inserting review in row " + row + ", contentValues: " + contentValues);

                long inserted = db.insert(DatabaseContract.ReviewEntry.TABLE_NAME, null, contentValues);

                if (inserted < 0) {
                    throw new ValidationException("review in line " + row + " could not be inserted with '" + contentValues + "'!");
                }
                iR++;
            }


            db.setTransactionSuccessful();
            message = mActivity.getString(R.string.all_in_one_imported_msg, iP, uP, iD, uD, iU, iL, uL, iR);
        } catch (Exception e) {
            // no successful = rollback
            mErrorHappened = true;
            message = "error in line " + row + ": " + e.getMessage();
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }

        deleteTempFiles();

        return message;
    }

    private boolean isEmpty(List<String> entry) {
        for (String e : entry) {
            if (!e.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }


    private ContentValues getContentValues(List<String> entry, List<String> dataColumns, List<String> attributes, boolean ignoreEmpty) {
        ContentValues cv = new ContentValues();
        for (int i=0; i < dataColumns.size(); i++) {
            String header = dataColumns.get(i);
            if (attributes.contains(header) && i < entry.size()) {
                if (!ignoreEmpty || !entry.get(i).isEmpty()) {
                    cv.put(header, entry.get(i));
                }
            }
        }
        return cv;
    }

    @Override
    protected void onPostExecute(String s) {
        if (mErrorHappened) {
            mImportHandler.onImportAllInOneFailed(s);
        } else {
            TaskUtils.updateWidgets(mActivity);
            mImportHandler.onImportAllInOneFinished(s);
        }
    }

    private void validateHeaders(List<String> dataColumns) {
        StringBuilder sb = new StringBuilder();
        if (!(dataColumns.contains(Producer.PRODUCER_ID) ||
                (dataColumns.contains(Producer.NAME) && dataColumns.contains(Producer.INPUT)))){
            sb.append("producer cannot be identified, add either '" + Producer.PRODUCER_ID + "' (for already existing producers) or '"
                    + Producer.NAME + "' and '" + Producer.INPUT + "' (for new producers)!");
        }
        if (!(dataColumns.contains(Drink.DRINK_ID) ||
                (dataColumns.contains(Drink.NAME) && dataColumns.contains(Drink.TYPE)))){
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("drink cannot be identified, add either '" + Drink.DRINK_ID + "' (for already existing drinks) or '"
                    + Drink.NAME + "' and '" + Drink.TYPE + "' (for new drinks)!");
        }
        if (!(dataColumns.contains(User.USER_ID) || dataColumns.contains(User.NAME))){
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("user cannot be identified, add either '" + User.USER_ID + "' (for already existing users) or '"
                    + User.NAME + "' (for new users)!");
        }
        if (dataColumns.contains(Location.COUNTRY) || dataColumns.contains(Location.FORMATTED_ADDRESS)
                || dataColumns.contains(Location.DESCRIPTION) || dataColumns.contains(Location.LATITUDE)
                || dataColumns.contains(Location.LONGITUDE)) {
            if (!(dataColumns.contains(Location.INPUT) || dataColumns.contains(Location.LOCATION_ID))) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append("location cannot be identified, add either '" + Location.LOCATION_ID + "' (for already existing locations) or '"
                        + Location.INPUT + "' (for new locations)!");
            }
        }
        if (!(dataColumns.contains(Review.RATING))) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("review cannot be identified, add the rating column '" + Review.RATING + "'!");
        }

        if (sb.length() > 0) {
            throw new ValidationException(sb.toString());
        }
    }


    private class ValidationException extends RuntimeException{
        public ValidationException(String message) {
            super(message);
        }
    }
}
