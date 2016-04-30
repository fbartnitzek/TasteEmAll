package com.example.fbartnitzek.tasteemall;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate, " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (true) {
            getSupportActionBar().setElevation(0f);
        }

        //MainFragment seems to get called by xml...
//        MainFragment fragment = (MainFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);
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
            startActivity(intent);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        refreshFragmentLists(query);
        return true;
    }

    private void refreshFragmentLists(String query) {
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (fragment != null) {
            Log.v(LOG_TAG, "refreshFragmentLists refreshing fragment, hashCode=" + this.hashCode() + ", " + "query = [" + query + "]");
            fragment.refreshLists(query);
        } else {
            Log.v(LOG_TAG, "refreshFragmentLists and fragment == null..., hashCode=" + this.hashCode() + ", " + "query = [" + query + "]");
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        refreshFragmentLists(newText);
        return false;
    }
}
