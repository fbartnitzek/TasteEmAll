package com.fbartnitzek.tasteemall.addentry;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.fbartnitzek.tasteemall.CustomApplication;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.location.GeocodeIntentService;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.InsertLocationTask;
import com.fbartnitzek.tasteemall.tasks.QueryAndInsertUserTask;
import com.fbartnitzek.tasteemall.tasks.QueryDrinkTask;
import com.fbartnitzek.tasteemall.tasks.QueryLocationTask;
import com.fbartnitzek.tasteemall.tasks.QueryNearbyLocationsTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.fbartnitzek.tasteemall.tasks.ValidateUserTask;
import com.fbartnitzek.tasteemall.ui.CustomSpinnerAdapter;
import com.fbartnitzek.tasteemall.ui.OnTouchHideKeyboardListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddReviewFragment extends Fragment implements
        View.OnClickListener, QueryDrinkTask.QueryDrinkFoundHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        QueryAndInsertUserTask.UserCreatedHandler, ValidateUserTask.ValidateUserHandler,
        OnMapReadyCallback, QueryNearbyLocationsTask.QueryNearbyLocationHandler,
        InsertLocationTask.InsertLocationHandler, QueryLocationTask.QueryLocationFoundHandler {

    private static final String LOG_TAG = AddReviewFragment.class.getName();
    private static final String STATE_CONTENT_URI = "STATE_ADD_REVIEW_CONTENT_URI";
    private static final String STATE_DRINK_NAME = "STATE_ADD_REVIEW_DRINK_NAME";
    private static final String STATE_PRODUCER_NAME = "STATE_ADD_REVIEW_PRODUCER_NAME";
    private static final String STATE_DRINK_ID = "STATE_ADD_REVIEW_DRINK_ID";
    private static final String STATE_REVIEW_RATING_POSITION = "STATE_ADD_REVIEW_REVIEW_RATING_POSITION";
    private static final String STATE_REVIEW_DESCRIPTION = "STATE_ADD_REVIEW_DESCRIPTION";
    private static final String STATE_REVIEW_RECOMMENDED_SIDES = "STATE_ADD_REVIEW_RECOMMENDED_SIDES";
    private static final String STATE_USER_NAME = "STATE_ADD_REVIEW_USER_NAME";
    private static final String STATE_USER_ID = "STATE_ADD_REVIEW_USER_ID";
    private static final String STATE_REVIEW_READABLE_DATE = "STATE_ADD_REVIEW_READABLE_DATE";
    private static final String STATE_REVIEW_LOCATION = "STATE_ADD_REVIEW_LOCATION";

    private static final int DRINK_ACTIVITY_REQUEST_CODE = 999;
    private static final int EDIT_REVIEW_LOADER_ID = 57892;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 43923;
    private static final int LOCATION_ACTIVITY_REQUEST_CODE = 2356;
    private View mRootView;

    private static AutoCompleteTextView mEditCompletionDrinkName;
    private static AutoCompleteTextView mEditCompletionUserName;
    private static LocationAutoCompleteTextView mEditReviewLocation;
    private static Spinner mSpinnerRating;
    private static EditText mEditReviewDescription;
    private static EditText mEditReviewRecommendedSides;
    private static EditText mEditReviewReadableDate;
    private static EditText mEditReviewLocationDescription;

    private String mDrinkName;
    private String mProducerName;
    private String mDrinkId;
    private String mUserName;
    private String mUserId;
    private String mLocationId;
    private Uri mContentUri = null;
    private String mReviewId = null;
    private ArrayAdapter<String> mRatingAdapter;
    private CompletionLocationAdapter mLocationAdapter;

    private int mRatingPosition;
    private int mReview_Id;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private boolean mEditValuesLoaded = false;
    private static GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private Marker mCurrentMarker = null;
    private LocationParcelable mLocationParcelable;
    private String mLocationInput;
    private LocationParcelable mProducerLocationParcelable;
    private boolean mEditReviewLocationIgnoreTextChange = false;

    // TODO: pagify whole add sequence!

    private final SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            Log.v(LOG_TAG, "onDateTimeSet, hashCode=" + this.hashCode() + ", " + "date = [" + date + "]");
            if (mEditReviewReadableDate != null) {
                mEditReviewReadableDate.setText(Utils.getIso8601Time(date));
            }
        }
    };

    public AddReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CONTENT_URI)) {
            mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
        }
        setRetainInstance(true);
        mResultReceiver = new AddressResultReceiver(new Handler());
        super.onCreate(savedInstanceState);
        if (mContentUri == null) {
            getCurrentLocation();  //TODO: maybe in onCreateView better...
        }

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

        mEditCompletionDrinkName = mRootView.findViewById(R.id.drink_name);
        mEditCompletionUserName = mRootView.findViewById(R.id.review_user_name);

        createToolbar();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_DRINK_NAME)) {   //just typed some letters
                mDrinkName = savedInstanceState.getString(STATE_DRINK_NAME);
                mEditCompletionDrinkName.setText(mDrinkName);
//                mEditCompletionDrinkName.dismissDropDown();   //TODO: needed?
            }
            if (savedInstanceState.containsKey(STATE_DRINK_ID)) {    //found drink
                mDrinkId = savedInstanceState.getString(STATE_DRINK_ID);
                mProducerName = savedInstanceState.getString(STATE_PRODUCER_NAME);
                updateToolbar();
            }

            if (savedInstanceState.containsKey(STATE_USER_NAME)) {  //just text
                mUserName = savedInstanceState.getString(STATE_USER_NAME);
                mUserName = savedInstanceState.getString(STATE_USER_NAME);
                mEditCompletionUserName.setText(mUserName);
            }

            if (savedInstanceState.containsKey(STATE_USER_ID)) {    // existing ids
                mUserId = savedInstanceState.getString(STATE_USER_ID);
            }
        }

        CompletionDrinkAdapter completionAdapter = new CompletionDrinkAdapter(getActivity());

        mEditCompletionDrinkName.setAdapter(completionAdapter);
        mEditCompletionDrinkName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);

                mDrinkName = c.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_DRINK_NAME);
                mDrinkId = c.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_DRINK_ID);
                mProducerName = c.getString(QueryColumns.ReviewFragment.DrinkCompletionQuery.COL_PRODUCER_NAME);
                Log.v(LOG_TAG, "onItemClick, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "], mDrinkId=" + mDrinkId);
                updateToolbar();
                queryNearbyProducer();
            }
        });

        mEditCompletionDrinkName.setTransitionName(getString(R.string.shared_transition_add_drink_name));

        if (savedInstanceState == null || !savedInstanceState.containsKey(STATE_USER_NAME)) {
            mEditCompletionUserName.setText(Utils.getUserNameFromSharedPrefs(getActivity()));
        }
        CompletionUserAdapter completionUserAdapter = new CompletionUserAdapter(getActivity());
        mEditCompletionUserName.setAdapter(completionUserAdapter);
        mEditCompletionUserName.setOnItemClickListener((parent, view, position, id) -> {
            Cursor c = (Cursor) parent.getItemAtPosition(position);

            mUserName = c.getString(QueryColumns.ReviewFragment.UserQuery.COL_USER_NAME);
            mUserId = c.getString(QueryColumns.ReviewFragment.UserQuery.COL_USER_ID);
        });

        mRootView.findViewById(R.id.add_drink_button).setOnClickListener(this);
        mRootView.findViewById(R.id.add_user_button).setOnClickListener(this);
        mRootView.findViewById(R.id.search_review_location_button).setOnClickListener(this);
        mRootView.findViewById(R.id.help_review_rating_button).setOnClickListener(this);

        mSpinnerRating = mRootView.findViewById(R.id.review_rating);


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
        mEditReviewDescription = mRootView.findViewById(R.id.review_description);
        mEditReviewRecommendedSides = mRootView.findViewById(R.id.review_recommended_sides);
        if (savedInstanceState != null) {
            mEditReviewDescription.setText(savedInstanceState.getString(STATE_REVIEW_DESCRIPTION));
            mEditReviewRecommendedSides.setText(savedInstanceState.getString(STATE_REVIEW_RECOMMENDED_SIDES));
        }

        mEditReviewReadableDate = mRootView.findViewById(R.id.review_readable_date);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_READABLE_DATE)) {
            mEditReviewReadableDate.setText(savedInstanceState.getString(STATE_REVIEW_READABLE_DATE));
        } else if (!mEditValuesLoaded){ // TODO: needed variable
            mEditReviewReadableDate.setText(Utils.getCurrentLocalIso8601Time());
        }

        mEditReviewReadableDate.setOnClickListener(view -> {
            Date date;
            if (mEditReviewReadableDate.getText() != null) {
                date = Utils.getDate(mEditReviewReadableDate.getText().toString());
            } else {
                date = new Date();
            }
            new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                    .setListener(listener)
                    .setInitialDate(date)
                    .setIs24HourTime(true)
                    .build()
                    .show();
        });

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // usually hide it first
        hideMap();
        if (mMapFragment == null) {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        } else {
            mMapFragment.getMapAsync(this);
        }

        mEditReviewLocation = mRootView.findViewById(R.id.review_location);
        mLocationAdapter = new CompletionLocationAdapter(getActivity(), false);
        mEditReviewLocation.setThreshold(1);
        mEditReviewLocation.setAdapter(mLocationAdapter);
        mEditReviewLocationDescription = mRootView.findViewById(R.id.review_location_description);

        mEditReviewLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.v(LOG_TAG, "EditReviewLocation.afterTextChanged, mLocationId=" + mLocationId + ", mLastLocation=" + mLastLocation + ", mLocationParcelable=" + mLocationParcelable);
                mEditReviewLocation.setShowDropDown(false);
                if (mEditReviewLocationIgnoreTextChange) {
                    mEditReviewLocationIgnoreTextChange = false;
                } else {
                    mLocationId = null;
                    mLocationParcelable = null;
                }

            }
        });

        mEditReviewLocation.setOnItemClickListener((parent, view, position, id) -> {
            Cursor c = (Cursor) parent.getItemAtPosition(position);
            mLocationId = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_ID);
            Log.v(LOG_TAG, "onItemClick Cursor with locationId=" + mLocationId);

            if (mEditReviewLocationDescription.getText().toString().isEmpty()) {
                String description = c.getString(QueryColumns.LocationPart.CompletionQuery.COL_DESCRIPTION);
                if (description != null && !description.isEmpty()) {
                    mEditReviewLocationDescription.setText(description);
                    mEditReviewLocationDescription.setEnabled(false);
                }
            }

        });



        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_LOCATION)) {
            editReviewLocationIgnoreTextChange(savedInstanceState.getString(STATE_REVIEW_LOCATION));
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
        outState.putString(STATE_REVIEW_READABLE_DATE, mEditReviewReadableDate.getText().toString().trim());
        outState.putString(STATE_REVIEW_LOCATION, mEditReviewLocation.getText().toString().trim());

        if (mDrinkId != null) {
            outState.putString(STATE_DRINK_ID, mDrinkId);
            outState.putString(STATE_DRINK_NAME, mDrinkName);
            outState.putString(STATE_PRODUCER_NAME, mProducerName);
        } else {
            outState.putString(STATE_DRINK_NAME, mEditCompletionDrinkName.getText().toString().trim());
        }

        if (mUserId != null) {
            outState.putString(STATE_USER_ID, mUserId);
            outState.putString(STATE_USER_NAME, mUserName);
        } else {
            outState.putString(STATE_USER_NAME, mEditCompletionUserName.getText().toString().trim());
        }

        if (mContentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, mContentUri);
        }

        super.onSaveInstanceState(outState);
    }

    private void createToolbar() {
        Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
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
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_review_activity_preview, readableDrink));
            } else {
                ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_review_activity_preview,
                                readableDrink));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }


    private void updateToolbar() {
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
            new QueryDrinkTask(getActivity(), this).execute(drinkUri);
        } else if (requestCode == LOCATION_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            Uri locationUri = data.getData();
            new QueryLocationTask(getActivity(), this).execute(locationUri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");

        if (mDrinkId == null || mDrinkName == null) {
            Snackbar.make(mRootView, R.string.msg_choose_existing_drink, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (getString(R.string.pre_filled_rating).equals(mSpinnerRating.getSelectedItem().toString())) {
            Snackbar.make(mRootView, R.string.msg_rate_drink, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (!Utils.checkTimeFormat(mEditReviewReadableDate.getText().toString().trim())) {
            Snackbar.make(mRootView, R.string.msg_invalid_review_date, Snackbar.LENGTH_SHORT).show();
            return;
        } else {    //opt. async user check
            mUserName = mEditCompletionUserName.getText().toString();
            if (mUserName.length() < 1){
                Snackbar.make(mRootView, R.string.msg_no_review_without_user, Snackbar.LENGTH_SHORT).show();
            } else {
                if (mUserId == null) {
                    new ValidateUserTask(this, getActivity()).execute(mUserName);
                } else {
                    onUserValidated(mUserId);
                }
            }
        }
    }

    @Override
    public void onUserValidated(String userId) {
        Log.v(LOG_TAG, "onUserValidated, hashCode=" + this.hashCode() + ", " + "userId = [" + userId + "]");
        if (userId == null) {
            Snackbar.make(mRootView, getString(R.string.msg_user_not_found, mUserName),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }
        mUserId = userId;

        validateLocation();
    }

    private void validateLocation() {
        Log.v(LOG_TAG, "validateLocation, hashCode=" + this.hashCode() + ", " + "");

        // location validation
        // 1) real location with location id f.e. through nearby location -> fine
        if (mLocationId != null) {
            Log.v(LOG_TAG, "validateLocation with mLocationId - existing, hashCode=" + this.hashCode() + ", " + "");
            onLocationValidated();
            return;
        }
        // 2) new location with valid latLng and no location nearby, geocoded or not -> fine
        // TODO: still buggy? location with empty locationString was added in bayreuth => workaround added

        if (mLocationParcelable != null && mLocationParcelable.isGeocodeable()) {
            Log.v(LOG_TAG, "validateLocation with mLocationParcelable - geocode & new, hashCode=" + this.hashCode() + ", " + "");

            new InsertLocationTask(getActivity(),
                    mRootView, mLocationInput, this).execute(
                    DatabaseHelper.buildLocationValues(mLocationParcelable.getLocationId(),
                            mLocationParcelable.getInput(), mLocationParcelable.getLatitude(),
                            mLocationParcelable.getLongitude(), mLocationParcelable.getCountry(),
                            mLocationParcelable.getFormattedAddress(),
                            mEditReviewLocationDescription.getText().toString()));
            return;
        }

        // 4) no lastLocation (or resettet), and nothing found -> needs to search elsewhere...
        //  but: location is optional - so ask user if he want's
        // a) positive: search for location
        // b) negative: add location-input and geocode later
        // c) neutral:  ignore location and choose to not enter it for review (not recommended)

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // TODO: message from strings and 2 cases w/o add... (if ever happens...)
        builder.setTitle("location unclear")
                .setMessage("Do you want to SEARCH for the review-location, " +
                        "ADD a new locaction-stub based on your current input (and geocode it later) " +
                        "or IGNORE the review-location (not recommended)")
                .setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        useAddLocation();   // WORKS!
                    }
                });
        if (mLocationParcelable != null && mLocationParcelable.isGeocodeable()) {   // TODO: never happens?
            builder.setNeutralButton("add", (dialog, which) -> {
                addLocation();  // WORKS!
            });
        }

        builder.setNegativeButton("ignore", (dialog, which) -> {
            mLocationId = null; // LEFT JOIN instead of INNER JOIN - WORKS!
            insertReview();
        }).show();

    }

    private void addLocation() {
        mLocationInput = mEditReviewLocation.getText().toString();
        new InsertLocationTask(getActivity(),
                mRootView, mLocationInput, this).execute(
                DatabaseHelper.buildLocationValues(Utils.calcLocationId(mLocationInput),
                        mLocationInput, DatabaseContract.LocationEntry.INVALID_LAT_LNG,
                        DatabaseContract.LocationEntry.INVALID_LAT_LNG, null,
                        DatabaseContract.LocationEntry.GEOCODE_ME,
                        mEditReviewLocationDescription.getText().toString()));
    }

    @Override
    public void onInsertedLocation(Uri uri, String mEntryName, String locationId) {
        Log.v(LOG_TAG, "onInsertedLocation, hashCode=" + this.hashCode() + ", " + "uri = [" + uri + "], mEntryName = [" + mEntryName + "], locationId = [" + locationId + "]");
        mLocationId = locationId;
        onLocationValidated();
    }

    private void onLocationValidated() {
        Log.v(LOG_TAG, "onLocationValidated, hashCode=" + this.hashCode() + ", " + "");
        if (mContentUri != null) {
            updateReview();
        } else {
            insertReview();
        }
    }

    private void insertReview() {
        Log.v(LOG_TAG, "insertReview, hashCode=" + this.hashCode() + ", mLocationId=" + mLocationId);
        String date = mEditReviewReadableDate.getText().toString().trim();
        new InsertEntryTask(
                getActivity(),
                DatabaseContract.ReviewEntry.CONTENT_URI, mRootView, "Review for " + mDrinkName)
                .execute(DatabaseHelper.buildReviewValues(
                        Utils.calcReviewId(mUserId, mDrinkId, date),
                        mSpinnerRating.getSelectedItem().toString(),
                        mEditReviewDescription.getText().toString().trim(),
                        date,
                        mEditReviewRecommendedSides.getText().toString().trim(),
                        mDrinkId,
                        mLocationId,
                        mUserId));

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
            case R.id.add_user_button:
                validateNewUser();
                break;
            case R.id.search_review_location_button:
                useAddLocation();
                break;
            case R.id.help_review_rating_button:
                showHelp();
                break;
        }
    }


    private void showHelp() {
        // TODO: something better ;-)
        Toast.makeText(getActivity(),R.string.help_show_review_rating_help, Toast.LENGTH_LONG).show();
    }

    // user handling

    private void validateNewUser() {
        Log.v(LOG_TAG, "validateNewUser, hashCode=" + this.hashCode() + ", " + "");
        String userName = mEditCompletionUserName.getText().toString();
        if (userName.length() < 1) {
            Snackbar.make(mRootView, R.string.msg_no_username, Snackbar.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.msg_add_new_user, userName))
                .setCancelable(true)
                .setPositiveButton(R.string.add_user_button,
                        (dialog, which) -> createUser()
                )
                .setNegativeButton(R.string.do_not_add_button,
                        (dialog, which) -> Toast.makeText(getActivity(), getString(R.string.msg_user_not_added,
                                mEditCompletionUserName.getText().toString()), Toast.LENGTH_SHORT).show()
                );
        builder.show();
    }

    private void createUser() {
        Log.v(LOG_TAG, "createUser, hashCode=" + this.hashCode() + ", " + "");
        new QueryAndInsertUserTask(getActivity(), this)
                .execute(mEditCompletionUserName.getText().toString());
    }

    @Override
    public void onUserCreated(String userId, String userName) {
        mUserId = userId;
        mUserName = userName;

        mEditCompletionUserName.setText(mUserName);
        mEditCompletionUserName.dismissDropDown();
        Snackbar.make(mRootView, getString(R.string.msg_user_created, mUserName), Snackbar.LENGTH_SHORT).show();
    }

    // drink handling

    private void createDrink() {
        Log.v(LOG_TAG, "createDrink, hashCode=" + this.hashCode() + ", " + "");
        Bundle bundle = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // with shared element transition every transition is working ...
            bundle = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(),
                    mEditCompletionDrinkName, getString(R.string.shared_transition_add_drink_name)
            ).toBundle();

        }
        Intent intent = new Intent(getActivity(), AddDrinkActivity.class);
        intent.putExtra(AddDrinkActivity.PATTERN_EXTRA, mEditCompletionDrinkName.getText().toString().trim());
        startActivityForResult(intent, DRINK_ACTIVITY_REQUEST_CODE, bundle);
    }

    @Override
    public void onFoundDrink(int drink_Id, String drinkName, String drinkId, String producerName, LocationParcelable object) {
        Log.v(LOG_TAG, "onFoundDrink, hashCode=" + this.hashCode() + ", " + "drink_Id = [" + drink_Id + "], drinkName = [" + drinkName + "], drinkId = [" + drinkId + "], producerName = [" + producerName + "]");
        // NOT for both: (completionView and query after startActivityForResult)!!!
        mDrinkId = drinkId;
        mDrinkName = drinkName;
        mProducerName = producerName;
        mProducerLocationParcelable = object;

        mEditCompletionDrinkName.setText(mDrinkName);
        mEditCompletionDrinkName.dismissDropDown();

        updateToolbar();
        // should be invoked on found after add and hopefully in completionHandler... TODO!!!
        queryNearbyProducer();
    }


    // location via fragment handling

    private void useAddLocation() {
        Log.v(LOG_TAG, "useAddLocation, hashCode=" + this.hashCode() + ", " + "");
        Intent intent = new Intent(getActivity(), AddLocationActivity.class);
        intent.putExtra(AddLocationActivity.LOCATION_INPUT_EXTRA, mEditReviewLocation.getText().toString().trim());
        intent.putExtra(AddLocationActivity.LOCATION_DESCRIPTION_EXTRA, mEditReviewLocationDescription.getText().toString().trim());
        startActivityForResult(intent, LOCATION_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onFoundLocation(int location_id, LocationParcelable locationParcelable) {
        Log.v(LOG_TAG, "onFoundLocation, hashCode=" + this.hashCode() + ", " + "location_id = [" + location_id + "], locationParcelable = [" + locationParcelable + "]");
        mLocationParcelable = locationParcelable;
        mLocationId = mLocationParcelable.getLocationId();
        editReviewLocationIgnoreTextChange(mLocationParcelable.getFormattedAddress());
        mEditReviewLocation.dismissDropDown();
        if (mLocationParcelable.getDescription() != null && !mLocationParcelable.getDescription().isEmpty()){
            mEditReviewLocationDescription.setText(mLocationParcelable.getDescription());
            mEditReviewLocationDescription.setEnabled(false);
        }
    }


    // GoogleApi-lastLocation

    private void getCurrentLocation() {
        Log.v(LOG_TAG, "getCurrentLocation, hashCode=" + this.hashCode() + ", " + "");
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            handlePermission(); //request permissions - may help

            Log.v(LOG_TAG, "getCurrentLocation - no permission");
            Toast.makeText(AddReviewFragment.this.getActivity(), R.string.msg_no_location_access, Toast.LENGTH_SHORT).show();

            return;
        } else {
            Log.v(LOG_TAG, "getCurrentLocation - with permission");
        }
        if (CustomApplication.isGoogleApiClientConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(CustomApplication.getGoogleApiClient());
            queryNearbyProducer();
            return;
        } else {
            Log.e(LOG_TAG, "getCurrentLocation - googleApiClient not connected!, hashCode=" + this.hashCode() + ", " + "");
        }
        Toast.makeText(AddReviewFragment.this.getActivity(), R.string.msg_no_location_provided, Toast.LENGTH_SHORT).show();
    }

    private void queryNearbyProducer() {
        if (mDrinkId != null) {
            Log.v(LOG_TAG, "queryNearbyProducer, mDrinkId=" + mDrinkId + ", mLastLocation=" + mLastLocation + ", hashCode=" + this.hashCode() + ", " + "");
            if (mLastLocation != null) {

                // match latLng of drink.producerParcelable with mLastLocation
                if (mProducerLocationParcelable != null) {
                    if (Utils.calcDistance(new Double[]{mLastLocation.getLatitude(), mLastLocation.getLongitude()},
                            new Double[]{mProducerLocationParcelable.getLatitude(), mProducerLocationParcelable.getLongitude()})
                            < DatabaseContract.LocationEntry.DISTANCE_SQUARE_THRESHOLD){
                        Log.v(LOG_TAG, "queryNearbyProducer - at producer location, hashCode=" + this.hashCode() + ", " + "");

                        mLocationParcelable = mProducerLocationParcelable;
                        editReviewLocationIgnoreTextChange(mProducerLocationParcelable.getFormattedAddress());

                        if (mEditReviewLocationDescription.getText().toString().isEmpty() && mProducerLocationParcelable.getDescription() != null) {
                            mEditReviewLocationDescription.setText(mProducerLocationParcelable.getDescription());
                        }
                        showMap();
                        updateAndMoveToMarker();    // might work
                        return;
                    }
                }

                new QueryNearbyLocationsTask(getActivity(), this).execute(
                        new double[]{mLastLocation.getLatitude(), mLastLocation.getLongitude()});

            } else {
                // no need to check if mLastLocation failed or not yet ready - should always be ready...
                // so no latLong - missing permissions
                Toast.makeText(AddReviewFragment.this.getActivity(), R.string.msg_no_location_provided, Toast.LENGTH_SHORT).show();
            }
        } else {    // else: ignore (not valid without a drink)
            Log.v(LOG_TAG, "queryNearbyProducer without drinkId - do nothing");
        }
    }

    // successfully tested:
    //      - states: inputText | inputLocation | geocoded (T|L|G)
    //      producer    review  result
    //         T            T
    //         T            L
    //         T            G   works
    //         L            T
    //         L            L
    //         L            G
    //         G            T
    //         G            L
    //         G            G

    // TODO: test no nearby found - add new (geocoder / gps / nothing)

    @Override
    public void onNearbyLocationsFound(LocationParcelable[] locations) {
        Log.v(LOG_TAG, "onNearbyLocationsFound, hashCode=" + this.hashCode() + ", " + "locations = [" + locations + "]");

        MatrixCursor dataCursor = new MatrixCursor(QueryColumns.LocationPart.CompletionQuery.COLUMNS);
        for (LocationParcelable location : locations) {
            Log.v(LOG_TAG, "onNearbyLocationsFound, hashCode=" + this.hashCode() + ", " + "location= [" + location + "]");
            dataCursor.addRow(new Object[]{
                    location.getId(),
                    location.getLocationId(),
                    location.getInput(),
                    location.getFormattedAddress(),
                    location.getCountry(),
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getDescription()}
            );
        }

        // adapter worked, but was not shown

        mLocationAdapter.swapCursor(dataCursor);
        mLocationAdapter.notifyDataSetChanged();

        if (locations.length == 1) {
            mLocationParcelable = locations[0];
            Log.v(LOG_TAG, "onNearbyLocationsFound, hashCode=" + this.hashCode() + ", " + "location = [" + mLocationParcelable + "]");
            editReviewLocationIgnoreTextChange(mLocationParcelable.getFormattedAddress());
            if (mEditReviewLocationDescription.getText().toString().isEmpty() && mLocationParcelable.getDescription() != null) {
                mEditReviewLocationDescription.setText(mLocationParcelable.getDescription());
            }
            mLocationId = mLocationParcelable.getLocationId();
            showMap();
            updateAndMoveToMarker();
        } else if (locations.length > 1){
            mEditReviewLocation.setShowDropDown(true);
        }
    }

    @Override
    public void onNearbyLocationNotFound() {   // a.k.a. new location
        Log.v(LOG_TAG, "onNearbyLocationNotFound, mLastLocation=" + mLastLocation);
        if (mLastLocation != null) {
            startGeocodeServiceByPosition();
        }
    }


    // init-order:
    // ) getCurrentLocation worked?
    // .Y) isProducerLocationNearby?
    //   .Y) use it and producerName as description
    //   .N) getNearbyLocations?
    //     .Y) use other location and show in map => on change behave ... different :-p
    //     .N) store mLastLoc for saving
    // .N) let empty

    // onChange
    // searchLocationFormattedAndDescriptionWithPattern
    // found: list
    //  selected: show in map
    // not in list/not found: TODO: Find/AddLocationActivity... (not that often) - adds onSave like Drink


    // Geocoder

    private void startGeocodeServiceByPosition() {    // should not be called with an address string - extra activity
        Log.v(LOG_TAG, "startGeocodeServiceByPosition, hashCode=" + this.hashCode() + ", " + "");

        if (Utils.isNetworkUnavailable(getActivity())) {
            if (mLastLocation != null) {
                Log.v(LOG_TAG, "startGeocodeServiceByPosition - TODO: call geocoder later..., hashCode=" + this.hashCode() + ", " + "");
                mLocationParcelable = Utils.getLocationStubFromLastLocation(
                        mLastLocation, mEditReviewLocationDescription.getText().toString());
                editReviewLocationIgnoreTextChange(mLocationParcelable.getFormattedAddress());
            }

            Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        if (mResultReceiver == null) {
            Log.e(LOG_TAG, "startGeocodeServiceByPosition - no resultReceiver found!, hashCode=" + this.hashCode() + ", " + "");
            return;
        }
        Intent intent = new Intent(this.getActivity(), GeocodeIntentService.class);
        intent.putExtra(GeocodeIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(GeocodeIntentService.EXTRA_LOCATION_LATLNG, mLastLocation);
        getActivity().startService(intent);
    }


    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from GeocodeIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.v(LOG_TAG, "onReceiveResult, hashCode=" + this.hashCode() + ", " + "resultCode = [" + resultCode + "], resultData = [" + resultData + "]");
            // Display the address string or an error message sent from the intent service.
            if (getActivity() == null) {    // if changes happen while saving - ignore them...
                return;
            }
            if (GeocodeIntentService.SUCCESS_RESULT == resultCode) {
                if (resultData.containsKey(GeocodeIntentService.RESULT_ADDRESS_KEY)) {


                    Address address = resultData.getParcelable(GeocodeIntentService.RESULT_ADDRESS_KEY);
                    if (address == null) {
                        Log.e(LOG_TAG, "onReceiveResult: address result = NULL!!!");
                    } else {
                        mLocationParcelable = Utils.getLocationFromAddress(address,
                                Utils.getLocationInput(address.getLatitude(), address.getLongitude()),
                                null);
                        MatrixCursor dataCursor = new MatrixCursor(QueryColumns.LocationPart.CompletionQuery.COLUMNS);
                        dataCursor.addRow(new Object[]{
                                LocationParcelable.INVALID_ID,
                                mLocationParcelable.getLocationId(),
                                mLocationParcelable.getInput(),
                                mLocationParcelable.getFormattedAddress(),
                                mLocationParcelable.getCountry(),
                                mLocationParcelable.getLatitude(),
                                mLocationParcelable.getLongitude(),
                                ""} //?
                        );

                        mLocationAdapter.swapCursor(dataCursor);
                        mLocationAdapter.notifyDataSetChanged();
                        editReviewLocationIgnoreTextChange(mLocationParcelable.getFormattedAddress());
                        if (mEditReviewLocationDescription.getText().toString().isEmpty() && mLocationParcelable.getDescription() != null) {
                            mEditReviewLocationDescription.setText(mLocationParcelable.getDescription());
                        }

                        showMap();
                        updateAndMoveToMarker();
                    }

                } else {
                    Log.e(LOG_TAG, "onReceiveResult - SUCCESS without address - should never happen...");
                }

            } else {  // somehow failed

                if (Utils.isNetworkUnavailable(getActivity())) {
                    Toast.makeText(getActivity(), R.string.msg_service_network_not_available, Toast.LENGTH_LONG).show();
                    mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation, null);
                    editReviewLocationIgnoreTextChange(mLocationParcelable.getFormattedAddress());
                } else if (GeocodeIntentService.FAILURE_SERVICE_NOT_AVAILABLE == resultCode) {
                    Toast.makeText(getActivity(), R.string.msg_service_not_available, Toast.LENGTH_LONG).show();
                    mLocationParcelable = Utils.getLocationStubFromLastLocation(mLastLocation, null);
                    editReviewLocationIgnoreTextChange(mLocationParcelable.getFormattedAddress());
                } else {
                    int toastRes;
                    switch (resultCode) {
                        // ignore that one - TODO: just show it for "complete search" - how to detect...?
                        case GeocodeIntentService.FAILURE_INVALID_LAT_LONG_USED:
                            toastRes = R.string.msg_invalid_lat_long;
                            break;
                        case GeocodeIntentService.FAILURE_NO_LOCATION_DATA_PROVIDED:
                            toastRes = R.string.msg_no_location_provided;
                            break;
                        default:
                            toastRes = R.string.msg_no_location_generic;
                    }

                    Toast.makeText(getActivity(), toastRes, Toast.LENGTH_LONG).show();
                    mEditReviewLocation.setText("");
                }
            }
        }
    }


    // permissions

    private void handlePermission() {
        // src: FilePickerFragment
        // https://developer.android.com/training/permissions/requesting.html
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_LOCATION_PERMISSION_CODE == requestCode && permissions.length > 0) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0] ||
                    PackageManager.PERMISSION_GRANTED == grantResults[grantResults.length - 1]) { // at least one allowed
//                Log.v(LOG_TAG, "onRequestPermissionsResult - permission granted, try again!");
                getCurrentLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    // map parts

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateAndMoveToMarker();
    }

    private void hideMap() {
        if (mMapFragment != null && mMapFragment.getView() != null) {
            mMapFragment.getView().setVisibility(View.INVISIBLE);
        }

    }

    private void showMap() {
        if (mMapFragment != null && mMapFragment.getView() != null) {
            mMapFragment.getView().setVisibility(View.VISIBLE);
        }
    }

    private void focusOnMap() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "focusOnMap-run, hashCode=" + this.hashCode() + ", " + "");

                NestedScrollView scrollView = getActivity().findViewById(R.id.nested_scrollView);
                if (scrollView != null) {
                    Log.v(LOG_TAG, "focusOnMap-run, hashCode=" + this.hashCode() + ", position=" + scrollView.getBottom() + "");
                    scrollView.smoothScrollTo(0, scrollView.getBottom());
                } else {
                    Log.v(LOG_TAG, "focusOnMap-run, scrollView == null");
                }
            }
        });
    }

    private void updateAndMoveToMarker() {
        if (mMap != null && mLocationParcelable != null) {
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            LatLng latLng = new LatLng(mLocationParcelable.getLatitude(), mLocationParcelable.getLongitude());
            mCurrentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(mLocationParcelable.getCountry())
                    .snippet(mLocationParcelable.getFormattedAddress())
                    .draggable(false));
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }


    // edit - use loaders

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

                    mReview_Id = data.getInt(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW__ID);
                    mReviewId = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_ID);
                    // animation might work with separate textView in toolbar...
                    mProducerName = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_PRODUCER_NAME);

                    mDrinkName = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_DRINK_NAME);
                    mDrinkId = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_DRINK_ID);

                    mUserName = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_USER_NAME);
                    mUserId = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_USER_ID);

                    mLocationId = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_LOCATION_ID);

                    String reviewDesc = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_DESCRIPTION);

                    String rating = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_RATING);
                    String readableDate = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_READABLE_DATE);

                    String recommendedSides = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_RECOMMENDED_SIDES);

                    String location = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_LOCATION_FORMATTED);
                    String locationDescription = data.getString(QueryColumns.ReviewFragment.EditQuery.COL_REVIEW_LOCATION_DESCRIPTION);

                    mEditCompletionDrinkName.setText(mDrinkName);
                    mEditCompletionDrinkName.dismissDropDown();
                    mEditCompletionDrinkName.setTransitionName(
                            getString(R.string.shared_transition_review_drink) + mReview_Id);

                    mEditCompletionUserName.setText(mUserName);
                    mEditCompletionUserName.dismissDropDown();

                    mRatingPosition = mRatingAdapter.getPosition(rating);
                    setSpinner();
                    mEditReviewDescription.setText(reviewDesc);
                    mEditReviewRecommendedSides.setText(recommendedSides);

                    mEditReviewReadableDate.setText(readableDate);
                    editReviewLocationIgnoreTextChange(location);

                    mEditReviewLocation.dismissDropDown();
                    mEditReviewLocationDescription.setText(locationDescription);
                    mEditValuesLoaded = true;
                    updateToolbar();

                    resumeActivityEnterTransition();    // from edit
                }
                break;
            default:
                Log.w(LOG_TAG, "onLoadFinished - other loader?, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");
        }
    }

    private void editReviewLocationIgnoreTextChange(String location) {
        mEditReviewLocationIgnoreTextChange = true;
        mEditReviewLocation.setText(location);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // nothing
    }


    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");

        ((AddReviewActivity) Objects.requireNonNull(getActivity())).scheduleStartPostponedTransition(mEditReviewReadableDate);
    }


    // save for edit

    private void updateReview() {   //TODO: might depend on insertLocation...
        Uri singleEntryUri = Utils.calcSingleReviewUri(mContentUri);
        new UpdateEntryTask(getActivity(), singleEntryUri, "Review for " + mDrinkName, mRootView)
                .execute(DatabaseHelper.buildReviewValues(
                        mReviewId,
                        mSpinnerRating.getItemAtPosition(mRatingPosition).toString(),
                        mEditReviewDescription.getText().toString().trim(),
                        mEditReviewReadableDate.getText().toString().trim(),
                        mEditReviewRecommendedSides.getText().toString().trim(),
                        mDrinkId,
                        mLocationId,
                        mUserId));
    }

}
