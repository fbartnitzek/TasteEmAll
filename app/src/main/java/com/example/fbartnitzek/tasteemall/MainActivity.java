package com.example.fbartnitzek.tasteemall;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fbartnitzek.tasteemall.tasks.ExportToDirTask;
import com.example.fbartnitzek.tasteemall.tasks.GeocodeReviewsTask;
import com.example.fbartnitzek.tasteemall.tasks.ImportFilesTask;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ExportToDirTask.ExportHandler, ImportFilesTask.ImportHandler {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    private static final int REQUEST_EXPORT_DIR_CODE = 1233;
    private static final int REQUEST_IMPORT_FILES_CODE = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // explicitly add fragment, toolbar from fragment...
        if (findViewById(R.id.fragment_container) != null) {

            MainFragment fragment = getFragment();
            if (fragment == null) { //create new fragment
                Log.v(LOG_TAG, "onCreate - add a new fragment, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                fragment = new MainFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment, MAIN_FRAGMENT_TAG)
                        .commit();

            } else {    // use old fragment
                Log.v(LOG_TAG, "onCreate - old fragment exists, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            }

            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            }

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

        // add toolbar from fragment (when view is initialized)
    }

    private MainFragment getFragment() {
        return (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
    }

    // might be better in the fragment...
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "onSaveInstanceState - just call super, hashCode=" + this.hashCode() + ", " + "outState = [" + outState + "]");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_geocode:
                startGeocoding();
                return true;
            case R.id.action_export:
                startExport();
                return true;
            case R.id.action_import:
                startImport();
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);   //may call fragment for others
    }

    private void startExport() {
        Log.v(LOG_TAG, "startExport, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(this, FilePickerActivity.class);

        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        intent.putExtra(FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, REQUEST_EXPORT_DIR_CODE);
    }

    private void startImport() {
        Log.v(LOG_TAG, "startImport, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(this, FilePickerActivity.class);

        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, REQUEST_IMPORT_FILES_CODE);
    }

    private void startGeocoding() {
        Log.v(LOG_TAG, "startGeocoding, hashCode=" + this.hashCode() + ", " + "");
        new GeocodeReviewsTask(this, new GeocodeReviewsTask.GeocodeReviewsUpdatedHandler() {
            @Override
            public void onUpdatedReviews(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        }).execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (requestCode == REQUEST_EXPORT_DIR_CODE || requestCode == REQUEST_IMPORT_FILES_CODE
                && resultCode == AppCompatActivity.RESULT_OK) {
            Uri uri = null;
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {

                List<File> files = new ArrayList<>();
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            uri = clip.getItemAt(i).getUri();
                            Log.v(LOG_TAG, "onActivityResult, uri=" + uri + ", hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                            files.add(new File(uri.getPath()));
                            // Do something with the URI
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            uri = Uri.parse(path);  // TODO: might be useless conversion...
                            Log.v(LOG_TAG, "onActivityResult, uri=" + uri + ", hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                            files.add(new File(uri.getPath()));
                        }
                    }
                }

                if (!files.isEmpty() && requestCode == REQUEST_IMPORT_FILES_CODE) {
                    new ImportFilesTask(this, this).execute(files);

                }

            } else {
                Log.v(LOG_TAG, "onActivityResult - single file, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                uri = data.getData();
                // Do something with the URI
                if (uri != null && requestCode == REQUEST_EXPORT_DIR_CODE) {
                    //somehow it returned a filepath (confusing use of multiple flag...
                    new ExportToDirTask(this, this).execute(new File(uri.getPath()));
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // TODO: show Msg Activity/Dialog/Fragment (LONG is to short ...) with ok button

    @Override
    public void onExportFinished(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onImportFinished(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
