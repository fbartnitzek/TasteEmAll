package com.fbartnitzek.tasteemall.mainpager;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbartnitzek.tasteemall.R;

import org.json.JSONException;
import org.json.JSONObject;

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


abstract public class BasePagerFragment extends Fragment {

    private static final String LOG_TAG = BasePagerFragment.class.getName();
    private static final String STATE_JSON_FILTER = "STATE_JSON_FILTER";


    protected View mRootView;
    protected Uri jsonUri = null;
    protected JSONObject jsonTextFilter = null;
    protected String entity = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_JSON_FILTER)) {
            try {
                this.jsonTextFilter = new JSONObject(savedInstanceState.getString(STATE_JSON_FILTER));
            } catch (JSONException e) {
                this.jsonTextFilter = null;
            }
        }

        restartLoader();
    }

    public void setJsonUri(Uri jsonUri) {
        this.jsonUri = jsonUri;
    }

    public Uri getJsonUri() {
        return jsonUri;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_JSON_FILTER, jsonTextFilter.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_page_entity, container, false);
        return mRootView;
    }

    public abstract int getSharedTransitionId();   // TODO: later with matching adds...

    public abstract void restartLoader();

    public void fragmentBecameVisible() {
//        Log.v(LOG_TAG, "fragmentBecameVisible, hashCode=" + this.hashCode() + ", " + "");
        restartLoader();
    }

    public String getEntity() {
        return entity;
    }
}
