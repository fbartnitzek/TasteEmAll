package com.example.fbartnitzek.tasteemall;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseContract.ProducerEntry;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDrinkFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int PRODUCER_ACTIVITY_REQUEST_CODE = 666;

    private static AutoCompleteTextView mEditCompletionProducerName;
    private static EditText mEditDrinkName;
    private static Spinner mSpinnerDrinkType;
    private static EditText mEditDrinkStyle;
    private static EditText mEditDrinkIngredients;
    private static EditText mEditDrinkSpecifics;
    private static View mRootView;

    //    private ProducerCompletionAdapter mProducerAdapter;
    SimpleCursorAdapter mAdapter;

    private static final int PRODUCER_COMPLETION_LOADER_ID = 124;

    private static final String LOG_TAG = AddDrinkFragment.class.getName();

    public static final String[] PRODUCER_QUERY_COLUMNS = {
            ProducerEntry.TABLE_NAME + "." +  ProducerEntry._ID,
            Producer.NAME,
            Producer.LOCATION,
            Producer.PRODUCER_ID};

    static final int COL_QUERY_PRODUCER__ID = 0;
    static final int COL_QUERY_PRODUCER_NAME = 1;
    static final int COL_QUERY_PRODUCER_LOCATION = 2;
    static final int COL_QUERY_PRODUCER_ID = 3;

    private String mProducerName;
    private int mProducer_Id;
    private String mProducerId;
    private String mFilter = "";

    public AddDrinkFragment() {
        // Required empty public constructor
    }

    public static AddDrinkFragment newInstance() {
        return new AddDrinkFragment();
    }

    public Cursor getCursor(CharSequence str) {

        //TODO: might produce lots of cursors...

        // working with SimpleCursorAdapter - modify to Custom CursorAdapter later...
        // Loader currently useless
        String select = Producer.NAME + " LIKE ? ";
        String[] selectArgs = { "%" + str + "%"};

        return getActivity().getContentResolver().query(
                ProducerEntry.CONTENT_URI, PRODUCER_QUERY_COLUMNS,
                select, selectArgs, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_drink, container, false);

        mEditCompletionProducerName = (AutoCompleteTextView) mRootView.findViewById(R.id.producer_name);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_producer_completion,
                null,
                new String[]{Producer.NAME, Producer.LOCATION},
                new int[]{R.id.list_item_producer_name, R.id.list_item_producer_location },
                0){

            @Override
            public void setViewText(TextView v, String text) {
                if (v.getId() == R.id.list_item_producer_location) {
                    v.setText(getActivity().getString(
                            R.string.location_completion_postfix, text));
                } else {
                    super.setViewText(v, text);
                }
            }
        };

        mEditCompletionProducerName.setAdapter(mAdapter);

        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return getCursor(constraint);
            }
        });

        mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                // TODO: really bad side effect...
                Log.v(LOG_TAG, "convertToString, hashCode=" + this.hashCode() + ", " + "cursor = [" + cursor + "]");
                mProducer_Id = cursor.getInt(COL_QUERY_PRODUCER__ID);
                mProducerId = cursor.getString(COL_QUERY_PRODUCER_ID);
                mProducerName = cursor.getString(COL_QUERY_PRODUCER_NAME);
                return mProducerName;
            }
        });


        mRootView.findViewById(R.id.add_producer_button).setOnClickListener(this);

        mEditDrinkName = (EditText) mRootView.findViewById(R.id.drink_name);
        mSpinnerDrinkType = (Spinner) mRootView.findViewById(R.id.drink_type);

        // fill type with drink_type from settings
        String drinkType = Utils.getDrinkTypeFromSharedPrefs(getActivity(), false);

        String[] drinkTypes = getActivity().getResources().getStringArray(R.array.pref_type_values);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                drinkTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDrinkType.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(drinkType);
        if (spinnerPosition > -1) {
            mSpinnerDrinkType.setSelection(spinnerPosition);
        }
        mSpinnerDrinkType.setOnItemSelectedListener(this);

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
            case R.id.add_producer_button:
                createProducer();
                break;
            default:
                Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "useless view = [" + view + "]");
        }
    }

    //TODO: async task
    void insertData() {
        Log.v(LOG_TAG, "insertData, hashCode=" + this.hashCode() + ", " + "");

        String drinkName = mEditDrinkName.getText().toString();

        //validate
        if (mProducerId == null || mProducerName == null) {
            Snackbar.make(mRootView, "choose an existing producer first...", Snackbar.LENGTH_SHORT).show();
            return;
        } else if ("".equals(drinkName)) {
            Snackbar.make(mRootView, "choose a name first...", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Uri drinkUri = getActivity().getContentResolver().insert(
                DatabaseContract.DrinkEntry.CONTENT_URI,
                DatabaseHelper.buildDrinkValues(
                        Utils.calcDrinkId(drinkName, mProducerName),
                        drinkName,
                        mEditDrinkSpecifics.getText().toString(),
                        mEditDrinkStyle.getText().toString(),
                        mSpinnerDrinkType.getItemAtPosition(
                                mSpinnerDrinkType.getSelectedItemPosition()).toString(),
                        mEditDrinkIngredients.getText().toString(),
                        mProducerId
                )
        );

        if (drinkUri != null) {
            Intent output = new Intent();
            output.setData(drinkUri);
            getActivity().setResult(Activity.RESULT_OK, output);
            getActivity().finish();
        } else {
            Snackbar.make(mRootView, "Creating new entry " + drinkName + " didn't work ...",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == PRODUCER_ACTIVITY_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK && data != null) {
            Uri producerUri = data.getData();
            updateProvider(producerUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createProducer() {
        Log.v(LOG_TAG, "createProducer, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(getActivity(), AddProducerActivity.class);
        // use pre filled name
        intent.putExtra(AddProducerActivity.PRODUCER_NAME_EXTRA,
                mEditCompletionProducerName.getText().toString());
        startActivityForResult(intent, PRODUCER_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String drinkType = mSpinnerDrinkType.getItemAtPosition(position).toString();
        Log.v(LOG_TAG, "onItemSelected - changed drinkType to: " + drinkType + ", hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        // just a bit wrong - resets all to generic...
        // TODO: AddDrinkFragment needs Toolbar for best implementation
        Utils.setSharedPrefsDrinkType(getActivity(), drinkType);
        int drinkTypeIndex = Utils.getDrinkTypeIndexFromSharedPrefs(getActivity(), false);
        String readableDrinkType = getString(Utils.getDrinkName(drinkTypeIndex));
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(
                    getString(R.string.title_add_drink_activity,
                            readableDrinkType));
        }
        ((TextView) mRootView.findViewById(R.id.drink_label)).setText(readableDrinkType);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.v(LOG_TAG, "onNothingSelected, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "]");
    }

    public void updateProvider(Uri producerUri) {

        Log.v(LOG_TAG, "updateProvider, hashCode=" + this.hashCode() + ", " + "producerUri = [" + producerUri + "]");
        Cursor cursor = getActivity().getContentResolver().query(producerUri, PRODUCER_QUERY_COLUMNS, null, null, null);
        if (cursor.moveToFirst()) {
            mProducer_Id = cursor.getInt(COL_QUERY_PRODUCER__ID);
            mProducerId = cursor.getString(COL_QUERY_PRODUCER_ID);
            mProducerName = cursor.getString(COL_QUERY_PRODUCER_NAME);
            Log.v(LOG_TAG, "updateProvider, mProducerName=" + mProducerName + ", hashCode=" + this.hashCode() + ", " + "producerUri = [" + producerUri + "]");

            mEditCompletionProducerName.setText(mProducerName);
            mEditCompletionProducerName.dismissDropDown();
        }

    }


//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Log.v(LOG_TAG, "onItemSelected, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
//        switch (view.getId()) {
//            case R.id.producer_name:
//                if (mAdapter != null && mAdapter.getCursor() != null) {
//                    int currentPos = mAdapter.getCursor().getPosition();
//                    Log.v(LOG_TAG, "onItemSelected, hashCode=" + this.hashCode() + ", currentCursorPosition=" + currentPos + "], position = [" + position + "], id = [" + id + "]");
//                    mProducerName = mAdapter.getCursor().getString(COL_QUERY_PRODUCER_NAME);
//                    mProducer_Id = mAdapter.getCursor().getInt(COL_QUERY_PRODUCER__ID);
//                    mProducerId = mAdapter.getCursor().getString(COL_QUERY_PRODUCER_ID);
//                    Log.v(LOG_TAG, "onClick, prodName: " + mProducerName + ", prodId: " + mProducerId + ", hashCode=" + this.hashCode() + ", " + "view = [" + view + "]");
//                    mEditCompletionProducerName.setText(mProducerName);
//                }
//                break;
//            default:
//                Log.v(LOG_TAG, "onItemSelected - something other selected..., hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
//        }
//    }


//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
//        switch (id) {
//            case PRODUCER_COMPLETION_LOADER_ID:
//                Uri searchUri = ProducerEntry.buildUriWithName(mFilter == null ? "" : mFilter);
//                return new CursorLoader(getActivity(),
//                        searchUri,
//                        PRODUCER_QUERY_COLUMNS,
//                        null, null,
//                        ProducerEntry.TABLE_NAME + "." + Producer.NAME + " ASC");
//        }
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
////        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
//        switch (loader.getId()) {
//            case PRODUCER_COMPLETION_LOADER_ID:
//
//                if (data != null) {
//                    Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "data.closed() = [" + data.isClosed() + "]" + ", " + "data.count() = [" + data.getCount() + "]" + ", " + "data.getColumnCount() = [" + data.getColumnCount() + "]");
//                }
//
//                //confusing and seems wrong...
////                mProducerAdapter = new ProducerCompletionAdapter(getActivity(), data, false);
////                mEditCompletionProducerName.setAdapter(mProducerAdapter);
//
////                mProducerAdapter.swapCursor(data);
//
//                break;
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
//        // seems useless...
////        mProducerAdapter.swapCursor(null);
//
//    }
}
