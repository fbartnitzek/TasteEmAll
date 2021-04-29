package com.fbartnitzek.tasteemall.filter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.BundleBuilder;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

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

public class AttributeFilterDialogFragment extends AttributeFilterBaseDialogFragment {

    private ViewPager mViewPager;
    private SelectionsPagerAdapter mSelectionPagerAdapter;
    private static final String LOG_TAG = AttributeFilterDialogFragment.class.getName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_attribute_dialog, container);

        // tab slider
        mSelectionPagerAdapter = new SelectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)view.findViewById(R.id.pager);
        mViewPager.setAdapter(mSelectionPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    Log.v(LOG_TAG, "onPageScrollStateChanged, currentItem=" + mViewPager.getCurrentItem() + "]");
                    if (mViewPager.getCurrentItem() == 1) {
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
//                    } else if (mViewPager.getCurrentItem() == 0) {
//                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
//                                .showSoftInputFromInputMethod(mViewPager.getWindowToken(), 0);
                    }
                }
            }
        });

        setButtonListeners(view);
        return view;
    }

    @Override
    protected void onOkClicked() {
        if (mViewPager != null && mSelectionPagerAdapter != null) {
            AttributeBaseFilterFragment fragment =
                    (AttributeBaseFilterFragment) mSelectionPagerAdapter.instantiateItem(
                            mViewPager, mViewPager.getCurrentItem());
            try {
                sendFilterUpdate(fragment.getFilter());
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public class SelectionsPagerAdapter extends FragmentPagerAdapter{

        public SelectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AttributeFilterTextTabFragment textTabFragment = new AttributeFilterTextTabFragment();
                    textTabFragment.setArguments(new BundleBuilder()
                                    .putString(BASE_ENTITY, mBaseEntity)
                                    .putString(ATTRIBUTE_NAME, mAttributeName)
                                    .build());
                    return textTabFragment;
                case 1:
                    AttributeFilterSelectTabFragment selectTabFragment = new AttributeFilterSelectTabFragment();
                    selectTabFragment.setArguments(new BundleBuilder()
                                    .putString(BASE_ENTITY, mBaseEntity)
                                    .putString(ATTRIBUTE_NAME, mAttributeName)
                                    .build());
                    return selectTabFragment;
                default:
                    throw new RuntimeException("wrong position in AFDF.FragmentAdapter");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Text Filter";
                case 1:
                    return "Multi Select";
                default:
                    throw new RuntimeException("wrong position!");
            }

        }


    }
}
