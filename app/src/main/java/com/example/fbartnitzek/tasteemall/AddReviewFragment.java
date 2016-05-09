package com.example.fbartnitzek.tasteemall;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.example.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.example.fbartnitzek.tasteemall.tasks.QueryDrinkTask;
import com.example.fbartnitzek.tasteemall.ui.OnTouchHideKeyboardListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddReviewFragment extends Fragment implements CompletionDrinkAdapter.CompletionDrinkAdapterSelectionHandler, View.OnClickListener, QueryDrinkTask.QueryDrinkFoundHandler {

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
    private static final int DRINK_ACTIVITY_REQUEST_CODE = 999;
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

    public AddReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CONTENT_URI)) {
            mContentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mContentUri != null) {
            Log.v(LOG_TAG, "onActivityCreated with contentUri, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            // TODO - initLoader & co
        } else {
            Log.v(LOG_TAG, "onActivityCreated without contentUri, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: save button (and therefore toolbar) should always be visible...
    // tuning... later

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_review, container, false);

        mEditCompletionDrinkName = (AutoCompleteTextView) mRootView.findViewById(R.id.drink_name);

        createToolbar();

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(STATE_DRINK_NAME)){   //just typed some letters
                //TODO: should it display drinkName (producerName)?
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
        // TODO: spinner with invalid start-text...?
        // try later: http://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
        String[] reviewRatings = getActivity().getResources().getStringArray(R.array.pref_rating_values);
        mRatingAdapter = new CustomSpinnerAdapter(getActivity(),
                new ArrayList<>(Arrays.asList(reviewRatings)), R.layout.spinner_small_row);
        mSpinnerRating.setAdapter(mRatingAdapter);
//        mRatingAdapter = new ArrayAdapter<>(
//                getActivity(),
//                android.R.layout.simple_list_item_1,
//                reviewRatings);
//
//        mRatingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinnerRating.setAdapter(mRatingAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_REVIEW_RATING_POSITION)) {
            mRatingPosition = savedInstanceState.getInt(STATE_REVIEW_RATING_POSITION);
            if (mRatingPosition> -1) {
                mSpinnerRating.setSelection(mRatingPosition);
                mSpinnerRating.clearFocus();    //TODO: needed?
            }
        }
        mSpinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRatingPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // TODO: might be buggy...
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
            // TODO init with preferences
            mEditReviewUser.setText("user1");
        }

        // TODO: onClick some calendar-usage
        // later, maybe this one: https://github.com/roomorama/Caldroid
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
            // TODO init with lastLocation
            mEditReviewLocation.setText("here");
        }

        return mRootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "onSaveInstanceState, hashCode=" + this.hashCode() + ", " + "outState = [" + outState + "]");
        outState.putInt(STATE_REVIEW_RATING_POSITION, mRatingPosition); //rating
        outState.putString(STATE_REVIEW_DESCRIPTION, mEditReviewDescription.getText().toString());
        outState.putString(STATE_REVIEW_RECOMMENDED_SIDES, mEditReviewRecommendedSides.getText().toString());
        outState.putString(STATE_REVIEW_USER, mEditReviewUser.getText().toString());
        outState.putString(STATE_REVIEW_READABLE_DATE, mEditReviewReadableDate.getText().toString());
        outState.putString(STATE_REVIEW_LOCATION, mEditReviewLocation.getText().toString());

        if (mDrinkId != null) {
            outState.putString(STATE_DRINK_ID, mDrinkId);
            outState.putInt(STATE_DRINK__ID, mDrink_Id);
            outState.putString(STATE_DRINK_NAME, mDrinkName);
            outState.putString(STATE_PRODUCER_NAME, mProducerName);
        } else {
            outState.putString(STATE_DRINK_NAME, mEditCompletionDrinkName.getText().toString());
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
            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableDrink = getString(Utils.getDrinkName(drinkType));

            if (mContentUri != null) {
                Log.v(LOG_TAG, "createToolbar with contentUri, hashCode=" + this.hashCode() + ", " + "");
                supportActionBar.setTitle(
                        getString(R.string.title_edit_review_activity_preview,
                                readableDrink));
            } else {
                Log.v(LOG_TAG, "createToolbar without contentUri, hashCode=" + this.hashCode() + ", " + "");
                supportActionBar.setTitle(
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

        if (activity.getSupportActionBar()!= null) {
            if (mContentUri != null) {  // edit
                activity.getSupportActionBar().setTitle(
                        getString(R.string.title_edit_review_activity,
                                mDrinkName, mProducerName));
            } else {    // add
                if (mDrinkId != null) { //known names
                    activity.getSupportActionBar().setTitle(
                            getString(R.string.title_add_review_activity,
                                    mDrinkName, mProducerName));
                }
                // do nothing if only generic names are known
            }
        } else {
            Log.e(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
//            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "drinkNameOrDrinkType = [" + drinkNameOrDrinkType + "], producerName = [" + producerName + "]");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        // TODO: update drink and update toolbar (drinkType may vary...)

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
        // TODO insert or update

        if (mDrinkId == null || mDrinkName == null) {
            //TODO: hardcoded strings!
            Snackbar.make(mRootView, "choose an existing drink first...", Snackbar.LENGTH_SHORT).show();
            return;
        } else if (getString(R.string.pre_filled_rating).equals(mSpinnerRating.getSelectedItem().toString())) {
            Snackbar.make(mRootView, "rate your drink first...", Snackbar.LENGTH_SHORT).show();
            return;
        } else if ("".equals(mEditReviewUser.getText().toString())) {
            Snackbar.make(mRootView, "no rating without username...", Snackbar.LENGTH_SHORT).show();
            return;
        } else if ("".equals(mEditReviewReadableDate.getText().toString())) {
            Snackbar.make(mRootView, "no rating without date...", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mContentUri != null) {
            updateReview();
        } else {
            insertReview();
        }

        Snackbar.make(mRootView, "TODO: save something", Snackbar.LENGTH_SHORT).show();
    }

    private void updateReview() {
        // TODO
    }

    private void insertReview() {
        String userName = mEditReviewUser.getText().toString();
        String date = mEditReviewReadableDate.getText().toString();
        new InsertEntryTask(
                getActivity(),
                DatabaseContract.ReviewEntry.CONTENT_URI, mRootView, "Review for " + mDrinkName)
                .execute(DatabaseHelper.buildReviewValues(
                        Utils.calcReviewId(userName, mDrinkId, date),
                        mSpinnerRating.getSelectedItem().toString(),
                        mEditReviewDescription.getText().toString(),
                        date,
                        mEditReviewRecommendedSides.getText().toString(),
                        mDrinkId,
                        mEditReviewLocation.getText().toString(),
                        userName));

    }

    public void setmContentUri(Uri mContentUri) {
        this.mContentUri = mContentUri;
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
        Intent intent = new Intent(getActivity(), AddDrinkActivity.class);
        intent.putExtra(AddDrinkActivity.PATTERN_EXTRA, mEditCompletionDrinkName.getText().toString());
        startActivityForResult(intent, DRINK_ACTIVITY_REQUEST_CODE);
    }



    private void showHelp() {
        // TODO: something better ;-)
        Toast.makeText(getActivity(),
                "++ really good, + buy again, 0 neither good nor bad, - don't buy again, -- spit & spill it out, (the remaining for the uncertain ones :-p)",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFoundDrink(int drink_Id, String drinkName, String drinkId, String producerName) {
        Log.v(LOG_TAG, "onFoundDrink, hashCode=" + this.hashCode() + ", " + "drink_Id = [" + drink_Id + "], drinkName = [" + drinkName + "], drinkId = [" + drinkId + "], producerName = [" + producerName + "]");
        // NOT for both: (completionView and query after startActivityForResult)!!!
        mDrink_Id = drink_Id;
        mDrinkId = drinkId;
        mDrinkName = drinkName;
        mProducerName = producerName;

        // TODO: might as well be set with producer - drinkName
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
}
