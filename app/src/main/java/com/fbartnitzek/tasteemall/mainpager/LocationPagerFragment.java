package com.fbartnitzek.tasteemall.mainpager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fbartnitzek.tasteemall.MainActivity;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.showentry.ShowLocationActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

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

public class LocationPagerFragment extends BasePagerFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = LocationPagerFragment.class.getName();
    private static final int LOCATION_LOADER_ID = 200;

    private LocationAdapter mLocationAdapter;
    private TextView mLocationHeading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.entity = Location.ENTITY;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mLocationAdapter = new LocationAdapter((formatted, contentUri, viewHolder) -> {
            Intent intent = new Intent(getActivity(), ShowLocationActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        });

        mLocationHeading = mRootView.findViewById(R.id.heading_entities);
        mLocationHeading.setText(
                getString(R.string.label_list_entries_preview,
                        getString(R.string.label_locations)));
        RecyclerView reviewRecyclerView = mRootView.findViewById(R.id.recyclerview_entities);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(mLocationAdapter);

        return mRootView;
    }


    @Override
    public void restartLoader() {
//        Log.v(LOG_TAG, "restartLoader, hashCode=" + this.hashCode() + ", " + "");
        LoaderManager.getInstance(this).restartLoader(LOCATION_LOADER_ID, null, this);
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOCATION_LOADER_ID) {
            if (this.jsonUri == null) {
                Log.v(LOG_TAG, "onCreateLoader before jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

                String pattern = ((MainActivity) Objects.requireNonNull(getActivity())).getSearchPattern();

                // Location.FORMATTED_ADDRESS + " LIKE ? OR " + Location.INPUT + " LIKE ? OR "
                // + Location.COUNTRY + " LIKE ? OR " + Location.DESCRIPTION + " LIKE ?",
                try {
                    String encodedValue = DatabaseContract.encodeValue(pattern);
                    if (jsonTextFilter == null) {
                        jsonTextFilter = new JSONObject().put(Location.ENTITY, new JSONObject()
                                .put(DatabaseContract.OR, new JSONObject()
                                        .put(Location.ENTITY, new JSONObject()
                                                .put(Location.FORMATTED_ADDRESS, new JSONObject())
                                                .put(Location.INPUT, new JSONObject())
                                                .put(Location.COUNTRY, new JSONObject())
                                                .put(Location.DESCRIPTION, new JSONObject())
                                        )
                                )
                        );
                    }

                    JSONObject location = jsonTextFilter.getJSONObject(Location.ENTITY).getJSONObject(DatabaseContract.OR).getJSONObject(Location.ENTITY);
                    location.getJSONObject(Location.FORMATTED_ADDRESS).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                    location.getJSONObject(Location.INPUT).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                    location.getJSONObject(Location.COUNTRY).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                    location.getJSONObject(Location.DESCRIPTION).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                    jsonTextFilter.getJSONObject(Location.ENTITY).getJSONObject(DatabaseContract.OR).put(Location.ENTITY, location);

                    jsonUri = DatabaseContract.buildUriWithJson(jsonTextFilter);
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "onCreateLoader building jsonUri failed, hashCode=" + this.hashCode() + ", " + "pattern= [" + pattern + "]");
                    throw new RuntimeException("building jsonUri failed");
                }
                Log.v(LOG_TAG, "onCreateLoader after jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

            }


            return new CursorLoader(Objects.requireNonNull(getActivity()),
                    jsonUri != null ? jsonUri :
                            DatabaseContract.LocationEntry.buildUriWithPatternOrDescription(
                                    ((MainActivity) getActivity()).getSearchPattern()),
                    QueryColumns.MainFragment.LocationQuery.COLUMNS,
                    null, null,
                    Location.COUNTRY + ", " + Location.FORMATTED_ADDRESS);
        }
        throw new RuntimeException("wrong loader_id in " + this.getClass().getSimpleName() + "...");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        if (loader.getId() == LOCATION_LOADER_ID) {
            mLocationAdapter.swapCursor(data);
            mLocationHeading.setText(
                    getString(R.string.label_list_entries,
                            getString(R.string.label_locations),
                            numberAppendix));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOCATION_LOADER_ID) {
            mLocationAdapter.swapCursor(null);
        }
    }

    @Override
    public int getSharedTransitionId() {
        return R.string.shared_transition_add_drink_name;
    }
}

