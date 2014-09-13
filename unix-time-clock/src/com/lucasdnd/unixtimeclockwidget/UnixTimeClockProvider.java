package com.lucasdnd.unixtimeclockwidget;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class UnixTimeClockProvider extends AppWidgetProvider {
	
	public static String CLOCK_UPDATE = "com.lucasdnd.unixtimeclockwidget.CLOCK_UPDATE";
	public static String SWITCH_FORMAT = "com.lucasdnd.unixtimeclockwidget.SWITCH_FORMAT";
	private static boolean shouldUseSeparator = false;
	private static long time = 1337L;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		// Get the widget manager and ids for this widget provider, then call the shared clock update method.
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				    
		if(CLOCK_UPDATE.equals(intent.getAction())) {
			
		    int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		    for (int appWidgetID: ids) {
				updateClock(context, appWidgetManager, appWidgetID, true);
		    }
		}
		
		if(SWITCH_FORMAT.equals(intent.getAction())) {
			int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		    for (int appWidgetID: ids) {
		    	shouldUseSeparator = !shouldUseSeparator;
				updateClock(context, appWidgetManager, appWidgetID, false);
		    }
		}
	}
	
	private PendingIntent createClockTickIntent(Context context) {
		Intent intent = new Intent(CLOCK_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
	}
	private PendingIntent createFormatSwitchIntent(Context context) {
		Intent intent = new Intent(SWITCH_FORMAT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
	}
	
	@Override
	public void onDisabled(Context context) {
		
		super.onDisabled(context);
		
		// Stop the Timer
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));	
	}
	
	@Override 
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
		
		// Create the Timer
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createClockTickIntent(context));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < appWidgetIds.length; i++) {
			
			int appWidgetId = appWidgetIds[i];

			// Get the layout for the App Widget and attach an on-click listener to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
			
			// Update The clock label using a shared method
			updateClock(context, appWidgetManager, appWidgetId, true);
			
			// Touch Intent
			PendingIntent p = createFormatSwitchIntent(context);
			views.setOnClickPendingIntent(R.id.clockTextView, p);
			
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	public static void updateClock(Context context,	AppWidgetManager appWidgetManager, int appWidgetId, boolean updateTime) {
		
		// Update the time?
		if(updateTime) {
			time = System.currentTimeMillis() / 1000L;
		}
		
		// Time format
		String timeString = "";
		if(shouldUseSeparator) {
			timeString = String.format(Locale.getDefault(), "%,d", time);
		} else {
			timeString = "" + time;
		}
		
		// Update the views
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),	R.layout.widgetlayout);
		remoteViews.setTextViewText(R.id.clockTextView, "" + timeString);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}	
}