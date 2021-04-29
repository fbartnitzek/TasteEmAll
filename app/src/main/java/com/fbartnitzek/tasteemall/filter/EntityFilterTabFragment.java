package com.fbartnitzek.tasteemall.filter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.BundleBuilder;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Review;

import java.util.ArrayList;
import java.util.List;

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

public class EntityFilterTabFragment extends Fragment implements AttributeAdapter.AttributeAdapterHandler {

    public static final String BASE_ENTITY = "BASE_ENTITY";
    public static final String NAME = "NAME";
    private static final int REQUEST_ATTRIBUTE_FILTER_CODE = 63451;
    private static final String ADAPTER_POSITION = "ADAPTER_POSITION";
    public static final String EXTRA_ATTRIBUTE_FILTERED = "EXTRA_ATTRIBUTE_FILTERED";
    private static final String LIST_SELECTED = "STATE_LIST_SELECTED";

    private String mName;
    private String mBaseEntity;
    private List<String> mEntityArguments;
    private RecyclerView mAttributesRecycler;
    private AttributeAdapter mAttributeAdapter;
    private int mPosition;
    private ArrayList<Integer> mSelected = new ArrayList<>();


    // TODO special attribute fragments later... (date, map) => later

    // TODO special attribute for location (empty)

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_entity_tab, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        if (bundle == null) {
            throw new RuntimeException("neither args nor savedInstance - should never happen...");
        }

        mBaseEntity = bundle.getString(BASE_ENTITY);
        mName = bundle.getString(NAME, "no name");
        mEntityArguments = DatabaseContract.ATTRIBUTES.get(mBaseEntity);
        mPosition = bundle.getInt(ADAPTER_POSITION, 0);

        ((TextView)rootView.findViewById(R.id.name)).setText(this.mName);


        // TODO: restore from json...
        if (bundle.containsKey(LIST_SELECTED)) {
            mSelected.clear();
            mSelected.addAll(bundle.getIntegerArrayList(LIST_SELECTED));
        }

        mAttributesRecycler = (RecyclerView) rootView.findViewById(R.id.recyclerview_attributes);
        mAttributesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAttributeAdapter = new AttributeAdapter(mEntityArguments, this, getContext(), mSelected);
        mAttributesRecycler.setAdapter(mAttributeAdapter);

        return rootView;
    }



    public EntityFilterTabFragment setArguments2(Bundle args) {
        super.setArguments(args);
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(NAME, mName);
        outState.putString(BASE_ENTITY, mBaseEntity);
        outState.putInt(ADAPTER_POSITION, mPosition);
        outState.putIntegerArrayList(LIST_SELECTED, mSelected);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(String attributeName, int adapterPosition) {
        // open textfilterdialog
//        Toast.makeText(getActivity(), attributeName + " was clicked", Toast.LENGTH_SHORT).show();

        FragmentManager fm = getActivity().getSupportFragmentManager();

        // special fragments
        if (Review.ENTITY.equals(mBaseEntity) && Review.READABLE_DATE.equals(attributeName)) {

            DateFilterDialogFragment dateFilterDialogFragment = new DateFilterDialogFragment();
            dateFilterDialogFragment.setArguments(new BundleBuilder()
                    .putString(EntityFilterTabFragment.BASE_ENTITY, mBaseEntity)
                    .putString(AttributeFilterDialogFragment.ATTRIBUTE_NAME, attributeName)
                    .build());
            mPosition = adapterPosition;
            dateFilterDialogFragment.setTargetFragment(this, REQUEST_ATTRIBUTE_FILTER_CODE);
            dateFilterDialogFragment.show(fm, "DateFilterDialogFragment");

        } else {

            AttributeFilterDialogFragment attributeFilterDialog = new AttributeFilterDialogFragment();
            attributeFilterDialog.setArguments(new BundleBuilder()
                    .putString(EntityFilterTabFragment.BASE_ENTITY, mBaseEntity)
                    .putString(AttributeFilterDialogFragment.ATTRIBUTE_NAME, attributeName)
                    .build());
            mPosition = adapterPosition;
            Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "attributeName = [" + attributeName + "], adapterPosition = [" + adapterPosition + "]");
            attributeFilterDialog.setTargetFragment(this, REQUEST_ATTRIBUTE_FILTER_CODE);
            attributeFilterDialog.show(fm, "AttributeFilterDialogFragment");
        }

    }

    private static final String LOG_TAG = EntityFilterTabFragment.class.getName();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == REQUEST_ATTRIBUTE_FILTER_CODE && resultCode == Activity.RESULT_OK) {

            if (data.hasExtra(EXTRA_ATTRIBUTE_FILTERED)) {
                RecyclerView.ViewHolder vh = mAttributesRecycler.findViewHolderForAdapterPosition(mPosition);
                Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "vh=" + vh);

                // snackbar with all filters for entity...? - not usable for selection...

                // TODO: store to restore, maybe via jsonFilter...

                if (data.getBooleanExtra(EXTRA_ATTRIBUTE_FILTERED, false)) {
                    mSelected.add(mPosition);
                    vh.itemView.setBackgroundColor(Color.GREEN);
                } else {
                    mSelected.remove((Integer) mPosition);
                    vh.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void updateSelectedList() {
//        // TODO: when to call after restore...?
//        for (int i = 0; i < mEntityArguments.size(); ++i) {
//            RecyclerView.ViewHolder vh = mAttributesRecycler.findViewHolderForAdapterPosition(i);
//            if (mSelected.contains(i)) {
//                vh.itemView.setBackgroundColor(Color.GREEN);
//            } else {
//                vh.itemView.setBackgroundColor(Color.TRANSPARENT);
//            }
//        }
//    }
}
