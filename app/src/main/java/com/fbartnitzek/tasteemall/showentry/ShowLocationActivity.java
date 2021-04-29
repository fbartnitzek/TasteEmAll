package com.fbartnitzek.tasteemall.showentry;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.Utils;
import com.fbartnitzek.tasteemall.addentry.AddLocationActivity;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.tasks.DeleteEntryTask;

public class ShowLocationActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOW_LOCATION_TAG";
    private static final String LOG_TAG = ShowLocationActivity.class.getName();
    public static final String EXTRA_PRODUCER_URI = "EXTRA_PRODUCER_URI";
    private static final int EDIT_LOCATION_REQUEST = 4124;
    private Uri mContentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_location);

        if (findViewById(R.id.container_show_location_fragment) != null) {
            if (savedInstanceState != null) {
                return;
            }

            ShowLocationFragment fragment = new ShowLocationFragment();

            mContentUri = getIntent().getData();
            if (mContentUri != null) {
                Bundle args = new Bundle();
                args.putParcelable(EXTRA_PRODUCER_URI, mContentUri);
                fragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_show_location_fragment, fragment, FRAGMENT_TAG)
                    .commit();
        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found - should never happe! ");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    private ShowLocationFragment getFragment() {
        return (ShowLocationFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                supportFinishAfterTransition();
                finish();
                return true;
            case R.id.action_edit:
                startEditActivity();
                return true;
            case R.id.action_delete:
                startDelete();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startDelete() {
        Log.v(LOG_TAG, "startDelete, hashCode=" + this.hashCode() + ", " + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_really_delete_entry);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.delete_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri deleteUri = Utils.calcSingleLocationUri(mContentUri);
                        // TODO: check for foreign keys ... generic seems unlikely...?
//                        int id = DatabaseContract.getIdFromUri(deleteUri);
//                        Uri checkUri = DatabaseContract.ReviewEntry.buildUriWithDrinkId(id);
                        new DeleteEntryTask(
                                ShowLocationActivity.this,
                                DatabaseContract.LocationEntry.TABLE_NAME + "." + Location.FORMATTED_ADDRESS)
                                .execute(deleteUri);
                    }
                }
        );
        builder.setNegativeButton(
                R.string.keep_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ShowLocationActivity.this, "keeping entry", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startEditActivity() {
        Log.v(LOG_TAG, "startEditActivity, hashCode=" + this.hashCode() + ", " + "");
//        Toast.makeText(ShowLocationActivity.this, "TODO: edit", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AddLocationActivity.class);
        intent.setData(mContentUri);
        startActivityForResult(intent, EDIT_LOCATION_REQUEST);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            View drinkName = findViewById(R.id.drink_name);
//            int review_Id = DatabaseContract.getIdFromUri(mContentUri);
//
//            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
//                    this,
//                    new Pair<>(drinkName, //drinkName.getTransitionName())
//                            getString(R.string.shared_transition_review_drink) + review_Id)
//            ).toBundle();
//            startActivityForResult(intent, EDIT_REVIEW_REQUEST, bundle);
//        } else {
//            startActivityForResult(intent, EDIT_REVIEW_REQUEST);
//        }
    }
}
