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
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.showentry.ShowReviewActivity;

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


public class ReviewPagerFragment extends BasePagerFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int REVIEW_LOADER_ID = 500;

    private static final String LOG_TAG = ReviewPagerFragment.class.getName();

    private ReviewAdapter mReviewAdapter;
    private TextView mReviewsHeading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.entity = Review.ENTITY;
    }

    // TODO: optimize DB standard search back to uri-mapper

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mReviewAdapter = new ReviewAdapter(new ReviewAdapter.ReviewAdapterClickHandler() {

            @Override
            public void onClick(Uri contentUri, ReviewAdapter.ViewHolder vh) {
                Bundle bundle = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    bundle = ActivityOptions.makeSceneTransitionAnimation(
                            getActivity(),
                            new Pair<View, String>(vh.drinkNameView, vh.drinkNameView.getTransitionName()),
                            new Pair<View, String>(vh.producerNameView, vh.producerNameView.getTransitionName())
                    ).toBundle();
                }

                startActivity(
                        new Intent(getActivity(), ShowReviewActivity.class).setData(contentUri),
                        bundle);
            }
        }, getActivity());

        mReviewsHeading = (TextView) mRootView.findViewById(R.id.heading_entities);
        mReviewsHeading.setText(
                getString(R.string.label_list_entries_preview,
                        getString(R.string.label_reviews)));
        RecyclerView reviewRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_entities);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(mReviewAdapter);

        return mRootView;
    }

    @Override
    public void restartLoader() {
        Log.v(LOG_TAG, "restartLoader, hashCode=" + this.hashCode() + ", " + "");
        getLoaderManager().restartLoader(REVIEW_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case REVIEW_LOADER_ID:
                if (this.jsonUri == null) { // explicitly nulled by activity before (and set searchpattern)
                    Log.v(LOG_TAG, "onCreateLoader before jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

                    String pattern = ((MainActivity) getActivity()).getSearchPattern();
                    String drinkType = Utils.getDrinkTypeFromSharedPrefs(getActivity(), true);
                    try {
                        // private static final String REVIEWS_DRINKS_OR_PRODUCERS_BY_NAME_AND_TYPE_SELECTION =
//                        "(" + DA + "." + Drink.NAME + " LIKE ? OR " + PA + "." + Producer.NAME + " LIKE ?)" +
//                                " AND " + DA + "." + Drink.TYPE + " = ?";
                        String encodedValue = DatabaseContract.encodeValue(pattern);
                        if (jsonTextFilter == null) {
                            jsonTextFilter = new JSONObject().put(Review.ENTITY, new JSONObject()
                                    .put(DatabaseContract.OR, new JSONObject()
                                            .put(Review.ENTITY, new JSONObject()
                                                    .put(Drink.ENTITY, new JSONObject()
                                                            .put(Drink.NAME, new JSONObject())
                                                            .put(Producer.ENTITY, new JSONObject()
                                                                    .put(Producer.NAME, new JSONObject())
                                                            )
                                                    )
                                            )
                                    )
                            );
                        }
                        JSONObject drink = jsonTextFilter.getJSONObject(Review.ENTITY).getJSONObject(DatabaseContract.OR)
                                .getJSONObject(Review.ENTITY).getJSONObject(Drink.ENTITY);
                        drink.getJSONObject(Drink.NAME).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                        drink.getJSONObject(Producer.ENTITY).getJSONObject(Producer.NAME).put(DatabaseContract.Operations.CONTAINS, encodedValue);
                        jsonTextFilter.getJSONObject(Review.ENTITY).getJSONObject(DatabaseContract.OR)
                                .getJSONObject(Review.ENTITY).put(Drink.ENTITY, drink);

                        if (Drink.TYPE_ALL.equals(drinkType)) {
                            jsonTextFilter.getJSONObject(Review.ENTITY).remove(Drink.ENTITY);
                        } else {
                            jsonTextFilter.getJSONObject(Review.ENTITY).put(Drink.ENTITY, new JSONObject()
                                    .put(Drink.TYPE, new JSONObject().put(DatabaseContract.Operations.IS, DatabaseContract.encodeValue(drinkType))));
                        }

                        jsonUri = DatabaseContract.buildUriWithJson(jsonTextFilter);
                        Log.v(LOG_TAG, "onCreateLoader after jsonCreation, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");

                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "onCreateLoader building jsonUri failed, hashCode=" + this.hashCode() + ", " + "pattern= [" + pattern + "], drinkType= [" + drinkType + "]");
                        throw new RuntimeException("building jsonUri failed");
                    }
                }

                return new CursorLoader(getActivity(), jsonUri,
                        QueryColumns.MainFragment.ReviewAllQuery.COLUMNS,
                        null, null, DatabaseContract.ReviewEntry.ALIAS + "." + Review.READABLE_DATE + " DESC");
            default:
                throw new RuntimeException("wrong loader_id in ReviewPagerFragment...");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        String numberAppendix = data == null ? "" : getString(R.string.label_numberAppendix, data.getCount());
        switch (loader.getId()) {
            case REVIEW_LOADER_ID:
                mReviewAdapter.swapCursor(data);
                mReviewsHeading.setText(
                        getString(R.string.label_list_entries,
                                getString(R.string.label_reviews),
                                numberAppendix));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case REVIEW_LOADER_ID:
                mReviewAdapter.swapCursor(null);
                break;
        }

    }

    @Override
    public int getSharedTransitionId() {
        return R.string.shared_transition_add_drink_name;
    }

}
