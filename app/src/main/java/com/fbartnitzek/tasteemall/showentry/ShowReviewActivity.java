package com.fbartnitzek.tasteemall.showentry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.addentry.AddReviewActivity;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Review;
import com.fbartnitzek.tasteemall.tasks.DeleteEntryTask;

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

public class ShowReviewActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOw_REVIEW_TAG";
    private static final String LOG_TAG = ShowReviewActivity.class.getName();
    public static final String EXTRA_REVIEW_URI = "EXTRA_REVIEW_URI";
    private static final int EDIT_REVIEW_REQUEST = 65434;
    private Uri mContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_review);

        // explicitly add fragment with pattern
        if (findViewById(R.id.container_show_review_fragment) != null) {
            if (savedInstanceState != null) {   // no overlapping fragments on return
//                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            supportPostponeEnterTransition();   // wait until Fragment-Views are done

            ShowReviewFragment fragment = new ShowReviewFragment();

            mContentUri = getIntent().getData();
            if (mContentUri != null) {
                Bundle args = new Bundle();
                args.putParcelable(EXTRA_REVIEW_URI, mContentUri);
                fragment.setArguments(args);
            } else {
                Log.e(LOG_TAG, "onCreate - without intentData???, hashCode=" + this.hashCode() + "]");
            }

            getSupportFragmentManager().beginTransaction()
                    // only for fragment transition within same activity!
//                    .setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                    .add(R.id.container_show_review_fragment, fragment, FRAGMENT_TAG)
                    .commit();
        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + "]");
        }

        //init toolbar in fragment
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    private ShowReviewFragment getFragment(){
        return (ShowReviewFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        } else if (itemId == R.id.action_edit) {
            startEditActivity();
            return true;
        } else if (itemId == R.id.action_delete) {
            startDelete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startDelete() {
        Log.v(LOG_TAG, "startDelete, hashCode=" + this.hashCode() + ", " + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_really_delete_entry);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.delete_button,
                (dialog, which) -> {
                    Uri deleteUri = Utils.calcSingleReviewUri(mContentUri);
                    new DeleteEntryTask(
                            ShowReviewActivity.this,
                            DatabaseContract.ReviewEntry.TABLE_NAME + "." + Review.READABLE_DATE)
                            .execute(deleteUri);
                }
        );
        builder.setNegativeButton(
                R.string.keep_button,
                (dialog, which) -> Toast.makeText(ShowReviewActivity.this, "keeping entry", Toast.LENGTH_SHORT).show()
        );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startEditActivity() {
        Intent intent = new Intent(this, AddReviewActivity.class);
        intent.setData(mContentUri);

        View drinkName = findViewById(R.id.drink_name);
        int review_Id = DatabaseContract.getIdFromUri(mContentUri);

        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                this,
                new Pair<>(drinkName, //drinkName.getTransitionName())
                        getString(R.string.shared_transition_review_drink) + review_Id)
        ).toBundle();
        startActivityForResult(intent, EDIT_REVIEW_REQUEST, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == EDIT_REVIEW_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri reviewUri = data.getData();
            ShowReviewFragment fragment = getFragment();
            if (fragment != null && reviewUri != null) {
                fragment.updateFragment(reviewUri);
            } else {
                Log.v(LOG_TAG, "onActivityResult - data or fragment missing..., hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy. Some common places where it might make
     * sense to call this method are:
     *
     * (1) Inside a Fragment's onCreateView() method (if the shared element
     *     lives inside a Fragment hosted by the called Activity).
     *
     * (2) Inside a Picasso Callback object (if you need to wait for Picasso to
     *     asynchronously load/scale a bitmap before the transition can begin).
     *
     * (3) Inside a LoaderCallback's onLoadFinished() method (if the shared
     *     element depends on data queried by a Loader).
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void scheduleStartPostponedTransition(final View view) {
        // http://www.androiddesignpatterns.com/2015/03/activity-postponed-shared-element-transitions-part3b.html
        Log.v(LOG_TAG, "scheduleStartPostponedTransition, hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
        if (view == null) {    //simple transition
            supportStartPostponedEnterTransition(); //does not work as expected

        } else {    // shared element transition
                view.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public boolean onPreDraw() {
                                view.getViewTreeObserver().removeOnPreDrawListener(this);
                                supportStartPostponedEnterTransition();
                                return true;
                            }
                        });
        }
    }
}
