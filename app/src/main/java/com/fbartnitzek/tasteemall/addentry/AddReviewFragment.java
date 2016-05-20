package com.fbartnitzek.tasteemall.addentry;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.location.GeocodeAddressIntentService;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.QueryColumns;
import com.fbartnitzek.tasteemall.tasks.QueryDrinkTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.fbartnitzek.tasteemall.ui.CustomSpinnerAdapter;
import com.fbartnitzek.tasteemall.ui.OnTouchHideKeyboardListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddReviewFragment extends Fragment implements
        CompletionDrinkAdapter.CompletionDrinkAdapterSelectionHandler,
        View.OnClickListener, QueryDrinkTask.QueryDrinkFoundHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = AddReviewFragment.class.getName();
    private static final String STATE_CONTENT_URI = "STATE_ADD_REVIEW_CONTENT_URI";
    private static final String STATE_DRINK_NAME = "STATE_ADD_REVIEW_DRINK_NAME";
    private static final String STATE_PRODUCER_NAME = "STATE_ADD_REVIEW_PRODUCER_NAME";
    private static final String STATE_DRINK_ID = "STATE_ADD_REVIEW_DRINK_ID";
    private static final String STATE_DRINK__ID = "STATE_ADD_REVIEW_DRINK__ID";
    private static final String STATE_REVIEW_RATING_POSITION = "STATE_ADD_REVIEW_REVIEW_RATING_POSITION";
    private static final String STATE_REVIEW_DESCRIPTION = "STATE_ADD_REVIEW_DESCRIPTION";
    private static final String STATE_REVIEW_RECOMMENDED_SIDES = "STATE_ADD_REVIEW_RECOMMENDED_SIDES";
    private static final String STATE_REVIEW_USER = "STATE_ADD_REVIEW_USER";
    private static final String STATE_REVIEW_READABLE_DATE = "STATE_ADD_REVIEW_READABLE_DATE";
    private static final String STATE_REVIEW_LOCATION = "STATE_ADD_REVIEW_LOCATION";
    private static final String STATE_GEOCODING_DONE = "STATE_GEOCODING_DONE";
    private static final String STATE_GEOCODING_RUNNING = "STATE_GEOCODING_RUNNING";

    private static final int DRINK_ACTIVITY_REQUEST_CODE = 999;
    private static final int EDIT_REVIEW_LOADER_ID = 57892;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 43923;
    private View mRootView;

    private static AutoCompleteTextView mEditCompletionDrinkName;
    private static Spinner mSpinnerRating;
    private static EditText mEditReviewDescription;
    private static EditText mEditReviewRecommendedSides;
    private static EditText mEditReviewUser;
    private static EditText mEditReviewReadableDate;
    private static EditText mEditReviewLocation;

    private String mDrinkName;
    private String mProducerName;
    private int mDrink_Id;
    private String mDrinkId;
    private Uri mContentUri = null;
    private String mReviewId = null;
    private ArrayAdapter<String> mRatingAdapter;
    private int mRatingPosition;
    private int mReview_Id;
    private GoogleApiClient mGoogleApiClient;
    private String mCurrentLocation;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private boolean mGeocodingDone = false;
    private boolean mGeocodingRunning = false;

    public AddReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CONTENT_URI)) {
            mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
            mGeocodingDone = savedInstanceState.getBoolean(STATE_GEOCODING_DONE, false);
            mGeocodingRunning = savedInstanceState.getBoolean(STATE_GEOCODING_RUNNING, false);
        }
        setRetainInstance(true);
        buildGoogleApiClient();
        mResultReceiver = new AddressResultReceiver(new Handler());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mContentUri != null) {
            Log.v(LOG_TAG, "onActivityCreated with contentUri, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        } else {
            Log.v(LOG_TAG, "onActivityCreated without contentUri, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_review, container, false);

        mEditCompletionDrinkName = (AutoCompleteTextView) mRootView.findViewById(R.id.drink_name);

        createToolbar();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_DRINK_NAME)) {   //just typed some letters
                mDrinkName = savedInstanceState.getString(STATE_DRINK_NAME);
                mEditCompletionDrinkName.setText(mDrinkName);
//                mEditCompletionDrinkName.dismissDropDown();   //TODO: needed?
            }
            if (savedInstanceState.containsKey(STATE_DRINK_ID)) {    //found drink
                mDrink_Id = savedInstanceState.getInt(STATE_DRINK__ID);
                mDrinkId = savedInstanceState.getString(STATE_DRINK_ID);
                mProducerName = savedInstanceState.getString(STATE_PRODUCER_NAME);
                updateToolbar();
            }
        }

        CompletionDrinkAdapter completionAdapter = new CompletionDrinkAdapter(getActivity(), this);
        mEditCompletionDrinkName.setAdapter(completionAdapter);

        mRootView.findViewById(R.id.add_drink_button).setOnClickListener(this);
        mRootView.findViewById(R.id.help_review_rating_button).setOnClickListener(this);

        mSpinnerRating = (Spinner) mRootView.findViewById(R.id.review_rating);


        // try later: http://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
        String[] reviewRatings = getActivity().getResources().getStringArray(R.array.pref_rating_values);
        mRatingAdapter = new CustomSpinnerAdapter(getActivity(),
                new ArrayList<>(Arrays.asList(reviewRatings)), R.layout.spinner_small_row,
                R.string.a11y_review_rating);
        mSpinnerRating.setAdapter(mRatingAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_RATING_POSITION)) {
            mRatingPosition = savedInstanceState.getInt(STATE_REVIEW_RATING_POSITION);
            setSpinner();
        }
        mSpinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRatingPosition = position;
                mSpinnerRating.setContentDescription(
                        getString(R.string.a11y_chosen_drinkType, mSpinnerRating.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // might be buggy...
        mSpinnerRating.setOnTouchListener(new OnTouchHideKeyboardListener(this));


        // restore usual fields
        mEditReviewDescription = (EditText) mRootView.findViewById(R.id.review_description);
        mEditReviewRecommendedSides = (EditText) mRootView.findViewById(R.id.review_recommended_sides);
        if (savedInstanceState != null) {
            mEditReviewDescription.setText(savedInstanceState.getString(STATE_REVIEW_DESCRIPTION));
            mEditReviewRecommendedSides.setText(savedInstanceState.getString(STATE_REVIEW_RECOMMENDED_SIDES));
        }

        // restore or init fields
        mEditReviewUser = (EditText) mRootView.findViewById(R.id.review_user_name);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_USER)) {
            mEditReviewUser.setText(savedInstanceState.getString(STATE_REVIEW_USER));
        } else {
            mEditReviewUser.setText(Utils.getUserNameFromSharedPrefs(getActivity()));
        }

        // TODO: onClick some dateAndTime-picker
        // that seems to be the best option, but no ext lib - so: later :-p
        // https://github.com/jjobes/SlideDateTimePicker
        mEditReviewReadableDate = (EditText) mRootView.findViewById(R.id.review_readable_date);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_READABLE_DATE)) {
            mEditReviewReadableDate.setText(savedInstanceState.getString(STATE_REVIEW_READABLE_DATE));
        } else {
            mEditReviewReadableDate.setText(Utils.getCurrentLocalIso8601Time());
        }

        mEditReviewLocation = (EditText) mRootView.findViewById(R.id.review_location);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_LOCATION)) {
            mEditReviewLocation.setText(savedInstanceState.getString(STATE_REVIEW_LOCATION));
        } else {
            updateLocation();
        }

        if (mContentUri == null) {
            resumeActivityEnterTransition();    // from add
        }

        return mRootView;
    }

    private void setSpinner() {
        Log.v(LOG_TAG, "setSpinner, mRatingPosition=" + mRatingPosition);
        if (mRatingPosition > -1) {
            String rating = (String) mSpinnerRating.getItemAtPosition(mRatingPosition);
            mSpinnerRating.setSelection(mRatingPosition);
            mSpinnerRating.setContentDescription(getString(R.string.a11y_chosen_review_rating, rating));
            mSpinnerRating.clearFocus();    //TODO: needed?
        }
    }

    @Override
    public void onResume() {
        if (mContentUri != null) {
            Log.v(LOG_TAG, "onResume with contentUri - edit, hashCode=" + this.hashCode() + ", " + "");
            getLoaderManager().initLoader(EDIT_REVIEW_LOADER_ID, null, this);
        } else {
            Log.v(LOG_TAG, "onResume without contentUri - add, hashCode=" + this.hashCode() + ", " + "");
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Log.v(LOG_TAG, "onSaveInstanceState, hashCode=" + this.hashCode() + ", " + "outState = [" + outState + "]");
        outState.putInt(STATE_REVIEW_RATING_POSITION, mRatingPosition); //rating
        outState.putString(STATE_REVIEW_DESCRIPTION, mEditReviewDescription.getText().toString().trim());
        outState.putString(STATE_REVIEW_RECOMMENDED_SIDES, mEditReviewRecommendedSides.getText().toString().trim());
        outState.putString(STATE_REVIEW_USER, mEditReviewUser.getText().toString().trim());
        outState.putString(STATE_REVIEW_READABLE_DATE, mEditReviewReadableDate.getText().toString().trim());
        outState.putString(STATE_REVIEW_LOCATION, mEditReviewLocation.getText().toString().trim());
        outState.putBoolean(STATE_GEOCODING_DONE, mGeocodingDone);
        outState.putBoolean(STATE_GEOCODING_RUNNING, mGeocodingRunning);

        if (mDrinkId != null) {
            outState.putString(STATE_DRINK_ID, mDrinkId);
            outState.putInt(STATE_DRINK__ID, mDrink_Id);
            outState.putString(STATE_DRINK_NAME, mDrinkName);
            outState.putString(STATE_PRODUCER_NAME, mProducerName);
        } else {
            outState.putString(STATE_DRINK_NAME, mEditCompletionDrinkName.getText().toString().trim());
        }

        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }

        super.onSaveInstanceState(outState);
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
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
            String readableDrink = getString(Utils.getReadableDrinkNameId(getActivity(), drinkType));

            if (mContentUri != null) {
                Log.v(LOG_TAG, "createToolbar with contentUri, hashCode=" + this.hashCode() + ", " + "");
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_review_activity_preview, readableDrink));
            } else {
                Log.v(LOG_TAG, "createToolbar without contentUri, hashCode=" + this.hashCode() + ", " + "");
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_review_activity_preview,
                                readableDrink));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }


    private void updateToolbar() {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode());
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity.getSupportActionBar() != null) {
            if (mContentUri != null) {  // edit
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_review_activity,
                                mDrinkName, mProducerName));
            } else {    // add
                if (mDrinkId != null) { //known names
                    ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                            getString(R.string.title_add_review_activity,
                                    mDrinkName, mProducerName));
                }
                // do nothing if only generic names are known
            }
        } else {
            Log.e(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (requestCode == DRINK_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            Uri drinkUri = data.getData();
            updateDrink(drinkUri);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateDrink(Uri drinkUri) {
        Log.v(LOG_TAG, "updateDrink, hashCode=" + this.hashCode() + ", " + "drinkUri = [" + drinkUri + "]");

        new QueryDrinkTask(getActivity(), this).execute(drinkUri);
    }

    public void saveData() {

        if (mDrinkId == null || mDrinkName == null) {
            Snackbar.make(mRootView, R.string.msg_choose_existing_drink, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (getString(R.string.pre_filled_rating).equals(mSpinnerRating.getSelectedItem().toString())) {
            Snackbar.make(mRootView, R.string.msg_rate_drink, Snackbar.LENGTH_SHORT).show();
            return;
        } else if ("".equals(mEditReviewUser.getText().toString().trim())) {
            Snackbar.make(mRootView, R.string.msg_no_username, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (!Utils.checkTimeFormat(mEditReviewReadableDate.getText().toString().trim())) {
            Snackbar.make(mRootView, R.string.msg_invalid_review_date, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (!validateLocation()) {
            Snackbar.make(mRootView, R.string.msg_invalid_geocode_location, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mContentUri != null) {
            updateReview();
        } else {
            insertReview();
        }
    }

    private boolean validateLocation() {
        return !mEditReviewLocation.getText().toString().startsWith(Utils.GEOCODE_ME)
                || Utils.checkGeocodeAddressFormat(mEditReviewLocation.getText().toString());
    }

    private void updateReview() {
        Uri singleEntryUri = Utils.calcSingleReviewUri(mContentUri);
        new UpdateEntryTask(getActivity(), singleEntryUri, "Review for " + mDrinkName, mRootView)
                .execute(DatabaseHelper.buildReviewValues(
                        mReviewId,
                        mSpinnerRating.getItemAtPosition(mRatingPosition).toString(),
                        mEditReviewDescription.getText().toString().trim(),
                        mEditReviewReadableDate.getText().toString().trim(),
                        mEditReviewRecommendedSides.getText().toString().trim(),
                        mDrinkId,
                        mEditReviewLocation.getText().toString().trim(),
                        mEditReviewUser.getText().toString().trim()));
    }

    private void insertReview() {
        String userName = mEditReviewUser.getText().toString().trim();
        String date = mEditReviewReadableDate.getText().toString().trim();
        new InsertEntryTask(
                getActivity(),
                DatabaseContract.ReviewEntry.CONTENT_URI, mRootView, "Review for " + mDrinkName)
                .execute(DatabaseHelper.buildReviewValues(
                        Utils.calcReviewId(userName, mDrinkId, date),
                        mSpinnerRating.getSelectedItem().toString(),
                        mEditReviewDescription.getText().toString().trim(),
                        date,
                        mEditReviewRecommendedSides.getText().toString().trim(),
                        mDrinkId,
                        mEditReviewLocation.getText().toString().trim(),
                        userName));

    }

    public void setmContentUri(Uri contentUri) {
        this.mContentUri = Utils.calcJoinedReviewUri(contentUri);
        Log.v(LOG_TAG, "setmContentUri, hashCode=" + this.hashCode() + ", " + "contentUri = [" + contentUri + "]");
    }

    @Override
    public void onClick(View v) {
        Log.v(LOG_TAG, "onClick, hashCode=" + this.hashCode() + ", " + "v = [" + v + "]");
        switch (v.getId()) {
            case R.id.add_drink_button:
                createDrink();
                break;
            case R.id.help_review_rating_button:
                showHelp();
                break;
        }
    }

    private void createDrink() {
        Log.v(LOG_TAG, "createDrink, hashCode=" + this.hashCode() + ", " + "");
        Bundle bundle = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View button = mRootView.findViewById(R.id.add_drink_button);
            bundle = ActivityOptions.makeScaleUpAnimation(
                    button, 0, 0, button.getWidth(), button.getHeight()).toBundle();
        }
        Intent intent = new Intent(getActivity(), AddDrinkActivity.class);
        intent.putExtra(AddDrinkActivity.PATTERN_EXTRA, mEditCompletionDrinkName.getText().toString().trim());
        startActivityForResult(intent, DRINK_ACTIVITY_REQUEST_CODE, bundle);
    }


    private void showHelp() {
        // TODO: something better ;-)
        Toast.makeText(getActivity(),R.string.help_show_review_rating_help, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFoundDrink(int drink_Id, String drinkName, String drinkId, String producerName) {
        Log.v(LOG_TAG, "onFoundDrink, hashCode=" + this.hashCode() + ", " + "drink_Id = [" + drink_Id + "], drinkName = [" + drinkName + "], drinkId = [" + drinkId + "], producerName = [" + producerName + "]");
        // NOT for both: (completionView and query after startActivityForResult)!!!
        mDrink_Id = drink_Id;
        mDrinkId = drinkId;
        mDrinkName = drinkName;
        mProducerName = producerName;

        mEditCompletionDrinkName.setText(mDrinkName);
        mEditCompletionDrinkName.dismissDropDown();

        updateToolbar();
    }

    @Override
    public void onSelectedDrink(int drink_Id, String drinkName, String drinkId, String producerName) {
        mDrink_Id = drink_Id;
        mDrinkId = drinkId;
        mDrinkName = drinkName;
        mProducerName = producerName;
        updateToolbar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case EDIT_REVIEW_LOADER_ID:
                if (mContentUri != null) {
                    return new CursorLoader(
                            getActivity(),
                            mContentUri,
                            QueryColumns.ReviewFragment.EditQuery.COLUMNS,
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
        switch (loader.getId()) {
            case EDIT_REVIEW_LOADER_ID:
                if (data != null && data.moveToFirst()) {
                    // variables not really needed - optimize later...
                    mProducerName = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_PRODUCER_NAME);
                    mDrinkName = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_DRINK_NAME);
                    mDrinkId = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_DRINK_ID);
                    mDrink_Id = data.getInt(QueryColumns.ReviewFragment.EditQuery.COL_DRINK__ID);
                    mReview_Id = data.getInt(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW__ID);
                    mReviewId = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_ID);
                    // later for a matching icon...
//                    String drinkType = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_DRINK_TYPE);
                    String reviewDesc = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_DESCRIPTION);
                    String location = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_LOCATION);
                    String rating = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_RATING);
                    String readableDate = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_READABLE_DATE);
                    String userName = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_USER_NAME);
                    String recommendedSides = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_RECOMMENDED_SIDES);

                    mEditCompletionDrinkName.setText(mDrinkName);
                    mEditCompletionDrinkName.dismissDropDown();
                    mRatingPosition = mRatingAdapter.getPosition(rating);
                    setSpinner();
                    mEditReviewDescription.setText(reviewDesc);
                    mEditReviewRecommendedSides.setText(recommendedSides);
                    mEditReviewUser.setText(userName);
                    mEditReviewReadableDate.setText(readableDate);
                    mEditReviewLocation.setText(location);

                    updateToolbar();

                    resumeActivityEnterTransition();    // from edit

                    Log.v(LOG_TAG, "onLoadFinished - all updated, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
                }
                break;
            default:
                Log.e(LOG_TAG, "onLoadFinished - other loader?, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // nothing
    }

    protected synchronized void buildGoogleApiClient() {
        Log.v(LOG_TAG, "buildGoogleApiClient, hashCode=" + this.hashCode() + ", " + "");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(LOG_TAG, "onConnected, hashCode=" + this.hashCode() + ", " + "bundle = [" + bundle + "]");
        initLocationAndGeoCoder();
    }

    private void initLocationAndGeoCoder() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            handlePermission(); //request permissions - may help

            Log.v(LOG_TAG, "initLocationAndGeoCoder - no permission");
            Toast.makeText(AddReviewFragment.this.getActivity(), R.string.msg_no_location_access, Toast.LENGTH_SHORT).show();

            return;
        } else {
            Log.v(LOG_TAG, "initLocationAndGeoCoder - with permission");
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            startGeocodeService();
        } else {
            Toast.makeText(AddReviewFragment.this.getActivity(), R.string.msg_no_location_provided, Toast.LENGTH_SHORT).show();

        }
    }

    protected void handlePermission() {
        // src: FilePickerFragment
        // https://developer.android.com/training/permissions/requesting.html
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(LOG_TAG, "onRequestPermissionsResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");
        if (REQUEST_LOCATION_PERMISSION_CODE == requestCode && permissions.length > 0) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0] ||
                    PackageManager.PERMISSION_GRANTED == grantResults[grantResults.length - 1]) { // at least one allowed
                Log.v(LOG_TAG, "onRequestPermissionsResult - permission granted, try again!");
                initLocationAndGeoCoder();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateLocation() {
        Log.v(LOG_TAG, "updateLocation - mCurrentLocation: " + mCurrentLocation + ", hashCode=" + this.hashCode() + ", " + "");
        if (mCurrentLocation != null && mEditReviewLocation != null) {
            mEditReviewLocation.setText(mCurrentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "onConnectionSuspended - try again, hashCode=" + this.hashCode() + ", " + "i = [" + i + "]");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(LOG_TAG, "onConnectionFailed, hashCode=" + this.hashCode() + ", " + "connectionResult = [" + connectionResult + "]");
    }

    @Override
    public void onStart() {

        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            Log.v(LOG_TAG, "onStart - connecting googleApiClient, hashCode=" + this.hashCode() + ", " + "");
            mGoogleApiClient.connect();
        } else {
            Log.v(LOG_TAG, "onStart - googleApiClient not found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v(LOG_TAG, "onStop - disconnecting googleApiClient, hashCode=" + this.hashCode() + ", " + "");
            mGoogleApiClient.disconnect();
        } else {
            Log.v(LOG_TAG, "onStop - googleApiClient not found or connected, hashCode=" + this.hashCode() + ", " + "");
        }
    }


    protected void startGeocodeService() {
        Log.v(LOG_TAG, "startGeocodeService, mGeocodingDone=" + mGeocodingDone + ", hashCode=" + this.hashCode() + ", " + "");
        if (mGeocodingDone || mGeocodingRunning) {
            Log.v(LOG_TAG, "startGeocodeService, geocoding already done");
            return;
        }

        Intent intent = new Intent(this.getActivity(), GeocodeAddressIntentService.class);
        if (mResultReceiver == null) {
            Log.e(LOG_TAG, "startGeocodeService - no resultReceiver found!, hashCode=" + this.hashCode() + ", " + "");
            return;
        }
        intent.putExtra(GeocodeAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(GeocodeAddressIntentService.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
        mGeocodingRunning = true;
    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from GeocodeAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.v(LOG_TAG, "onReceiveResult, hashCode=" + this.hashCode() + ", " + "resultCode = [" + resultCode + "], resultData = [" + resultData + "]");
            // Display the address string or an error message sent from the intent service.
            mGeocodingDone = true;
            if (GeocodeAddressIntentService.SUCCESS_RESULT == resultCode) {
                if (resultData.containsKey(GeocodeAddressIntentService.RESULT_ADDRESS_KEY)) {
                    mCurrentLocation = Utils.formatAddress(
                            (Address) resultData.getParcelable(GeocodeAddressIntentService.RESULT_ADDRESS_KEY));
                    updateLocation();
                } else {
                    Log.e(LOG_TAG, "onReceiveResult - SUCCESS without address - should never happen...");
                }

            } else if (getActivity() == null) { // fast enough on back button - useless result

                mGeocodingRunning = false;
                mGeocodingDone = false;
                return;

            } else {  // somehow failed

                if (Utils.isNetworkUnavailable(getActivity())) {
                    Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
                    mCurrentLocation = Utils.formatLocationForGeocoder(mLastLocation);
                    updateLocation();
                } else if (GeocodeAddressIntentService.FAILURE_SERVICE_NOT_AVAILABLE == resultCode) {
                    Toast.makeText(getActivity(), R.string.msg_service_not_available, Toast.LENGTH_LONG).show();
                    mCurrentLocation = Utils.formatLocationForGeocoder(mLastLocation);
                    updateLocation();
                } else {
                    int toastRes;
                    switch (resultCode) {
                        case GeocodeAddressIntentService.FAILURE_NO_RESULT_FOUND:
                            toastRes = R.string.msg_no_address_found;
                            break;
                        case GeocodeAddressIntentService.FAILURE_INVALID_LAT_LONG_USED:
                            toastRes = R.string.msg_invalid_lat_long;
                            break;
                        case GeocodeAddressIntentService.FAILURE_NO_LOCATION_DATA_PROVIDED:
                            toastRes = R.string.msg_no_location_provided;
                            break;
                        default:
                            toastRes = R.string.msg_no_location_generic;
                    }

                    Toast.makeText(getActivity(), toastRes, Toast.LENGTH_LONG).show();
                    mCurrentLocation = "";
                    updateLocation();
                }
            }
            mGeocodingRunning = false;

        }
    }


    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // not that important which one - but is lowest :-)
            ((AddReviewActivity) getActivity()).scheduleStartPostponedTransition(mEditReviewUser);
        }
    }
}
