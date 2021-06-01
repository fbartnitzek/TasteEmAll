package com.fbartnitzek.tasteemall.filter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.fbartnitzek.tasteemall.filter.AttributeFilterDialogFragment.ATTRIBUTE_NAME;
import static com.fbartnitzek.tasteemall.filter.EntityFilterTabFragment.BASE_ENTITY;

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


public abstract class AttributeBaseFilterFragment extends Fragment {

    protected String mBaseEntity;
    protected String mAttributeName;
    protected View mRootView;
    protected TextView mNameView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getTabLayout(), container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        if (bundle == null) {
            throw new RuntimeException("neither args nor savedInstance - should never happen...");
        }

        mBaseEntity = bundle.getString(BASE_ENTITY);
        mAttributeName = bundle.getString(ATTRIBUTE_NAME);

        mNameView = mRootView.findViewById((R.id.name));
        mNameView.setText(getAttributeTitle(getContext(), -1));

        return mRootView;
    }

    protected String getAttributeTitle(Context context, int count) {
        if (count < 0) {
            return context.getString(R.string.label_attribute_of_entity,
                    context.getString(Utils.getAttributeNameId(context, mAttributeName)), mBaseEntity);
        } else {
            return context.getString(R.string.label_attribute_of_entity_n,
                    context.getString(Utils.getAttributeNameId(context, mAttributeName)), mBaseEntity, count);
        }

    }

    protected abstract int getTabLayout();


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ATTRIBUTE_NAME, mAttributeName);
        outState.putString(BASE_ENTITY, mBaseEntity);
        super.onSaveInstanceState(outState);
    }

    abstract public JSONObject getFilter() throws JSONException, UnsupportedEncodingException;
}
