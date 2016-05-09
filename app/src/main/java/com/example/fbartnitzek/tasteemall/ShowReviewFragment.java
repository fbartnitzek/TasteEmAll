package com.example.fbartnitzek.tasteemall;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;

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

public class ShowReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    private static final String LOG_TAG = ShowReviewFragment.class.getName();
    private static final int SHOW_REVIEW_LOADER_ID = 52561;

//    private TextView mProducerLabelView;
//    private TextView mProducerNameView;
//    private TextView mProducerNameLabelView;
//    private TextView mProducerLocationView;

    //    private TextView mDrinkLabelView;
//    private TextView mDrinkNameView;
//    private TextView mDrinkNameLabelView;
//    private TextView mDrinkTypeView;
//    private TextView mDrinkStyleView;
//    private TextView mDrinkSpecificsView;
//    private TextView mDrinkIngredientsView;
    private Uri mUri;
    private View mRootView;
//    private int mDrinkTypeIndex;


    public ShowReviewFragment() {
        Log.v(LOG_TAG, "ShowReviewFragment, hashCode=" + this.hashCode() + ", " + "");
    }

    private void calcCompleteUri() {
        if (mUri != null) {
            int id = DatabaseContract.getIdFromUri(mUri);
            // TODO
//            mUri = DatabaseContract.ReviewEntry.buildUriIncludingAll(id);
        }
    }


    public void updateFragment(Uri reviewUri) {
        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "reviewUri = [" + reviewUri + "]");
        mUri = reviewUri;
        calcCompleteUri();
        getLoaderManager().restartLoader(SHOW_REVIEW_LOADER_ID, null, this);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
