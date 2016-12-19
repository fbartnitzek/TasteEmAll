package com.fbartnitzek.tasteemall.mainpager;

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
import android.widget.Toast;

import com.fbartnitzek.tasteemall.MainActivity;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.User;

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

public class UserPagerFragment extends BasePagerFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int USER_LOADER_ID = 100;
    private static final String LOG_TAG = UserPagerFragment.class.getName();
    private UserAdapter mUserAdapter;
    private TextView mUsersHeading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mUserAdapter = new UserAdapter(new UserAdapter.UserAdapterClickHandler() {
            @Override
            public void onClick(String userName, Uri contentUri, RecyclerView.ViewHolder viewHolder) {
                Toast.makeText(getActivity(), userName + " was clicked", Toast.LENGTH_SHORT).show();
            }
        }, getActivity());

        mUsersHeading = (TextView) mRootView.findViewById(R.id.heading_entities);
        mUsersHeading.setText(
                getString(R.string.label_list_entries_preview,
                        getString(R.string.label_users)));
        RecyclerView reviewRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_entities);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(mUserAdapter);

        return mRootView;
    }

    @Override
    public int getSharedTransitionId() {
        return R.string.shared_transition_add_drink_name;
    }

    @Override
    public void restartLoader() {
        getLoaderManager().restartLoader(USER_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case USER_LOADER_ID:
                return new CursorLoader(getActivity(),
                        DatabaseContract.UserEntry.buildUriWithName(((MainActivity)getActivity()).getSearchPattern()),
                        QueryColumns.MainFragment.UserQuery.COLUMNS,
                        null, null,
                        User.NAME);
            default:
                throw new RuntimeException("wrong loader_id in UserPagerFragment...");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        switch (loader.getId()) {
            case USER_LOADER_ID:
                mUserAdapter.swapCursor(data);
                mUsersHeading.setText(
                        getString(R.string.label_list_entries,
                                getString(R.string.label_users),
                                numberAppendix));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case USER_LOADER_ID:
                mUserAdapter.swapCursor(null);
                break;
        }
    }

}
