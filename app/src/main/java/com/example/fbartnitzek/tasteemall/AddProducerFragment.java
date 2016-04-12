package com.example.fbartnitzek.tasteemall;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fbartnitzek.tasteemall.data.DatabaseContract;
import com.example.fbartnitzek.tasteemall.data.DatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link AddProducerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProducerFragment extends Fragment implements View.OnClickListener {

    private static EditText mEditProducerName;
    private static EditText mEditProducerLocation;
    private static EditText mEditProducerWebsite;
    private static EditText mEditProducerDescription;
    private static View mRootView;

    //TODO: remaining attributes...

    private static final String LOG_TAG = AddProducerFragment.class.getName();

    public AddProducerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AddProducerFragment.
     */

    public static AddProducerFragment newInstance() {
        return new AddProducerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: restore savedInstanceState with json/parcelable/cursor
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "onCreateView, hashCode=" + this.hashCode() + ", " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        mRootView = inflater.inflate(R.layout.fragment_add_producer, container, false);

        mRootView.findViewById(R.id.fab_save).setOnClickListener(this);
        mEditProducerName = (EditText) mRootView.findViewById(R.id.producer_name);
        mEditProducerLocation = (EditText) mRootView.findViewById(R.id.producer_location);
        mEditProducerWebsite = (EditText) mRootView.findViewById(R.id.producer_website);
        mEditProducerDescription = (EditText) mRootView.findViewById(R.id.producer_description);

//        android.app.ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        return mRootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_save) {
            insertData();
        }
    }
// TODO: on save: close keyboard and go to parent activity/fragment
    private void insertData() {
        //TODO: remaining producer attributes
        Uri insertProducerUri = getActivity().getContentResolver().insert(
                DatabaseContract.ProducerEntry.CONTENT_URI,
                DatabaseHelper.buildProducerValues(
                        "producer_" + mEditProducerName.getText().toString(),
                        mEditProducerName.getText().toString(),
                        mEditProducerDescription.getText().toString(),
                        mEditProducerWebsite.getText().toString(),
                        mEditProducerLocation.getText().toString())
        );

        Snackbar.make(mRootView, "Created new entry " + mEditProducerName.getText().toString(),
                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}