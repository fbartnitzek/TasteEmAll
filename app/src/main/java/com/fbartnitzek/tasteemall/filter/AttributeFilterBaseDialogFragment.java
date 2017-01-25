package com.fbartnitzek.tasteemall.filter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.fbartnitzek.tasteemall.R;

import org.json.JSONObject;

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


public abstract class AttributeFilterBaseDialogFragment extends DialogFragment {

    public static final String ATTRIBUTE_NAME = "ATTRIBUTE_NAME";

    protected String mBaseEntity;
    protected String mAttributeName;

    private static final String LOG_TAG = AttributeFilterBaseDialogFragment.class.getName();

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

        if (window != null) {
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
        }

        return dialog;
    }

    abstract protected void onOkClicked();

    protected void setButtonListeners(View rootView) {
        rootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOkClicked();
                Log.v(LOG_TAG, "onClick, trying dismiss...");
                AttributeFilterBaseDialogFragment.this.dismiss();

            }
        });
        rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFilterUpdate(null);
                Log.v(LOG_TAG, "onClick, trying dismiss...");
                AttributeFilterBaseDialogFragment.this.dismiss();
            }
        });
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

    protected void sendFilterUpdate(JSONObject filter) {
        Log.v(LOG_TAG, "sendFilterUpdate, hashCode=" + this.hashCode() + ", " + "filter = [" + filter + "]");
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

//    @Override
//    public void onAttach(Context activity) {
//        super.onAttach(activity);
//    }
}
