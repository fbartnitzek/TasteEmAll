package com.fbartnitzek.tasteemall.filter;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Copyright 2017.  Frank Bartnitzek
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

public class AttributeFilterSelectTabFragment extends AttributeBaseFilterFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ATTRIBUTE_VALUES_LOADER_ID = 67549;

    private RecyclerView mValuesRecycler;
    private AttributeValuesSelectAdapter mAttributeValuesSelectAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        getLoaderManager().restartLoader(ATTRIBUTE_VALUES_LOADER_ID, null, AttributeFilterSelectTabFragment.this);

        mValuesRecycler = (RecyclerView) mRootView.findViewById(R.id.attribute_list);
        mValuesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mAttributeValuesSelectAdapter = new AttributeValuesSelectAdapter();
        mValuesRecycler.setAdapter(mAttributeValuesSelectAdapter);

        return mRootView;
    }

    @Override
    protected int getTabLayout() {
        return R.layout.fragment_filter_attribute_select_tab;
    }


    @Override
    public JSONObject getFilter() throws JSONException, UnsupportedEncodingException {
        List<String> items = mAttributeValuesSelectAdapter.getSelectedItems();
        JSONArray array = new JSONArray();
        for (String item : items) {
            array.put(DatabaseContract.encodeValue(item));
        }
        return new JSONObject().put(DatabaseContract.Operations.IS, array);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ATTRIBUTE_VALUES_LOADER_ID:
                String alias = DatabaseContract.ALIASES.get(mBaseEntity) + ".";
                JSONObject json = new JSONObject();
                Uri uri;
                try {
                    json.put(mBaseEntity, new JSONObject());
                    uri = DatabaseContract.buildUriWithJson(json);
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("invalid json query " + json.toString());
                }
                return new CursorLoader(getActivity(), uri,
                        new String[]{"DISTINCT " + alias + mAttributeName},
                        null, null, alias + mAttributeName + " ASC");
            default:
                throw new RuntimeException("wrong loaderId in " + AttributeFilterSelectTabFragment.class.getSimpleName());

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ATTRIBUTE_VALUES_LOADER_ID:
                mAttributeValuesSelectAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ATTRIBUTE_VALUES_LOADER_ID:
                mAttributeValuesSelectAdapter.swapCursor(null);
                break;
        }
    }

}
