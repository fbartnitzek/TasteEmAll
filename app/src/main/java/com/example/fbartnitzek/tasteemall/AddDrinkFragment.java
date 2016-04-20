package com.example.fbartnitzek.tasteemall;


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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDrinkFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private String mFilter = "";

    private static NonFilterableAutoCompleteTextView mEditCompletionProducerName;
    private static EditText mEditDrinkName;
    private static EditText mEditDrinkType;
    private static EditText mEditDrinkStyle;
    private static EditText mEditDrinkIngredients;
    private static EditText mEditDrinkSpecifics;
    private static View mRootView;

    private ProducerCompletionAdapter mProducerAdapter;
    private static final int PRODUCER_COMPLETION_LOADER_ID = 124;

    private static final String LOG_TAG = AddDrinkFragment.class.getName();

    public static final String[] PRODUCER_QUERY_COLUMNS = {
            ProducerEntry.TABLE_NAME + "." +  ProducerEntry._ID,  // without the CursurAdapter doesn't work
            Producer.NAME,
            Producer.LOCATION};

    static final int COL_QUERY_PRODUCER__ID = 0;
    static final int COL_QUERY_PRODUCER_NAME = 1;
    static final int COL_QUERY_PRODUCER_LOCATION = 2;

    private String mProducerName;
    private int mProducerId;

    public AddDrinkFragment() {
        // Required empty public constructor
    }

    public static AddDrinkFragment newInstance() {
        return new AddDrinkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_drink, container, false);

//        mEditCompletionProducerName = new NonFilterableAutoCompleteTextView(getActivity());

        mEditCompletionProducerName = (NonFilterableAutoCompleteTextView) mRootView.findViewById(R.id.producer_name);
//        mProducerAdapter = new ProducerCompletionAdapter(getActivity(), null, false);
//        mEditCompletionProducerName.setAdapter(mProducerAdapter);

        mEditCompletionProducerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v(LOG_TAG, "afterTextChanged, hashCode=" + this.hashCode() + ", " + "s = [" + s + "]");
                mFilter = s.toString();
                getLoaderManager().restartLoader(PRODUCER_COMPLETION_LOADER_ID, null, AddDrinkFragment.this);
            }
        });
        getLoaderManager().initLoader(PRODUCER_COMPLETION_LOADER_ID, null, this);

        mRootView.findViewById(R.id.add_producer_button).setOnClickListener(this);
        mRootView.findViewById(R.id.fab_save).setOnClickListener(this);


        mEditDrinkName = (EditText) mRootView.findViewById(R.id.drink_name);
        mEditDrinkType = (EditText) mRootView.findViewById(R.id.drink_type);
        mEditDrinkStyle = (EditText) mRootView.findViewById(R.id.drink_style);
        mEditDrinkIngredients = (EditText) mRootView.findViewById(R.id.drink_ingredients);
        mEditDrinkSpecifics = (EditText) mRootView.findViewById(R.id.drink_specifics);

        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add:
                createProducer();
                break;
            case R.id.fab_save:
                insertData();
                break;
            default:
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "useless view = [" + view + "]");
        }
    }

    //TODO: async task
    private void insertData() {
        Log.v(LOG_TAG, "insertData, hashCode=" + this.hashCode() + ", " + "");
        // 1) find producer
//        getActivity().getContentResolver().query();

        // 2) found producer? use him or show a message...

        String producerName = mEditCompletionProducerName.getText().toString();
        // TODO: store id on query...
        String drinkName = mEditDrinkName.getText().toString();
        getActivity().getContentResolver().insert(
                DatabaseContract.DrinkEntry.CONTENT_URI,
                DatabaseHelper.buildDrinkValues(
                        Utils.calcDrinkId(drinkName, producerName),
                        drinkName,
                        mEditDrinkSpecifics.getText().toString(),
                        mEditDrinkStyle.getText().toString(),
                        mEditDrinkType.getText().toString(),
                        mEditDrinkIngredients.getText().toString(),
                        Utils.calcProducerId(producerName)
                )
        );

        Snackbar.make(mRootView, "Created new entry " + drinkName,
                Snackbar.LENGTH_SHORT).setAction("Action", null).show();

    }

    private void createProducer() {
        Log.v(LOG_TAG, "createProducer, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(getActivity(), AddProducerActivity.class);
        // use pre filled name
        intent.putExtra(AddProducerActivity.PRODUCER_NAME_EXTRA,
                mEditCompletionProducerName.getText().toString());
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case PRODUCER_COMPLETION_LOADER_ID:
                Uri searchUri = ProducerEntry.buildUriWithName(mFilter == null ? "" : mFilter);
                return new CursorLoader(getActivity(),
                        searchUri,
                        PRODUCER_QUERY_COLUMNS,
                        null, null,
                        ProducerEntry.TABLE_NAME + "." + Producer.NAME + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        switch (loader.getId()) {
            case PRODUCER_COMPLETION_LOADER_ID:

                if (data != null) {
                    Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "data.closed() = [" + data.isClosed() + "]" + ", " + "data.count() = [" + data.getCount() + "]" + ", " + "data.getColumnCount() = [" + data.getColumnCount() + "]");
                }

                //confusing and seems wrong...
                mProducerAdapter = new ProducerCompletionAdapter(getActivity(), data, false);
                mEditCompletionProducerName.setAdapter(mProducerAdapter);

//                mProducerAdapter.swapCursor(data);

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
        // seems useless...
//        mProducerAdapter.swapCursor(null);

    }
}
