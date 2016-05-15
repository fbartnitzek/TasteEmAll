package com.fbartnitzek.tasteemall;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

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

public class ShowMapActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_MAP_URI = "EXTRA_SHOW_MAP_URI";
    private static final String FRAGMENT_TAG = "SHOW_MAP_FRAGMENT_TAG";


    private Uri mContentUri;
    private static final String LOG_TAG = ShowMapActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        setContentView(R.layout.activity_show_map);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            ShowMapFragment fragment = new ShowMapFragment();

            mContentUri = getIntent().getData();
            if (mContentUri != null) {
                Bundle args = new Bundle();
                args.putParcelable(EXTRA_SHOW_MAP_URI, mContentUri);
                fragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit();

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

}
