package com.fbartnitzek.tasteemall.mainpager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.MainActivity;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.showentry.ShowProducerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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

public class ProducerPagerFragment extends BasePagerFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ProducerPagerFragment.class.getName();
    private static final int PRODUCER_LOADER_ID = 300;

    private ProducerAdapter mProducerAdapter;
    private TextView mProducerHeading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.entity = Producer.ENTITY;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mProducerAdapter = new ProducerAdapter(new ProducerAdapter.ProducerAdapterClickHandler() {
            @Override
            public void onClick(String producerName, Uri contentUri, ProducerAdapter.ViewHolder vh) {
                Bundle bundle = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    bundle = ActivityOptions.makeSceneTransitionAnimation(
                            getActivity(),
                            new Pair<View, String>(vh.nameView, vh.nameView.getTransitionName())
                    ).toBundle();
                }
                Intent intent = new Intent(getActivity(), ShowProducerActivity.class)
                        .setData(contentUri);
                startActivity(intent, bundle);
            }
        }, getActivity());

        mProducerHeading = (TextView) mRootView.findViewById(R.id.heading_entities);
        mProducerHeading.setText(
                getString(R.string.label_list_entries_preview,
                        getString(R.string.label_producers)));
        RecyclerView reviewRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_entities);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(mProducerAdapter);

        return mRootView;
    }

    @Override
    public int getSharedTransitionId() {
        return R.string.shared_transition_add_drink_name;
    }

    @Override
    public void restartLoader() {
        getLoaderManager().restartLoader(PRODUCER_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PRODUCER_LOADER_ID:
                if (this.jsonUri == null) {
                    Log.v(LOG_TAG, "onCreateLoader before jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

                    String pattern = ((MainActivity) getActivity()).getSearchPattern();
                    try {
                        String encodedValue = DatabaseContract.encodeValue(pattern);

                        if (jsonTextFilter == null) {
                            jsonTextFilter = new JSONObject().put(Producer.ENTITY, new JSONObject()
                                    .put(DatabaseContract.OR, new JSONObject()
                                            .put(Producer.ENTITY, new JSONObject()
                                                    .put(Producer.NAME, new JSONObject())
                                                    .put(Producer.FORMATTED_ADDRESS, new JSONObject())
                                                    .put(Producer.COUNTRY, new JSONObject())
                                            )
                                    )
                            );
                        }

                        JSONObject producer = jsonTextFilter.getJSONObject(Producer.ENTITY).getJSONObject(DatabaseContract.OR).getJSONObject(Producer.ENTITY);
                        producer.getJSONObject(Producer.NAME).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                        producer.getJSONObject(Producer.FORMATTED_ADDRESS).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                        producer.getJSONObject(Producer.COUNTRY).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                        jsonTextFilter.getJSONObject(Producer.ENTITY).getJSONObject(DatabaseContract.OR).put(Producer.ENTITY, producer);

                        jsonUri = DatabaseContract.buildUriWithJson(jsonTextFilter);
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "onCreateLoader building jsonUri failed, hashCode=" + this.hashCode() + ", " + "pattern= [" + pattern + "]");
                        throw new RuntimeException("building jsonUri failed");
                    }
                    Log.v(LOG_TAG, "onCreateLoader after jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

                }

                return new CursorLoader(getActivity(),
                        jsonUri, QueryColumns.MainFragment.ProducerQuery.COLUMNS,
                        null, null,
                        Producer.NAME);
            default:
                throw new RuntimeException("wrong loader_id in " + this.getClass().getSimpleName() + "...");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        switch (loader.getId()) {
            case PRODUCER_LOADER_ID:
                mProducerAdapter.swapCursor(data);
                mProducerHeading.setText(
                        getString(R.string.label_list_entries,
                                getString(R.string.label_producers),
                                numberAppendix));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case PRODUCER_LOADER_ID:
                mProducerAdapter.swapCursor(null);
                break;
        }
    }

}
