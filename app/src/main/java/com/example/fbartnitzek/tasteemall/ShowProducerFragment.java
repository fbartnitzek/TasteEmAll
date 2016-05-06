package com.example.fbartnitzek.tasteemall;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class ShowProducerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ShowProducerFragment.class.getName();

    private static final int SHOW_PRODUCER_LOADER_ID = 42;

    private TextView mProducerNameView;
    private TextView mProducerLocationView;
    private TextView mProducerDescriptionView;
    private TextView mProducerWebsiteView;
    private Uri mUri;
    private View mRootView;
    private int mDrinkTypeIndex;

    public ShowProducerFragment() {
        Log.v(LOG_TAG, "ShowProducerFragment, " + "");
    }

    public void updateFragment(Uri contentUri) {
        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "contentUri = [" + contentUri + "]");
        mUri = contentUri;
        getLoaderManager().restartLoader(SHOW_PRODUCER_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_show_producer, container, false);

        Bundle args = getArguments();
        if (args == null){
            Log.v(LOG_TAG, "onCreateView, " + "without args - something went wrong...");
        } else {
            if (args.containsKey(ShowProducerActivity.EXTRA_PRODUCER_URI)) {
                mUri = args.getParcelable(ShowProducerActivity.EXTRA_PRODUCER_URI);
                Log.v(LOG_TAG, "onCreateView, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            }
        }

        mProducerNameView = (TextView) mRootView.findViewById(R.id.producer_name);
        mProducerLocationView = (TextView) mRootView.findViewById(R.id.producer_location);
        mProducerDescriptionView = (TextView) mRootView.findViewById(R.id.producer_description);
        mProducerWebsiteView = (TextView) mRootView.findViewById(R.id.producer_website);

        createToolbar();

        return mRootView;
    }

    private void createToolbar() {
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            Log.v(LOG_TAG, "createToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
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
                    ProducerFragmentHelper.DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }

        return null;
    }

    private void updateToolbar() {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {

            // later: when called from drink you may use the drinkType - now it's ... wrong
//            String readableProducerType = getString(Utils.getProducerName(mDrinkTypeIndex));
            String producerName= mProducerNameView.getText().toString();
            actionBar.setTitle(
                    getString(R.string.title_show_producer, producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...
            String name = data.getString(ProducerFragmentHelper.COL_PRODUCER_NAME);
            mProducerNameView.setText(name);
            String location = data.getString(ProducerFragmentHelper.COL_PRODUCER_LOCATION);
            mProducerLocationView.setText(location);
            String website = data.getString(ProducerFragmentHelper.COL_PRODUCER_WEBSITE);
            mProducerWebsiteView.setText(website);
            String description = data.getString(ProducerFragmentHelper.COL_PRODUCER_DESCRIPTION);
            mProducerDescriptionView.setText(description);

            updateToolbar();

            Log.v(LOG_TAG, "onLoadFinished, name=" + name + ", location=" + location + ", " + "website= [" + website+ "], description= [" + description+ "]");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }
}
