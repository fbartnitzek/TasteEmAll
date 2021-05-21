package com.fbartnitzek.tasteemall.addentry;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.DatabaseHelper;
import com.fbartnitzek.tasteemall.location.AddressData;
import com.fbartnitzek.tasteemall.parcelable.LocationParcelable;
import com.fbartnitzek.tasteemall.tasks.InsertEntryTask;
import com.fbartnitzek.tasteemall.tasks.UpdateEntryTask;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.fbartnitzek.tasteemall.data.QueryColumns.ProducerFragment.ShowQuery;

public class AddProducerFragment2 extends GeocoderFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_PRODUCER_LOADER_ID = 12345;
    private static final String STATE_CONTENT_URI = "STATE_CONTENT_URI";
    private static final String STATE_ORIGINAL_LOCATION_INPUT = "STATE_ORIGINAL_LOCATION_INPUT";
    private static final String STATE_PRODUCER_NAME = "STATE_PRODUCER_NAME";
    private static final String STATE_PRODUCER_WEBSITE = "STATE_PRODUCER_WEBSITE";
    private static final String STATE_PRODUCER_DESCRIPTION = "STATE_PRODUCER_DESCRIPTION";
    private static final String STATE_LOCATION = "STATE_LOCATION";

    private static final String LOG_TAG = AddProducerFragment2.class.getName();

    private EditText editProducerName;
    private EditText editProducerWebsite;
    private EditText editProducerDescription;

    private String producerName;
    private Uri contentUri;
    private String originalLocationInput;

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putString(STATE_PRODUCER_NAME, editProducerName.getText().toString().trim().trim());
        outState.putString(STATE_LOCATION, editLocation.getText().toString().trim());
        outState.putString(STATE_PRODUCER_WEBSITE, editProducerWebsite.getText().toString().trim());
        outState.putString(STATE_PRODUCER_DESCRIPTION, editProducerDescription.getText().toString().trim());

        if (contentUri != null) {
            outState.putParcelable(STATE_CONTENT_URI, contentUri);
        }
        if (originalLocationInput != null) {
            outState.putString(STATE_ORIGINAL_LOCATION_INPUT, originalLocationInput);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CONTENT_URI)) {
                contentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI);
            }
            if (savedInstanceState.containsKey(STATE_ORIGINAL_LOCATION_INPUT)) {
                originalLocationInput = savedInstanceState.getString(STATE_ORIGINAL_LOCATION_INPUT);
            }
        }
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    // todo: other location-fragments...

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        rootView = inflater.inflate(R.layout.fragment_add_producer, container, false);

        super.onCreateView(inflater, container, savedInstanceState);

        editProducerName = rootView.findViewById(R.id.producer_name);
        editProducerName.setTransitionName(getString(R.string.shared_transition_add_producer_name));

        editLocation = rootView.findViewById(R.id.producer_location);
        editLocation.addTextChangedListener(this);

        rootView.findViewById(R.id.location_here_button).setOnClickListener(v -> getCurrentLocation());
        editProducerWebsite = rootView.findViewById(R.id.producer_website);
        editProducerDescription = rootView.findViewById(R.id.producer_description);

        if (savedInstanceState != null) {
            editProducerName.setText(savedInstanceState.getString(STATE_PRODUCER_NAME));
            editProducerWebsite.setText(savedInstanceState.getString(STATE_PRODUCER_WEBSITE));
            editProducerDescription.setText(savedInstanceState.getString(STATE_PRODUCER_DESCRIPTION));
            if (savedInstanceState.containsKey(STATE_LOCATION)) {
                editLocation.setText(savedInstanceState.getString(STATE_LOCATION));
            }
        }

        createToolbar();
        if (contentUri == null) {
            resumeActivityEnterTransition();    // from add
        }
        return rootView;
    }

    private void createToolbar() {
        Log.v(LOG_TAG, "createToolbar, hashCode=" + this.hashCode() + ", " + "");
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Objects.requireNonNull(activity).setSupportActionBar(toolbar);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar == null) {
                Log.v(LOG_TAG, "createToolbar - no supportActionBar found..., hashCode=" + this.hashCode() + ", " + "");
                return;
            }

            supportActionBar.setDisplayHomeAsUpEnabled(true);  //false: not visible anymore
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(R.layout.action_bar_title_layout);
            supportActionBar.setDisplayShowCustomEnabled(true);

            int drinkType = Utils.getDrinkTypeIndexFromSharedPrefs(activity, false);
            String readableProducer = getString(Utils.getReadableProducerNameId(
                    getActivity(), drinkType));
            if (contentUri != null) {
                ((TextView) rootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_edit_producer_activity_preview,
                                readableProducer));
            } else {
                ((TextView) rootView.findViewById(R.id.action_bar_title)).setText(
                        getString(R.string.title_add_drink_activity,
                                readableProducer));
            }

        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    private void updateToolbar(String producerName) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            ((TextView) rootView.findViewById(R.id.action_bar_title))
                    .setText(getString(R.string.title_edit_producer_activity, producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "producerName = [" + producerName + "]");
        }
    }

    private void resumeActivityEnterTransition() {
        Log.v(LOG_TAG, "resumeActivityEnterTransition, hashCode=" + this.hashCode() + ", " + "");
        ((AddProducerActivity) Objects.requireNonNull(getActivity()))
                .scheduleStartPostponedTransition(editProducerName);
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setContentUri(Uri contentUri) {
        Log.v(LOG_TAG, "set contentUri to " + contentUri);
        this.contentUri = contentUri;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (contentUri != null) {
            LoaderManager.getInstance(this).initLoader(EDIT_PRODUCER_LOADER_ID, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    @NonNull
    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, contentUri=" + contentUri + ", hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (contentUri != null && getActivity() != null) {
            return new CursorLoader(
                    getActivity(),
                    contentUri,
                    ShowQuery.COLUMNS,
                    null,
                    null,
                    null
            );
        } else {
            Log.wtf(LOG_TAG, "could not create loader!, contentUri=" + contentUri);
            // todo: what would be appropriate?
            return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...

            String name = data.getString(ShowQuery.COL_PRODUCER_NAME);
            editProducerName.setText(name);
            editProducerName.setTransitionName(
                    getString(R.string.shared_transition_producer_producer)
                            + data.getInt(ShowQuery.COL_PRODUCER__ID));

            originalLocationInput = data.getString(ShowQuery.COL_PRODUCER_INPUT);
            String formatted = data.getString(ShowQuery.COL_PRODUCER_FORMATTED_ADDRESS);
            String countryName = data.getString(ShowQuery.COL_PRODUCER_COUNTRY);
            Optional<String> countryCode = Optional.empty();
            if (countryName != null) {
                Log.v(LOG_TAG, "countryName of loaded entry: " + countryName);
                countryCode = Arrays.stream(Locale.getISOCountries())
                        .filter(code -> countryName.equals(new Locale("", code).getDisplayCountry()))
                        .findFirst();
            } else {
                Log.v(LOG_TAG, "countryName of loaded entry is null!");
            }

            address = new AddressData(
                    data.getDouble(ShowQuery.COL_PRODUCER_LATITUDE),
                    data.getDouble(ShowQuery.COL_PRODUCER_LONGITUDE),
                    countryCode.orElse("NA"),
                    countryName,
                    data.getString(ShowQuery.COL_PRODUCER_FORMATTED_ADDRESS),
                    data.getString(ShowQuery.COL_PRODUCER_INPUT)
            );

            String website = data.getString(ShowQuery.COL_PRODUCER_WEBSITE);
            editProducerWebsite.setText(website);
            String description = data.getString(ShowQuery.COL_PRODUCER_DESCRIPTION);
            editProducerDescription.setText(description);

            if (!Utils.isNetworkUnavailable(Objects.requireNonNull(getActivity()))
                    && DatabaseContract.LocationEntry.GEOCODE_ME.equals(formatted)) {
                // geocode
                editLocation.setText(originalLocationInput);
            } else {
                skipNextGeocoding();
                editLocation.setText(address.getFormatted());
            }

            showValidPosition();
            updateToolbar(name);
            resumeActivityEnterTransition();    // from edit
        }
    }

    public void saveData() {
        Log.v(LOG_TAG, "saveData, hashCode=" + this.hashCode() + ", " + "");
        producerName = editProducerName.getText().toString().trim();

        //validate
        if (producerName.length() == 0) {
            Snackbar.make(rootView, R.string.msg_enter_producer_name, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (contentUri != null) { //update
            updateData();   // location exists
        } else { // insert
            insertData();
        }
    }

    private void insertData() {
        Log.v(LOG_TAG, "insertProducer, hashCode=" + this.hashCode() + ", " + "");
        LocationParcelable locationParcelable = createLocationParcelable();
        if (locationParcelable == null) {
            Log.e(LOG_TAG, "insertProducer without locationParcelable!!!");
            return;
        }
        if (locationParcelable.getInput() == null) {
            Snackbar.make(rootView, R.string.msg_enter_producer_location, Snackbar.LENGTH_SHORT).show();
            return;
        }
        new InsertEntryTask(
                getActivity(), DatabaseContract.ProducerEntry.CONTENT_URI, rootView, producerName)
                .execute(DatabaseHelper.buildProducerValues(
                        Utils.calcProducerId(producerName, locationParcelable.getInput()),
                        producerName,
                        editProducerDescription.getText().toString().trim(),
                        editProducerWebsite.getText().toString().trim(),
                        locationParcelable.getInput(),
                        locationParcelable.getLatitude(),
                        locationParcelable.getLongitude(),
                        locationParcelable.getCountry(),
                        locationParcelable.getFormattedAddress()
                ));
    }

    private void updateData() {
        Log.v(LOG_TAG, "updateData, hashCode=" + this.hashCode() + ", " + "");
        Uri singleProducerUri = Utils.calcSingleProducerUri(contentUri);
        if (address == null) {
            Toast.makeText(getActivity(), "no locationParcelable - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        } else if (originalLocationInput == null) {
            Toast.makeText(getActivity(), "no originalLocationInput - should never happen...", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.v(LOG_TAG, "updateData, address=" + address.toString());
        new UpdateEntryTask(getActivity(), singleProducerUri, producerName, rootView)
                .execute(DatabaseHelper.buildProducerUpdateValues(
                        producerName,
                        editProducerDescription.getText().toString().trim(),
                        editProducerWebsite.getText().toString().trim(),
                        originalLocationInput,
                        address.getLatitude(),
                        address.getLongitude(),
                        address.getCountryName(),
                        address.getFormatted()));
    }
}