package com.fbartnitzek.tasteemall.mainpager;

import android.app.ActivityOptions;
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
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.showentry.ShowReviewActivity;

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
    private String mReviewsSortOrder;
    private Uri mCurrentReviewsUri;


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

    public String getmReviewsSortOrder() {
        return mReviewsSortOrder;
    }

    public Uri getmCurrentReviewsUri() {
        return mCurrentReviewsUri;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case REVIEW_LOADER_ID:
                mReviewsSortOrder = DatabaseContract.ReviewEntry.ALIAS + "." + Review.READABLE_DATE + " DESC";
                mCurrentReviewsUri = DatabaseContract.ReviewEntry.buildUriForShowReviewWithPatternAndType(
                        ((MainActivity)getActivity()).getSearchPattern(),
                        Utils.getDrinkTypeFromSharedPrefs(getActivity(), true));
                return new CursorLoader(getActivity(), mCurrentReviewsUri,
                        QueryColumns.MainFragment.ReviewAllQuery.COLUMNS,
                        null, null, mReviewsSortOrder);
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
