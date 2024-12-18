package com.fbartnitzek.tasteemall.addentry;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.QueryProducerTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDrinkFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        QueryProducerTask.QueryProducerFoundHandler {

    private static final int PRODUCER_ACTIVITY_REQUEST_CODE = 666;
    private static final int EDIT_DRINK_LOADER_ID = 1234567;

    private static final String STATE_PRODUCER_NAME = "STATE_PRODUCER_NAME";
    private static final String STATE_PRODUCER_ID = "STATE_PRODUCER_ID";
    private static final String STATE_PRODUCER__ID = "STATE_PRODUCER__ID";
    private static final String STATE_DRINK_NAME = "STATE_DRINK_NAME";
    private static final String STATE_DRINK_TYPE_POSITION = "STATE_DRINK_TYPE_POSITION";
    private static final String STATE_DRINK_STYLE = "STATE_DRINK_STYLE";
    private static final String STATE_DRINK_INGREDIENTS = "STATE_DRINK_INGREDIENTS";
    private static final String STATE_DRINK_SPECIFICS = "STATE_DRINK_SPECIFICS";
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";

    private AutoCompleteTextView mEditCompletionProducerName;
    private EditText mEditDrinkName;
    private Spinner mSpinnerDrinkType;
    private EditText mEditDrinkStyle;
    private EditText mEditDrinkIngredients;
    private EditText mEditDrinkSpecifics;
    private View mRootView;

    private static final String LOG_TAG = AddDrinkFragment.class.getName();

    private String mProducerName;
    private String mPreFilledPattern;
    private int mProducer_Id;
    private String mProducerId;
//    private String mFilter = "";
    private Uri mContentUri = null;
    private String mDrinkId = null;
    private ArrayAdapter<String> mDrinkTypeAdapter;

    public AddDrinkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_drink, container, false);

        mEditCompletionProducerName = mRootView.findViewById(R.id.producer_name);

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(STATE_PRODUCER_NAME)){   //just typed some letters
                mEditCompletionProducerName.setText(savedInstanceState.getString(STATE_PRODUCER_NAME));
            }
            if (savedInstanceState.containsKey(STATE_PRODUCER_ID)) {    //found producer
                mProducer_Id = savedInstanceState.getInt(STATE_PRODUCER__ID);
                mProducerId = savedInstanceState.getString(STATE_PRODUCER_ID);
            }
        } else {
            if (mPreFilledPattern != null) {
                mEditCompletionProducerName.setText(mPreFilledPattern);
            }
        }


        createToolbar();

        CompletionProducerAdapter completionAdapter = new CompletionProducerAdapter(getActivity());

        mEditCompletionProducerName.setAdapter(completionAdapter);
        mEditCompletionProducerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor c = (Cursor) parent.getItemAtPosition(position);

                mProducer_Id = c.getInt(QueryColumns.DrinkFragment.ProducerCompletionQuery.COL_PRODUCER__ID);
                mProducerId = c.getString(QueryColumns.DrinkFragment.ProducerCompletionQuery.COL_PRODUCER_ID);
                mProducerName = c.getString(QueryColumns.DrinkFragment.ProducerCompletionQuery.COL_PRODUCER_NAME);

                Log.v(LOG_TAG, "onSelectedProducer, hashCode=" + this.hashCode() + ", " + "producer_Id = [" + mProducer_Id + "]");
            }
        });

        mRootView.findViewById(R.id.add_producer_button).setOnClickListener(this);

        mEditDrinkName = mRootView.findViewById(R.id.drink_name);

        mEditCompletionProducerName.setTransitionName(getString(R.string.shared_transition_add_producer_name));
        mEditDrinkName.setTransitionName(getString(R.string.shared_transition_add_drink_name));

        mSpinnerDrinkType = mRootView.findViewById(R.id.drink_type);

        // fill type with drink_type from settings

        String[] drinkTypes = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.pref_type_values);

        mDrinkTypeAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                drinkTypes);
        mDrinkTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDrinkType.setAdapter(mDrinkTypeAdapter);

        int spinnerPosition;
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_DRINK_TYPE_POSITION)) {
            spinnerPosition = savedInstanceState.getInt(STATE_DRINK_TYPE_POSITION);
        } else {
            String drinkType = Utils.getDrinkTypeFromSharedPrefs(getActivity(), false);
            spinnerPosition = mDrinkTypeAdapter.getPosition(drinkType);
        }
        setSpinner(spinnerPosition);

        mSpinnerDrinkType.setOnItemSelectedListener(this);

        mEditDrinkStyle = mRootView.findViewById(R.id.drink_style);
        mEditDrinkIngredients = mRootView.findViewById(R.id.drink_ingredients);
        mEditDrinkSpecifics = mRootView.findViewById(R.id.drink_specifics);

        if (savedInstanceState != null) {
            mEditDrinkName.setText(savedInstanceState.getString(STATE_DRINK_NAME));
            mEditDrinkStyle.setText(savedInstanceState.getString(STATE_DRINK_STYLE));
            mEditDrinkIngredients.setText(savedInstanceState.getString(STATE_DRINK_INGREDIENTS));
            mEditDrinkSpecifics.setText(savedInstanceState.getString(STATE_DRINK_SPECIFICS));
        } else {
            if (mPreFilledPattern != null) {
                mEditDrinkName.setText(mPreFilledPattern);
            }
        }

        if (mContentUri == null) {
            resumeActivityEnterTransition();    // from add
        }

        return mRootView;
    }

    private void setSpinner(int spinnerPosition) {
        if (spinnerPosition > -1) {
            String drinkType = (String) mSpinnerDrinkType.getItemAtPosition(spinnerPosition);
            mSpinnerDrinkType.setSelection(spinnerPosition);
            mSpinnerDrinkType.setContentDescription(getString(R.string.a11y_chosen_drinkType, drinkType));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_PRODUCER_NAME, mEditCompletionProducerName.getText().toString().trim());

        outState.putString(STATE_DRINK_NAME, mEditDrinkName.getText().toString().trim());
        outState.putInt(STATE_DRINK_TYPE_POSITION, mSpinnerDrinkType.getSelectedItemPosition());
        outState.putString(STATE_DRINK_STYLE, mEditDrinkStyle.getText().toString().trim());
        outState.putString(STATE_DRINK_INGREDIENTS, mEditDrinkIngredients.getText().toString().trim());
        outState.putString(STATE_DRINK_SPECIFICS, mEditDrinkSpecifics.getText().toString().trim());

        if (mProducerId != null) {
            outState.putString(STATE_PRODUCER_ID, mProducerId);
            outState.putInt(STATE_PRODUCER__ID, mProducer_Id);
        }

        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }

        super.onSaveInstanceState(outState);
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Objects.requireNonNull(activity).setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                Log.e(LOG_TAG, "createToolbar - no actionbar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);
            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableDrink = getString(Utils.getReadableDrinkNameId(activity, drinkType));

            if (mContentUri != null) {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_drink_activity_preview,
                                readableDrink));
            } else {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                                getString(R.string.title_add_drink_activity,
                                readableDrink));
            }

//        } else {
//            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void updateToolbar(String drinkNameOrDrinkType, String producerName) {
//        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "drinkNameOrDrinkType = [" + drinkNameOrDrinkType + "], producerName = [" + producerName + "]");
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (Objects.requireNonNull(activity).getSupportActionBar()!= null) {
            if (mContentUri != null) {  // edit
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_drink_activity,
                                drinkNameOrDrinkType, producerName));
            } else {    // add
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_drink_activity,
                                drinkNameOrDrinkType));
            }
//        } else {
//            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "drinkNameOrDrinkType = [" + drinkNameOrDrinkType + "], producerName = [" + producerName + "]");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CONTENT_URI)) {
                mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
            }
        }
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mContentUri != null) {
            Log.v(LOG_TAG, "onResume with contentUri - edit, hashCode=" + this.hashCode() + ", " + "");
            LoaderManager.getInstance(this).initLoader(EDIT_DRINK_LOADER_ID, null, this);
        } else {
            Log.v(LOG_TAG, "onResume without contentUri - add, hashCode=" + this.hashCode() + ", " + "");
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_producer_button) {
            createProducer();
        }
    }


    void saveData() {
        String drinkName = mEditDrinkName.getText().toString().trim();

        //validate
        if (mProducerId == null || mProducerName == null) {
            Snackbar.make(mRootView, R.string.msg_choose_existing_provider, Snackbar.LENGTH_SHORT).show();
            return;
        } else if ("".equals(drinkName)) {
            Snackbar.make(mRootView, R.string.msg_choose_drink_name, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mContentUri != null) {
            updateData(drinkName);
        } else {
            insertData(drinkName);
        }
    }



    private void updateData(String drinkName) {
        Uri singleEntryUri = Utils.calcSingleDrinkUri(mContentUri);
        new UpdateEntryTask(getActivity(), singleEntryUri, drinkName, mRootView)
                .execute(DatabaseHelper.buildDrinkValues(
                        mDrinkId,
                        drinkName,
                        mEditDrinkSpecifics.getText().toString().trim(),
                        mEditDrinkStyle.getText().toString().trim(),
                        mSpinnerDrinkType.getItemAtPosition(
                                mSpinnerDrinkType.getSelectedItemPosition()).toString(),
                        mEditDrinkIngredients.getText().toString().trim(),
                        mProducerId)
                );

    }

    private void insertData(String drinkName) {
        new InsertEntryTask(
                getActivity(), DatabaseContract.DrinkEntry.CONTENT_URI, mRootView, drinkName)
                    .execute(DatabaseHelper.buildDrinkValues(
                        Utils.calcDrinkId(drinkName, mProducerId),
                        drinkName,
                        mEditDrinkSpecifics.getText().toString().trim(),
                        mEditDrinkStyle.getText().toString().trim(),
                        mSpinnerDrinkType.getItemAtPosition(
                                mSpinnerDrinkType.getSelectedItemPosition()).toString(),
                        mEditDrinkIngredients.getText().toString().trim(),
                        mProducerId));
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
        Bundle bundle;
        bundle = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                mEditCompletionProducerName, getString(R.string.shared_transition_add_producer_name)
        ).toBundle();
        Intent intent = new Intent(getActivity(), AddProducerActivity.class);
        // use pre filled name
        intent.putExtra(AddProducerActivity.PRODUCER_NAME_EXTRA,
                mEditCompletionProducerName.getText().toString().trim());
        startActivityForResult(intent, PRODUCER_ACTIVITY_REQUEST_CODE, bundle);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v(LOG_TAG, "onItemSelected, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        if (mDrinkTypeAdapter == parent.getAdapter()) {
            // resets all to generic - but there is no better way...
            String drinkType = mSpinnerDrinkType.getItemAtPosition(position).toString();
//            Log.v(LOG_TAG, "onItemSelected - changed drinkType to: " + drinkType + ", hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");

            Utils.setSharedPrefsDrinkType(getActivity(), drinkType);
            int drinkTypeIndex = Utils.getDrinkTypeIndexFromSharedPrefs(getActivity(), false);
            String readableDrinkType = getString(Utils.getReadableDrinkNameId(getActivity(), drinkTypeIndex));

            if (mContentUri == null) {  //just update for new drinks, ignore for edits
                updateToolbar(readableDrinkType, null);
            }

            ((TextView) mRootView.findViewById(R.id.label_drink)).setText(readableDrinkType);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.v(LOG_TAG, "onNothingSelected, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "]");
    }

    private void updateProvider(Uri producerUri) {
        Log.v(LOG_TAG, "updateProvider, hashCode=" + this.hashCode() + ", " + "producerUri = [" + producerUri + "]");

        new QueryProducerTask(getActivity(), this).execute(producerUri);
    }

    public void setContentUri(Uri contentUri) {

        this.mContentUri = Utils.calcDrinkIncludingProducerUri(contentUri);
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (id == EDIT_DRINK_LOADER_ID) {
            if (mContentUri != null) {
                return new CursorLoader(
                        Objects.requireNonNull(getActivity()),
                        mContentUri,
                        QueryColumns.DrinkFragment.EditQuery.COLUMNS,
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

        if (loader.getId() == EDIT_DRINK_LOADER_ID) {
            if (data != null && data.moveToFirst()) {
                // variables not really needed - optimize later...
                mProducerName = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_PRODUCER_NAME);
                String drinkName = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_DRINK_NAME);
                mDrinkId = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_DRINK_ID);
                mProducerId = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_PRODUCER_ID);
                String drinkStyle = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_DRINK_STYLE);
                String drinkIngredients = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_DRINK_INGREDIENTS);
                String drinkSpecifics = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_DRINK_SPECIFICS);
                String drinkType = data.getString(QueryColumns.DrinkFragment.EditQuery.COL_DRINK_TYPE);

                mEditCompletionProducerName.setText(mProducerName);
                mEditCompletionProducerName.dismissDropDown();
                mEditDrinkName.setText(drinkName);

                int drink_Id = DatabaseContract.getIdFromUri(mContentUri);
                mEditCompletionProducerName.setTransitionName(
                        getString(R.string.shared_transition_drink_producer) + drink_Id);
                mEditDrinkName.setTransitionName(
                        getString(R.string.shared_transition_drink_drink) + drink_Id);

                mEditDrinkStyle.setText(drinkStyle);
                mEditDrinkIngredients.setText(drinkIngredients);
                mEditDrinkSpecifics.setText(drinkSpecifics);
                int spinnerPosition = mDrinkTypeAdapter.getPosition(drinkType);
                setSpinner(spinnerPosition);

                updateToolbar(drinkName, mProducerName);

                resumeActivityEnterTransition();

                Log.v(LOG_TAG, "onLoadFinished - all updated, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
            }
        } else {
            Log.e(LOG_TAG, "onLoadFinished - other loader?, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        }
    }

    @Override
    public void onLoaderReset(@NotNull Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    @Override
    public void onFoundProducer(int producer_Id, String producerName, String producerId) {
        Log.v(LOG_TAG, "onFoundProducer, hashCode=" + this.hashCode() + ", " + "producer_Id = [" + producer_Id + "], producerName = [" + producerName + "], producerId = [" + producerId + "]");
        mProducer_Id = producer_Id;
        mProducerId = producerId;
        mProducerName = producerName;

        if (!mEditCompletionProducerName.getText().toString().trim().equals(mProducerName)){
            mEditCompletionProducerName.setText(mProducerName);
            mEditCompletionProducerName.dismissDropDown();
        }
    }

    public void setmPreFilledPattern(String mPreFilledPattern) {
        this.mPreFilledPattern = mPreFilledPattern;
    }

    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");

        ((AddDrinkActivity) Objects.requireNonNull(getActivity())).scheduleStartPostponedTransition(mEditDrinkName);
    }
}
