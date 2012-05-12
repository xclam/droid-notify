package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.calendar.CalendarCommon;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class CalendarNotificationAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public CalendarNotificationAlarmBroadcastReceiverService() {
		super("CalendarNotificationAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.CalendarNotificationAlarmBroadcastReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if calendar notifications are disabled.
		    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() Calendar Notifications Disabled. Exiting... ");
				return;
			}
	    	Bundle bundle = intent.getExtras();
    		Bundle calendarEventNotificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
    		if(calendarEventNotificationBundle != null){
    			Bundle calendarEventNotificationBundleSingle = calendarEventNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
    			if(calendarEventNotificationBundleSingle != null){
				    //Check to ensure that this calendar event should be displayed.
			    	//if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() CalendarID: " + calendarEventNotificationBundleSingle.getLong(Constants.BUNDLE_CALENDAR_ID));
			    	//if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() Is Calendar Enabled: " + CalendarCommon.isCalendarEnabled(context, calendarEventNotificationBundleSingle.getLong(Constants.BUNDLE_CALENDAR_ID)));
				    if(!CalendarCommon.isCalendarEnabled(context, calendarEventNotificationBundleSingle.getLong(Constants.BUNDLE_CALENDAR_ID))){
						if (_debug) Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() Specific Calendar Not Enabled. Exiting... ");
						return;
				    }
	    		}else{
	    			Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() CalendarEventBundle is null. Exiting... ");
	    			return;
	    		}
    		}else{
    			Log.v("CalendarNotificationAlarmBroadcastReceiverService.onReceive() CalendarEventBundle is null. Exiting... ");
    			return;
    		}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = true;
		    boolean rescheduleNotificationInQuickReply = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean inQuickReplyApp = Common.isUserInQuickReplyApp(context);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else if(inQuickReplyApp){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInQuickReply = preferences.getBoolean(Constants.IN_QUICK_REPLY_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				Intent calendarIntent = new Intent(context, CalendarService.class);
				calendarIntent.putExtras(intent.getExtras());
				WakefulIntentService.sendWakefulWork(context, calendarIntent);
		    }else{	    	
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		if(calendarEventNotificationBundle != null){
		    			Bundle calendarEventNotificationBundleSingle = calendarEventNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
		    			if(calendarEventNotificationBundleSingle != null){
							//Display Status Bar Notification
						    Common.setStatusBarNotification(context, 1, Constants.NOTIFICATION_TYPE_CALENDAR, -1, callStateIdle, null, -1, null, calendarEventNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, null, false, Common.getStatusBarNotificationBundle(context, Constants.NOTIFICATION_TYPE_CALENDAR));
		    			}
		    		}
		    	}
		    	if(calendarEventNotificationBundle != null) Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, Constants.NOTIFICATION_TYPE_CALENDAR, calendarEventNotificationBundle);
		    }
		}catch(Exception ex){
			Log.e("CalendarNotificationAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}