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

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.*;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainFragment.class.getName();
    private static final int PRODUCER_LOADER_ID = 100;
    private static final int DRINK_LOADER_ID = 200;

    private RecyclerView mProducerRecyclerView;
    private RecyclerView mDrinkRecyclerView;
    private int mProducerPosition = ListView.INVALID_POSITION;
    private ProducerAdapter mProducerAdapter;
    private DrinkAdapter mDrinkAdapter;

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

    private static final String[] DRINK_WITH_PRODUCER_QUERY_COLUMNS = {
            DrinkEntry.TABLE_NAME + "." +  DrinkEntry._ID,  // without the CursurAdapter doesn't work
            Drink.NAME,
            Drink.PRODUCER_ID,
            Drink.TYPE,
            Drink.SPECIFICS,
            Drink.STYLE,
            Producer.NAME,
            Producer.LOCATION};

    static final int COL_QUERY_DRINK__ID = 0;
    static final int COL_QUERY_DRINK_NAME = 1;
    static final int COL_QUERY_DRINK_PRODUCER_ID = 2;
    static final int COL_QUERY_DRINK_TYPE= 3;
    static final int COL_QUERY_DRINK_SPECIFICS= 4;
    static final int COL_QUERY_DRINK_STYLE= 5;
    static final int COL_QUERY_DRINK_PRODUCER_NAME= 6;
    static final int COL_QUERY_DRINK_PRODUCER_LOCATION= 7;



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
                restartLoaders();    // producers and drinks
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchString = newText;
                restartLoaders();    // just producers
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

        mDrinkAdapter = new DrinkAdapter(new DrinkAdapter.DrinkAdapterClickHandler() {
            @Override
            public void onClick(String drinkName, Uri contentUri, DrinkAdapter.ViewHolder viewHolder) {
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "drinkName = [" + drinkName + "], contentUri = [" + contentUri + "], viewHolder = [" + viewHolder + "]");
                Snackbar.make(rootView, drinkName + " clicked...", LENGTH_SHORT).show();


                Intent intent = new Intent(getActivity(), ShowDrinkActivity.class)
                        .setData(contentUri);
                startActivity(intent);
            }
        });



        mProducerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_producer);
        mProducerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProducerRecyclerView.setAdapter(mProducerAdapter);

        mDrinkRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_drink);
        mDrinkRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDrinkRecyclerView.setAdapter(mDrinkAdapter);

        return rootView;
    }

    private void restartLoaders() {
        getLoaderManager().restartLoader(PRODUCER_LOADER_ID, null, this);
        getLoaderManager().restartLoader(DRINK_LOADER_ID, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PRODUCER_LOADER_ID, null, this);
        getLoaderManager().initLoader(DRINK_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoaders();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, mSearchString=" + mSearchString + ", id = [" + id + "], args = [" + args + "]");
        // TODO: get latest entries ... - insertDate?

        switch (id) {
            case PRODUCER_LOADER_ID:
                return new CursorLoader(getActivity(),
                        ProducerEntry.buildUriWithPattern(mSearchString == null ? "" : mSearchString),
                        PRODUCER_QUERY_COLUMNS,
                        null, null,
                        ProducerEntry.TABLE_NAME + "." + Producer.NAME + " ASC");
            case DRINK_LOADER_ID:
                //TODO: should include producer-name... => maybe sort by drink-id
                String sortOrder = DrinkEntry.TABLE_NAME + "." + Drink.NAME + " ASC";
                return new CursorLoader(getActivity(),
                        DrinkEntry.buildUriWithName(mSearchString == null ? "" : mSearchString),
                        DRINK_WITH_PRODUCER_QUERY_COLUMNS,
                        null, null,
                        sortOrder);
            default:
                throw new RuntimeException("wrong loader_id in MainFragment...");
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, " + "loader = [" + loader + "], data = [" + data + "]");
        switch (loader.getId()) {
            case PRODUCER_LOADER_ID:
                mProducerAdapter.swapCursor(data);
                break;
            case DRINK_LOADER_ID:
                mDrinkAdapter.swapCursor(data);
                break;
        }
//        if (mProducerPosition != ListView.INVALID_POSITION) {
//            mBreweryListView.smoothScrollToPosition(mProducerPosition);
//        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, " + "loader = [" + loader + "]");
        switch (loader.getId()) {
            case PRODUCER_LOADER_ID:
                mProducerAdapter.swapCursor(null);
                break;
            case DRINK_LOADER_ID:
                mDrinkAdapter.swapCursor(null);
                break;
        }
    }
}
