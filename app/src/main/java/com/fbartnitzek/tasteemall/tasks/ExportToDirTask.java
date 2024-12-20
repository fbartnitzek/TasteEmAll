package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.csv.CsvFileWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
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

public class ExportToDirTask extends AsyncTask<String, Void, String>{

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

    private void insertFileViaMediaStore(String displayName, File file, String path) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        Uri myUri = mActivity.getContentResolver().insert(
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                values);
        try (OutputStream outStream = mActivity.getContentResolver().openOutputStream(myUri)) {
            outStream.write(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "written file via MediaStore");
    }

    void deleteTempFiles() {
        final File[] files = mActivity.getCacheDir().listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.getName().contains(EXPORT_PREFIX)) {
                    Log.v(LOG_TAG, "deleteTempFiles - file:" + file.getName());
                    file.delete();
                }
            }
        }
    }

    @Override
    protected String doInBackground(String... paths) {
        Log.v(LOG_TAG, "doInBackground, hashCode=" + this.hashCode() + ", " + "paths = [" +
                Arrays.toString(paths) + "]");
        if (paths.length == 0) {
            return mActivity.getString(R.string.msg_no_export_directory);
        }

        String path = paths[0];

        String message;
        message = exportEntries(DatabaseContract.LocationEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.LocationColumns.COLUMNS, path,
                mActivity.getString(R.string.file_locations), mActivity.getString(R.string.label_locations));

        message += exportEntries(DatabaseContract.ProducerEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.ProducerColumns.COLUMNS, path,
                mActivity.getString(R.string.file_producers), mActivity.getString(R.string.label_producers));

        message += "\n" + exportEntries(DatabaseContract.DrinkEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.DrinkColumns.COLUMNS, path,
                mActivity.getString(R.string.file_drinks), mActivity.getString(R.string.label_drinks));

        message += "\n" + exportEntries(DatabaseContract.UserEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.UserColumns.COLUMNS, path,
                mActivity.getString(R.string.file_users), mActivity.getString(R.string.label_users));

        message += "\n" + exportEntries(DatabaseContract.ReviewEntry.CONTENT_URI,
                QueryColumns.ExportAndImport.ReviewColumns.COLUMNS, path,
                mActivity.getString(R.string.file_reviews), mActivity.getString(R.string.label_reviews));

        deleteTempFiles();

        return message;
    }

    @Override
    protected void onPostExecute(String s) {
        mExportHandler.onExportFinished(s);
    }

    private String exportEntries(Uri contentUri, String[] columns, String path, String fileEntries,
                                 String msgEntries) {

        String fileName = EXPORT_PREFIX + Utils.getCurrentLocalTimePrefix() + "_" + fileEntries +  mActivity.getString(R.string.file_extension);

        final File file = new File(mActivity.getCacheDir(), fileName);

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
                message = mActivity.getString(R.string.msg_entries_exported_success_shorter,
                        msgEntries, cursor.getCount());
            } else {
                Log.e(LOG_TAG, "exportEntries - CSVException: " + error + ", hashCode=" + this.hashCode() + ", " + "contentUri = [" + contentUri + "], columns = [" + Arrays.toString(columns) + "], fileEntries = [" + fileEntries+ "]");
                message = mActivity.getString(R.string.msg_writing_entries_failed, msgEntries);
            }

            cursor.close();

            Log.v(LOG_TAG, "written temp file");
            insertFileViaMediaStore(fileName, file, path);
        } else {
            message = mActivity.getString(R.string.msg_export_entries_failed_no_cursor, msgEntries);
        }
        return message;
    }
}
