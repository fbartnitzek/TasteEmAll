package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract.DrinkEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String LOG_TAG = MainFragment.class.getName();
    private static final int PRODUCER_LOADER_ID = 100;
    private static final int DRINK_LOADER_ID = 200;

    private static final int ADD_DRINK_REQUEST = 555;
    private static final String STATE_SEARCH_PATTERN = "STATE_SEARCH_PATTERN";


    private ProducerAdapter mProducerAdapter;
    private DrinkAdapter mDrinkAdapter;

    private static final String[] PRODUCER_QUERY_COLUMNS = {
            ProducerEntry.TABLE_NAME + "." +  ProducerEntry._ID,
            Producer.NAME,
            Producer.DESCRIPTION,
            Producer.LOCATION};

    static final int COL_QUERY_PRODUCER__ID = 0;
    static final int COL_QUERY_PRODUCER_NAME = 1;
    static final int COL_QUERY_PRODUCER_DESCRIPTION = 2;
    static final int COL_QUERY_PRODUCER_LOCATION = 3;

    private static final String[] DRINK_WITH_PRODUCER_QUERY_COLUMNS = {
            DrinkEntry.TABLE_NAME + "." +  DrinkEntry._ID,
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

    private String mDrinkType;
    private View mRootView;
    private String mSearchPattern;
    private Spinner mSpinnerType;

    private CustomSpinnerAdapter mSpinnerAdapter;


    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mSearchPattern = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SEARCH_PATTERN)) {
            mSearchPattern = savedInstanceState.getString(STATE_SEARCH_PATTERN);
        }
        updateDrinkTypeFromPrefs();

        // both exist... try it :-p
        // might be to early... - lets see
        getLoaderManager().initLoader(PRODUCER_LOADER_ID, null, this);
        getLoaderManager().initLoader(DRINK_LOADER_ID, null, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "onSaveInstanceState, mSearchPattern=" + mSearchPattern + ", hashCode=" + this.hashCode() + ", " + "outState = [" + outState + "]");
        outState.putString(STATE_SEARCH_PATTERN, mSearchPattern);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        createToolbar();
        createSpinner();

        mProducerAdapter = new ProducerAdapter(new ProducerAdapter.ProducerAdapterClickHandler() {
            @Override
            public void onClick(String producerName, Uri contentUri, ProducerAdapter.ViewHolder viewHolder) {
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "], contentUri = [" + contentUri + "], viewHolder = [" + viewHolder + "]");
                Intent intent = new Intent(getActivity(), ShowProducerActivity.class)
                        .setData(contentUri);
                startActivity(intent);
            }
        });

        mDrinkAdapter = new DrinkAdapter(new DrinkAdapter.DrinkAdapterClickHandler() {
            @Override
            public void onClick(String drinkName, Uri contentUri, DrinkAdapter.ViewHolder viewHolder) {
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "drinkName = [" + drinkName + "], contentUri = [" + contentUri + "], viewHolder = [" + viewHolder + "]");
                Intent intent = new Intent(getActivity(), ShowDrinkActivity.class)
                        .setData(contentUri);
                startActivity(intent);
            }
        });

        RecyclerView mProducerRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_producer);
        mProducerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProducerRecyclerView.setAdapter(mProducerAdapter);

        RecyclerView mDrinkRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_drink);
        mDrinkRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDrinkRecyclerView.setAdapter(mDrinkAdapter);

        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "onCreateOptionsMenu with pattern:" + mSearchPattern + ", hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "], inflater = [" + inflater + "]");
        inflater.inflate(R.menu.menu_main_fragment, menu);
        final MenuItem item = menu.findItem(R.id.search_all);

        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        if (mSearchView == null) {
            Log.e(LOG_TAG, "onCreateOptionsMenu - searchView not found!!, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "], inflater = [" + inflater + "]");
            return;
        }

        // TODO: restore state not shown
        mSearchView.setQuery(mSearchPattern, false);    // does not change text
        mSearchView.clearFocus();

        // 1. workaround test - not working
//        mSearchView.post(new Runnable() {
//            @Override
//            public void run() {
//                mSearchView.setQuery(mSearchPattern, false);
//            }
//        });

        // 2. workaround - src: http://stackoverflow.com/questions/22498344/is-there-a-better-way-to-restore-searchview-state
        // text is shown, but no results (not possible in onCreateOptionsMenu
//        if (!TextUtils.isEmpty(mSearchPattern)) {
//            item.expandActionView();
//            mSearchView.setQuery(mSearchPattern, true);
//            mSearchView.clearFocus();
//        }

        mSearchView.setOnQueryTextListener(this);


        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            // MainFragment is home!
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                Log.e(LOG_TAG, "createToolbar - no supportActionBar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setHomeButtonEnabled(false);
            supportActionBar.setDisplayShowTitleEnabled(false);

            //        //TODO: when might i not need that...?
//        if (true) {
//            getSupportActionBar().setElevation(0f);
//        }


        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void createSpinner() {
        Log.v(LOG_TAG, "createSpinner, hashCode=" + this.hashCode() + ", " + "");

        mSpinnerType = (Spinner) mRootView.findViewById(R.id.spinner_type);
        String[] typesArray = getResources().getStringArray(R.array.pref_type_filter_values);
        ArrayList<String> typesList = new ArrayList<>(Arrays.asList(typesArray));

        mSpinnerAdapter = new CustomSpinnerAdapter(getActivity().getApplicationContext(), typesList);
        mSpinnerType.setAdapter(mSpinnerAdapter);

        updateSpinnerType();

        mSpinnerType.setOnItemSelectedListener(this);
    }

    private void updateDrinkTypeFromPrefs() {
        mDrinkType = Utils.getDrinkTypeFromSharedPrefs(getActivity(), true);
        Log.v(LOG_TAG, "updateDrinkTypeFromPrefs: " + mDrinkType + ", hashCode=" + this.hashCode() + ", " + "");
    }

    private void updateSpinnerType() {
        Log.v(LOG_TAG, "updateSpinnerType - drinkType: " + mDrinkType + ", hashCode=" + this.hashCode() + ", " + "");

        if (mSpinnerAdapter != null) {
            int spinnerPosition = mSpinnerAdapter.getPosition(mDrinkType);
            if (spinnerPosition > -1) {
                Log.v(LOG_TAG, "updateSpinnerType - spinner position found, hashCode=" + this.hashCode() + ", " + "");
                mSpinnerType.setSelection(spinnerPosition);
                mSpinnerType.clearFocus();
            } else {
                Log.v(LOG_TAG, "updateSpinnerType - no spinner position, hashCode=" + this.hashCode() + ", " + "");
            }
        }
    }

    private void restartLoaders() {
        Log.v(LOG_TAG, "restartLoaders, hashCode=" + this.hashCode() + ", " + "");
        getLoaderManager().restartLoader(PRODUCER_LOADER_ID, null, this);
        getLoaderManager().restartLoader(DRINK_LOADER_ID, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume, hashCode=" + this.hashCode() + ", " + "");
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, mSearchPattern=" + mSearchPattern + ", mDrinkType=" + mDrinkType + ", id = [" + id + "], args = [" + args + "]");
        // TODO: get latest entries ... - insertDate?

        switch (id) {
            case PRODUCER_LOADER_ID:
                return new CursorLoader(getActivity(),
                        ProducerEntry.buildUriWithPattern(mSearchPattern == null ? "" : mSearchPattern),
                        PRODUCER_QUERY_COLUMNS,
                        null, null,
                        ProducerEntry.TABLE_NAME + "." + Producer.NAME + " ASC");
            case DRINK_LOADER_ID:
                //TODO: should include producer-name... => maybe sort by drink-id
                String sortOrder = DrinkEntry.TABLE_NAME + "." + Drink.NAME + " ASC";
                return new CursorLoader(getActivity(),
                        DrinkEntry.buildUriWithNameAndType(
                                mSearchPattern == null ? "" : mSearchPattern,
                                mDrinkType == null ? "All" : mDrinkType),
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v(LOG_TAG, "onQueryTextSubmit, hashCode=" + this.hashCode() + ", " + "query = [" + query + "]");
        mSearchPattern = query;
        restartLoaders();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v(LOG_TAG, "onQueryTextChange, hashCode=" + this.hashCode() + ", " + "newText = [" + newText + "]");
        mSearchPattern = newText;
        restartLoaders();
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mSpinnerAdapter == parent.getAdapter()) {
            Log.v(LOG_TAG, "onItemSelected in spinnerType, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
            mDrinkType = parent.getItemAtPosition(position).toString();
            Utils.setSharedPrefsDrinkType(MainFragment.this.getActivity(), mDrinkType);
            restartLoaders();
        } else {
//            Log.v(LOG_TAG, "onItemSelected anywhere else..., hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add){
            // TODO: twoPane-mode and maybe some other stuff
            Intent intent = new Intent(getActivity(), AddDrinkActivity.class);
            startActivityForResult(intent, ADD_DRINK_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == ADD_DRINK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            Uri drinkUri = data.getData();
            Intent intent = new Intent(getActivity(), ShowDrinkActivity.class)
                    .setData(drinkUri);
            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
