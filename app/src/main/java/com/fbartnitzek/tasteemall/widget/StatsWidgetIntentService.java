package com.fbartnitzek.tasteemall.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.fbartnitzek.tasteemall.MainActivity;
import com.fbartnitzek.tasteemall.R;
import com.fbartnitzek.tasteemall.addentry.AddReviewActivity;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.tasks.QueryColumns;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class StatsWidgetIntentService extends IntentService{

    private static final String LOG_TAG = StatsWidgetIntentService.class.getName();

    public StatsWidgetIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        Log.v(LOG_TAG, "onHandleIntent, hashCode=" + this.hashCode() + ", " + "intent = [" + intent + "]");

        // get all widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StatsWidgetProvider.class));

        // query all entities
        int numProducers = queryAndGetCount(
                DatabaseContract.ProducerEntry.CONTENT_URI, QueryColumns.Widget.ProviderQuery.COLUMNS);
        int numDrinks = queryAndGetCount(
                DatabaseContract.DrinkEntry.CONTENT_URI, QueryColumns.Widget.DrinkQuery.COLUMNS);
        int numReviews = queryAndGetCount(
                DatabaseContract.ReviewEntry.CONTENT_URI, QueryColumns.Widget.ReviewQuery.COLUMNS);

//        Log.v(LOG_TAG, "onHandleIntent, producer=" + numProducers + ", drinks=" + numDrinks + ", reviews=" + numReviews);

        for (int appWidgetId : appWidgetIds) {
            // dynamically adapt widget width ... later

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.info_widget);

            // fill stats
            views.setTextViewText(R.id.stats_producers,
                    getString(R.string.widget_statistics_producers, numProducers));

            views.setTextViewText(R.id.stats_drinks,
                    getString(R.string.widget_statistics_drinks, numDrinks));

            views.setTextViewText(R.id.stats_reviews,
                    getString(R.string.widget_statistics_reviews, numReviews));

            // seems to be impossible to get contentDescription for whole widget...
//            views.setContentDescription(R.id.widget_layout,
//                    getString(R.string.a11y_widget_statistics_all, numProducers, numDrinks, numReviews));

            views.setContentDescription(R.id.stats_reviews,
                    getString(R.string.a11y_widget_statistics_all, numProducers, numDrinks, numReviews));


            // set on click listener for add and search on every update (kind of useless...)

            // add button - create backStack for add
            Intent addIntent = new Intent(this, AddReviewActivity.class);
            PendingIntent addPendingIntent = TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(addIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_add_button, addPendingIntent);

            // search button
            PendingIntent searchPendingIntent = PendingIntent.getActivity(this,
                    0, new Intent(this, MainActivity.class), 0);
            views.setOnClickPendingIntent(R.id.widget_search_button, searchPendingIntent);

            // update each StatsWidget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int queryAndGetCount(Uri uri, String[] columns) {
        int number = 0;
        Cursor data = getContentResolver().query(
                uri,
                columns,
                null, null, null);
        if (data != null){
            number = data.getCount();
            data.close();
        }
        return number;
    }
}
