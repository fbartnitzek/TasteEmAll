package com.fbartnitzek.tasteemall.showentry;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.tasks.QueryColumns;


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
            mUri = DatabaseContract.DrinkEntry.buildUriIncludingProducer(id);
        }
    }

    //TODO: onSaveInstance?

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

        mProducerLabelView = (TextView) mRootView.findViewById(R.id.heading_choose_drink);
        mProducerNameView = (TextView) mRootView.findViewById(R.id.producer_name);
        mProducerNameLabelView = (TextView) mRootView.findViewById(R.id.label_producer_name);
        mProducerLocationView = (TextView) mRootView.findViewById(R.id.producer_location);

        mDrinkLabelView = (TextView) mRootView.findViewById(R.id.label_drink);
        mDrinkNameView = (TextView) mRootView.findViewById(R.id.drink_name);
        mDrinkNameLabelView = (TextView) mRootView.findViewById(R.id.label_drink_name);
        mDrinkTypeView = (TextView) mRootView.findViewById(R.id.drink_type);
        mDrinkStyleView = (TextView) mRootView.findViewById(R.id.drink_style);
        mDrinkSpecificsView = (TextView) mRootView.findViewById(R.id.drink_specifics);
        mDrinkIngredientsView = (TextView) mRootView.findViewById(R.id.drink_ingredients);

        createToolbar(mRootView, LOG_TAG);

        return mRootView;
    }


    @Override
    void updateToolbar() {
        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {


//            String readableDrink = getString(Utils.getRgetDrinkName(mDrinkTypeIndex));
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
        getLoaderManager().initLoader(SHOW_DRINK_LOADER_ID, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    QueryColumns.DrinkFragment.ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            String drinkType = data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_TYPE);
//            mDrinkTypeIndex = Utils.getDrinkTypeIndex(getActivity(), drinkType);
            mDrinkTypeIndex = Utils.getDrinkTypeId(getActivity(), drinkType);
            mDrinkTypeView.setText(drinkType);

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

            mProducer_Id = data.getInt(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER_ID);
            String producerName = data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER_NAME);
            mProducerNameView.setText(producerName);
            mProducerNameView.setOnClickListener(this);
            mProducerLocationView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_PRODUCER_LOCATION));

            String drinkName = data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_NAME);
            mDrinkNameView.setText(drinkName);

            mDrinkStyleView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_STYLE));
            mDrinkSpecificsView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_SPECIFICS));
            mDrinkIngredientsView.setText(data.getString(QueryColumns.DrinkFragment.ShowQuery.COL_DRINK_INGREDIENTS));

            updateToolbar();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }

    @Override
    public void updateFragment(Uri drinkUri) {
        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "drinkUri = [" + drinkUri + "]");
        mUri = drinkUri;
        calcCompleteUri();
        getLoaderManager().restartLoader(SHOW_DRINK_LOADER_ID, null, this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.producer_name && mProducer_Id > -1) {  // open producer
            startActivity(
                    new Intent(getActivity(), ShowProducerActivity.class)
                            .setData(DatabaseContract.ProducerEntry.buildUri(mProducer_Id)));
        }
    }
}
