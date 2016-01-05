package com.example.fbartnitzek.tasteemall;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.BreweryEntry;
import com.example.fbartnitzek.tasteemall.data.pojo.Brewery;
import com.example.fbartnitzek.tasteemall.data.pojo.Location;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainFragment.class.getName();
    private static final int BREWERY_LOADER_ID = 123;
    private BreweryAdapter mBreweryAdapter;

    private int mBreweryPosition = ListView.INVALID_POSITION;
    private ListView mBreweryListView;

    private static final String[] BREWERY_QUERY_COLUMNS = {
            BreweryEntry.TABLE_NAME + "." +  BreweryEntry._ID,  // without the CursurAdapter doesn't work
            Brewery.NAME,
            Brewery.INTRODUCED,
            Location.LOCALITY,
            Location.COUNTRY};

//    static final int COL_QUERY_BREWERY__ID = 0;
    static final int COL_QUERY_BREWERY_NAME = 1;
    static final int COL_QUERY_BREWERY_INTRODUCED = 2;
    static final int COL_QUERY_BREWERY_LOCALITY = 3;
    static final int COL_QUERY_BREWERY_COUNTRY = 4;
    private String mSearchString;


    public MainFragment() {
        // TODO: something with mSearchString;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final SearchView searchView = (SearchView) rootView.findViewById(R.id.search_all);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), "submitted: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getActivity(), "on change: " + newText, Toast.LENGTH_SHORT).show();
                return false;
            }

        });

        // start Task for each -> result either something or empty
        // if empty: create? -> new Fragment
        // for now just an extra button...
        final Button newBreweryButton = (Button) rootView.findViewById(R.id.button_new_brewery);
        newBreweryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: new fragment to create brewery
                Toast.makeText(getActivity(),
                        "create new brewery " + searchView.getQuery(), Toast.LENGTH_SHORT).show();
            }
        });

        mBreweryAdapter = new BreweryAdapter(getActivity(), null, 0);

        mBreweryListView = (ListView) rootView.findViewById(R.id.listview_brewery);
        mBreweryListView.setAdapter(mBreweryAdapter);

        mBreweryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    Toast.makeText(getActivity(), cursor.getString(COL_QUERY_BREWERY_NAME),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BREWERY_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, " + "id = [" + id + "], args = [" + args + "]");
        // TODO: get latest entries ... - insertDate?
        Uri searchUri = DatabaseContract.BreweryEntry.buildBreweryLocationWithName(
                mSearchString == null ? "" : mSearchString);
        String sortOrder = DatabaseContract.BreweryEntry.TABLE_NAME + "." + Brewery.NAME + " ASC";

        return new CursorLoader(getActivity(), searchUri, BREWERY_QUERY_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, " + "loader = [" + loader + "], data = [" + data + "]");
        mBreweryAdapter.swapCursor(data);

        if (mBreweryPosition != ListView.INVALID_POSITION) {
            mBreweryListView.smoothScrollToPosition(mBreweryPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, " + "loader = [" + loader + "]");
        mBreweryAdapter.swapCursor(null);
    }
}
