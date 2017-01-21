package com.fbartnitzek.tasteemall.location;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.JsonHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.showentry.ShowReviewActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.fbartnitzek.tasteemall.location.ShowMapActivity.REVIEW_URI;

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

public class ShowProducerMapFragment extends ShowBaseMapFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ShowProducerMapFragment.class.getName();

    private static final int PRODUCERS_LOADER_ID = 23454;
    private static final int REVIEWS_OF_PRODUCER_LOADER_ID = 34565;

    private Uri mBaseUri;
    private Uri mReviewsOfProducerUri;

    private String mProducerId;
    private String mLocationName;

    private TextView mHeadingProducerLocations;
    private TextView mHeadingReviewsOfProducer;

    private ProducerLocationAdapter mProducerLocationAdapter;
    private ReviewOfLocationAdapter mReviewOfProducerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            Log.w(LOG_TAG, "onCreateView without args...");
        } else {
            Log.v(LOG_TAG, "onCreateView with args: " + args);

            if (args.containsKey(REVIEW_URI)){
                mBaseUri = args.getParcelable(REVIEW_URI);
                getLoaderManager().restartLoader(PRODUCERS_LOADER_ID, null, this);
            }
        }

        mProducerLocationAdapter = new ProducerLocationAdapter(new ProducerLocationAdapter.ProducerLocationAdapterClickHandler() {
            @Override
            public void onClick(String producerId, ProducerLocationAdapter.ViewHolder viewHolder, LatLng latLng, String formatted, String name) {
                addProducerLocationMarker(producerId, viewHolder, latLng, formatted, name);
            }
        });

        mHeadingProducerLocations = (TextView) mRootView.findViewById(R.id.heading_map_producers);
        mHeadingProducerLocations.setText(R.string.label_list_map_producer_locations_preview);
        RecyclerView locationRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_map_producer);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationRecyclerView.setAdapter(mProducerLocationAdapter);


        mHeadingReviewsOfProducer = (TextView) mRootView.findViewById(R.id.heading_map_sub_list_reviews);
        mHeadingReviewsOfProducer.setText(R.string.label_list_map_reviews_of_producer_preview);
        mReviewOfProducerAdapter = new ReviewOfLocationAdapter(new ReviewOfLocationAdapter.ReviewAdapterClickHandler() {
            @Override
            public void onClick(Uri contentUri, ReviewOfLocationAdapter.ViewHolder vh) {
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
        RecyclerView reviewsRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_map_sub_list_reviews);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsRecyclerView.setAdapter(mReviewOfProducerAdapter);

        return mRootView;
    }

    private void addProducerLocationMarker(String producerId, ProducerLocationAdapter.ViewHolder viewHolder, LatLng latLng, String formatted, String name) {
//        Log.v(LOG_TAG, "addProducerLocationMarker, hashCode=" + this.hashCode() + ", " + "producerId = [" + producerId + "], viewHolder = [" + viewHolder + "], latLng = [" + latLng + "], formatted = [" + formatted + "], name = [" + name + "]");
        mProducerId = producerId;
        mMarkerOptions = new MarkerOptions()
                .position(latLng)
                .title(name)
                .snippet(formatted)
                .draggable(false);
        tryUpdatingMarker();

        updateReviews();
    }

    private void updateReviews() {
//        Log.v(LOG_TAG, "updateReviews, hashCode=" + this.hashCode() + ", " + "");

        try {
            JSONObject jsonObject = new JSONObject(DatabaseContract.getJson(mBaseUri));
//            Log.v(LOG_TAG, "updateReviews, hashCode=" + this.hashCode() + ", jsonObject" + jsonObject.toString());
            JSONObject reviewObject = jsonObject.getJSONObject(Review.ENTITY);
            JSONObject drinkObject = JsonHelper.getOrCreateJsonObject(reviewObject, Drink.ENTITY);
            JSONObject producerObject = JsonHelper.getOrCreateJsonObject(drinkObject, Producer.ENTITY);
            producerObject.put(Producer.PRODUCER_ID, new JSONObject()
                    .put(DatabaseContract.Operations.IS, DatabaseContract.encodeValue(mProducerId)));
            drinkObject.put(Producer.ENTITY, producerObject);
            jsonObject.getJSONObject(Review.ENTITY).put(Drink.ENTITY, drinkObject);
//            Log.v(LOG_TAG, "updateReviews, hashCode=" + this.hashCode() + ", jsonObject=" + jsonObject.toString());
            mReviewsOfProducerUri = DatabaseContract.buildUriWithJson(jsonObject);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getLoaderManager().restartLoader(REVIEWS_OF_PRODUCER_LOADER_ID, null, this);
    }

    @Override
    protected int getTabLayout() {
        return R.layout.fragment_show_map_producer;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case PRODUCERS_LOADER_ID:
                return new CursorLoader(getActivity(),
                        mBaseUri, QueryColumns.MapFragment.ProducerLocations.COLUMNS,
                        null, null, null);
            case REVIEWS_OF_PRODUCER_LOADER_ID:
                return new CursorLoader(getActivity(), mReviewsOfProducerUri,
                        QueryColumns.MapFragment.ReviewsSubQuery.COLUMNS, null, null, null);
            default:
                throw new RuntimeException("wrong loaderId in " + ShowProducerMapFragment.class.getSimpleName() + ": " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        int count = data == null ? 0 : data.getCount();
        switch (loader.getId()) {
            case PRODUCERS_LOADER_ID:
//                Log.v(LOG_TAG, "onLoadFinished - swapping " + count + " Producers");
                mProducerLocationAdapter.swapCursor(data);
                mHeadingProducerLocations.setText(getString(R.string.label_list_map_producer_locations, count));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((ShowMapActivity)getActivity()).scheduleStartPostponedTransition(mHeadingProducerLocations);
                }
                break;
            case REVIEWS_OF_PRODUCER_LOADER_ID:
//                Log.v(LOG_TAG, "onLoadFinished - swapping " + count + " Reviews of Producer");
                mReviewOfProducerAdapter.swapCursor(data);
                mHeadingReviewsOfProducer.setText(getString(R.string.label_list_map_reviews_of_producer, count));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
        switch (loader.getId()) {
            case PRODUCERS_LOADER_ID:
                mProducerLocationAdapter.swapCursor(null);
                break;
            case REVIEWS_OF_PRODUCER_LOADER_ID:
                mReviewOfProducerAdapter.swapCursor(null);
                break;
        }
    }
}
