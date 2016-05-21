package com.fbartnitzek.tasteemall.addentry;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.QueryColumns;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 */
public class AddProducerFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_PRODUCER_LOADER_ID = 12345;
    private static final String STATE_PRODUCER_NAME = "STATE_PRODUCER_NAME";
    private static final String STATE_PRODUCER_LOCATION = "STATE_PRODUCER_LOCATION";
    private static final String STATE_PRODUCER_WEBSITE = "STATE_PRODUCER_WEBSITE";
    private static final String STATE_PRODUCER_DESCRIPTION = "STATE_PRODUCER_DESCRIPTION";
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";
    private static final String STATE_PRODUCER_ID = "STATE_PRODUCER_ID";
    private static EditText mEditProducerName;
    private static EditText mEditProducerLocation;
    private static EditText mEditProducerWebsite;
    private static EditText mEditProducerDescription;
    private static View mRootView;
    private String mProducerName;

    private static final String LOG_TAG = AddProducerFragment.class.getName();
    private Uri mContentUri = null;
    private String mProducerId = null;

    public AddProducerFragment() {
        // Required empty public constructor
        mProducerName = "";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CONTENT_URI)) {
                mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
            }
            if (savedInstanceState.containsKey(STATE_PRODUCER_ID)) {
                mProducerId = savedInstanceState.getString(STATE_PRODUCER_ID);
            }
        }
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_producer, container, false);

        mEditProducerName = (EditText) mRootView.findViewById(R.id.producer_name);
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "producerName = " + mProducerName);
        mEditProducerName.setText(mProducerName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditProducerName.setTransitionName(getString(R.string.shared_transition_add_producer_name));
        }

        mEditProducerLocation = (EditText) mRootView.findViewById(R.id.producer_location);
        mEditProducerWebsite = (EditText) mRootView.findViewById(R.id.producer_website);
        mEditProducerDescription = (EditText) mRootView.findViewById(R.id.producer_description);

        if (savedInstanceState != null) {
            mEditProducerName.setText(savedInstanceState.getString(STATE_PRODUCER_NAME));
            mEditProducerLocation.setText(savedInstanceState.getString(STATE_PRODUCER_LOCATION));
            mEditProducerWebsite.setText(savedInstanceState.getString(STATE_PRODUCER_WEBSITE));
            mEditProducerDescription.setText(savedInstanceState.getString(STATE_PRODUCER_DESCRIPTION));
        }

        createToolbar();

        if (mContentUri == null) {
            resumeActivityEnterTransition();    // from add
        }

        return mRootView;
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                Log.v(LOG_TAG, "createToolbar - no supportActionBar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }

            supportActionBar.setDisplayHomeAsUpEnabled(true);  //false: not visible anymore
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableProducer = getString(Utils.getReadableProducerNameId(getActivity(), drinkType));
            if (mContentUri != null) {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_producer_activity_preview,
                                readableProducer));
            } else {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_drink_activity,
                                readableProducer));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void updateToolbar(String producerName) {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                    getString(R.string.title_edit_producer_activity,
                            producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_PRODUCER_NAME, mEditProducerName.getText().toString().trim().trim());
        outState.putString(STATE_PRODUCER_LOCATION, mEditProducerLocation.getText().toString().trim());
        outState.putString(STATE_PRODUCER_WEBSITE, mEditProducerWebsite.getText().toString().trim());
        outState.putString(STATE_PRODUCER_DESCRIPTION, mEditProducerDescription.getText().toString().trim());

        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }
        if (mProducerId != null) {
            outState.putString(STATE_PRODUCER_ID, mProducerId);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        if (mContentUri != null) {
            Log.v(LOG_TAG, "onActivityCreated with contentUri - edit, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            getLoaderManager().initLoader(EDIT_PRODUCER_LOADER_ID, null, this);
        } else {
            Log.v(LOG_TAG, "onActivityCreated without contentUri - add, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
    }

    private void insertData(String producerName, String location) {
        new InsertEntryTask(
                getActivity(), ProducerEntry.CONTENT_URI, mRootView, mProducerName)
                .execute(DatabaseHelper.buildProducerValues(
                        Utils.calcProducerId(producerName, location),
                        producerName,
                        mEditProducerDescription.getText().toString().trim(),
                        mEditProducerWebsite.getText().toString().trim(),
                        location));
    }

    private void updateData(String producerName, String location) {
        Uri singleProducerUri = Utils.calcSingleProducerUri(mContentUri);
        new UpdateEntryTask(getActivity(), singleProducerUri, mProducerName, mRootView)
                .execute(DatabaseHelper.buildProducerValues(
                        mProducerId,
                        producerName,
                        mEditProducerDescription.getText().toString().trim(),
                        mEditProducerWebsite.getText().toString().trim(),
                        location));
    }

    void saveData() {

        String producerName = mEditProducerName.getText().toString().trim();
        String location = mEditProducerLocation.getText().toString().trim();

        //validate
        if (producerName.length() == 0) {
            Snackbar.make(mRootView, R.string.msg_enter_producer_name, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (location.length() == 0) {
            Snackbar.make(mRootView, R.string.msg_enter_producer_location, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mContentUri != null) { //update

            updateData(producerName, location);

        } else { // insert

            insertData(producerName, location);
        }

    }

    public String getProducerName() {
        return mProducerName;
    }

    public void setProducerName(String producerName) {
        this.mProducerName = producerName;
    }

    public void setContentUri(Uri contentUri) {
        this.mContentUri = contentUri;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, mContentUri=" + mContentUri + ", hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mContentUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mContentUri,
                    QueryColumns.ProducerFragment.DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...
            int producer_Id = data.getInt(QueryColumns.ProducerFragment.COL_PRODUCER__ID);
            String name = data.getString(QueryColumns.ProducerFragment.COL_PRODUCER_NAME);
            mEditProducerName.setText(name);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mEditProducerName.setTransitionName(
                        getString(R.string.shared_transition_producer_producer) + producer_Id);
            }

            String location = data.getString(QueryColumns.ProducerFragment.COL_PRODUCER_LOCATION);
            mEditProducerLocation.setText(location);
            String website = data.getString(QueryColumns.ProducerFragment.COL_PRODUCER_WEBSITE);
            mEditProducerWebsite.setText(website);
            String description = data.getString(QueryColumns.ProducerFragment.COL_PRODUCER_DESCRIPTION);
            mEditProducerDescription.setText(description);
            mProducerId = data.getString(QueryColumns.ProducerFragment.COL_PRODUCER_ID);

            updateToolbar(name);

            resumeActivityEnterTransition();    // from edit

            Log.v(LOG_TAG, "onLoadFinished, name=" + name + ", location=" + location + ", " + "website= [" + website + "], description= [" + description + "]");
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");

    }

    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((AddProducerActivity) getActivity()).scheduleStartPostponedTransition(mEditProducerName);
        }
    }

}