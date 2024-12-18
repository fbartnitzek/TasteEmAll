package com.fbartnitzek.tasteemall.showentry;


import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowDrinkFragment extends ShowBaseFragment implements View.OnClickListener {

    private static final String LOG_TAG = ShowDrinkFragment.class.getName();

    private static final int SHOW_DRINK_LOADER_ID = 21;

    private TextView mProducerLabelView;
    private TextView mProducerNameView;
    private TextView mProducerNameLabelView;
    private TextView mProducerLocationView;
    private TextView mProducerLocationCountryView;

    private TextView mDrinkLabelView;
    private TextView mDrinkNameView;
    private TextView mDrinkNameLabelView;
    private TextView mDrinkTypeView;
    private TextView mDrinkStyleView;
    private TextView mDrinkSpecificsView;
    private TextView mDrinkIngredientsView;
    private Uri mUri;
    private View mRootView;
    private int mDrinkTypeIndex;
    private int mProducer_Id;

    public ShowDrinkFragment() {
        Log.v(LOG_TAG, "ShowDrinkFragment, hashCode=" + this.hashCode() + ", " + "");
    }

    @Override
    void calcCompleteUri() {    //if called with drink-only-id...
        if (mUri != null) {
            int id = DatabaseContract.getIdFromUri(mUri);
//            mUri = DatabaseContract.DrinkEntry.buildUriIncludingProducer(id);
            mUri = DatabaseContract.DrinkEntry.buildUriIncludingProducerAndLocation(id);
        }
    }

    //TODO: onSaveInstance? - seems to work out of the box ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_show_drink, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.e(LOG_TAG, "onCreateView without args - something went wrong..., hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        } else {
            if (args.containsKey(ShowDrinkActivity.EXTRA_DRINK_URI)) {
                mUri = args.getParcelable(ShowDrinkActivity.EXTRA_DRINK_URI);
                calcCompleteUri();
                Log.v(LOG_TAG, "onCreateView, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            }
        }

        mProducerLabelView = mRootView.findViewById(R.id.heading_producer_details);
        mProducerNameView = mRootView.findViewById(R.id.producer_name);
        mProducerNameLabelView = mRootView.findViewById(R.id.label_producer_name);
        mProducerLocationView = mRootView.findViewById(R.id.producer_location);
        mProducerLocationCountryView = mRootView.findViewById(R.id.producer_location_country);

        mDrinkLabelView = mRootView.findViewById(R.id.label_drink);
        mDrinkNameView = mRootView.findViewById(R.id.drink_name);
        mDrinkNameLabelView = mRootView.findViewById(R.id.label_drink_name);
        mDrinkTypeView = mRootView.findViewById(R.id.drink_type);
        mDrinkStyleView = mRootView.findViewById(R.id.drink_style);
        mDrinkSpecificsView = mRootView.findViewById(R.id.drink_specifics);
        mDrinkIngredientsView = mRootView.findViewById(R.id.drink_ingredients);

        createToolbar(mRootView, LOG_TAG);

        return mRootView;
    }


    @Override
    void updateToolbar() {
        ActionBar actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        if (actionBar != null) {

            String readableDrink = getString(Utils.getReadableDrinkNameId(getActivity(), mDrinkTypeIndex));
            String drinkName = mDrinkNameView.getText().toString();
            String producerName= mProducerNameView.getText().toString();
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                    getString(R.string.title_show_drink,
                            readableDrink, producerName, drinkName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onResume() {
        LoaderManager.getInstance(this).initLoader(SHOW_DRINK_LOADER_ID, null, this);
        super.onResume();
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mUri != null) {
            return new CursorLoader(
                    Objects.requireNonNull(getActivity()),
                    mUri,
                    QueryColumns.DrinkFragment.ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

            String drinkType = data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_TYPE);
            mDrinkTypeIndex = Utils.getDrinkTypeId(Objects.requireNonNull(getActivity()), drinkType);
            mDrinkTypeView.setText(drinkType);

            //createView slower than loader - NO, every time it's the wrong id...
            // onResume/ActivityCreated gets called after onCreateView!
            int readableProducerTypeIndex = Utils.getReadableProducerNameId(getActivity(), mDrinkTypeIndex);
            mProducerLabelView.setText(
                    getString(R.string.producer_details_label,
                            getString(readableProducerTypeIndex)));
            mProducerNameLabelView.setText(readableProducerTypeIndex);

            int readableDrinkTypeIndex = Utils.getReadableDrinkNameId(getActivity(), mDrinkTypeIndex);
            mDrinkLabelView.setText(
                    getString(R.string.drink_details_label,
                            getString(readableDrinkTypeIndex)));
            mDrinkNameLabelView.setText(readableDrinkTypeIndex);

            int drinkId = DatabaseContract.getIdFromUri(mUri);
            mDrinkNameView.setTransitionName(getString(R.string.shared_transition_drink_drink ) + drinkId);
            mProducerNameView.setTransitionName(getString(R.string.shared_transition_drink_producer ) + drinkId);

            mProducer_Id = data.getInt(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER__ID);
            String producerName = data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER_NAME);
            mProducerNameView.setText(producerName);
            mProducerNameView.setOnClickListener(this);
            mProducerLocationView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER_LOCATION));
            mProducerLocationCountryView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER_COUNTRY));

            String drinkName = data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_NAME);
            mDrinkNameView.setText(drinkName);

            mDrinkStyleView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_STYLE));
            mDrinkSpecificsView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_SPECIFICS));
            mDrinkIngredientsView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_INGREDIENTS));

            updateToolbar();

            // resume activity enter transition
            ((ShowDrinkActivity) getActivity()).scheduleStartPostponedTransition(mDrinkNameView);
        }
    }

    @Override
    public void onLoaderReset(@NotNull Loader<Cursor> loader) {
//        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    @Override
    public void updateFragment(Uri drinkUri) {
//        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "drinkUri = [" + drinkUri + "]");
        mUri = drinkUri;
        calcCompleteUri();
        LoaderManager.getInstance(this).restartLoader(SHOW_DRINK_LOADER_ID, null, this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.producer_name && mProducer_Id > -1) {  // open producer
//            Log.v(LOG_TAG, "onClick, producerName=" + mProducerNameView.getText().toString() + ", " + "producerId = [" + mProducer_Id + "]");
            Bundle bundle;
            bundle = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(),
                    mProducerNameView,
                    getString(R.string.shared_transition_producer_producer) + mProducer_Id
            ).toBundle();
            startActivity(
                    new Intent(getActivity(), ShowProducerActivity.class)
                            .setData(DatabaseContract.ProducerEntry.buildUri(mProducer_Id)), bundle);
        }
    }
}
