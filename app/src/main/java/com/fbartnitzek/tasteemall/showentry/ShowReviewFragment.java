package com.fbartnitzek.tasteemall.showentry;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns.ReviewFragment.ShowQuery;

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

public class ShowReviewFragment extends ShowBaseFragment implements View.OnClickListener {

    private static final String LOG_TAG = ShowReviewFragment.class.getName();
    private static final int SHOW_REVIEW_LOADER_ID = 52561;

    private TextView mProducerLabelView;
    private TextView mProducerNameView;
    private TextView mProducerNameLabelView;
    private TextView mProducerLocationView;

    private TextView mDrinkLabelView;
    private TextView mDrinkNameView;
    private TextView mDrinkNameLabelView;
    private TextView mDrinkTypeView;
    private TextView mDrinkStyleView;
    private TextView mDrinkSpecificsView;
    private TextView mDrinkIngredientsView;

    private Uri mUri;
    private View mRootView;
    private TextView mReviewUserView;
    private TextView mReviewRatingView;
    private TextView mReviewDescriptionView;
    private TextView mReviewReadableDateView;
    private TextView mReviewLocationView;
    private TextView mReviewRecommendedSidesView;
    private int mDrink_Id;
    private int mProducer_Id;
    private int mLocation_Id;
    private TextView mReviewLocationDescriptionView;


    public ShowReviewFragment() {
        Log.v(LOG_TAG, "ShowReviewFragment, hashCode=" + this.hashCode() + ", " + "");
    }

    @Override
    void calcCompleteUri() {
        if (mUri != null) {
            int id = DatabaseContract.getIdFromUri(mUri);
            mUri = DatabaseContract.ReviewEntry.buildUriForShowReview(id);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_show_review, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.e(LOG_TAG, "onCreateView without args - something went wrong..., hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        } else {
            if (args.containsKey(ShowReviewActivity.EXTRA_REVIEW_URI)) {
                mUri = args.getParcelable(ShowReviewActivity.EXTRA_REVIEW_URI);
                calcCompleteUri();
                Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            }
        }

        mProducerLabelView = (TextView) mRootView.findViewById(R.id.heading_choose_drink);
        mProducerNameView = (TextView) mRootView.findViewById(R.id.producer_name);
        mProducerNameLabelView = (TextView) mRootView.findViewById(R.id.label_producer_name);
        mProducerLocationView = (TextView) mRootView.findViewById(R.id.producer_location);

        mDrinkLabelView = (TextView) mRootView.findViewById(R.id.label_drink);
        mDrinkNameView = (TextView) mRootView.findViewById(R.id.drink_name);
        mDrinkNameLabelView = (TextView) mRootView.findViewById(R.id.label_drink_name);
        mDrinkTypeView = (TextView) mRootView.findViewById(R.id.drink_type);
        mDrinkStyleView = (TextView) mRootView.findViewById(R.id.drink_style);
        mDrinkSpecificsView = (TextView) mRootView.findViewById(R.id.drink_specifics);
        mDrinkIngredientsView = (TextView) mRootView.findViewById(R.id.drink_ingredients);

        mReviewUserView = (TextView) mRootView.findViewById(R.id.review_user_name);
        mReviewRatingView = (TextView) mRootView.findViewById(R.id.review_rating);
        mReviewDescriptionView = (TextView) mRootView.findViewById(R.id.review_description);
        mReviewReadableDateView = (TextView) mRootView.findViewById(R.id.review_readable_date);
        mReviewLocationView = (TextView) mRootView.findViewById(R.id.review_location);
        mReviewLocationDescriptionView = (TextView) mRootView.findViewById(R.id.review_location_description);
        mReviewRecommendedSidesView = (TextView) mRootView.findViewById(R.id.review_recommended_sides);

        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab_share);
        fab.setOnClickListener(this);

        createToolbar(mRootView, LOG_TAG);

        return mRootView;
    }

    @Override
    void updateToolbar() {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {

            String drinkName = mDrinkNameView.getText().toString();
            String producerName= mProducerNameView.getText().toString();
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                    getString(R.string.title_show_review, producerName, drinkName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume, hashCode=" + this.hashCode() + ", " + "");
//        getLoaderManager().initLoader(SHOW_REVIEW_LOADER_ID, null, this);
        getLoaderManager().restartLoader(SHOW_REVIEW_LOADER_ID, null, this);    //overkill, but init wont change the views... TODO
        super.onResume();
    }

    @Override
    public void updateFragment(Uri reviewUri) {
        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "reviewUri = [" + reviewUri + "]");
        mUri = reviewUri;
        calcCompleteUri();
        getLoaderManager().restartLoader(SHOW_REVIEW_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

            // each value has its label before - a11y should work that way...
            String drinkType = data.getString(ShowQuery.COL_DRINK_TYPE);
            int drinkTypeIndex = Utils.getDrinkTypeId(getActivity(), drinkType);
            mDrinkTypeView.setText(drinkType);

            int readableProducerTypeIndex = Utils.getReadableProducerNameId(getActivity(), drinkTypeIndex);
            mProducerLabelView.setText(getActivity()
                    .getString(R.string.producer_details_label,
                            getString(readableProducerTypeIndex)));
            mProducerNameLabelView.setText(readableProducerTypeIndex);

            int readableDrinkTypeIndex = Utils.getReadableDrinkNameId(getActivity(), drinkTypeIndex);
            mDrinkLabelView.setText(
                    getString(R.string.drink_details_label,
                            getString(readableDrinkTypeIndex)));
            mDrinkNameLabelView.setText(readableDrinkTypeIndex);

            mDrink_Id = data.getInt(ShowQuery.COL_DRINK__ID);
            mProducer_Id = data.getInt(ShowQuery.COL_PRODUCER__ID);
            mLocation_Id = data.getInt(ShowQuery.COL_REVIEW_LOCATION__ID);
            Log.v(LOG_TAG, "onLoadFinished, mLocation_Id=" + mLocation_Id);

            int reviewId = DatabaseContract.getIdFromUri(mUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    //shared element transition
                mProducerNameView.setTransitionName(getString(R.string.shared_transition_review_producer) + reviewId);
                mDrinkNameView.setTransitionName(getString(R.string.shared_transition_review_drink) + reviewId);
            }

            String producerName = data.getString(ShowQuery.COL_PRODUCER_NAME);
            String drinkName = data.getString(ShowQuery.COL_DRINK_NAME);

            mProducerNameView.setText(producerName);
            mProducerNameView.setOnClickListener(this);
            // why were values not updated...? - TODO: maybe restored through state...? - later
//            String prodLoc = data.getString(ShowQuery.COL_PRODUCER_LOCATION);
//            Log.v(LOG_TAG, "onLoadFinished, getActivity()==null? " + (getActivity()==null) + ", prodLoc=" + prodLoc );  // updated result, but won't be displayed...?
//            mProducerLocationView.setText(prodLoc);
            mProducerLocationView.setText(data.getString(ShowQuery.COL_PRODUCER_LOCATION));

            mDrinkNameView.setText(drinkName);
            mDrinkNameView.setOnClickListener(this);

            mDrinkStyleView.setText(data.getString(ShowQuery.COL_DRINK_STYLE));
            mDrinkSpecificsView.setText(data.getString(ShowQuery.COL_DRINK_SPECIFICS));
            mDrinkIngredientsView.setText(data.getString(ShowQuery.COL_DRINK_INGREDIENTS));

            mReviewUserView.setText(data.getString(ShowQuery.COL_USER_NAME));
            mReviewRatingView.setText(data.getString(ShowQuery.COL_REVIEW_RATING));
            mReviewDescriptionView.setText(data.getString(ShowQuery.COL_REVIEW_DESCRIPTION));
            mReviewReadableDateView.setText(data.getString(ShowQuery.COL_REVIEW_READABLE_DATE));
            mReviewLocationView.setText(data.getString(ShowQuery.COL_REVIEW_LOCATION_FORMATTED));
            mReviewLocationDescriptionView.setText(data.getString(ShowQuery.COL_REVIEW_LOCATION_DESCRIPTION));
            mReviewRecommendedSidesView.setText(data.getString(ShowQuery.COL_REVIEW_RECOMMENDED_SIDES));

            mReviewLocationView.setOnClickListener(this);

            updateToolbar();

            resumeActivityEnterTransition();
        }
    }

    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // not that important which one - but is lowest :-)
            ((ShowReviewActivity) getActivity()).scheduleStartPostponedTransition(mDrinkNameView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing
    }

    @Override
    public void onClick(View v) {
//        Log.v(LOG_TAG, "onClick, mProducer_Id=" + mProducer_Id + ", mDrink_Id=" + mDrink_Id + ", mLocation_Id=" + mLocation_Id);
        if (v.getId() == R.id.producer_name && mProducer_Id > -1) {  // open producer
            Bundle bundle = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                bundle = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(),
                        new Pair<View, String>(mProducerNameView,
                                getString(R.string.shared_transition_producer_producer) + mProducer_Id)
                ).toBundle();
            }
            startActivity(
                    new Intent(getActivity(), ShowProducerActivity.class)
                        .setData(DatabaseContract.ProducerEntry.buildUri(mProducer_Id)), bundle);
        } else if (v.getId() == R.id.drink_name && mDrink_Id > -1) {    // open drink
            Bundle bundle = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                bundle = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(),
                        new Pair<View, String>(mProducerNameView,
                                getString(R.string.shared_transition_drink_producer) + mDrink_Id),
                        new Pair<View, String>(mDrinkNameView,
                                getString(R.string.shared_transition_drink_drink) + mDrink_Id)
                ).toBundle();
            }
            startActivity(
                    new Intent(getActivity(), ShowDrinkActivity.class)
                            .setData(DatabaseContract.DrinkEntry.buildUri(mDrink_Id)), bundle);
        } else if (v.getId() == R.id.review_location && mLocation_Id > -1) {
            startActivity(
                    new Intent(getActivity(), ShowLocationActivity.class)
                            .setData(DatabaseContract.LocationEntry.buildUri(mLocation_Id)));
        } else if (v.getId() == R.id.fab_share && mProducer_Id > -1) {  // all loaded
            shareReview();
        }
    }

    private void shareReview() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType(getString(R.string.share_content_type));
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getString(R.string.share_empty_subject));
        String shareBody = getString(R.string.share_review_text,
                mProducerNameView.getText().toString(),
                mDrinkNameView.getText().toString(),
                mReviewRatingView.getText().toString(),
                mReviewDescriptionView.getText().toString(),
                mReviewUserView.getText().toString(),
                mReviewReadableDateView.getText().toString());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.choose_share_provider)));
    }
}
