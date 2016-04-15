package com.example.fbartnitzek.tasteemall;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainFragment.class.getName();
    private static final int PRODUCER_LOADER_ID = 123;

    private RecyclerView mProducerRecyclerView;
    private int mProducerPosition = ListView.INVALID_POSITION;
    private ProducerAdapter mProducerAdapter;

    private static final String[] PRODUCER_QUERY_COLUMNS = {
            ProducerEntry.TABLE_NAME + "." +  ProducerEntry._ID,  // without the CursurAdapter doesn't work
            Producer.NAME,
            Producer.DESCRIPTION,
            Producer.LOCATION};

    static final int COL_QUERY_PRODUCER__ID = 0;
    static final int COL_QUERY_PRODUCER_NAME = 1;
    static final int COL_QUERY_PRODUCER_DESCRIPTION = 2;
    static final int COL_QUERY_PRODUCER_LOCATION = 3;
    private String mSearchString;

    public MainFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // useless for now
        final SearchView searchView = (SearchView) rootView.findViewById(R.id.search_all);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchString = query;
//                Toast.makeText(getActivity(), "submitted: " + query, Toast.LENGTH_SHORT).show();
                restartLoader();    // producers and drinks
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchString = newText;
//                Toast.makeText(getActivity(), "on change: " + newText, Toast.LENGTH_SHORT).show();
                restartLoader();    // just producers
                return false;
            }

        });


        mProducerAdapter = new ProducerAdapter(new ProducerAdapter.ProducerAdapterClickHandler() {
            @Override
            public void onClick(String producerName, Uri contentUri, ProducerAdapter.ViewHolder viewHolder) {
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "], contentUri = [" + contentUri + "], viewHolder = [" + viewHolder + "]");
                Snackbar.make(rootView, producerName + " clicked ...", LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), ShowProducerActivity.class)
                        .setData(contentUri);
                startActivity(intent);
            }
        });

        mProducerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_producer);
        mProducerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProducerRecyclerView.setAdapter(mProducerAdapter);

        return rootView;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(PRODUCER_LOADER_ID, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PRODUCER_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, mSearchString=" + mSearchString + ", id = [" + id + "], args = [" + args + "]");
        // TODO: get latest entries ... - insertDate?

        Uri searchUri = ProducerEntry.buildUriWithName(mSearchString == null ? "" : mSearchString);
        String sortOrder = ProducerEntry.TABLE_NAME + "." + Producer.NAME + " ASC";

        return new CursorLoader(getActivity(),
                searchUri,
                PRODUCER_QUERY_COLUMNS,
                null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, " + "loader = [" + loader + "], data = [" + data + "]");
        mProducerAdapter.swapCursor(data);

//        if (mProducerPosition != ListView.INVALID_POSITION) {
//            mBreweryListView.smoothScrollToPosition(mProducerPosition);
//        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, " + "loader = [" + loader + "]");
        mProducerAdapter.swapCursor(null);
    }
}
