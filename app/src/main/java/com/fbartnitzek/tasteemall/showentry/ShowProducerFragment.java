package com.fbartnitzek.tasteemall.showentry;

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
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * A placeholder fragment containing a simple view.
 */
public class ShowProducerFragment extends ShowBaseFragment implements OnMapReadyCallback {

    private static final String LOG_TAG = ShowProducerFragment.class.getName();

    private static final int SHOW_PRODUCER_LOADER_ID = 42;

    private TextView mProducerNameView;
    private TextView mProducerLocationCountryView;
    private TextView mProducerLocationAddressView;
    private TextView mProducerDescriptionView;
    private TextView mProducerWebsiteView;
    private static GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private Uri mUri;
    private View mRootView;
    private LatLng mLatLng;

    public ShowProducerFragment() {
        Log.v(LOG_TAG, "ShowProducerFragment, " + "");
    }

    @Override
    void calcCompleteUri() {    //if called with producer-only-id...
        mUri = DatabaseContract.ProducerEntry.buildUriIncLocation(DatabaseContract.getIdFromUri(mUri));
        // still the same for now...
    }

    @Override
    public void updateFragment(Uri contentUri) {
        Log.v(LOG_TAG, "updateFragment, hashCode=" + this.hashCode() + ", " + "contentUri = [" + contentUri + "]");
        mUri = contentUri;
        calcCompleteUri();
        LoaderManager.getInstance(this).restartLoader(SHOW_PRODUCER_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_show_producer, container, false);

        Bundle args = getArguments();
        if (args == null){
            Log.v(LOG_TAG, "onCreateView, " + "without args - something went wrong...");
        } else {
            if (args.containsKey(ShowProducerActivity.EXTRA_PRODUCER_URI)) {
                mUri = args.getParcelable(ShowProducerActivity.EXTRA_PRODUCER_URI);
                calcCompleteUri();
//                Log.v(LOG_TAG, "onCreateView, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
            }
        }

        mProducerNameView = mRootView.findViewById(R.id.producer_name);
        mProducerLocationCountryView = mRootView.findViewById(R.id.producer_location_country);
        mProducerLocationAddressView = mRootView.findViewById(R.id.producer_location_address);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        } else {
            Log.e(LOG_TAG, "onCreateView, MapFragment not found...");
        }

        mProducerDescriptionView = mRootView.findViewById(R.id.producer_description);
        mProducerWebsiteView = mRootView.findViewById(R.id.producer_website);
        mProducerWebsiteView.setOnClickListener(v -> {
            String website = mProducerWebsiteView.getText().toString();
            Utils.openInBrowser(website, getActivity());
        });
        createToolbar(mRootView, LOG_TAG);

        return mRootView;
    }

    @Override
    public void onResume() {
        LoaderManager.getInstance(this).initLoader(SHOW_PRODUCER_LOADER_ID, null, this);
        super.onResume();
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader, mUri=" + mUri + ", hashCode=" + this.hashCode() + ", " + "id = [" + id + "], args = [" + args + "]");
        if (mUri != null) {
            return new CursorLoader(
                    Objects.requireNonNull(getActivity()),
                    mUri,
                    QueryColumns.ProducerFragment.ShowQuery.COLUMNS,
                    null,
                    null,
                    null);
        }
        // todo: what would be appropriate?
        return null;
    }

    @Override
    void updateToolbar() {
//        Log.v(LOG_TAG, "updateToolbar, hashCode=" + this.hashCode() + ", " + "");

        ActionBar actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        if (actionBar != null) {

            // later: when called from drink you may use the drinkType - now it's ... wrong
//            String readableProducerType = getString(Utils.getProducerName(mDrinkTypeIndex));
            String producerName= mProducerNameView.getText().toString();
            ((TextView) mRootView.findViewById(R.id.action_bar_title)).setText(
                    getString(R.string.title_show_producer, producerName));
        } else {
            Log.v(LOG_TAG, "updateToolbar - no toolbar found, hashCode=" + this.hashCode() + ", " + "");
        }
    }

    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "], data = [" + data + "]");

        if (data != null && data.moveToFirst()) {
            // variables not really needed - optimize later...
            String name = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_NAME);
            mProducerNameView.setText(name);
            String country = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_COUNTRY);
            String address = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_FORMATTED_ADDRESS);
            mProducerLocationCountryView.setText(country);
            mProducerLocationAddressView.setText(address);
            String website = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_WEBSITE);
            mProducerWebsiteView.setText(website);
            String description = data.getString(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_DESCRIPTION);
            mProducerDescriptionView.setText(description);

            mLatLng = new LatLng(
                    data.getDouble(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_LATITUDE),
                    data.getDouble(QueryColumns.ProducerFragment.ShowQuery.COL_PRODUCER_LONGITUDE));
            updateToolbar();

            ((ShowProducerActivity) Objects.requireNonNull(getActivity())).scheduleStartPostponedTransition(mProducerNameView);

            updateAndMoveToMarker();

//            Log.v(LOG_TAG, "onLoadFinished, name=" + name + ", location=" + location + ", " + "website= [" + website+ "], description= [" + description+ "]");
        }
    }

    @Override
    public void onLoaderReset(@NotNull Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset, hashCode=" + this.hashCode() + ", " + "loader = [" + loader + "]");
    }


    // map

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        updateAndMoveToMarker();
    }

    private void updateAndMoveToMarker() {
        Log.v(LOG_TAG, "updateAndMoveToMarker, mMap=" + mMap + ", mLatLng=" + mLatLng);
        if (mMap != null && mLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .title(mProducerNameView.getText().toString())
                    .snippet(mProducerLocationAddressView.getText().toString())
                    .draggable(false));
            mMap.animateCamera(
                    CameraUpdateFactory
                            .newCameraPosition(new CameraPosition.Builder()
                                    .target(mLatLng)
                                    .zoom(9)
                                    .build()),
                    2000, null);
        }
    }
}
