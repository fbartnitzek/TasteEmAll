package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ImportFileBaseTask extends AsyncTask<Uri, Void, String> {

    final Activity mActivity;
    private static final String TEMP_FILE = "temp_import_";

    public static final String LOG_TAG = ImportFileBaseTask.class.getName();

    public ImportFileBaseTask(Activity mActivity) {
        this.mActivity = mActivity;
    }

    void deleteTempFiles() {
        final File[] files = mActivity.getCacheDir().listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.getName().contains(TEMP_FILE)) {
                    Log.v(LOG_TAG, "deleteTempFiles - file:" + file.getName());
                    file.delete();
                }
            }
        }
    }

    File createTempFile(Uri uri) {
        String tmpFileName = TEMP_FILE + uri.getLastPathSegment().replace(":", "_");
        Log.v(LOG_TAG, "tmpFileName: " + tmpFileName);

        final File file = new File(mActivity.getCacheDir(), tmpFileName);
        try (InputStream inputStream = mActivity.getContentResolver().openInputStream(uri);
             OutputStream output = new FileOutputStream(file)) {
            final byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
            return file;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
