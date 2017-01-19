package com.fbartnitzek.tasteemall.filter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.data.BundleBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.fbartnitzek.tasteemall.filter.EntityFilterDialogFragment.EXTRA_ATTRIBUTE_NAME;
import static com.fbartnitzek.tasteemall.filter.EntityFilterDialogFragment.EXTRA_BASE_ENTITY;
import static com.fbartnitzek.tasteemall.filter.EntityFilterDialogFragment.EXTRA_JSON;
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

public class AttributeFilterDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String ATTRIBUTE_NAME = "ATTRIBUTE_NAME";
    private String mBaseEntity;
    private String mAttributeName;
    private ViewPager mViewPager;
    private SelectionsPagerAdapter mSelectionPagerAdapter;
    private static final String LOG_TAG = AttributeFilterDialogFragment.class.getName();


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        if (bundle == null) {
            throw new RuntimeException("neither args nor savedInstance - should never happen...");
        }

        mBaseEntity = bundle.getString(BASE_ENTITY);
        mAttributeName = bundle.getString(ATTRIBUTE_NAME);

        // nothing really works to restrict the size => TODO

        Window window = dialog.getWindow();

        window.setGravity(Gravity.CENTER);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//        WindowManager.LayoutParams p = window.getAttributes();
//        Log.v(LOG_TAG, "onCreateDialog, p.verticalMargin=" + p.verticalMargin + ", " + "p.horizontalMargin = [" + p.horizontalMargin+ "]");

//        p.height = getActivity().getResources().getDisplayMetrics().heightPixels / 2;
//        float vertMargin = p.verticalMargin;
//        p.verticalMargin = vertMargin + dpToPx(100);
//        p.y = prevY + dpToPx(100);

//        p.x = 150;
//        window.setAttributes(p);
        // fullscreen: height and width = -1
//        Log.v(LOG_TAG, "onCreateDialog, p.width=" + p.width + ", " + "p.height = [" + p.height+ "]");

        return dialog;
    }

//    public int dpToPx(float valueInDp) {
//        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BASE_ENTITY, mBaseEntity);
        outState.putString(ATTRIBUTE_NAME, mAttributeName);
        super.onSaveInstanceState(outState);
    }



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

        view.findViewById(R.id.ok).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (mViewPager != null && mSelectionPagerAdapter != null) {
                    AttributeBaseFilterFragment fragment = (AttributeBaseFilterFragment) mSelectionPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

                    try {
                        sendFilterUpdate(fragment.getFilter());
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.clear:
                sendFilterUpdate(null);
            case R.id.cancel:
        }
        this.dismiss();
    }



    private void sendFilterUpdate(JSONObject filter) {
        Intent intent = new Intent(EntityFilterDialogFragment.ACTION_FILTER_UPDATES);
        intent.putExtra(EXTRA_ATTRIBUTE_NAME, mAttributeName);
        intent.putExtra(EXTRA_BASE_ENTITY, mBaseEntity);
        Intent data = new Intent();
        data.putExtra(EntityFilterTabFragment.EXTRA_ATTRIBUTE_FILTERED, filter != null);
        if (filter != null) {
            intent.putExtra(EXTRA_JSON, filter.toString());
        }
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
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
