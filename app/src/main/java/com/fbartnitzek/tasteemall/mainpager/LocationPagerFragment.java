package com.fbartnitzek.tasteemall.mainpager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.MainActivity;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.showentry.ShowLocationActivity;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mLocationAdapter = new LocationAdapter(new LocationAdapter.LocationAdapterClickHandler() {
            @Override
            public void onClick(String formatted, Uri contentUri, LocationAdapter.ViewHolder viewHolder) {
                Intent intent = new Intent(getActivity(), ShowLocationActivity.class)
                        .setData(contentUri);
                startActivity(intent);
            }
        }, getActivity());

        mLocationHeading = (TextView) mRootView.findViewById(R.id.heading_entities);
        mLocationHeading.setText(
                getString(R.string.label_list_entries_preview,
                        getString(R.string.label_locations)));
        RecyclerView reviewRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_entities);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(mLocationAdapter);

        return mRootView;
    }


    @Override
    public void restartLoader() {
//        Log.v(LOG_TAG, "restartLoader, hashCode=" + this.hashCode() + ", " + "");
        getLoaderManager().restartLoader(LOCATION_LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOCATION_LOADER_ID:
                return new CursorLoader(getActivity(),
                        DatabaseContract.LocationEntry.buildUriWithPatternOrDescription(
                                ((MainActivity)getActivity()).getSearchPattern()),
                        QueryColumns.MainFragment.LocationQuery.COLUMNS,
                        null, null,
                        Location.COUNTRY + ", " + Location.FORMATTED_ADDRESS);
            default:
                throw new RuntimeException("wrong loader_id in " + this.getClass().getSimpleName() + "...");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        switch (loader.getId()) {
            case LOCATION_LOADER_ID:
                mLocationAdapter.swapCursor(data);
                mLocationHeading.setText(
                        getString(R.string.label_list_entries,
                                getString(R.string.label_locations),
                                numberAppendix));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOCATION_LOADER_ID:
                mLocationAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public int getSharedTransitionId() {
        return R.string.shared_transition_add_drink_name;
    }
}

