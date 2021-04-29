package com.fbartnitzek.tasteemall.filter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.DatabaseContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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

public class AttributeFilterTextTabFragment extends AttributeBaseFilterFragment implements LoaderManager.LoaderCallbacks<Cursor>,AttributeValuesAdapter.AttributeValuesAdapterClickHandler {

    private static final int ATTRIBUTE_VALUES_LOADER_ID = 67548;
    private EditText mEditFilter;
    private RecyclerView mValuesRecycler;
    private AttributeValuesAdapter mAttributeValuesAdapter;
    private String mAttributeFilter;

    private static final String LOG_TAG = AttributeFilterTextTabFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        mEditFilter= (EditText) mRootView.findViewById(R.id.attribute_filter);
        mEditFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAttributeFilter = s.toString();
//                Log.v(LOG_TAG, "afterTextChanged, hashCode=" + this.hashCode() + ", " + "s = [" + s + "]");
                getLoaderManager().restartLoader(ATTRIBUTE_VALUES_LOADER_ID, null, AttributeFilterTextTabFragment.this);
            }
        });

        mEditFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                Log.v(LOG_TAG, "onEditorAction, hashCode=" + this.hashCode() + ", " + "v = [" + v + "], actionId = [" + actionId + "], event = [" + event + "]");
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditFilter.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // focus editFilter - thx for: http://stackoverflow.com/a/26012003/5477716
        mEditFilter.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mValuesRecycler = (RecyclerView) mRootView.findViewById(R.id.attribute_filter_list);
        mValuesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mAttributeValuesAdapter = new AttributeValuesAdapter(this);
        mValuesRecycler.setAdapter(mAttributeValuesAdapter);

        return mRootView;
    }

    @Override
    protected int getTabLayout() {
        return R.layout.fragment_filter_attribute_text_tab;
    }

    @Override
    public JSONObject getFilter() throws JSONException, UnsupportedEncodingException {
        // TODO: view or string?
        return new JSONObject().put(DatabaseContract.Operations.CONTAINS,
                DatabaseContract.encodeValue(mEditFilter.getText().toString()));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ATTRIBUTE_VALUES_LOADER_ID:

                String alias = DatabaseContract.ALIASES.get(mBaseEntity) + ".";
                JSONObject json = new JSONObject();
                Uri uri;
                try {
                    json.put(mBaseEntity, new JSONObject().put(mAttributeName,
                            new JSONObject().put(DatabaseContract.Operations.CONTAINS,
                                    DatabaseContract.encodeValue(mAttributeFilter))));
                    uri = DatabaseContract.buildUriWithJson(json);
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("invalid json query " + json.toString());
                }
//                Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", uri=" + uri +"]");
                return new CursorLoader(getActivity(), uri,
                        new String[]{"DISTINCT " + alias + mAttributeName},
                        null, null, alias + mAttributeName + " ASC");
            default:
                throw new RuntimeException("wrong loaderId in " + AttributeFilterTextTabFragment.class.getSimpleName());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ATTRIBUTE_VALUES_LOADER_ID:
//                Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
                mAttributeValuesAdapter.swapCursor(data);
                mNameView.setText(getAttributeTitle(getContext(), data.getCount()));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ATTRIBUTE_VALUES_LOADER_ID:
                mAttributeValuesAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onAttributeClick(String s, int adapterPosition) {
        mEditFilter.setText(s);
    }
}
