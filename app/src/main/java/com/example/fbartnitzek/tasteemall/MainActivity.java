package com.example.fbartnitzek.tasteemall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.fbartnitzek.tasteemall.data.pojo.Drink;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int ADD_DRINK_REQUEST = 555;


    private Spinner mSpinnerType;
    private String mSearchPattern;
    private CustomSpinnerAdapter mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinnerType = (Spinner) findViewById(R.id.spinner_type);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            String drinkType = initSpinner();
            refreshFragmentLists(mSearchPattern, drinkType);
            if (true) {
                getSupportActionBar().setElevation(0f);
            }
        } else {
            Log.e(LOG_TAG, "onCreate - something went wrong with the toolbar..., hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");
        }


        //MainFragment seems to get called by xml...
//        MainFragment fragment = (MainFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);
    }

    private String initSpinner() {
        Log.v(LOG_TAG, "initSpinner, hashCode=" + this.hashCode() + ", " + "");

        String[] typesArray = getResources().getStringArray(R.array.pref_type_filter_values);
        ArrayList<String> typesList = new ArrayList<>(Arrays.asList(typesArray));

        mSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), typesList);
        mSpinnerType.setAdapter(mSpinnerAdapter);

        String drinkType = updateSpinnerType();

        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_TAG, "onItemSelected in spinnerType, hashCode=" + this.hashCode() + ", " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
                String item = parent.getItemAtPosition(position).toString();
                refreshFragmentLists(mSearchPattern, item);
                Utils.setSharedPrefsDrinkType(MainActivity.this, item);
                Snackbar.make(parent, "selected: " + item, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return drinkType;
    }

    private String updateSpinnerType() {
        String drinkType = Utils.getDrinkTypeFromSharedPrefs(this, true);
        if (mSpinnerAdapter != null) {
            int spinnerPosition = mSpinnerAdapter.getPosition(drinkType);
            Log.v(LOG_TAG, "updateSpinnerType - prefDrinkType: " + drinkType + ", hashCode=" + this.hashCode() + ", " + "");
            if (spinnerPosition > -1) {
                Log.v(LOG_TAG, "updateSpinnerType - spinner position found, hashCode=" + this.hashCode() + ", " + "");
                mSpinnerType.setSelection(spinnerPosition);
            } else {
                Log.v(LOG_TAG, "updateSpinnerType - no spinner position, hashCode=" + this.hashCode() + ", " + "");
            }
        }
        return drinkType;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu, hashCode=" + this.hashCode() + ", " + "menu = [" + menu + "]");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.search_all);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "onResume, hashCode=" + this.hashCode() + ", " + "");

        String drinkType = updateSpinnerType();
        MainFragment fragment = getMainFragment();  //fragment exists every time...

        if (fragment != null) {
            Log.v(LOG_TAG, "onResume - fragment exists, hashCode=" + this.hashCode() + ", " + "");
            refreshFragmentLists(mSearchPattern, drinkType);
        } else {
            Log.v(LOG_TAG, "onResume - fragment does not exist yet, hashCode=" + this.hashCode() + ", " + "");
        }
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add){
            // TODO: twoPane-mode and maybe some other stuff
            Intent intent = new Intent(this, AddDrinkActivity.class);
//            startActivity(intent);
            startActivityForResult(intent, ADD_DRINK_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult, hashCode=" + this.hashCode() + ", " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == ADD_DRINK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            Uri drinkUri = data.getData();
            Intent intent = new Intent(this, ShowDrinkActivity.class)
                    .setData(drinkUri);
            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getSelectedDrinkType() {
        if (mSpinnerType != null) {
            return mSpinnerType.getSelectedItem().toString();
        } else {
            return Drink.TYPE_ALL;
        }
    }


    private MainFragment getMainFragment() {
        return (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    }

    private void refreshFragmentLists(String pattern, String type) {
        MainFragment fragment = getMainFragment();
        if (fragment != null) {
            Log.v(LOG_TAG, "refreshFragmentLists - fragment exists, hashCode=" + this.hashCode() + ", " + "pattern = [" + pattern + "], type = [" + type + "]");
            fragment.refreshLists(pattern, type);
        } else {
            Log.v(LOG_TAG, "refreshFragmentLists - fragment not existing, hashCode=" + this.hashCode() + ", " + "pattern = [" + pattern + "], type = [" + type + "]");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchPattern = query;
        String drinkType = getSelectedDrinkType();
        Log.v(LOG_TAG, "onQueryTextSubmit, drinkType=" + drinkType + ", hashCode=" + this.hashCode() + ", " + "query = [" + query + "]");
        refreshFragmentLists(query, drinkType);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchPattern = newText;
        String drinkType = getSelectedDrinkType();
        Log.v(LOG_TAG, "onQueryTextChange, drinkType=" + drinkType + ", hashCode=" + this.hashCode() + ", " + "newText = [" + newText + "]");
        refreshFragmentLists(newText, drinkType);
        return false;
    }
}
