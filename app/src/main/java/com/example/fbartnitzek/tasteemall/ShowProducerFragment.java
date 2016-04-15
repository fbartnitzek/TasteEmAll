package com.example.fbartnitzek.tasteemall;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.*;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;


/**
 * A placeholder fragment containing a simple view.
 */
public class ShowProducerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ShowProducerFragment.class.getName();

    private static final String[] DETAIL_COLUMNS = {
            ProducerEntry.TABLE_NAME + "." + ProducerEntry._ID,
            Producer.PRODUCER_ID,
            Producer.NAME,
            Producer.DESCRIPTION,
            Producer.WEBSITE,
            Producer.LOCATION
    };

    static final int COL_PRODUCER__ID = 0;
    static final int COL_PRODUCER_ID = 1;
    static final int COL_PRODUCER_NAME = 2;
    static final int COL_PRODUCER_DESCRIPTION = 3;
    static final int COL_PRODUCER_WEBSITE = 4;
    static final int COL_PRODUCER_LOCATION = 5;
    private static final int SHOW_PRODUCER_LOADER_ID = 42;

    private TextView mProducerNameView;
    private TextView mProducerLocationView;
    private TextView mProducerDescriptionView;
    private TextView mProducerWebsiteView;
    private Uri mUri;

    public ShowProducerFragment() {
        Log.v(LOG_TAG, "ShowProducerFragment, " + "");
        setHasOptionsMenu(true);    // maybe needed later...
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        View rootView = inflater.inflate(R.layout.fragment_show_producer, container, false);

        Bundle args = getArguments();
        if (args == null){
            Log.v(LOG_TAG, "onCreateView, " + "without args - something went wrong...");
        } else {
            if (args.containsKey(ShowProducerActivity.EXTRA_PRODUCER_URI)) {
                mUri = args.getParcelable(ShowProducerActivity.EXTRA_PRODUCER_URI);
                Log.v(LOG_TAG, "onCreateView, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            }
        }

        mProducerNameView = (TextView) rootView.findViewById(R.id.producer_name);
        mProducerLocationView = (TextView) rootView.findViewById(R.id.producer_location);
        mProducerDescriptionView = (TextView) rootView.findViewById(R.id.producer_description);
        mProducerWebsiteView = (TextView) rootView.findViewById(R.id.producer_website);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        getLoaderManager().initLoader(SHOW_PRODUCER_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "onCreateLoader, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
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
            String name = data.getString(COL_PRODUCER_NAME);
            mProducerNameView.setText(name);
            String location = data.getString(COL_PRODUCER_LOCATION);
            mProducerLocationView.setText(location);
            String website = data.getString(COL_PRODUCER_WEBSITE);
            mProducerWebsiteView.setText(website);
            String desciption = data.getString(COL_PRODUCER_DESCRIPTION);
            mProducerDescriptionView.setText(desciption);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
            Log.v(LOG_TAG, "onLoadFinished, name=" + name + ", location=" + location + ", " + "website= [" + website+ "], description= [" + desciption+ "]");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }
}
