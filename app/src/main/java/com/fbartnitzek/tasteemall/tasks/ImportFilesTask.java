package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.csv.CsvFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class ImportFilesTask extends ImportFileBaseTask {


    private final ImportHandler mImportHandler;

    private static final String LOG_TAG = ImportFilesTask.class.getName();


    public interface ImportHandler {
        void onImportFinished(String message);
    }

    public ImportFilesTask(Activity mActivity, ImportHandler mImportHandler) {
        super(mActivity);
        this.mImportHandler = mImportHandler;
    }

    @Override
    protected String doInBackground(Uri... uris) {
        if (uris.length == 0 || uris[0] == null) {
            return mActivity.getString(R.string.msg_on_import_files_chosen);
        }
        Log.v(LOG_TAG, uris.length + " uris: " + Arrays.toString(uris));

        List<File> files = new ArrayList<>();
        for (Uri uri : uris) {
            File file = createTempFile(uri);
            if (file != null) {
                files.add(file);
            } else {
                Log.w(LOG_TAG, "temp file could not be created from uri: " + uri);
            }
        }

        // TODO: later multiple files might be allowed - but not for now...
        List<File> locationFiles = getEntryFile(files, "location_id;");
        List<File> producerFiles = getEntryFile(files, "producer_id;");
        List<File> drinkFiles = getEntryFile(files, "drink_id;");
        List<File> userFiles = getEntryFile(files, "user_id;");
        List<File> reviewFiles = getEntryFile(files, "review_id;");

        String locations = mActivity.getString(R.string.label_locations);
        String producers = mActivity.getString(R.string.label_producers);
        String drinks = mActivity.getString(R.string.label_drinks);
        String users = mActivity.getString(R.string.label_users);
        String reviews = mActivity.getString(R.string.label_reviews);

        String message;
        if (locationFiles.size() == 1) {
            message = importEntries(DatabaseContract.LocationEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.LocationColumns.COLUMNS, locationFiles.get(0), locations);
        } else {
            message = mActivity.getString(R.string.msg_wrong_number_files, locations, locationFiles.size());
        }

        if (producerFiles.size() == 1) {
            message += "\n" + importEntries(DatabaseContract.ProducerEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.ProducerColumns.COLUMNS, producerFiles.get(0), producers);
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, producers, producerFiles.size());
        }

        if (drinkFiles.size() == 1) {
            message += "\n" + importEntries(DatabaseContract.DrinkEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.DrinkColumns.COLUMNS,
                    drinkFiles.get(0), drinks);
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, drinks, drinkFiles.size());
        }

        if (userFiles.size() == 1) {
            message += "\n" + importEntries(DatabaseContract.UserEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.UserColumns.COLUMNS, userFiles.get(0), users);
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, users, userFiles.size());
        }

        if (reviewFiles.size() == 1) {
            message += "\n" + importEntries(DatabaseContract.ReviewEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.ReviewColumns.COLUMNS, reviewFiles.get(0), reviews);
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, reviews, reviewFiles.size());
        }

        deleteTempFiles();
        
        return message;
    }

    @Override
    protected void onPostExecute(String s) {
        TaskUtils.updateWidgets(mActivity);
        mImportHandler.onImportFinished(s);
    }

    private String importEntries(Uri contentUri, String[] dbColumns, File file, String entries) {
        if (file == null) {
            return mActivity.getString(R.string.msg_no_entry_file_import, entries);
        }

        List<String> expectedColumns = Arrays.asList(dbColumns);
        List<String> dataColumns = new ArrayList<>();
        List<List< String>> dataEntries = CsvFileReader.readCsvFileHeadingAndData(file, dataColumns);

        if (dataColumns.isEmpty() || dataEntries.isEmpty()) {
            return mActivity.getString(R.string.msg_invalid_import_file, entries, file.getName());
        }

        // TODO: some validation on nonEmpty and primaryKey (later)

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
        // TODO: when producer cannot bulk insert, it counts as inserted...
        int number = mActivity.getContentResolver().bulkInsert(contentUri, allContentValues);

        String appendix = "";
        if (unknownColumnNames.length() > 0) {
            unknownColumnNames = unknownColumnNames.substring(0, unknownColumnNames.length() - 2);
            appendix = mActivity.getString(R.string.msg_unused_column_names, unknownColumnNames);
        }

        return mActivity.getString(R.string.msg_entries_imported, number, entries, appendix);
    }

    private List<File> getEntryFile(List<File> files, String startingEntityId) {
        List<File> matchingFiles = new ArrayList<>();
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String firstLine = reader.readLine();
                if (firstLine.startsWith(startingEntityId)) {
                    Log.v(LOG_TAG, "matching file: " + file.getName());
                    matchingFiles.add(file);
                } else {
                    Log.v(LOG_TAG, "not matching file: " + file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return matchingFiles;
    }

}
