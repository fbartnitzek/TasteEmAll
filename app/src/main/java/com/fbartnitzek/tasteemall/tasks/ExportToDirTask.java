package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.csv.CsvFileWriter;

import java.io.File;
import java.util.ArrayList;
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

public class ExportToDirTask extends AsyncTask<File, Void, String>{

    public static final String EXPORT_PREFIX = "export_";
    private final Activity mActivity;
    private final ExportHandler mExportHandler;

    private static final String LOG_TAG = ExportToDirTask.class.getName();
    
    public interface ExportHandler {
        void onExportFinished(String message);
    }

    public ExportToDirTask(Activity mActivity, ExportHandler mExportHandler) {
        this.mActivity = mActivity;
        this.mExportHandler = mExportHandler;
    }

    @Override
    protected String doInBackground(File... params) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "params = [" + params + "]");
        if (params.length == 0) {
            return mActivity.getString(R.string.toast_no_export_directory);
        }
        File dir = params[0];
        if (dir == null || !dir.isDirectory() || !dir.canWrite()) {
            return mActivity.getString(R.string.toast_export_files_no_write,
                    dir == null ? "" : dir.getAbsolutePath());
        }

        String message;
        // TODO: different strings for message and filename (i18n between phones...)
        message = exportEntries(DatabaseContract.ProducerEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.ProducerColumns.COLUMNS,
                dir, mActivity.getString(R.string.label_producers));

        message += "\n" + exportEntries(DatabaseContract.DrinkEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.DrinkColumns.COLUMNS,
                dir, mActivity.getString(R.string.label_drinks));

        message += "\n" + exportEntries(DatabaseContract.ReviewEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.ReviewColumns.COLUMNS,
                dir, mActivity.getString(R.string.label_reviews));

        return message;
    }

    @Override
    protected void onPostExecute(String s) {
        mExportHandler.onExportFinished(s);
    }

    private String exportEntries(Uri contentUri, String[] columns, File dir, String entries) {

        String fileName = EXPORT_PREFIX + entries + Utils.getCurrentLocalTimePrefix() + ".csv";
        File file = new File(dir, fileName);

        Log.v(LOG_TAG, "exportEntries, File: " + file.getAbsolutePath() + ", hashCode=" + this.hashCode() + ", " + "contentUri = [" + contentUri + "], columns = [" + columns + "], dir = [" + dir + "], entries = [" + entries + "]");
        
        Cursor cursor = mActivity.getContentResolver().query(
                contentUri, columns, null, null, null);
        
        String message;
        if (cursor != null) {
            List<List<String>> dataEntries = new ArrayList<>();
            while (cursor.moveToNext()) {   //every attribute is string
                List<String> dataEntry =new ArrayList<>();
                for (int i = 0; i < columns.length; ++i) {
                    dataEntry.add(cursor.getString(i));
                }
                dataEntries.add(dataEntry);
            }

            String error = CsvFileWriter.writeFile(
                    columns,
                    dataEntries,
                    file);
            if (error == null) {
                message = mActivity.getString(R.string.toast_entries_exported_success,
                        entries, cursor.getCount(), file.getName());
            } else {
                Log.e(LOG_TAG, "exportEntries - CSVException: " + error + ", hashCode=" + this.hashCode() + ", " + "contentUri = [" + contentUri + "], columns = [" + columns + "], dir = [" + dir + "], entries = [" + entries + "]");
                message = mActivity.getString(R.string.toast_writing_entries_failed, entries);
            }

            cursor.close();
        } else {
            message = mActivity.getString(R.string.toast_export_entries_failed_no_cursor, entries);
        }
        return message;
    }
}
