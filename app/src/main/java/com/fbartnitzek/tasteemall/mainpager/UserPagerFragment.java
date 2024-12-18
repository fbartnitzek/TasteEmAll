package com.fbartnitzek.tasteemall.mainpager;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fbartnitzek.tasteemall.data.pojo.User;

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

public class UserPagerFragment extends BasePagerFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int USER_LOADER_ID = 100;
    private static final String LOG_TAG = UserPagerFragment.class.getName();
    private UserAdapter mUserAdapter;
    private TextView mUsersHeading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.entity = User.ENTITY;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mUserAdapter = new UserAdapter((userName, contentUri, viewHolder) ->
                Toast.makeText(getActivity(), userName + " was clicked", Toast.LENGTH_SHORT).show(), getActivity());

        mUsersHeading = mRootView.findViewById(R.id.heading_entities);
        mUsersHeading.setText(
                getString(R.string.label_list_entries_preview,
                        getString(R.string.label_users)));
        RecyclerView reviewRecyclerView = mRootView.findViewById(R.id.recyclerview_entities);
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
        LoaderManager.getInstance(this).restartLoader(USER_LOADER_ID, null, this);
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == USER_LOADER_ID) {
            if (this.jsonUri == null) {
                Log.v(LOG_TAG, "onCreateLoader before jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

                String pattern = ((MainActivity) Objects.requireNonNull(getActivity())).getSearchPattern();
                try {
                    if (jsonTextFilter == null) {
                        jsonTextFilter = new JSONObject().put(User.ENTITY, new JSONObject()
                                .put(User.NAME, new JSONObject()));
                    }
                    jsonTextFilter.getJSONObject(User.ENTITY).getJSONObject(User.NAME)
                            .put(DatabaseContract.Operations.CONTAINS, DatabaseContract.encodeValue(pattern));
                    jsonUri = DatabaseContract.buildUriWithJson(jsonTextFilter);
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "onCreateLoader building jsonUri failed, hashCode=" + this.hashCode() + ", " + "pattern= [" + pattern + "]");
                    throw new RuntimeException("building jsonUri failed");
                }
                Log.v(LOG_TAG, "onCreateLoader after jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

            }

            return new CursorLoader(Objects.requireNonNull(getActivity()), jsonUri,
                    QueryColumns.MainFragment.UserQuery.COLUMNS,
                    null, null, User.NAME);
        }
        throw new RuntimeException("wrong loader_id in UserPagerFragment...");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        if (loader.getId() == USER_LOADER_ID) {
            mUserAdapter.swapCursor(data);
            mUsersHeading.setText(
                    getString(R.string.label_list_entries,
                            getString(R.string.label_users),
                            numberAppendix));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == USER_LOADER_ID) {
            mUserAdapter.swapCursor(null);
        }
    }

}
