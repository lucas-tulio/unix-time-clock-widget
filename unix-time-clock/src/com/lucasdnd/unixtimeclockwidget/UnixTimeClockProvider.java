package com.lucasdnd.unixtimeclockwidget;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.lucasdnd.unixtimeclockwidget.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;


public class UnixTimeClockProvider extends AppWidgetProvider {
	
	public static String CLOCK_WIDGET_UPDATE = "com.lucasdnd.unixtimeclockwidget.CLOCK_UPDATE";

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {

			// Get the widget manager and ids for this widget provider, then call the shared clock update method.
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
		    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		    
		    int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

		    for (int appWidgetID: ids) {
				updateAppWidget(context, appWidgetManager, appWidgetID);
		    }
		}
	}

	private PendingIntent createClockTickIntent(Context context) {
    
		Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
	}

	@Override
	public void onDisabled(Context context) {
		
		super.onDisabled(context);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));	
	}

	@Override 
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Calendar calendar = Calendar.getInstance();
        
    	calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createClockTickIntent(context));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < N; i++) {
			
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, UnixTimeClockActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,	intent, 0);

			// Get the layout for the App Widget and attach an on-click listener to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
			
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);

			// Update The clock label using a shared method
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	public static void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),	R.layout.widgetlayout);
		
		updateViews.setTextViewText(R.id.clockTextView, "" + (System.currentTimeMillis() / 1000L));
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}
}
