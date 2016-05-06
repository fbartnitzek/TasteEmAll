package com.example.fbartnitzek.tasteemall;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link AddProducerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProducerFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_PRODUCER_LOADER_ID = 12345;
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
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AddProducerFragment.
     */

    public static AddProducerFragment newInstance() {
        AddProducerFragment fragment = new AddProducerFragment();
        fragment.setProducerName("");
        return fragment;
    }

    public static AddProducerFragment newInstance(String producerName) {
        AddProducerFragment fragment = new AddProducerFragment();
        fragment.setProducerName(producerName);
        return fragment;
    }

    public static AddProducerFragment newInstance(Uri contentUri) {
        AddProducerFragment fragment = new AddProducerFragment();
        fragment.setContentUri(contentUri);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: restore savedInstanceState with json/parcelable/cursor
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
        mEditProducerLocation = (EditText) mRootView.findViewById(R.id.producer_location);
        mEditProducerWebsite = (EditText) mRootView.findViewById(R.id.producer_website);
        mEditProducerDescription = (EditText) mRootView.findViewById(R.id.producer_description);

        createToolbar();

        return mRootView;
    }

    public void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableProducer = getString(Utils.getProducerName(drinkType));
            if (mContentUri != null) {
                activity.getSupportActionBar().setTitle(
                        getString(R.string.title_edit_producer_activity_preview,
                                readableProducer));
            } else {
                activity.getSupportActionBar().setTitle(
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
        if (activity.getSupportActionBar()!= null) {
            activity.getSupportActionBar().setTitle(
                    getString(R.string.title_edit_producer_activity,
                            producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    //TODO: async task

    private Uri insertData(String producerName) {

        return getActivity().getContentResolver().insert(
                DatabaseContract.ProducerEntry.CONTENT_URI,
                DatabaseHelper.buildProducerValues(
                        Utils.calcProducerId(producerName),
                        producerName,
                        mEditProducerDescription.getText().toString(),
                        mEditProducerWebsite.getText().toString(),
                        mEditProducerLocation.getText().toString())
        );
    }

    private Uri updateData(String producerName) {
        String[] selectionArgs = new String[]{mProducerId};
        String where = DatabaseContract.ProducerEntry.TABLE_NAME + "." + Producer.PRODUCER_ID + " = ?";
        int rows = getActivity().getContentResolver().update(
                DatabaseContract.ProducerEntry.CONTENT_URI,
                DatabaseHelper.buildProducerValues(
                        mProducerId,
                        producerName,
                        mEditProducerDescription.getText().toString(),
                        mEditProducerWebsite.getText().toString(),
                        mEditProducerLocation.getText().toString()),
                where,
                selectionArgs);

        if (rows < 1) {
            return null;
        } else {
            return mContentUri;
        }
    }

    void saveData() {

        String producerName = mEditProducerName.getText().toString();

        Uri producerUri;
        if (mContentUri != null) { //update
            producerUri = updateData(producerName);
        } else { // insert
            producerUri = insertData(producerName);
        }

        if (producerUri != null) {
            Intent output = new Intent();
            output.setData(producerUri);
            getActivity().setResult(AddProducerActivity.RESULT_OK, output);
            getActivity().finish();
        } else {
            if (mContentUri != null) {
                Snackbar.make(mRootView, "Updating producer " + producerName + " didn't work...",
                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            } else {
                Snackbar.make(mRootView, "Creating new producer " + producerName + " didn't work...",
                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
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
        if (mContentUri!= null) {
            return new CursorLoader(
                    getActivity(),
                    mContentUri,
                    ProducerFragmentHelper.DETAIL_COLUMNS,
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
            String name = data.getString(ProducerFragmentHelper.COL_PRODUCER_NAME);
            mEditProducerName.setText(name);
            String location = data.getString(ProducerFragmentHelper.COL_PRODUCER_LOCATION);
            mEditProducerLocation.setText(location);
            String website = data.getString(ProducerFragmentHelper.COL_PRODUCER_WEBSITE);
            mEditProducerWebsite.setText(website);
            String description = data.getString(ProducerFragmentHelper.COL_PRODUCER_DESCRIPTION);
            mEditProducerDescription.setText(description);
            mProducerId = data.getString(ProducerFragmentHelper.COL_PRODUCER_ID);

            updateToolbar(name);

            Log.v(LOG_TAG, "onLoadFinished, name=" + name + ", location=" + location + ", " + "website= [" + website+ "], description= [" + description+ "]");
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");

    }


}
