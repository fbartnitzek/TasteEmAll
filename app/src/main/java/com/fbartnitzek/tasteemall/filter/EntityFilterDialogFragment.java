package com.fbartnitzek.tasteemall.filter;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.BundleBuilder;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.JsonHelper;
import com.fbartnitzek.tasteemall.data.pojo.Drink;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.data.pojo.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

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
 *
 * src: https://github.com/kiteflo/Android_Tabbed_Dialog
 */


public class EntityFilterDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String FILTER_JSON = "FILTER_JSON";
    public static final String EXTRA_ATTRIBUTE_NAME = "EXTRA_ATTRIBUTE_NAME";
    public static final String EXTRA_BASE_ENTITY = "BASE_ENTITY";
    public static final String EXTRA_JSON = "EXTRA_JSON";
    public static final String ACTION_FILTER_UPDATES = Objects.requireNonNull(
            EntityFilterDialogFragment.class.getPackage()).getName() + ".EntityFilterUpdates";
    private String mBaseEntity;
    private JSONObject mFilterJson;
    private GenericFilterFinishListener finishListener;

    private static final String LOG_TAG = EntityFilterDialogFragment.class.getName();

    public interface GenericFilterFinishListener {
        void onFinishEditDialog(Uri jsonFilterUri);
    }

    // TODO restore in child-fragments based on jsonFilter => later


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
        if (bundle.containsKey(FILTER_JSON)) {
            try {
                mFilterJson = new JSONObject(bundle.getString(FILTER_JSON));
            } catch (JSONException e) {
                // conversion did not work ... use new Object, but log error
                Log.e(LOG_TAG, "onCreateDialog: json could not be restored: " + mFilterJson, e);
                e.printStackTrace();
            }
        }

        if (mFilterJson == null) {
            mFilterJson = new JSONObject();
            try {
                mFilterJson.put(mBaseEntity, new JSONObject());
            } catch (JSONException e) {
                e.printStackTrace();// ignore...
            }
        }



        // TODO: restore position

        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BASE_ENTITY, mBaseEntity);
        outState.putString(FILTER_JSON, mFilterJson.toString());
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter_entity_dialog, container);

        // tab slider
        SectionsPagerAdapter mFilterEntityPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(mFilterEntityPagerAdapter);

        view.findViewById(R.id.filter).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);

        return view;

    }

    // todo: something different then Fragment.onAttach
    @Override
    public void onAttach(@NotNull Activity activity) {
        super.onAttach(activity);

        // communication with the MainActivity to return filter uri

        // http://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment
        if (activity instanceof GenericFilterFinishListener) {
            finishListener = (GenericFilterFinishListener) activity;
        } else {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement " + GenericFilterFinishListener.class.getSimpleName());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        finishListener = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.filter) {// return filter baseEntity with collected json
            try {
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", mFilterJson:" + mFilterJson);
                finishListener.onFinishEditDialog(DatabaseContract.buildUriWithJson(mFilterJson));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//        } else if (id == R.id.cancel) {
        }
        this.dismiss();

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new EntityFilterTabFragment().setArguments2(new BundleBuilder()
                        .putString(BASE_ENTITY, mBaseEntity)
                        .putString(EntityFilterTabFragment.NAME, getString(Utils.getEntityNameId(getContext(), mBaseEntity)))
                        .build());
            } else if (Drink.ENTITY.equals(mBaseEntity) && position == 1) {
                return new EntityFilterTabFragment().setArguments2(new BundleBuilder()
                                .putString(BASE_ENTITY, Producer.ENTITY)
                                .putString(EntityFilterTabFragment.NAME, getEntityOfEntityName(
                                        Objects.requireNonNull(getContext()), Producer.ENTITY, Drink.ENTITY))
                                .build());
            } else if (Review.ENTITY.equals(mBaseEntity)) {
                switch (position) {
                    case 1:
                        return new EntityFilterTabFragment().setArguments2(new BundleBuilder()
                                        .putString(BASE_ENTITY, User.ENTITY)
                                        .putString(EntityFilterTabFragment.NAME, getEntityOfEntityName(
                                                Objects.requireNonNull(getContext()), User.ENTITY, Review.ENTITY))
                                        .build());
                    case 2:
                        return new EntityFilterTabFragment().setArguments2(new BundleBuilder()
                                .putString(BASE_ENTITY, Location.ENTITY)
                                .putString(EntityFilterTabFragment.NAME, getEntityOfEntityName(
                                        Objects.requireNonNull(getContext()), Location.ENTITY, Review.ENTITY))
                                .build());
                    case 3:
                        return new EntityFilterTabFragment().setArguments2(new BundleBuilder()
                                .putString(BASE_ENTITY, Drink.ENTITY)
                                .putString(EntityFilterTabFragment.NAME, getEntityOfEntityName(
                                        Objects.requireNonNull(getContext()), Drink.ENTITY, Review.ENTITY))
                                .build());
                    case 4:
                        return new EntityFilterTabFragment().setArguments2(new BundleBuilder()
                                .putString(BASE_ENTITY, Producer.ENTITY)
                                .putString(EntityFilterTabFragment.NAME, getEntityOfEntityName(
                                        Objects.requireNonNull(getContext()), Producer.ENTITY, Review.ENTITY))
                                .build());
                    default:
                        throw new RuntimeException("wrong position in FragmentAdapter");
                }
            } else {
                throw new RuntimeException("wrong position in FragmentAdapter");
            }
        }

        @Override
        public int getCount() {
            switch (mBaseEntity) {
                case Review.ENTITY:
                    return 5;
                case Drink.ENTITY:
                    return 2;
                case User.ENTITY:
                case Location.ENTITY:
                case Producer.ENTITY:
                    return 1;
                default:
                    throw new RuntimeException("wrong baseEntity: '" + mBaseEntity + "'");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(Utils.getEntityNameId(getContext(), mBaseEntity));
            } else if (Drink.ENTITY.equals(mBaseEntity) && position == 1) {
                return getString(Utils.getEntityNameId(getContext(), Producer.ENTITY));// + " for " + Drink.ENTITY;
            } else if (Review.ENTITY.equals(mBaseEntity)) {
                switch (position) {
                    case 1:
                        return getString(Utils.getEntityNameId(getContext(), User.ENTITY)); // + " for " + Review.ENTITY
                    case 2:
                        return getString(Utils.getEntityNameId(getContext(), Location.ENTITY)); // + " for " + Review.ENTITY)
                    case 3:
                        return getString(Utils.getEntityNameId(getContext(), Drink.ENTITY)); //+ " for " + Review.ENTITY)
                    case 4:
                        return getString(Utils.getEntityNameId(getContext(), Producer.ENTITY)); // + " for " + Review.ENTITY
                    default:
                        throw new RuntimeException("wrong position in FragmentAdapter");
                }
            } else {
                throw new RuntimeException("wrong position in FragmentAdapter");
            }
        }
    }

    private static String getEntityOfEntityName(Context context, String childEntity, String baseEntity) {
        return context.getString(
                R.string.entity_of_entity,
                context.getString(Utils.getEntityNameId(context, childEntity)),
                context.getString(Utils.getEntityNameId(context, baseEntity)));
    }


    // get json-filter-updates from sub-fragments (attributes of entities)

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(mMessageReceiver,
                new IntentFilter(ACTION_FILTER_UPDATES));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()))
                .unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String attribute = intent.getStringExtra(EXTRA_ATTRIBUTE_NAME);
            String entity = intent.getStringExtra(EXTRA_BASE_ENTITY);
            String json = intent.getStringExtra(EXTRA_JSON);
            Log.v(LOG_TAG, "onReceive, baseEntity=" + mBaseEntity + ", entity=" + entity + ", attribute=" + attribute + ", json=" + json + ", hashCode=" + this.hashCode());
//            Log.v(LOG_TAG, "onReceive start, mFilterJson=" + mFilterJson);

            try {

                // todo: confusing
                JSONObject baseEntity = mFilterJson.getJSONObject(mBaseEntity);
                if (mBaseEntity.equals(entity)) {
                    mFilterJson.put(entity, updateEntity(baseEntity, attribute, json));
                } else if (Drink.ENTITY.equals(mBaseEntity) && Producer.ENTITY.equals(entity)) {
                    JSONObject producerEntity = JsonHelper.getOrCreateJsonObject(baseEntity, entity);
                    producerEntity = updateEntity(producerEntity, attribute, json);

                    mFilterJson.getJSONObject(mBaseEntity).put(entity, producerEntity);
                } else if (Review.ENTITY.equals(mBaseEntity)) {
                    if (!Producer.ENTITY.equals(entity)){
                        JSONObject childEntity = JsonHelper.getOrCreateJsonObject(baseEntity, entity);
                        childEntity = updateEntity(childEntity, attribute, json);

                        mFilterJson.getJSONObject(mBaseEntity).put(entity, childEntity);
                    } else {    // review.drink.producer
                        JSONObject drinkEntity = JsonHelper.getOrCreateJsonObject(baseEntity, Drink.ENTITY);
                        JSONObject producerEntity = JsonHelper.getOrCreateJsonObject(drinkEntity, Producer.ENTITY);
                        producerEntity = updateEntity(producerEntity, attribute, json);

                        mFilterJson.getJSONObject(mBaseEntity).put(Drink.ENTITY, drinkEntity.put(Producer.ENTITY, producerEntity));
                    }
                } else {
                    throw new RuntimeException("should never happen...!");
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "onReceive: jsonException", e);
            }

//            Log.v(LOG_TAG, "onReceive end, mFilterJson=" + mFilterJson.toString());
        }
    };



    private static JSONObject updateEntity(JSONObject entity, String attribute, String json) throws JSONException {
        if (json == null) {
            entity.remove(attribute);
        } else {
            entity.put(attribute, new JSONObject(json));
        }
        return entity;
    }
}
