package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ShowDrinkActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "SHOw_DRINK_TAG";
    private static final String LOG_TAG = ShowDrinkActivity.class.getName();
    public static final String EXTRA_DRINK_URI = "EXTRA_DRINK_URI";
    private static final int EDIT_DRINK_REQUEST = 432;
    private Uri mContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_show_drink);

        // explicitly add fragment with pattern
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {   // no overlapping fragments on return
                Log.v(LOG_TAG, "onCreate - saved state = do nothing..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
                return;
            }

            ShowDrinkFragment fragment = new ShowDrinkFragment();

            mContentUri = getIntent().getData();
            if (mContentUri != null) {
                Bundle args = new Bundle();
                args.putParcelable(EXTRA_DRINK_URI, mContentUri);
                fragment.setArguments(args);
            } else {
                Log.e(LOG_TAG, "onCreate - without intentData???, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit();
        } else {
            Log.e(LOG_TAG, "onCreate - no rootView container found, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }

        //init toolbar in fragment
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    private ShowDrinkFragment getFragment(){
        return (ShowDrinkFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Log.v(LOG_TAG, "onOptionsItemSelected - action_edit, hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");

                Intent intent = new Intent(this, AddDrinkActivity.class);
                intent.setData(mContentUri);
                startActivityForResult(intent, EDIT_DRINK_REQUEST);

                break;
            default:
                Log.e(LOG_TAG, "onOptionsItemSelected - pressed something unusual..., hashCode=" + this.hashCode() + ", " + "item = [" + item + "]");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == EDIT_DRINK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri drinkUri = data.getData();
            ShowDrinkFragment fragment = getFragment();
            if (fragment != null && drinkUri != null) {
                fragment.updateFragment(drinkUri);
            } else {
                Log.v(LOG_TAG, "onActivityResult - data or fragment missing..., hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
