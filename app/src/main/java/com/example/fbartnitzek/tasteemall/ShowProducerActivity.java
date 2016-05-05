package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ShowProducerActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOW_PRODUCER_TAG";
    private static final String LOG_TAG = ShowProducerActivity.class.getName();
    public static final String EXTRA_PRODUCER_URI = "EXTRA_PRODUCER_URI";
    private static final int EDIT_PRODUCER_REQUEST = 444;
    private Uri mContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_producer);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            ShowProducerFragment fragment = new ShowProducerFragment();

            mContentUri = getIntent().getData();
            if (mContentUri != null) {
                Bundle args = new Bundle();
                args.putParcelable(EXTRA_PRODUCER_URI, mContentUri);
                fragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit();

        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    private ShowProducerFragment getFragment() {
        return (ShowProducerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Log.v(LOG_TAG, "onOptionsItemSelected - action_edit, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
                Snackbar.make(findViewById(R.id.fragment_container),
                        "TODO: edit...", Snackbar.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, AddProducerActivity.class);
//                intent.setData(mContentUri);
//                startActivityForResult(intent, EDIT_PRODUCER_REQUEST);
                break;
            default:
                Log.v(LOG_TAG, "onOptionsItemSelected - pressed something unusual..., hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == EDIT_PRODUCER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri producerUri = data.getData();
            // TODO: update fragment
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
