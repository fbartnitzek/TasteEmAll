package com.fbartnitzek.tasteemall;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.addentry.AddLocationActivity;
import com.fbartnitzek.tasteemall.addentry.AddProducerActivity;
import com.fbartnitzek.tasteemall.addentry.AddReviewActivity;
import com.fbartnitzek.tasteemall.data.BundleBuilder;
import com.fbartnitzek.tasteemall.filter.EntityFilterDialogFragment;
import com.fbartnitzek.tasteemall.location.ShowMapActivity;
import com.fbartnitzek.tasteemall.mainpager.BasePagerFragment;
import com.fbartnitzek.tasteemall.mainpager.DrinkPagerFragment;
import com.fbartnitzek.tasteemall.mainpager.LocationPagerFragment;
import com.fbartnitzek.tasteemall.mainpager.ProducerPagerFragment;
import com.fbartnitzek.tasteemall.mainpager.ReviewPagerFragment;
import com.fbartnitzek.tasteemall.mainpager.UserPagerFragment;
import com.fbartnitzek.tasteemall.showentry.ShowReviewActivity;
import com.fbartnitzek.tasteemall.tasks.ExportToDirTask;
import com.fbartnitzek.tasteemall.tasks.GeocodeAllLocationsTask;
import com.fbartnitzek.tasteemall.tasks.ImportAllInOneFileTask;
import com.fbartnitzek.tasteemall.tasks.ImportFilesOldFormatTask;
import com.fbartnitzek.tasteemall.tasks.ImportFilesTask;
import com.fbartnitzek.tasteemall.ui.CustomSpinnerAdapter;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fbartnitzek.tasteemall.filter.EntityFilterTabFragment.BASE_ENTITY;

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


public class MainActivity extends AppCompatActivity implements
        GeocodeAllLocationsTask.GeocodeProducersUpdateHandler, ImportFilesTask.ImportHandler,
        ImportFilesOldFormatTask.ImportHandler, ExportToDirTask.ExportHandler,
        SearchView.OnQueryTextListener, View.OnClickListener,
        EntityFilterDialogFragment.GenericFilterFinishListener, ImportAllInOneFileTask.ImportAllInOneHandler {

    private static final int NUM_PAGES = 5;
    private static final String LOG_TAG = MainActivity.class.getName();
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    protected CustomSpinnerAdapter mSpinnerAdapter;
    protected Spinner mDrinkTypeSpinner;

    private String mSearchPattern;
    private static final String STATE_SEARCH_PATTERN = "STATE_SEARCH_PATTERN";
    private static final String STATE_PAGER_POSITION = "STATE_PAGER_POSITION";
    private static final String STATE_PRODUCERS_TO_GEOCODE = "STATE_PRODUCERS_TO_GEOCODE";
    private static final String STATE_REVIEW_LOCATIONS_TO_GEOCODE = "STATE_REVIEW_LOCATIONS_TO_GEOCODE";


    private static final int REQUEST_EXPORT_DIR_CODE = 1233;
    private static final int REQUEST_IMPORT_FILES_CODE = 1234;
    private static final int REQUEST_EDIT_PRODUCER_GEOCODE = 1235;
    private static final int REQUEST_EDIT_REVIEW_LOCATION_GEOCODE = 32421;
//    private static final int REQUEST_GENERIC_SEARCH_DIALOG = 32454;

    protected static final int ADD_REVIEW_REQUEST = 546;

    private ArrayList<Uri> mProducerLocationUris;
    private ArrayList<Uri> mReviewLocationUris;
    private List<File> mFiles;
    private int mPagerPosition = -1;
    private boolean mReloaded = false;

    // TODO: share filtered list

    // TODO: rotation everywhere - then new version for S6


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setHomeButtonEnabled(false);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

//        mSearchPattern = null;
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(STATE_SEARCH_PATTERN)) {
                mSearchPattern = savedInstanceState.getString(STATE_SEARCH_PATTERN);
                mReloaded = true;
            }
            if (savedInstanceState.containsKey(STATE_PRODUCERS_TO_GEOCODE)) {
                mProducerLocationUris = savedInstanceState.getParcelableArrayList(STATE_PRODUCERS_TO_GEOCODE);
            }
            if (savedInstanceState.containsKey(STATE_REVIEW_LOCATIONS_TO_GEOCODE)) {
                mReviewLocationUris = savedInstanceState.getParcelableArrayList(STATE_REVIEW_LOCATIONS_TO_GEOCODE);
            }
            if (savedInstanceState.containsKey(STATE_PAGER_POSITION)) {
                mPagerPosition = savedInstanceState.getInt(STATE_PAGER_POSITION);
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mViewPager.setCurrentItem(mPagerPosition < 0 ? 0 : mPagerPosition);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
//                Log.v(LOG_TAG, "onPageSelected, hashCode=" + this.hashCode() + ", " + "position = [" + position + "]");
                mPagerPosition = position;
                getFragment(position).fragmentBecameVisible();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


        createSpinner();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

    }

    private String getCurrentFragmentEntity() {
        if (mViewPager != null && mPagerAdapter != null) {
            BasePagerFragment fragment = (BasePagerFragment) mPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            return fragment.getEntity();
        } else {
            return null;
        }
    }

    private BasePagerFragment getFragment(int position) {
//        Log.v(LOG_TAG, "getFragment, hashCode=" + this.hashCode() + ", " + "position = [" + position + "]");
        // helpful advice: http://stackoverflow.com/questions/17845641/alternative-for-the-onresume-during-fragment-switching
        return (BasePagerFragment) mPagerAdapter.instantiateItem(mViewPager, position);
    }

    private void restartCurrentFragmentLoader() { // guarded restart
//        Log.v(LOG_TAG, "restartCurrentFragmentLoader, hashCode=" + this.hashCode() + ", " + "");
        if (mViewPager != null && mPagerAdapter != null) {
            BasePagerFragment fragment = (BasePagerFragment) mPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            if (fragment != null) {
                fragment.restartLoader();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        Log.v(LOG_TAG, "onSaveInstanceState, hashCode=" + this.hashCode() + ", " + "outState = [" + outState + "]");
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SEARCH_PATTERN, mSearchPattern);
        outState.putParcelableArrayList(STATE_PRODUCERS_TO_GEOCODE, mProducerLocationUris);
        outState.putParcelableArrayList(STATE_REVIEW_LOCATIONS_TO_GEOCODE, mReviewLocationUris);
        outState.putInt(STATE_PAGER_POSITION, mPagerPosition);
    }

    public String getSearchPattern() {
        return mSearchPattern == null ? "" : mSearchPattern;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        Log.v(LOG_TAG, "onQueryTextSubmit, hashCode=" + this.hashCode() + ", " + "query = [" + query + "]");
        if (mReloaded){
            mReloaded = false;
            return false;
        }
        mSearchPattern = query;
        setJsonUriInCurrentFragment(null);
        restartCurrentFragmentLoader();
        return false;
    }

    private void setJsonUriInCurrentFragment(Uri uri) {
        if (mViewPager != null && mViewPager.getCurrentItem() > -1) {
            BasePagerFragment fragment = getFragment(mViewPager.getCurrentItem());
            if (fragment != null) {
                fragment.setJsonUri(uri);
            }
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Log.v(LOG_TAG, "onQueryTextChange, hashCode=" + this.hashCode() + ", " + "newText = [" + newText + "]");
        if (mReloaded){
            mReloaded = false;
            return false;
        }
        mSearchPattern = newText;
        setJsonUriInCurrentFragment(null);
        restartCurrentFragmentLoader();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add) {
            Intent intent = new Intent(this, AddReviewActivity.class);

            Bundle bundle = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // no really useful element name transition possible...
                BasePagerFragment fragment = getFragment(mViewPager.getCurrentItem());
                if (fragment != null) {
                    bundle = ActivityOptions.makeSceneTransitionAnimation(this,
                            v, getString(fragment.getSharedTransitionId())
                    ).toBundle();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && bundle != null) {
                startActivityForResult(intent, ADD_REVIEW_REQUEST, bundle);
            } else {
                startActivityForResult(intent, ADD_REVIEW_REQUEST);
            }
        }
    }


    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(LOG_TAG, "getItem, hashCode=" + this.hashCode() + ", " + "position = [" + position + "]");
            switch (position) {
                case 0:
                    return new ReviewPagerFragment();
                case 1:
                    return new DrinkPagerFragment();
                case 2:
                    return new ProducerPagerFragment();
                case 3:
                    return new LocationPagerFragment();
                case 4:
                    return new UserPagerFragment();
                default:
                    Toast.makeText(MainActivity.this, "not allowed selection!", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("not allowed selection!");
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }


    private void createSpinner() {
//        Log.v(LOG_TAG, "createSpinner, hashCode=" + this.hashCode() + ", " + "");

        mDrinkTypeSpinner = (Spinner) findViewById(R.id.spinner_type);

        String[] typesArray = getResources().getStringArray(R.array.pref_type_filter_values);
        ArrayList<String> typesList = new ArrayList<>(Arrays.asList(typesArray));

        mSpinnerAdapter = new CustomSpinnerAdapter(this.getApplicationContext(), typesList,
                R.layout.spinner_row, R.string.a11y_drink_type_simple);
        mDrinkTypeSpinner.setAdapter(mSpinnerAdapter);

        initSpinnerType();

        mDrinkTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String drinkType = parent.getItemAtPosition(position).toString();
                Utils.setSharedPrefsDrinkType(MainActivity.this, drinkType);
                mDrinkTypeSpinner.setContentDescription(getString(R.string.a11y_chosen_drinkType, drinkType));
                setJsonUriInCurrentFragment(null);

                restartCurrentFragmentLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initSpinnerType() {
//        Log.v(LOG_TAG, "updateSpinnerType - drinkType: " + mDrinkType + ", hashCode=" + this.hashCode() + ", " + "");

        if (mSpinnerAdapter != null) {
            String drinkType = Utils.getDrinkTypeFromSharedPrefs(this, true);
            int spinnerPosition = mSpinnerAdapter.getPosition(drinkType);
            if (spinnerPosition > -1) {
                mDrinkTypeSpinner.setSelection(spinnerPosition);
                mDrinkTypeSpinner.setContentDescription(getString(R.string.a11y_chosen_drinkType, drinkType));
                mDrinkTypeSpinner.clearFocus();
            }
        }
    }


    // menu stuff


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "], searchPattern=" + mSearchPattern);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.search_all);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        // todo: restore the right way...   - still not working - should work!...?
        if (!TextUtils.isEmpty(mSearchPattern)) {
            item.expandActionView();
            searchView.setQuery(mSearchPattern, true);
            searchView.clearFocus();
//            restartCurrentFragmentLoader();
        }
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
            case R.id.action_export:    // TODO: class-based- and all-in-1-export
                startExport();
                return true;
            case R.id.action_import:    // TODO: restore and class-based-import
                startImport();
                return true;
            case R.id.action_show_map:
                startShowMap2();
                return true;
//            case R.id.action_demo_search:   // TODO: remove after MultiSelect is working...
//                startGenericDemoSearch();
//                return true;
            case R.id.action_delete_all:
                startDeleteAll();
                return true;
            case R.id.search_generic:
                startGenericSearchDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);   //may call fragment for others
    }

    private void startGenericSearchDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EntityFilterDialogFragment filterDialog = new EntityFilterDialogFragment();
        filterDialog.setArguments(new BundleBuilder()
                .putString(BASE_ENTITY, getCurrentFragmentEntity())
                .build());

        filterDialog.show(fm, "EntityFilterDialogFragment");
    }

    /*private void startGenericDemoSearch() {
        JSONObject json = new JSONObject();
        Uri jsonUri = null;
        try {
//            JSONArray array = new JSONArray();
//
//            array.put(DatabaseContract.encodeValue("+"));
//            array.put(DatabaseContract.encodeValue("+/++"));
//            array.put(DatabaseContract.encodeValue("++"));
//            json.put(Review.ENTITY,
//                    new JSONObject().put(Review.RATING,
//                            new JSONObject().put(DatabaseContract.Operations.IS, array)));

            json.put(Review.ENTITY, new JSONObject()
                    .put(Drink.ENTITY, new JSONObject()
                            .put(Drink.TYPE, new JSONObject().put(DatabaseContract.Operations.IS, "beer"))
                    )
                    .put(DatabaseContract.OR, new JSONObject()
                            .put(Review.ENTITY, new JSONObject()
                                    .put(Drink.ENTITY, new JSONObject()
                                            .put(Drink.NAME, new JSONObject().put(DatabaseContract.Operations.CONTAINS, "brew"))
                                            .put(Producer.ENTITY, new JSONObject()
                                                    .put(Producer.NAME, new JSONObject().put(DatabaseContract.Operations.CONTAINS, "brew"))
                                            )
                                    )
                            )
                    )
            );


            jsonUri = DatabaseContract.buildUriWithJson(json);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "startGenericSearch, hashCode=" + this.hashCode() + ", json=" + json.toString() + ", jsonUri=" + jsonUri);

        BasePagerFragment fragment = (BasePagerFragment) mPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

        if (fragment != null) {
            String entity = json.keys().next();
            if (!fragment.getEntity().equals(entity)) {
                Toast.makeText(this, "only usable in " + entity + "-view", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        setJsonUriInCurrentFragment(jsonUri);
        restartCurrentFragmentLoader();
    }*/

    @Override
    public void onFinishEditDialog(Uri genericFilterUri) {
//        Log.v(LOG_TAG, "onFinishEditDialog, hashCode=" + this.hashCode() + ", " + "genericFilterUri = [" + genericFilterUri + "]");
        setJsonUriInCurrentFragment(genericFilterUri);
        restartCurrentFragmentLoader();
    }



    private void startShowMap2() {
        BasePagerFragment baseFragment = getFragment(mViewPager.getCurrentItem());

        Intent intent = new Intent(this, ShowMapActivity.class);
        intent.putExtra(ShowMapActivity.BASE_URI, baseFragment.getJsonUri());
        intent.putExtra(ShowMapActivity.BASE_ENTITY, baseFragment.getEntity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            View rootView = findViewById(R.id.pager);

            // no useful element name transition possible
//            menuView.setTransitionName(getString(R.string.shared_transition_show_map));

            // workaround: start usual activity with wrong element transition ...
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this,
                    rootView, getString(R.string.no_shared_element_transition)).toBundle();

            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }

    }

    private void startDeleteAll() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete all? Do you want to export first?")
                .setNeutralButton("export", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startExport();
                        // TODO: state CALL_DELETE_AFTERWARDS
                    }
                })
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "TODO - deleteAllTask", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this, AddProducerActivity.class);
//                        MainActivity.this.startActivityForResult(intent, REQUEST_EDIT_PRODUCER_GEOCODE);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();

    }

    private void startExport() {
//        Log.v(LOG_TAG, "startExport, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(this, FilePickerActivity.class);

        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        intent.putExtra(FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, REQUEST_EXPORT_DIR_CODE);
    }

    private void startImport() {
//        Log.v(LOG_TAG, "startImport, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(this, FilePickerActivity.class);

        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH,
                Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, REQUEST_IMPORT_FILES_CODE);
    }


    private void startGeocoding() { //all geocoding seems to work :-)
        if (Utils.isNetworkUnavailable(this)) {
            Toast.makeText(this, R.string.msg_mass_geocoder_no_network, Toast.LENGTH_SHORT).show();
//            View view = findViewById(R.id.fragment_detail_layout);
//            if (view != null) {
//                Snackbar.make(view, R.string.msg_mass_geocoder_no_network, Snackbar.LENGTH_LONG).show();
//            }
            return;
        }

        new GeocodeAllLocationsTask(this, this).execute();
    }

    @Override
    public void onUpdatedLatLngLocations(String errorMsg, String producerMsg, String reviewLocationMsg,
                                         final ArrayList<Uri> producerUris, final ArrayList<Uri> reviewUris) {

        Log.v(LOG_TAG, "onUpdatedLatLngLocations, hashCode=" + this.hashCode() + ", " + "errorMsg = [" + errorMsg + "], producerMsg = [" + producerMsg + "], reviewLocationMsg = [" + reviewLocationMsg + "], producerUris = [" + producerUris + "], reviewUris = [" + reviewUris + "]");

        if (producerUris != null) {
            mProducerLocationUris = producerUris;
        }
        if (reviewUris != null) {
            mReviewLocationUris = reviewUris;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = (errorMsg == null ? "" : "Error: " + errorMsg + "\n")
                + (producerMsg == null ? "" : producerMsg + "\n")
                + (reviewLocationMsg == null ? "" : reviewLocationMsg + "\n");
        builder.setTitle(R.string.msg_title_gps_geocoding)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (producerUris != null && !producerUris.isEmpty()){
                            showProducerGeocodeDialog(false);
                        } else if (reviewUris != null && !reviewUris.isEmpty()) {
                            showReviewLocationGeocodeDialog(false);
                        } else {
                            restartCurrentFragmentLoader();
                        }
                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (requestCode == REQUEST_EXPORT_DIR_CODE || requestCode == REQUEST_IMPORT_FILES_CODE
                && resultCode == AppCompatActivity.RESULT_OK) {
            Uri uri;
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {

                List<File> files = new ArrayList<>();
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            uri = clip.getItemAt(i).getUri();
//                            Log.v(LOG_TAG, "onActivityResult, uri=" + uri + ", hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
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
//                            Log.v(LOG_TAG, "onActivityResult, uri=" + uri + ", hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                            files.add(new File(uri.getPath()));
                        }
                    }
                }

                if (!files.isEmpty() && requestCode == REQUEST_IMPORT_FILES_CODE) {

                    // TODO: refactor afterwards without mFiles
                    // new ImportFilesTask(MPA.this, MPA.this).execute(files.toArray(new File[files.size()]));

                    mFiles = files;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("which import shall be used?")
                            .setPositiveButton("class-based", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new ImportFilesTask(MainActivity.this, MainActivity.this)
                                            .execute(mFiles.toArray(new File[mFiles.size()]));
                                    mFiles = null;
                                }
                            })
//                            .setNegativeButton("old", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    new ImportFilesOldFormatTask(MainActivity.this, MainActivity.this)
//                                            .execute(mFiles.toArray(new File[mFiles.size()]));
//                                    mFiles = null;
//                                }
//                            });
                            .setNegativeButton("all-in-1", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // multiple all-in-1-files?
                                    new ImportAllInOneFileTask(MainActivity.this, MainActivity.this)
                                            .execute(mFiles.get(0));
                                    mFiles = null;
                                    // to get all easier:
                                        // either all ids are known or lots of parallel stuff...
                                        // dialog with warning!
                                    // task won't help => needs Dialog everywhere... => Task
                                    // merge is to complicated for now :-p

                                }
                            });
                    builder.show();
                }

            } else {
//                Log.v(LOG_TAG, "onActivityResult - single file, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                uri = data.getData();
                // Do something with the URI
                if (uri != null && requestCode == REQUEST_EXPORT_DIR_CODE) {
                    //somehow it returned a filepath (confusing use of multiple flag...
                    new ExportToDirTask(this, this).execute(new File(uri.getPath()));
                }

            }

        } else if (requestCode == REQUEST_EDIT_PRODUCER_GEOCODE && resultCode == AppCompatActivity.RESULT_OK) {

            if (mProducerLocationUris == null) {
                Log.e(LOG_TAG, "onActivityResult mProducerLocationUris == null! - should never happen...");
                return;
            }

            if (mProducerLocationUris.isEmpty()) {
                if (mReviewLocationUris != null && !mReviewLocationUris.isEmpty()) {
                    showReviewLocationGeocodeDialog(true);
                } else {
                    Toast.makeText(MainActivity.this, "all geocoding done", Toast.LENGTH_SHORT).show();
                    restartCurrentFragmentLoader();
                }
            } else {
                showProducerGeocodeDialog(true);
            }

        } else if (requestCode == REQUEST_EDIT_REVIEW_LOCATION_GEOCODE && resultCode == AppCompatActivity.RESULT_OK) {
            if (mReviewLocationUris == null) {
                Log.e(LOG_TAG, "onActivityResult mReviewLocationUris == null! - should never happen");
                return;
            }

            if (mReviewLocationUris.isEmpty()) {
                Toast.makeText(MainActivity.this, "all geocoding done", Toast.LENGTH_SHORT).show();
                restartCurrentFragmentLoader();
            } else {
                showReviewLocationGeocodeDialog(true);
            }
        } else if (requestCode == ADD_REVIEW_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
// TODO bugfix: refresh current pager
            startActivity(new Intent(this, ShowReviewActivity.class).setData(data.getData()));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showProducerGeocodeDialog(boolean cont) {
        Log.v(LOG_TAG, "showProducerGeocodeDialog, hashCode=" + this.hashCode() + ", " + "cont = [" + cont + "], mProducerLocationUris=" + mProducerLocationUris);
        if (mProducerLocationUris == null || mProducerLocationUris.isEmpty()) {
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(cont ?
                getString(R.string.geocode_continue_entries_by_text, mProducerLocationUris.size(),
                        getString(R.string.label_producers)) :
                getString(R.string.geocode_entries_by_text, mProducerLocationUris.size(),
                        getString(R.string.label_producers)))
                .setPositiveButton(R.string.geocode_button, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "dialog = [" + dialog + "], which = [" + which + "]");
                        Uri lastUri = mProducerLocationUris.get(mProducerLocationUris.size() - 1);
                        mProducerLocationUris.remove(mProducerLocationUris.size() - 1);
                        Intent intent = new Intent(MainActivity.this, AddProducerActivity.class);
                        intent.setData(lastUri);
                        // geocoding implicitly or explicitly? - implicit seems better:
                        //  resolve "geocode me" whenever possible on the fly - less non-geocoded-entries

                        // TODO: bundle for transition
                        MainActivity.this.startActivityForResult(intent, REQUEST_EDIT_PRODUCER_GEOCODE);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProducerLocationUris = null;
                    }
                });
        builder.show();
    }

    private void showReviewLocationGeocodeDialog(boolean cont) {
        Log.v(LOG_TAG, "showReviewLocationGeocodeDialog, hashCode=" + this.hashCode() + ", " + "cont = [" + cont + "]");
        if (mReviewLocationUris == null || mReviewLocationUris.isEmpty()) {
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(cont ?
                getString(R.string.geocode_continue_entries_by_text, mReviewLocationUris.size(),
                        getString(R.string.label_locations)) :
                getString(R.string.geocode_entries_by_text, mReviewLocationUris.size(),
                        getString(R.string.label_locations)))
                .setPositiveButton(R.string.geocode_button, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "dialog = [" + dialog + "], which = [" + which + "]");
                        Uri lastUri = mReviewLocationUris.get(mReviewLocationUris.size() - 1);
                        mReviewLocationUris.remove(mReviewLocationUris.size() - 1);


                        Intent intent = new Intent(MainActivity.this, AddLocationActivity.class);
                        intent.setData(lastUri);
                        // geocoding implicitly or explicitly? - implicit seems better:
                        //  resolve "geocode me" whenever possible on the fly - less non-geocoded-entries

                        // TODO: bundle for transition
                        MainActivity.this.startActivityForResult(intent, REQUEST_EDIT_REVIEW_LOCATION_GEOCODE);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mReviewLocationUris = null;
                    }
                });
        builder.show();

    }

    // TODO: show Msg Activity/Dialog/Fragment (LONG is to short ...) with ok button OR Notification

    @Override
    public void onExportFinished(String message) {

        // need at least 3 lines => toast
//        Snackbar.make(findViewById(R.id.fragment_detail_layout), message, Snackbar.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onImportFinished(String message) {

        // need at least 3 lines => toast
//        Snackbar.make(findViewById(R.id.fragment_detail_layout), message, Snackbar.LENGTH_LONG).show();
        restartCurrentFragmentLoader();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onImportAllInOneFinished(String message) {
        restartCurrentFragmentLoader();
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    @Override
    public void onImportAllInOneFailed(String message) {
        new AlertDialog.Builder(this).setMessage(message)
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
        .show();
    }

}
