package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.csv.CsvFileReader;

import java.io.File;
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

public class ImportFilesTask extends AsyncTask<File, Void, String> {

    private final Activity mActivity;
    private final ImportHandler mImportHandler;

    private static final String LOG_TAG = ImportFilesTask.class.getName();


    public interface ImportHandler {
        void onImportFinished(String message);
    }

    public ImportFilesTask(Activity mActivity, ImportHandler mImportHandler) {
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

        // TODO: later multiple files might be allowed - but not for now...
        List<File> producerFiles = getEntryFile(params,
                mActivity.getString(R.string.file_producers), extension);
        List<File> drinkFiles = getEntryFile(params,
                mActivity.getString(R.string.file_drinks),extension);
        List<File> reviewFiles = getEntryFile(params,
                mActivity.getString(R.string.file_reviews), extension);

        String message;
        if (producerFiles.size() == 1) {
            message = importEntries(DatabaseContract.ProducerEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.ProducerColumns.COLUMNS,
                    producerFiles.get(0), producers);
        } else {
            message = mActivity.getString(R.string.msg_wrong_number_files, producers, producerFiles.size());
        }

        if (drinkFiles.size() == 1) {
            message += "\n" + importEntries(DatabaseContract.DrinkEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.DrinkColumns.COLUMNS,
                    drinkFiles.get(0), drinks);
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, drinks, drinkFiles.size());
        }

        if (reviewFiles.size() == 1) {
            message += "\n" + importEntries(DatabaseContract.ReviewEntry.CONTENT_URI,
                    QueryColumns.ExportAndImport.ReviewColumns.COLUMNS,
                    reviewFiles.get(0), reviews);
        } else {
            message += "\n" + mActivity.getString(R.string.msg_wrong_number_files, reviews, reviewFiles.size());
        }
        
        return message;
    }

    @Override
    protected void onPostExecute(String s) {
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
