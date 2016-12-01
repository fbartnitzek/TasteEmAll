package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.csv.CsvFileReader;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2016.  Frank Bartnitzek
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


public class ImportFilesOldFormatTask extends AsyncTask<File, Void, String> {

    private final Activity mActivity;
    private final ImportFilesOldFormatTask.ImportHandler mImportHandler;

    private static final String LOG_TAG = ImportFilesOldFormatTask.class.getName();

    public interface ImportHandler {
        void onImportFinished(String message);
    }

    public ImportFilesOldFormatTask(Activity mActivity, ImportHandler mImportHandler) {
        this.mActivity = mActivity;
        this.mImportHandler = mImportHandler;
    }



    @Override
    protected String doInBackground(File... params) {
        if (params.length == 0 || params[0] == null) {
            return mActivity.getString(R.string.msg_on_import_files_chosen);
        }

        String producers = mActivity.getString(R.string.label_producers);
        String drinks = mActivity.getString(R.string.label_drinks);
        String reviews = mActivity.getString(R.string.label_reviews);
        String extension = mActivity.getString(R.string.file_extension);

        List<File> producerFiles = getEntryFile(params, mActivity.getString(R.string.file_producers), extension);
        List<File> drinkFiles = getEntryFile(params, mActivity.getString(R.string.file_drinks),extension);
        List<File> reviewFiles = getEntryFile(params, mActivity.getString(R.string.file_reviews), extension);

        String message;

        if (producerFiles.size() == 1) {
            message = "\n" + importProducers(producerFiles.get(0));
        } else {
            message = "\n" + mActivity.getString(R.string.msg_wrong_number_files, producers, producerFiles.size());
        }

        if (drinkFiles.size() == 1) {
            message += "\n" + importDrinks(drinkFiles.get(0));
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, drinks, drinkFiles.size());
        }


        if (reviewFiles.size() == 1) {
            message += "\n" + importReviews(reviewFiles.get(0));
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, reviews, reviewFiles.size());
        }

        return message;
    }

    private String importProducers(File file) {
        if (file == null) {
            return mActivity.getString(R.string.msg_no_entry_file_import, mActivity.getString(R.string.label_producers));
        }

        // should be: QueryColumns.ExportAndImport.ProducerColumns.COLUMNS
        List<String> expectedColumns = Arrays.asList(QueryColumns.ImportOldFormat.ProducerColumns.COLUMNS);
        List<String> dataColumns = new ArrayList<>();
        List<List< String>> dataEntries = CsvFileReader.readCsvFileHeadingAndData(file, dataColumns);

        if (dataColumns.isEmpty() || dataEntries.isEmpty()) {
            return mActivity.getString(R.string.msg_invalid_import_file, mActivity.getString(R.string.label_producers), file.getName());
        }

        // just use columns that are in expectedColumns and in dataColumns
        String unknownColumnNames= "";
        for (int i = 0 ; i < dataColumns.size(); ++i) {
            String columnName = dataColumns.get(i);
            if (!expectedColumns.contains(columnName)) {
                unknownColumnNames += unknownColumnNames + ", ";
            }
        }


        ContentValues[] allContentValues = new ContentValues[dataEntries.size()];
        for (int j = 0 ; j < dataEntries.size() ; ++j) {
            List<String> dataEntry = dataEntries.get(j);
            ContentValues values = new ContentValues();

            for (int i = 0 ; i < dataColumns.size(); ++i) {
                String columnName = dataColumns.get(i);
                if (expectedColumns.contains(columnName)) {
                    if ("producer_location".equals(columnName)) {
                        values.put(Producer.INPUT, dataEntry.get(i));
                        values.put(Producer.LATITUDE, DatabaseContract.LocationEntry.INVALID_LAT_LNG);
                        values.put(Producer.LONGITUDE, DatabaseContract.LocationEntry.INVALID_LAT_LNG);
                        values.put(Producer.COUNTRY, "");
                        values.put(Producer.FORMATTED_ADDRESS, DatabaseContract.LocationEntry.GEOCODE_ME);
                    } else {
                        values.put(columnName, dataEntry.get(i));
                    }
                }
            }
            allContentValues[j] = values;
        }
        //  modify bulkInsert to replace on demand - onConflict should work...
        int number = mActivity.getContentResolver().bulkInsert(DatabaseContract.ProducerEntry.CONTENT_URI,
                allContentValues);

        String appendix = "";
        if (unknownColumnNames.length() > 0) {
            unknownColumnNames = unknownColumnNames.substring(0, unknownColumnNames.length() - 2);
            appendix = mActivity.getString(R.string.msg_unused_column_names, unknownColumnNames);
        }

        return mActivity.getString(R.string.msg_entries_imported, number, mActivity.getString(R.string.label_producers), appendix);
    }


    private String importDrinks(File file) {
        Uri contentUri = DatabaseContract.DrinkEntry.CONTENT_URI;
        String drinks = mActivity.getString(R.string.label_drinks);

        List<String> expectedColumns = Arrays.asList(QueryColumns.ExportAndImport.DrinkColumns.COLUMNS);
        List<String> dataColumns = new ArrayList<>();
        List<List< String>> dataEntries = CsvFileReader.readCsvFileHeadingAndData(file, dataColumns);

        if (dataColumns.isEmpty() || dataEntries.isEmpty()) {
            return mActivity.getString(R.string.msg_invalid_import_file, drinks, file.getName());
        }

        // just use columns that are in expectedColumns and in dataColumns
        String unknownColumnNames= "";
        for (int i = 0 ; i < dataColumns.size(); ++i) {
            String columnName = dataColumns.get(i);
            if (!expectedColumns.contains(columnName)) {
                unknownColumnNames += unknownColumnNames + ", ";
            }
        }


        ContentValues[] allContentValues = new ContentValues[dataEntries.size()];
        for (int j = 0 ; j < dataEntries.size() ; ++j) {
            List<String> dataEntry = dataEntries.get(j);
            ContentValues values = new ContentValues();

            for (int i = 0 ; i < dataColumns.size(); ++i) {
                String columnName = dataColumns.get(i);
                if (expectedColumns.contains(columnName)) {
                    values.put(columnName, dataEntry.get(i));
                }

            }
            allContentValues[j] = values;
        }
        //  modify bulkInsert to replace on demand - onConflict should work...
        int number = mActivity.getContentResolver().bulkInsert(contentUri, allContentValues);

        String appendix = "";
        if (unknownColumnNames.length() > 0) {
            unknownColumnNames = unknownColumnNames.substring(0, unknownColumnNames.length() - 2);
            appendix = mActivity.getString(R.string.msg_unused_column_names, unknownColumnNames);
        }

        return mActivity.getString(R.string.msg_entries_imported, number, drinks, appendix);
    }


    private String importReviews(File file) {
        if (file == null) {
            return mActivity.getString(R.string.msg_no_entry_file_import, mActivity.getString(R.string.label_reviews));
        }

        List<String> expectedColumns = Arrays.asList(QueryColumns.ImportOldFormat.ReviewColumns.COLUMNS);
        List<String> dataColumns = new ArrayList<>();
        List<List< String>> dataEntries = CsvFileReader.readCsvFileHeadingAndData(file, dataColumns);

        if (dataColumns.isEmpty() || dataEntries.isEmpty()) {
            return mActivity.getString(R.string.msg_invalid_import_file, mActivity.getString(R.string.label_reviews), file.getName());
        }


        // just use columns that are in expectedColumns and in dataColumns
        String unknownColumnNames= "";
        for (int i = 0 ; i < dataColumns.size(); ++i) {
            String columnName = dataColumns.get(i);
            if (!expectedColumns.contains(columnName)) {
                unknownColumnNames += unknownColumnNames + ", ";
            }
        }


        ContentValues[] reviewContentValues = new ContentValues[dataEntries.size()];
        Map<String, ContentValues> userContentValues = new HashMap<>();
        Map<String, ContentValues> locationContentValues = new HashMap<>();
        for (int j = 0 ; j < dataEntries.size() ; ++j) {
            List<String> dataEntry = dataEntries.get(j);
            ContentValues values = new ContentValues();

            for (int i = 0 ; i < dataColumns.size(); ++i) {
                String columnName = dataColumns.get(i);
                if (expectedColumns.contains(columnName)) {
                    if ("review_user_name".equals(columnName)) {
                        String userName = dataEntry.get(i);
                        String userId = Utils.calcUserId(userName);
                        if (!userContentValues.containsKey(userId)) {
                            userContentValues.put(userId,
                                    DatabaseHelper.buildUserValues(userId, userName));
                        }
                        values.put(Review.USER_ID, userId);

                    } else if ("review_location".equals(columnName)){
                        String location = dataEntry.get(i); //only same strings are same => anymore through excel-tuning...

                        if (!location.isEmpty()) {
                            String locationId = Utils.calcLocationId(location);
                            if (!locationContentValues.containsKey(locationId)) {
                                locationContentValues.put(locationId,
                                        DatabaseHelper.buildLocationValues(locationId,
                                                location, DatabaseContract.LocationEntry.INVALID_LAT_LNG,
                                                DatabaseContract.LocationEntry.INVALID_LAT_LNG, null,
                                                DatabaseContract.LocationEntry.GEOCODE_ME, ""));
                            }
                            values.put(Review.LOCATION_ID, locationId);
                        } else {
                            values.put(Review.LOCATION_ID, "");
                        }

                    } else {
                        values.put(columnName, dataEntry.get(i));
                    }
                }

            }
            reviewContentValues[j] = values;
        }

        int numberUsers = mActivity.getContentResolver().bulkInsert(DatabaseContract.UserEntry.CONTENT_URI,
                userContentValues.values().toArray(new ContentValues[userContentValues.size()]));
        int numberLocations = mActivity.getContentResolver().bulkInsert(DatabaseContract.LocationEntry.CONTENT_URI,
                locationContentValues.values().toArray( new ContentValues[locationContentValues.size()]));

        //  modify bulkInsert to replace on demand - onConflict should work...
        int number = mActivity.getContentResolver().bulkInsert(DatabaseContract.ReviewEntry.CONTENT_URI, reviewContentValues);

        String appendix = "";
        if (unknownColumnNames.length() > 0) {
            unknownColumnNames = unknownColumnNames.substring(0, unknownColumnNames.length() - 2);
            appendix = mActivity.getString(R.string.msg_unused_column_names, unknownColumnNames);
        }

        String s = mActivity.getString(R.string.msg_entries_imported, number, mActivity.getString(R.string.label_reviews), appendix) + ", "
                + mActivity.getString(R.string.msg_entries_imported, numberUsers, mActivity.getString(R.string.label_users), "") + ", "
                + mActivity.getString(R.string.msg_entries_imported, numberLocations, mActivity.getString(R.string.label_locations), "");
        Log.v(LOG_TAG, "importReviews, hashCode=" + this.hashCode() + ", " + "file = [" + file + "], s:" + s);
        return s;
    }


    @Override
    protected void onPostExecute(String s) {
        TaskUtils.updateWidgets(mActivity);
        mImportHandler.onImportFinished(s);
    }


    private List<File> getEntryFile(File[] files, String entryName, String extension) {
        List<File> matchingFiles = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(ExportToDirTask.EXPORT_PREFIX) && name.contains(entryName) && name.endsWith(extension)) {
                matchingFiles.add(file);
            }
        }
        return matchingFiles;
    }
}
