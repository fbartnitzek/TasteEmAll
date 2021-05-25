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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.addentry.AddProducerActivity;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.fbartnitzek.tasteemall.tasks.DeleteEntryTask;

import org.jetbrains.annotations.NotNull;

public class ShowProducerActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOW_PRODUCER_TAG";
    private static final String LOG_TAG = ShowProducerActivity.class.getName();
    public static final String EXTRA_PRODUCER_URI = "EXTRA_PRODUCER_URI";
    private static final String STATE_CONTENT_URI = "SPA_STATE_CONTENT_URI"; 
    private static final int EDIT_PRODUCER_REQUEST = 444;
    private Uri mContentUri;

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_producer);

        if (findViewById(R.id.container_show_producer_fragment) != null) {
            supportPostponeEnterTransition();   // wait until Fragment-Views are done

            if (getIntent().getData() != null) {
                mContentUri = getIntent().getData();
            } else if (savedInstanceState.containsKey(STATE_CONTENT_URI)) {
                mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
            } else {
                Log.wtf(LOG_TAG, "contentUri missing!");
            }

            addShowProducerFragment();

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
    }

    private void addShowProducerFragment() {
        ShowProducerFragment fragment = new ShowProducerFragment();

        if (mContentUri != null) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_PRODUCER_URI, mContentUri);
            fragment.setArguments(args);
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_show_producer_fragment, fragment, FRAGMENT_TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    private ShowProducerFragment getFragment() {
        return (ShowProducerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
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
                    Uri deleteUri = Utils.calcSingleProducerUri(mContentUri);//TODO: joined error
                    // TODO: check for foreign keys ... generic seems unlikely...?
                    // int id = DatabaseContract.getIdFromUri(deleteUri);
                    // Uri checkUri = DatabaseContract.ReviewEntry.buildUriWithDrinkId(id);
                    new DeleteEntryTask(
                            ShowProducerActivity.this,
                            DatabaseContract.ProducerEntry.TABLE_NAME + "." + Producer.NAME)
                            .execute(deleteUri);
                }
        );
        builder.setNegativeButton(
                R.string.keep_button,
                (dialog, which) -> Toast.makeText(ShowProducerActivity.this, "keeping entry", Toast.LENGTH_SHORT).show()
        );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startEditActivity() {
        Log.v(LOG_TAG, "edit producer via AddProducerActivity with ContentUri: " + mContentUri);
        Intent intent = new Intent(this, AddProducerActivity.class);
        intent.setData(mContentUri);
        View view = findViewById(R.id.producer_name);
        int producer_Id = DatabaseContract.getIdFromUri(mContentUri);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                this,
                new Pair<>(view, //view.getTransitionName())
                        getString(R.string.shared_transition_producer_producer) + producer_Id)
        ).toBundle();
        startActivityForResult(intent, EDIT_PRODUCER_REQUEST, bundle);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == EDIT_PRODUCER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri producerUri = data.getData();

            ShowProducerFragment fragment = getFragment();
            if (fragment != null && producerUri != null) {
                fragment.updateFragment(producerUri);
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
