package apps.droidnotify.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.CalendarAlarmReceiver;
import apps.droidnotify.receivers.CalendarNotificationAlarmReceiver;

/**
 * This class is a collection of Calendar methods.
 * 
 * @author Camille Sévigny
 */
public class CalendarCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Read the phones calendars and events. 
	 * Schedules Calendar Event notifications based on the Event date and time.
	 * 
	 * @param context - Application Context.
	 */
	public static void readCalendars(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.readCalendars()");
		try{
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarCommon.readCalendars() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("CalendarCommon.readCalendars() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if calendar notifications are disabled.
		    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarCommon.readCalendars() Calendar Notifications Disabled. Exiting... ");
				return;
			}
			long reminderInterval = Long.parseLong(preferences.getString(Constants.CALENDAR_REMINDER_KEY, "15")) * 60 * 1000;
			long reminderIntervalAllDay = Long.parseLong(preferences.getString(Constants.CALENDAR_REMINDER_ALL_DAY_KEY, "6")) * 60 * 60 * 1000;
			long dayOfReminderIntervalAllDay = Long.parseLong(preferences.getString(Constants.CALENDAR_NOTIFY_DAY_OF_TIME_KEY, "12")) * 60 * 60 * 1000;
			String calendarPreferences = preferences.getString(Constants.CALENDAR_SELECTION_KEY, "");
			ArrayList<String> calendarsArray = new ArrayList<String>();
			if(!calendarPreferences.equals("")){
				Collections.addAll(calendarsArray, calendarPreferences.split("\\|")); 
			}
		 	Cursor cursor = null;
			try{
				ContentResolver contentResolver = context.getContentResolver();
				// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
				String contentProvider = "";
				contentProvider = "content://com.android.calendar";	
				HashMap<String, String> calendarIds = new HashMap<String, String>();
				try{
					cursor = contentResolver.query(
						Uri.parse(contentProvider + "/calendars"), 						
						null,
						null,
						null,
						null);
					if(cursor ==  null){
						return;
					}
					while (cursor.moveToNext()){
						final String calendarID = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_ID));
						String calendarDisplayName = null;
						Boolean calendarSelected = true;
						if(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME) >= 0){ //Android 2.2 - 3.x
							calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME));
							calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_SELECTED)).equals("0");
						}else{ //Android > 4.0
							calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME_NEW));
							calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_VISIBLE)).equals("0");
						}
						if(calendarsArray.contains(calendarID)){
							if (_debug) Log.v("CalendarCommon.readCalendars() CHECKING CALENDAR -  Calendar ID: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
							calendarIds.put(calendarID, calendarDisplayName);
						}else{
							if (_debug) Log.v("CalendarCommon.readCalendars() CALENDAR NOT BEING CHECKED -  Calendar ID: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
						}
					}
				}catch(Exception ex){
					if (_debug){
						Log.e("CalendarCommon.readCalendars() READ CALENDARS ERROR: " + ex.toString());
						Common.debugReadContentProviderColumns(context, contentProvider + "/calendars", null);
						cursor.close();
						return;
					}
				}
				if(calendarIds.isEmpty()){
					if (_debug) Log.v("CalendarCommon.readCalendars() No calendars were found. Exiting...");
					cursor.close();
					return;
				}
				// For each calendar, read the events.
				Iterator<Map.Entry<String, String>> calendarIdsEnumerator = calendarIds.entrySet().iterator();
				while(calendarIdsEnumerator.hasNext()){
					Map.Entry<String, String> calendarInfo = calendarIdsEnumerator.next();
					String calendarID = calendarInfo.getKey();
					String calendarName = calendarInfo.getValue();
					if (_debug) Log.v("CalendarCommon.readCalendars() CHECKING EVENTS FOR CALENDAR -  Calendar ID: " + calendarID  + " Calendar Name: " + calendarName);
					Uri.Builder builder = Uri.parse(contentProvider + "/instances/when").buildUpon();
					//The start time of the query.
					long queryStartTime = System.currentTimeMillis();
					ContentUris.appendId(builder, queryStartTime);
					//The end time of the query. One day past the start time.
					ContentUris.appendId(builder, queryStartTime + AlarmManager.INTERVAL_DAY);
		    		final String[] projection = new String[] {
		    				Constants.CALENDAR_CALENDAR_ID,
		    				Constants.CALENDAR_EVENT_ID,
		    				Constants.CALENDAR_EVENT_TITLE,
		    				Constants.CALENDAR_INSTANCE_BEGIN,
		    				Constants.CALENDAR_INSTANCE_END,
		    				Constants.CALENDAR_EVENT_ALL_DAY};
		            final String selection = Constants.CALENDAR_CALENDAR_ID + "=" + calendarID;
		    		final String[] selectionArgs = null;
		    		final String sortOrder = "startDay ASC, startMinute ASC";
					Cursor eventCursor = null;
					try{
						eventCursor = contentResolver.query(
								builder.build(),
								projection,
								selection,
								selectionArgs,
								sortOrder);
						if(eventCursor ==  null){
							cursor.close();
							return;
						}
						while (eventCursor.moveToNext()){
							long eventCalendarID = eventCursor.getLong(eventCursor.getColumnIndex(Constants.CALENDAR_CALENDAR_ID));
							String eventID = eventCursor.getString(eventCursor.getColumnIndex(Constants.CALENDAR_EVENT_ID));
							String eventTitle = eventCursor.getString(eventCursor.getColumnIndex(Constants.CALENDAR_EVENT_TITLE));
							long eventStartTime = eventCursor.getLong(eventCursor.getColumnIndex(Constants.CALENDAR_INSTANCE_BEGIN));
							long eventEndTime = eventCursor.getLong(eventCursor.getColumnIndex(Constants.CALENDAR_INSTANCE_END));
							final Boolean allDay = !eventCursor.getString(eventCursor.getColumnIndex(Constants.CALENDAR_EVENT_ALL_DAY)).equals("0");
							if (_debug) Log.v("CalendarCommon.readCalendars() Calendar ID: " + eventCalendarID + " Event ID: " + eventID + " Event Title: " + eventTitle + " Event Begin: " + eventStartTime + " Event End: " + eventEndTime + " Event All Day: " + allDay);
							long timezoneOffsetValue =  TimeZone.getDefault().getOffset(System.currentTimeMillis());
							//For all any event in the past, don't schedule them.
							long currentSystemTime = System.currentTimeMillis();
							if(eventStartTime > currentSystemTime){
								if(allDay){
									//Special case for all-day events.
									eventStartTime = eventStartTime  - timezoneOffsetValue;
									eventEndTime = eventEndTime  - timezoneOffsetValue;
									//Schedule the notification for the event time.
									Bundle calendarEventNotificationBundleSingle = new Bundle();
									calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_TITLE, eventTitle);
									calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, eventTitle);
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, eventStartTime);
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, eventEndTime);
									calendarEventNotificationBundleSingle.putBoolean(Constants.BUNDLE_ALL_DAY, allDay);
									calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_CALENDAR_NAME, calendarName);
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_ID, Long.parseLong(calendarID));
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, Long.parseLong(eventID));
									calendarEventNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_CALENDAR);
									scheduleCalendarNotification(context, eventStartTime + dayOfReminderIntervalAllDay, calendarEventNotificationBundleSingle, "apps.droidnotifydonate.VIEW/" + calendarID + "/" + eventID);
									//Schedule the reminder notification if it is enabled.
									if(preferences.getBoolean(Constants.CALENDAR_REMINDERS_ENABLED_KEY,true)){
										//Only schedule the all day event if the current time is before the notification time.
										if((eventStartTime - reminderIntervalAllDay) > currentSystemTime){
											scheduleCalendarNotification(context, eventStartTime - reminderIntervalAllDay, calendarEventNotificationBundleSingle, "apps.droidnotifydonate.VIEW/" + calendarID + "/" + eventID + "/REMINDER");
										}
									}
								}else{
									//Schedule non-all-day events.
									//Schedule the notification for the event time.
									Bundle calendarEventNotificationBundleSingle = new Bundle();
									calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_TITLE, eventTitle);
									calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, eventTitle);
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, eventStartTime);
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, eventEndTime);
									calendarEventNotificationBundleSingle.putBoolean(Constants.BUNDLE_ALL_DAY, allDay);
									calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_CALENDAR_NAME, calendarName);
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_ID, Long.parseLong(calendarID));
									calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, Long.parseLong(eventID));
									calendarEventNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_CALENDAR);
									scheduleCalendarNotification(context, eventStartTime, calendarEventNotificationBundleSingle, "apps.droidnotifydonate.VIEW/" + calendarID + "/" + eventID);
									//Schedule the reminder notification if it is enabled.
									if(preferences.getBoolean(Constants.CALENDAR_REMINDERS_ENABLED_KEY,true)){
										//Only schedule the event if the current time is before the notification time.
										if((eventStartTime - reminderInterval) > currentSystemTime){
											scheduleCalendarNotification(context, eventStartTime - reminderInterval, calendarEventNotificationBundleSingle, "apps.droidnotifydonate.VIEW/" + calendarID + "/" + eventID + "/REMINDER");
										}
									}
								}
							}
						}
					}catch(Exception ex){
						Log.e("CalendarCommon.readCalendars() Event Query ERROR: " + ex.toString());						
						Common.debugReadContentProviderColumns(context, null, builder.build());
						eventCursor.close();
						return;
					}finally{
						eventCursor.close();
					}
				}
			}catch(Exception ex){
				Log.e("CalendarCommon.readCalendars() Calendar Query ERROR: " + ex.toString());
				cursor.close();
				return;
			}finally{
				cursor.close();
			}
		}catch(Exception ex){
			Log.e("CalendarCommon.readCalendars() ERROR: " + ex.toString());
			return;
		}
	}

	/**
	 * Read the phones Calendars and return the information on them.
	 * 
	 * @param context - Application Context.
	 * 
	 * @return String - A string of the available Calendars. Specially formatted string with the Calendar information.
	 */
	public static String getAvailableCalendars(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.getAvailableCalendars()");
		StringBuilder calendarsInfo = new StringBuilder();
		Cursor cursor = null;
		try{
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = "";
			contentProvider = "content://com.android.calendar";
			cursor = contentResolver.query(
				Uri.parse(contentProvider + "/calendars"), 
				null,
				null,
				null,
				null);
			while (cursor.moveToNext()){
				final String calendarID = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_ID));
				String calendarDisplayName = null;
				Boolean calendarSelected = true;
				if(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME) >= 0){ //Android 2.2 - 3.x
					calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME));
					calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_SELECTED)).equals("0");
				}else{ // Android > 4.0
					calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME_NEW));
					calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_VISIBLE)).equals("0");
				}
				if(calendarSelected){
					if(!calendarsInfo.toString().equals("")){
						calendarsInfo.append(",");
					}
					calendarsInfo.append(calendarID + "|" + calendarDisplayName);
				}
			}	
		}catch(Exception ex){
			Log.e("CalendarCommon.getAvailableCalendars() ERROR: " + ex.toString());
			Common.debugReadContentProviderColumns(context, "content://com.android.calendar/calendars", null);
			return null;
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		if(calendarsInfo.toString().equals("")){
			if (_debug) Log.v("CalendarCommon.getAvailableCalendars() No Calendars Found.");
			return null;
		}else{
			return calendarsInfo.toString();
		}
	}
	
	/**
	 * Start the intent to add an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startAddCalendarEventActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startAddCalendarEventActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			Log.e("CalendarCommon.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.EditEvent"));
		        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				Common.setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				Log.e("CalendarCommon.startAddCalendarEventActivity ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
		}
	}
	
	/**
	 * Start the intent to view an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param calendarEventID - The id of the calendar event.
	 * @param calendarEventStartTime - The start time of the calendar event.
	 * @param calendarEventEndTime - The end time of the calendar event.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startViewCalendarEventActivity(Context context, NotificationActivity notificationActivity, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startViewCalendarEventActivity()");
		try{
			if(calendarEventID < 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("CalendarCommon.startViewCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to edit an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param calendarEventID - The id of the calendar event.
	 * @param calendarEventStartTime - The start time of the calendar event.
	 * @param calendarEventEndTime - The end time of the calendar event.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startEditCalendarEventActivity(Context context, NotificationActivity notificationActivity, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startEditCalendarEventActivity()");
		try{
			if(calendarEventID < 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("CalendarCommon.startEditCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to start the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startViewCalendarActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startViewCalendarActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity"); 
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			Log.e("CalendarCommon.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_MAIN); 
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.LaunchActivity"));
		        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				Common.setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				Log.e("CalendarCommon.startViewCalendarActivity() ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
		}
	}
	
	/**
	 * Start the Calendar recurring alarm.
	 * 
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startCalendarAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startCalendarAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.CALENDAR_POLLING_FREQUENCY_KEY, Constants.CALENDAR_POLLING_FREQUENCY_DEFAULT)) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			Log.e("CalendarCommon.startCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Calendar recurring alarm. 
	 * 
	 * @param context - The application context.
	 */
	public static void cancelCalendarAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.cancelCalendarAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			Log.e("CalendarCommon.cancelCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Format/create the Calendar Event message.
	 * 
	 * @param context - The application context.
	 * @param eventStartTime - Calendar Event's start time.
	 * @param eventEndTime - Calendar Event's end time.
	 * @param allDay - Boolean, true if the Calendar Event is all day.
	 * 
	 * @return String - Returns the formatted Calendar Event message.
	 */
	public static String formatCalendarEventMessage(Context context, String messageTitle, long eventStartTime, long eventEndTime, boolean allDay, String calendarName){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.formatCalendarEventMessage()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String formattedMessage = "";
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
		if(messageTitle == null || messageTitle.equals("No Title")){
			messageTitle = "";
		}else{
			messageTitle = messageTitle + "<br/>";
		}
		String startDateFormated = Common.formatDate(context, eventStartDate);
		String endDateFormated = Common.formatDate(context, eventEndDate);
		try{
			String[] startDateInfo = Common.parseDateInfo(context, startDateFormated);
			String[] endDateInfo = Common.parseDateInfo(context, endDateFormated);
    		if(allDay){
    			formattedMessage = startDateInfo[0] + " - All Day";
    		}else{
    			//Check if the event spans a single day or not.
    			if(startDateInfo[0].equals(endDateInfo[0]) && startDateInfo.length == 3){
    				if(startDateInfo.length < 3){
    					formattedMessage = startDateInfo[0] + " " + startDateInfo[1] + " - " + endDateInfo[1];
    				}else{
    					formattedMessage = startDateInfo[0] + " " + startDateInfo[1] + " " + startDateInfo[2] +  " - " + endDateInfo[1] + " " + startDateInfo[2];
    				}
    			}else{
    				formattedMessage = startDateFormated + " - " + endDateFormated;
    			}
    		}
    		formattedMessage =  messageTitle + formattedMessage;
		}catch(Exception ex){
			Log.e("CalendarCommon.formatCalendarEventMessage() ERROR: " + ex.toString());
			formattedMessage = startDateFormated + " - " + endDateFormated;
		}
    	if(preferences.getBoolean(Constants.CALENDAR_LABELS_KEY, true)){
    		formattedMessage = "<b>" + calendarName + "</b><br/>" + formattedMessage;
    	}
		return formattedMessage.replace("\n", "<br/>").trim();
	}
	
	/**
	 * Check whether a calendar has been selected 
	 * 
	 * @param context - The application context.
	 * @param calendarID - The calendar ID.
	 * 
	 * @return boolean - Returns true if the user has selected this calendar to receive event notifications.
	 */
	public static boolean isCalendarEnabled(Context context, long calendarID){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.isCalendarEnabled()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String calendarPreferences = preferences.getString(Constants.CALENDAR_SELECTION_KEY, "");
			ArrayList<String> calendarsArray = new ArrayList<String>();
			if(!calendarPreferences.equals("")){
				Collections.addAll(calendarsArray, calendarPreferences.split("\\|")); 
			}
			return calendarsArray.contains(String.valueOf(calendarID));
		}catch(Exception ex){
			Log.e("CalendarCommon.isCalendarEnabled() ERROR: " + ex.toString());
			return true;
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Schedule an alarm that will trigger a Notification for a Calendar Event.
	 * 
	 * @param context - Application Context.
	 * @param scheduledAlarmTime - Time the alarm should be scheduled.
	 * @param title - Title of the Calendar Event.
	 * @param timeStamp - TimeStamp of the Calendar Event.
	 * @param calendarID - Calendar ID of the Calendar Event.
	 * @param eventID - Event ID of the Calendar Event.
	 */
	private static void scheduleCalendarNotification(Context context, long scheduledAlarmTime, Bundle calendarEventNotificationBundleSingle, String intentAction){
		if (_debug) Log.v("CalendarCommon.scheduleCalendarNotification()");
		try{
	    	Bundle calendarEventNotificationBundle = new Bundle();
	    	calendarEventNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", calendarEventNotificationBundleSingle);
	    	calendarEventNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);
	    	Bundle bundle = new Bundle();
	    	bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_CALENDAR);
	    	bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, calendarEventNotificationBundle);	    	
			Common.startAlarm(context, CalendarNotificationAlarmReceiver.class, bundle, intentAction, scheduledAlarmTime);
		}catch(Exception ex){
			Log.e("CalendarCommon.scheduleCalendarNotification() ERROR: " + ex.toString());
		}
	}	
	
}
