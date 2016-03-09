package com.example.fbartnitzek.tasteemall;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProducerActivityFragment extends Fragment {

    public static final String PATTERN_NAME = "pattern";
    public static final String BREWERY_URI = "brewery_uri";
    private static final String LOG_TAG = ProducerActivityFragment.class.getName();

    static final int COL_QUERY_LOCATION__ID = 0;
    static final int COL_QUERY_LOCATION_ID = 1;
    static final int COL_QUERY_FORMATTED_ADDRESS = 2;

    public ProducerActivityFragment() {
        Log.v(LOG_TAG, "ProducerActivityFragment, " + "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView, " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        View rootView = inflater.inflate(R.layout.fragment_producer, container, false);
        final TextView breweryNameView = (TextView) rootView.findViewById(R.id.brewery_name);
        Bundle args = getArguments();
        if (args == null){
            Log.v(LOG_TAG, "onCreateView, " + "without args - something went wrong...");
        } else {
            if (args.containsKey(PATTERN_NAME)) {
                Log.v(LOG_TAG, "onCreateView, " + "with PATTERN_NAME - create new");
                breweryNameView.setText(args.getCharSequence(PATTERN_NAME, "nothing found..."));
            } else if (args.containsKey(BREWERY_URI)){
                Parcelable uri = args.getParcelable(BREWERY_URI);
                if (uri != null) {
                    Log.v(LOG_TAG, "onCreateView, " + "with BREWERY_URI not null - load existing");
                    breweryNameView.setText(uri.toString());
                } else {
                    Log.v(LOG_TAG, "onCreateView, " + "with BREWERY_URI null - should not happen...");
                    breweryNameView.setText("null...");
                }
            }
        }

        Button saveButton = (Button) rootView.findViewById(R.id.brewery_button_save);
        final TextView locationView = (TextView) rootView.findViewById(R.id.brewery_location);
        final TextView introducedView = (TextView) rootView.findViewById(R.id.brewery_introduced);
        final TextView websiteView = (TextView) rootView.findViewById(R.id.brewery_website);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO: in Task as transaction...
//                String breweryLocation = Utils.queryLocation(locationView.getText().toString());
//                Uri locationUri = getActivity().getContentResolver().insert(
//                        LocationEntry.CONTENT_URI, breweryLocation);
//                Log.v(LOG_TAG, "onClick, " + "locationUri = [" + locationUri + "]");
//                String id = LocationEntry.getIdString(locationUri);
//                String[] idArgs = {id + "%"};
////                cursor = db.query(LocationEntry.TABLE_NAME, projection, LocationEntry._ID + " = ? ",
////                        idArgs, null, null, sortOrder);
//                Cursor cursor = null;
//                // TODO: ContentProvider - see footballscores...
//                try {
//                    cursor = getActivity().getContentResolver().query(
//                            LocationEntry.CONTENT_URI,
//                            LOCATION_QUERY_COLUMNS, LocationEntry._ID + " = ? ", idArgs, null);
//                    // might that also work...
////                Cursor cursor = getActivity().getContentResolver().query(locationUri,
////                        LOCATION_QUERY_COLUMNS, null, null, null);
//                    if (cursor.getCount() > 0) {
//                        ContentValues brewery = DatabaseHelper.buildProducerValues(
//                                "brewery_" + breweryNameView.getText(),
//                                breweryNameView.getText().toString(),
//                                introducedView.getText().toString(),
//                                websiteView.getText().toString(),
//                                cursor.getString(COL_QUERY_LOCATION_ID));
//                        //                Producer brewery = new Producer(
//                        //                        "brewery_" + breweryNameView.getText(),
//                        //                        introducedView.getText().toString(),
//                        //                        breweryLocation,
//                        //                        breweryNameView.getText().toString(),
//                        //                        websiteView.getText().toString());
//
//                        Uri breweryUri = getActivity().getContentResolver().insert(
//                                DatabaseContract.ProducerEntry.CONTENT_URI, brewery);
//                        Log.v(LOG_TAG, "onClick, " + "all should be inserted...");
//
//                    }
//                }finally {
//                    cursor.close();
//                }
//
//            }
//        });


        return rootView;
    }
}
