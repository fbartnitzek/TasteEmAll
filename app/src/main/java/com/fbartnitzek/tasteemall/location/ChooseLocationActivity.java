package com.fbartnitzek.tasteemall.location;

import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;

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

public class ChooseLocationActivity extends AppCompatActivity {
    public static final String EXTRA_LOCATION_ADDRESSES = "EXTRA_LOCATION_ADDRESSES";
    private static final String LOG_TAG = ChooseLocationActivity.class.getName();
    private static final String CHOOSE_LOCATION_FRAGMENT_TAG = "CHOOSE_LOCATION_FRAGMENT_TAG";
    public static final String EXTRA_LOCATION_INPUT = "EXTRA_LOCATION_INPUT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        if (findViewById(R.id.container_choose_location_fragment) != null) {

            if (savedInstanceState != null) {
                return;
            }
            // TODO: animations...

            ChooseLocationFragment fragment = getFragment();
            if (fragment == null) {
                fragment = new ChooseLocationFragment();
                if (getIntent().hasExtra(EXTRA_LOCATION_INPUT)) {
                    fragment.setLocationInput(getIntent().getStringExtra(EXTRA_LOCATION_INPUT));
                }
                if (getIntent().hasExtra(EXTRA_LOCATION_ADDRESSES)) {
                    fragment.setmLocationAddresses(Utils.castParcelableArray(
                            Address.class, getIntent().getParcelableArrayExtra(EXTRA_LOCATION_ADDRESSES)));
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_choose_location_fragment, fragment, CHOOSE_LOCATION_FRAGMENT_TAG)
                        .commit();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_location, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_map:
                Toast.makeText(ChooseLocationActivity.this, "show selected in map", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_save:
//                Toast.makeText(ChooseLocationActivity.this, "save selected", Toast.LENGTH_SHORT).show();
                ChooseLocationFragment fragment = getFragment();
                if (fragment != null) {
                    Log.v(LOG_TAG, "onOptionsItemSelected - calling fragment for saving, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                    fragment.saveData();
                }
                return true;
            case android.R.id.home: //workaround: back button not working... - maybe feature with warning :-p
                onBackPressed();
                return true;
            default:
                Log.v(LOG_TAG, "onOptionsItemSelected, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }
        return super.onOptionsItemSelected(item);
    }

    private ChooseLocationFragment getFragment() {
        return (ChooseLocationFragment) getSupportFragmentManager().findFragmentByTag(CHOOSE_LOCATION_FRAGMENT_TAG);
    }


}


