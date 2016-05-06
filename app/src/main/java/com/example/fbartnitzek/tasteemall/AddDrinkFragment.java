package com.example.fbartnitzek.tasteemall;


import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.example.fbartnitzek.tasteemall.data.pojo.Drink;
import com.example.fbartnitzek.tasteemall.data.pojo.Producer;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDrinkFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,CompletionTextViewAdapter.CompletionAdapterUpdateHandler {

    public static final int PRODUCER_ACTIVITY_REQUEST_CODE = 666;
    private static final int EDIT_DRINK_LOADER_ID = 1234567;

    private static AutoCompleteTextView mEditCompletionProducerName;
    private static EditText mEditDrinkName;
    private static Spinner mSpinnerDrinkType;
    private static EditText mEditDrinkStyle;
    private static EditText mEditDrinkIngredients;
    private static EditText mEditDrinkSpecifics;
    private static View mRootView;

    private static final int PRODUCER_COMPLETION_LOADER_ID = 124;

    private static final String LOG_TAG = AddDrinkFragment.class.getName();

    private String mProducerName;
    private int mProducer_Id;
    private String mProducerId;
    private String mFilter = "";
    private Uri mContentUri = null;
    private String mDrinkId = null;
    private ArrayAdapter<String> mDrinkTypeAdapter;

    public AddDrinkFragment() {
        // Required empty public constructor
    }

    public static AddDrinkFragment newInstance() {
        return new AddDrinkFragment();
    }

    public static AddDrinkFragment newInstance(Uri contentUri) {
        AddDrinkFragment fragment = new AddDrinkFragment();
        fragment.setContentUri(contentUri);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_drink, container, false);

        mEditCompletionProducerName = (AutoCompleteTextView) mRootView.findViewById(R.id.producer_name);

        createToolbar();

        CompletionTextViewAdapter completionAdapter = new CompletionTextViewAdapter(
                getActivity(),
                R.layout.list_item_producer_completion,
                new String[]{Producer.NAME, Producer.LOCATION},
                new int[]{R.id.list_item_producer_name, R.id.list_item_producer_location },
                this);

        mEditCompletionProducerName.setAdapter(completionAdapter);

        mRootView.findViewById(R.id.add_producer_button).setOnClickListener(this);

        mEditDrinkName = (EditText) mRootView.findViewById(R.id.drink_name);
        mSpinnerDrinkType = (Spinner) mRootView.findViewById(R.id.drink_type);

        // fill type with drink_type from settings
        String drinkType = Utils.getDrinkTypeFromSharedPrefs(getActivity(), false);

        String[] drinkTypes = getActivity().getResources().getStringArray(R.array.pref_type_values);

        mDrinkTypeAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                drinkTypes);
        mDrinkTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDrinkType.setAdapter(mDrinkTypeAdapter);

        int spinnerPosition = mDrinkTypeAdapter.getPosition(drinkType);
        if (spinnerPosition > -1) {
            mSpinnerDrinkType.setSelection(spinnerPosition);
        }
        mSpinnerDrinkType.setOnItemSelectedListener(this);

        mEditDrinkStyle = (EditText) mRootView.findViewById(R.id.drink_style);
        mEditDrinkIngredients = (EditText) mRootView.findViewById(R.id.drink_ingredients);
        mEditDrinkSpecifics = (EditText) mRootView.findViewById(R.id.drink_specifics);

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
            String readableDrink = getString(Utils.getDrinkName(drinkType));

            if (mContentUri != null) {
                Log.v(LOG_TAG, "createToolbar with contentUri, hashCode=" + this.hashCode() + ", " + "");
                activity.getSupportActionBar().setTitle(
                        getString(R.string.title_edit_drink_activity_preview,
                                readableDrink));
            } else {
                Log.v(LOG_TAG, "createToolbar without contentUri, hashCode=" + this.hashCode() + ", " + "");
                activity.getSupportActionBar().setTitle(
                        getString(R.string.title_add_drink_activity,
                                readableDrink));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        if (mContentUri != null) {
            Log.v(LOG_TAG, "onActivityCreated with contentUri - edit, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            getLoaderManager().initLoader(EDIT_DRINK_LOADER_ID, null, this);
        } else {
            Log.v(LOG_TAG, "onActivityCreated without contentUri - add, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
        super.onActivityCreated(savedInstanceState);
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
    void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");

        String drinkName = mEditDrinkName.getText().toString();

        //validate
        if (mProducerId == null || mProducerName == null) {
            Snackbar.make(mRootView, "choose an existing producer first...", Snackbar.LENGTH_SHORT).show();
            return;
        } else if ("".equals(drinkName)) {
            Snackbar.make(mRootView, "choose a name first...", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Uri drinkUri;
        if (mContentUri != null) {
            drinkUri = updateData(drinkName);
        } else {
            drinkUri = insertData(drinkName);
        }

        if (drinkUri != null) {
            Intent output = new Intent();
            output.setData(drinkUri);
            getActivity().setResult(Activity.RESULT_OK, output);
            getActivity().finish();
        } else {
            if (mContentUri != null) {
                Snackbar.make(mRootView, "Updating drink " + drinkName + " didn't work ...",
                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            } else {
                Snackbar.make(mRootView, "Creating new entry " + drinkName + " didn't work ...",
                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        }
    }

    private Uri updateData(String drinkName) {
        String[] selectionArgs = new String[]{mDrinkId};
        String where = DatabaseContract.DrinkEntry.TABLE_NAME + "." + Drink.DRINK_ID + " = ?";
        int rows = getActivity().getContentResolver().update(
                DatabaseContract.DrinkEntry.CONTENT_URI,
                DatabaseHelper.buildDrinkValues(
                        mDrinkId,
                        drinkName,
                        mEditDrinkSpecifics.getText().toString(),
                        mEditDrinkStyle.getText().toString(),
                        mSpinnerDrinkType.getItemAtPosition(
                                mSpinnerDrinkType.getSelectedItemPosition()).toString(),
                        mEditDrinkIngredients.getText().toString(),
                        mProducerId),
                where,
                selectionArgs);

        if (rows < 1) {
            return null;
        } else {
            return mContentUri;
        }
    }


    private Uri insertData(String drinkName) {
        return getActivity().getContentResolver().insert(
                DatabaseContract.DrinkEntry.CONTENT_URI,
                DatabaseHelper.buildDrinkValues(
                        Utils.calcDrinkId(drinkName, mProducerId),
                        drinkName,
                        mEditDrinkSpecifics.getText().toString(),
                        mEditDrinkStyle.getText().toString(),
                        mSpinnerDrinkType.getItemAtPosition(
                                mSpinnerDrinkType.getSelectedItemPosition()).toString(),
                        mEditDrinkIngredients.getText().toString(),
                        mProducerId
                )
        );
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

        // resets all to generic - but there is no better way...
        String drinkType = mSpinnerDrinkType.getItemAtPosition(position).toString();
        Log.v(LOG_TAG, "onItemSelected - changed drinkType to: " + drinkType + ", hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");

        Utils.setSharedPrefsDrinkType(getActivity(), drinkType);
        int drinkTypeIndex = Utils.getDrinkTypeIndexFromSharedPrefs(getActivity(), false);
        String readableDrinkType = getString(Utils.getDrinkName(drinkTypeIndex));

        if (mContentUri == null) {  //just update for new drinks, ignore for edits
            updateToolbar(readableDrinkType, null);
        }

        ((TextView) mRootView.findViewById(R.id.label_drink)).setText(readableDrinkType);

    }

    private void updateToolbar(String drinkNameOrDrinkType, String producerName) {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "drinkNameOrDrinkType = [" + drinkNameOrDrinkType + "], producerName = [" + producerName + "]");
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity.getSupportActionBar()!= null) {
            if (mContentUri != null) {  // edit
                activity.getSupportActionBar().setTitle(
                        getString(R.string.title_edit_drink_activity,
                                drinkNameOrDrinkType, producerName));
            } else {    // add
                activity.getSupportActionBar().setTitle(
                        getString(R.string.title_add_drink_activity,
                                drinkNameOrDrinkType));
            }
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "drinkNameOrDrinkType = [" + drinkNameOrDrinkType + "], producerName = [" + producerName + "]");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.v(LOG_TAG, "onNothingSelected, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "]");
    }

    public void updateProvider(Uri producerUri) {

        Log.v(LOG_TAG, "updateProvider, hashCode=" + this.hashCode() + ", " + "producerUri = [" + producerUri + "]");
        Cursor cursor = getActivity().getContentResolver().query(producerUri, DrinkFragmentHelper.PRODUCER_QUERY_COLUMNS, null, null, null);
        if (cursor.moveToFirst()) {
            mProducer_Id = cursor.getInt(DrinkFragmentHelper.COL_QUERY_PRODUCER__ID);
            mProducerId = cursor.getString(DrinkFragmentHelper.COL_QUERY_PRODUCER_ID);
            mProducerName = cursor.getString(DrinkFragmentHelper.COL_QUERY_PRODUCER_NAME);
            Log.v(LOG_TAG, "updateProvider, mProducerName=" + mProducerName + ", hashCode=" + this.hashCode() + ", " + "producerUri = [" + producerUri + "]");

            mEditCompletionProducerName.setText(mProducerName);
            mEditCompletionProducerName.dismissDropDown();
        }

    }

    public void setContentUri(Uri contentUri) {
        this.mContentUri = contentUri;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case EDIT_DRINK_LOADER_ID:
                if (mContentUri != null) {
                    return new CursorLoader(
                            getActivity(),
                            mContentUri,
                            DrinkFragmentHelper.DETAIL_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        switch (loader.getId()) {
            case EDIT_DRINK_LOADER_ID:
                if (data != null && data.moveToFirst()) {
                    // variables not really needed - optimize later...
                    mProducerName= data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_PRODUCER_NAME);
                    String drinkName= data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_NAME);
                    mDrinkId = data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_ID);
                    mProducerId = data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_PRODUCER_ID);
                    String drinkStyle = data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_STYLE);
                    String drinkIngredients = data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_INGREDIENTS);
                    String drinkSpecifics = data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_SPECIFICS);
                    String drinkType = data.getString(DrinkFragmentHelper.COL_QUERY_DRINK_TYPE);

                    mEditCompletionProducerName.setText(mProducerName);
                    mEditCompletionProducerName.dismissDropDown();
                    mEditDrinkName.setText(drinkName);
                    mEditDrinkStyle.setText(drinkStyle);
                    mEditDrinkIngredients.setText(drinkIngredients);
                    mEditDrinkSpecifics.setText(drinkSpecifics);
                    int spinnerPosition = mDrinkTypeAdapter.getPosition(drinkType);
                    if (spinnerPosition > -1) {
                        mSpinnerDrinkType.setSelection(spinnerPosition);
                    }

                    updateToolbar(drinkName, mProducerName);

                    Log.v(LOG_TAG, "onLoadFinished - all updated, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
                }
                break;
            default:
                Log.e(LOG_TAG, "onLoadFinished - other loader?, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    @Override
    public void onUpdate(String entryName, String entryId, int entry_Id) {
        mProducer_Id = entry_Id;
        mProducerId = entryId;
        mProducerName = entryName;
    }
}
