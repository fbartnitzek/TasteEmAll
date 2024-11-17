package com.fbartnitzek.tasteemall.location;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.JsonHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.showentry.ShowReviewActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import static com.fbartnitzek.tasteemall.data.DatabaseContract.LocationEntry.ALIAS_REVIEW;
import static com.fbartnitzek.tasteemall.location.ShowMapActivity.REVIEW_URI;

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


public class ShowReviewMapFragment extends ShowBaseMapFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ShowReviewMapFragment.class.getName();
    private static final int REVIEW_LOCATIONS_LOADER_ID = 23452;
    private static final int REVIEWS_OF_LOCATION_LOADER_ID = 34563;


    private ReviewLocationAdapter mReviewLocationAdapter;
    private ReviewOfLocationAdapter mReviewOfLocationAdapter;
    private TextView mHeadingLocations;
    private TextView mHeadingReviewsOfLocation;
    private Uri mReviewsOfLocationUri;
    private Uri mBaseUri;
    private String mReviewLocationId;


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
//        super.onCreate(savedInstanceState);
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        super.onCreateView(inflater, container, savedInstanceState);

        // TODO: scrollable below map... - done...?

        Bundle args = getArguments();
        if (args == null) {
            Log.w(LOG_TAG, "onCreateView without args...");
        } else {
            Log.v(LOG_TAG, "onCreateView with args: " + args);

            if (args.containsKey(REVIEW_URI)) {
                mBaseUri = args.getParcelable(REVIEW_URI);
                LoaderManager.getInstance(this).restartLoader(REVIEW_LOCATIONS_LOADER_ID, null, this);
            }
        }

        mReviewLocationAdapter = new ReviewLocationAdapter(this::addReviewLocationMarker);
        mHeadingLocations = mRootView.findViewById(R.id.heading_map_locations);
        mHeadingLocations.setText(R.string.label_list_map_locations_preview);
        RecyclerView locationRecyclerView = mRootView.findViewById(R.id.recyclerview_map_locations);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationRecyclerView.setAdapter(mReviewLocationAdapter);


        mHeadingReviewsOfLocation = mRootView.findViewById(R.id.heading_map_sub_list);
        mHeadingReviewsOfLocation.setText(R.string.label_list_map_reviews_of_location_preview);
        mReviewOfLocationAdapter = new ReviewOfLocationAdapter((contentUri, vh) -> {
            Bundle bundle;
            bundle = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<>(vh.drinkNameView, vh.drinkNameView.getTransitionName()),
                    new Pair<>(vh.producerNameView, vh.producerNameView.getTransitionName())
            ).toBundle();

            startActivity(
                    new Intent(getActivity(), ShowReviewActivity.class).setData(contentUri),
                    bundle);
        }, getActivity());
        RecyclerView reviewsRecyclerView = mRootView.findViewById(R.id.recyclerview_map_sub_list);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsRecyclerView.setAdapter(mReviewOfLocationAdapter);

//        Log.v(LOG_TAG, "onCreateView before MapFragment, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");

        return mRootView;
    }



    @Override
    protected int getTabLayout() {
        return R.layout.fragment_show_map_review_location;
    }

    private void addReviewLocationMarker(String reviewLocationId, ReviewLocationAdapter.ViewHolder viewHolder, LatLng latLng, String formatted, String description) {
        Log.v(LOG_TAG, "addReviewLocationMarker, hashCode=" + this.hashCode() + ", " + "reviewLocationId = [" + reviewLocationId + "], latLng = [" + latLng + "], formatted = [" + formatted + "], description = [" + description + "]");
        mReviewLocationId = reviewLocationId;

        if (reviewLocationId == null) {
            hideMap();
        } else {
            showMap();
            if (latLng != null && Utils.isValidLatLong(latLng.latitude, latLng.longitude)) {
                String mLocationName = description == null || description.isEmpty() ? formatted : description;
                mMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(mLocationName)
                        .snippet(formatted)
                        .draggable(false);
                tryUpdatingMarker();
            }
        }

        updateReviews();
    }

    private void updateReviews() {
//        Log.v(LOG_TAG, "updateReviews, mReviewLocationId=" + mReviewLocationId);

        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(DatabaseContract.getJson(mBaseUri)));
            JSONObject reviewObject = jsonObject.getJSONObject(Review.ENTITY);
            JSONObject locationObject = JsonHelper.getOrCreateJsonObject(reviewObject, Location.ENTITY);
            if (mReviewLocationId == null) {
                Log.v(LOG_TAG, "updateReviews, ReviewLocation is null");
                locationObject.put(DatabaseContract.Operations.NULL, DatabaseContract.Operations.NULL);
            } else {
                locationObject.put(Location.LOCATION_ID, new JSONObject()
                        .put(DatabaseContract.Operations.IS, DatabaseContract.encodeValue(mReviewLocationId)));
            }
            jsonObject.getJSONObject(Review.ENTITY).put(Location.ENTITY, locationObject);
//            Log.v(LOG_TAG, "updateReviews, hashCode=" + this.hashCode() + ", jsonObject=" + jsonObject.toString());
            mReviewsOfLocationUri = DatabaseContract.buildUriWithJson(jsonObject);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoaderManager.getInstance(this).restartLoader(REVIEWS_OF_LOCATION_LOADER_ID, null, this);
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case REVIEW_LOCATIONS_LOADER_ID:
                return new CursorLoader(Objects.requireNonNull(getActivity()),
                        mBaseUri, QueryColumns.MapFragment.ReviewLocations.COLUMNS,
                        null, null,
                        ALIAS_REVIEW + "." + Location.COUNTRY + " ASC, " + ALIAS_REVIEW + "." + Location.FORMATTED_ADDRESS + " ASC");
            case REVIEWS_OF_LOCATION_LOADER_ID:
                return new CursorLoader(Objects.requireNonNull(getActivity()), mReviewsOfLocationUri,
                        QueryColumns.MapFragment.ReviewsSubQuery.COLUMNS, null, null,
                        DatabaseContract.ReviewEntry.ALIAS + "." + Review.READABLE_DATE + " DESC, " +
                        DatabaseContract.ProducerEntry.ALIAS + "." + Producer.NAME + " ASC, " +
                        DatabaseContract.DrinkEntry.ALIAS + "." + Drink.NAME + " ASC");
            default:
                throw new RuntimeException("wrong loaderId in " + ShowReviewMapFragment.class.getSimpleName());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        int count = data == null ? 0 : data.getCount();
        switch (loader.getId()) {
            case REVIEW_LOCATIONS_LOADER_ID:
//                Log.v(LOG_TAG, "onLoadFinished - swapping " + count + " ReviewLocation");
                mReviewLocationAdapter.swapCursor(data);
                mHeadingLocations.setText(getString(R.string.label_list_map_locations, count));
                ((ShowMapActivity) Objects.requireNonNull(getActivity())).scheduleStartPostponedTransition(mHeadingLocations);
                break;
            case REVIEWS_OF_LOCATION_LOADER_ID:
//                Log.v(LOG_TAG, "onLoadFinished - swapping " + count + " Reviews of Location");
                mReviewOfLocationAdapter.swapCursor(data);
                mHeadingReviewsOfLocation.setText(getString(R.string.label_list_map_reviews_of_location, count));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
        switch (loader.getId()) {
            case REVIEW_LOCATIONS_LOADER_ID:
                mReviewLocationAdapter.swapCursor(null);
                break;
            case REVIEWS_OF_LOCATION_LOADER_ID:
                mReviewOfLocationAdapter.swapCursor(null);
                break;
        }
    }
}