package com.fbartnitzek.tasteemall.addentry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.location.AddressData;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static com.fbartnitzek.tasteemall.data.QueryColumns.LocationFragment.ShowQuery;

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

public class AddLocationFragment extends GeocoderFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AddLocationFragment.class.getName();
    private static final int EDIT_LOCATION_LOADER_ID = 5432;

    private static final String STATE_LOCATION_INPUT = "STATE_LOCATION_INPUT";
    private static final String STATE_LOCATION_DESCRIPTION = "STATE_LOCATION_DESCRIPTION";
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";
    private static final String STATE_ORIGINAL_LOCATION_INPUT = "STATE_ORIGINAL_LOCATION_INPUT";

    private AutoCompleteTextView mEditLocationDescription;

    private String mLocationInput;
    private Uri contentUri;
    private String originalLocationInput;
    private String mLocationDescription;
    private long mLocation_Id;
    private String mLocationId;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_LOCATION_INPUT, editLocation.getText().toString());
        outState.putString(STATE_LOCATION_DESCRIPTION, mEditLocationDescription.getText().toString());

        if (contentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, contentUri);
        }
        if (originalLocationInput != null) {
            outState.putString(STATE_ORIGINAL_LOCATION_INPUT, originalLocationInput);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CONTENT_URI)) {
            contentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
        }

        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        rootView = inflater.inflate(R.layout.fragment_add_location, container, false);

        super.onCreateView(inflater, container, savedInstanceState);
        mEditLocationDescription = rootView.findViewById(R.id.location_description);
        mEditLocationDescription.setThreshold(1);

        CompletionLocationDescriptionAdapter mLocationDescriptionAdapter = new CompletionLocationDescriptionAdapter(getActivity());
        mEditLocationDescription.setAdapter(mLocationDescriptionAdapter);

        mEditLocationDescription.setOnItemClickListener((parent, view, pos, id) -> {
            Cursor c = (Cursor) parent.getItemAtPosition(pos);
            mLocation_Id = c.getLong(QueryColumns.LocationPart.CompletionQuery.COL__ID);
            mLocationId = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
            Log.v(LOG_TAG, "editDescription.onItemClick Cursor with locationId=" + mLocationId);

            if (editLocation.getText().toString().isEmpty()) {
                String location = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_FORMATTED_ADDRESS);
                if (location != null && !location.isEmpty()) {
                    editLocation.setText(location);
                }
            }
        });

        editLocation = rootView.findViewById(R.id.location_location);
        ((LocationAutoCompleteTextView) editLocation).setThreshold(1);
        CompletionLocationAdapter mLocationAdapter = new CompletionLocationAdapter(getActivity(), true);
        ((LocationAutoCompleteTextView) editLocation).setAdapter(mLocationAdapter);
        editLocation.addTextChangedListener(this);

        ((LocationAutoCompleteTextView) editLocation).setOnItemClickListener((parent, view, position, id) -> {
            // todo: confusing location stuff
            Cursor c = (Cursor) parent.getItemAtPosition(position);
            mLocation_Id = c.getLong(QueryColumns.LocationPart.CompletionQuery.COL__ID);
            mLocationId = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
            Log.v(LOG_TAG, "editLocation.onItemClick Cursor with locationId=" + mLocationId);

            if (mEditLocationDescription.getText().toString().isEmpty()) {
                String description = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_DESCRIPTION);
                if (description != null && !description.isEmpty()) {
                    mEditLocationDescription.setText(description);
                    mEditLocationDescription.setEnabled(false);
                }
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_LOCATION_INPUT)) {
                mLocationInput = savedInstanceState.getString(STATE_LOCATION_INPUT);
            }
            if (savedInstanceState.containsKey(STATE_LOCATION_DESCRIPTION)) {
                mLocationDescription = savedInstanceState.getString(STATE_LOCATION_DESCRIPTION);
            }
        }

        if (mLocationInput != null) {
            editLocation.setText(mLocationInput);
        }
        if (mLocationDescription != null) {
            mEditLocationDescription.setText(mLocationDescription);
        }

        rootView.findViewById(R.id.location_here_button).setOnClickListener(v -> getCurrentLocation());

        createToolbar();

        return rootView;
    }

    @Override
    void resetLocationId() {
        Log.v(LOG_TAG, "resetting locationId");
        mLocationId = null;
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Objects.requireNonNull(activity).setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                return;
            }

            supportActionBar.setDisplayHomeAsUpEnabled(true);  //false: not visible anymore
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            ((TextView) rootView.findViewById(R.id.action_bar_title)).setText(R.string.title_search_location);

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }


    public void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");

        mLocationInput = editLocation.getText().toString().trim();

        // validate ...
        if (mLocationInput.length() == 0 ) {
            Snackbar.make(rootView, R.string.msg_enter_location, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (contentUri != null) { //update
            updateData();   // location exists
        } else { // insert
            Log.v(LOG_TAG, "saveData with mLocationId=" + mLocationId);
            if (mLocationId != null) {
                finish();
            } else {
                insertData();
            }
        }
    }

    private void finish() {
        Log.v(LOG_TAG, "finish, hashCode=" + this.hashCode() + ", " + "");
        Intent output = new Intent();
        output.setData(DatabaseContract.LocationEntry.buildUri(mLocation_Id));
        Objects.requireNonNull(getActivity()).setResult(Activity.RESULT_OK, output);
        getActivity().finish();
    }

    private void insertData() {
        Log.v(LOG_TAG, "insertData, hashCode=" + this.hashCode() + ", " + "");
        LocationParcelable locationParcelable = createLocationParcelable();

        // TODO: check if location already exists...

        // insert Location
        new InsertEntryTask(
                getActivity(), DatabaseContract.LocationEntry.CONTENT_URI, rootView, mLocationInput)
                .execute(DatabaseHelper.buildLocationValues(
                        Utils.calcLocationId(locationParcelable.getInput()),
                        locationParcelable.getInput(),
                        locationParcelable.getLatitude(),
                        locationParcelable.getLongitude(),
                        locationParcelable.getCountry(),
                        locationParcelable.getFormattedAddress(),
                        mEditLocationDescription.getText().toString()
                ));
    }

    private void updateData() {
        Log.v(LOG_TAG, "updateData, hashCode=" + this.hashCode() + ", " + "");
        Uri locationUri = Utils.calcSingleLocationUri(contentUri);
        if (address == null) {
            Toast.makeText(getActivity(), "no locationParcelable - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        } else if (originalLocationInput == null) {
            Toast.makeText(getActivity(), "no originalLocationInput - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.v(LOG_TAG, "updateData, address=" + address.toString());
        new UpdateEntryTask(getActivity(), locationUri, address.getFormatted(), rootView)
                .execute(DatabaseHelper.buildLocationUpdateValues(
                        address.getLatitude(),
                        address.getLongitude(),
                        address.getCountryName(),
                        address.getFormatted(),
                        mEditLocationDescription.getText().toString()));
    }

    public void setInput(String input) {
        this.mLocationInput = input;
    }

    public void setDescription(String description) {
        this.mLocationDescription = description;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }


    @Override
    public void onResume() {    //TODO: onActivityCreated or onResume for loader.init?
        if (contentUri != null) {
            Log.v(LOG_TAG, "onResume with contentUri - edit, hashCode=" + this.hashCode() + ", " + "");
            LoaderManager.getInstance(this).initLoader(EDIT_LOCATION_LOADER_ID, null, this);
        } else {
            Log.v(LOG_TAG, "onResume without contentUri - add, hashCode=" + this.hashCode() + ", " + "");
        }
        super.onResume();
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

        if (contentUri != null) {
            return new CursorLoader(
                    Objects.requireNonNull(getActivity()),
                    contentUri,
                    ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...
            // todo: maybe right...
            mLocation_Id = data.getInt(ShowQuery.COL__ID);
            String desc = data.getString(ShowQuery.COL_DESCRIPTION);
            mEditLocationDescription.setText(desc);

            originalLocationInput = data.getString(ShowQuery.COL_INPUT);
            String mOriginalLocationFormatted = data.getString(ShowQuery.COL_FORMATTED_ADDRESS);

            String countryName = data.getString(ShowQuery.COL_COUNTRY);
            Optional<String> countryCode = calcCountryCode(countryName);

            address = new AddressData(
                    data.getDouble(ShowQuery.COL_LATITUDE),
                    data.getDouble(ShowQuery.COL_LONGITUDE),
                    countryCode.orElse("NA"),
                    countryName,
                    mOriginalLocationFormatted,
                    originalLocationInput
            );

            Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", address=[" + address + "]");

            if (!Utils.isNetworkUnavailable(Objects.requireNonNull(getActivity()))
                    && DatabaseContract.LocationEntry.GEOCODE_ME.equals(mOriginalLocationFormatted)) {
                // geocode
                editLocation.setText(originalLocationInput);
            } else {
                skipNextGeocoding();
                editLocation.setText(address.getFormatted());
            }

            showValidPosition();
        }
    }

    @Override
    public void onLoaderReset(@NotNull Loader<Cursor> loader) {
    }
}
