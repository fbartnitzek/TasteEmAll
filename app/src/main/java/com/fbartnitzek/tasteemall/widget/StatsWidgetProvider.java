package com.fbartnitzek.tasteemall.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fbartnitzek.tasteemall.tasks.TaskUtils;

/**
 * Implementation of App Widget functionality.
 */
public class StatsWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = StatsWidgetProvider.class.getName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        Log.v(LOG_TAG, "onUpdate, hashCode=" + this.hashCode() + ", " + "context = [" + context + "], appWidgetManager = [" + appWidgetManager + "], appWidgetIds = [" + appWidgetIds + "]");
        context.startService(new Intent(context, StatsWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.v(LOG_TAG, "onReceive, hashCode=" + this.hashCode() + ", " + "context = [" + context + "], intent = [" + intent + "]");
        super.onReceive(context, intent);
        if (TaskUtils.ACTION_DATA_CHANGED.equals(intent.getAction())) {
            context.startService(new Intent(context, StatsWidgetIntentService.class));
        }

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, StatsWidgetIntentService.class));
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

