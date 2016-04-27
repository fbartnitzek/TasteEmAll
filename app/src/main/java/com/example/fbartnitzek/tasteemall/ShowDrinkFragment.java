package com.example.fbartnitzek.tasteemall;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowDrinkFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ShowDrinkFragment.class.getName();

    private static final String[] DETAIL_COLUMNS = {
            DatabaseContract.DrinkEntry.TABLE_NAME + "." + DatabaseContract.DrinkEntry._ID,  // without the CursurAdapter doesn't work
            Drink.NAME,
            Drink.PRODUCER_ID,
            Drink.TYPE,
            Drink.SPECIFICS,
            Drink.STYLE,
            Drink.INGREDIENTS,
            Producer.NAME,
            Producer.LOCATION};

    static final int COL_QUERY_DRINK__ID = 0;
    static final int COL_QUERY_DRINK_NAME = 1;
    static final int COL_QUERY_DRINK_PRODUCER_ID = 2;
    static final int COL_QUERY_DRINK_TYPE = 3;
    static final int COL_QUERY_DRINK_SPECIFICS = 4;
    static final int COL_QUERY_DRINK_STYLE = 5;
    static final int COL_QUERY_DRINK_INGREDIENTS = 6;
    static final int COL_QUERY_DRINK_PRODUCER_NAME = 7;
    static final int COL_QUERY_DRINK_PRODUCER_LOCATION = 8;

    public static final int SHOW_DRINK_LOADER_ID = 21;

    private TextView mProducerNameView;
    private TextView mProducerLocationView;

    private TextView mDrinkNameView;
    private TextView mDrinkTypeView;
    private TextView mDrinkStyleView;
    private TextView mDrinkSpecificsView;
    private TextView mDrinkIngredientsView;
    private Uri mUri;

    public ShowDrinkFragment() {
        Log.v(LOG_TAG, "ShowDrinkFragment, hashCode=" + this.hashCode() + ", " + "");
        setHasOptionsMenu(true);    //later...

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_drink, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.v(LOG_TAG, "onCreateView without args - something went wrong..., hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        } else {
            if (args.containsKey(ShowDrinkActivity.EXTRA_DRINK_URI)) {
                mUri = args.getParcelable(ShowDrinkActivity.EXTRA_DRINK_URI);
                Log.v(LOG_TAG, "onCreateView, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            }
        }

        mProducerNameView = (TextView) rootView.findViewById(R.id.producer_name);
        mProducerLocationView = (TextView) rootView.findViewById(R.id.producer_location);

        mDrinkNameView = (TextView) rootView.findViewById(R.id.drink_name);
        mDrinkTypeView = (TextView) rootView.findViewById(R.id.drink_type);
        mDrinkStyleView = (TextView) rootView.findViewById(R.id.drink_style);
        mDrinkSpecificsView = (TextView) rootView.findViewById(R.id.drink_specifics);
        mDrinkIngredientsView = (TextView) rootView.findViewById(R.id.drink_ingredients);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        getLoaderManager().initLoader(SHOW_DRINK_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
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
        if (data != null && data.moveToFirst()) {
            mProducerNameView.setText(data.getString(COL_QUERY_DRINK_PRODUCER_NAME));
            mProducerLocationView.setText(data.getString(COL_QUERY_DRINK_PRODUCER_LOCATION));

            mDrinkNameView.setText(data.getString(COL_QUERY_DRINK_NAME));
            mDrinkTypeView.setText(data.getString(COL_QUERY_DRINK_TYPE));
            mDrinkStyleView.setText(data.getString(COL_QUERY_DRINK_STYLE));
            mDrinkSpecificsView.setText(data.getString(COL_QUERY_DRINK_SPECIFICS));
            mDrinkIngredientsView.setText(data.getString(COL_QUERY_DRINK_INGREDIENTS));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }
}
