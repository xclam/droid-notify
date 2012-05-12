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

public class RescheduleService extends WakefulIntentService {
	
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
	public RescheduleService() {
		super("RescheduleBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("RescheduleBroadcastReceiverService.RescheduleBroadcastReceiverService()");
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
		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
		    Bundle bundle = intent.getExtras();
		    int notificationType = bundle.getInt(Constants.BUNDLE_NOTIFICATION_TYPE);
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = true;
		    boolean rescheduleNotificationInQuickReply = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean inQuickReplyApp = Common.isUserInQuickReplyApp(context);
		    boolean showBlockedNotificationStatusBarNotification = false;
		    switch(notificationType){
			    case Constants.NOTIFICATION_TYPE_PHONE:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_SMS:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_MMS:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_CALENDAR:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_K9:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_GENERIC:{
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_PHONE:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_SMS:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_MMS:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_CALENDAR:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_K9:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_GENERIC:{
			    	break;
			    }
		    }
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
		    	Common.startNotificationActivity(getApplicationContext(), intent.getExtras());
		    }else{		    	
		    	if(notificationType == Constants.NOTIFICATION_TYPE_GENERIC && notificationType == Constants.NOTIFICATION_TYPE_RESCHEDULE_GENERIC){
		    		Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, notificationType, intent.getExtras());
		    	}else{
			    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    		Bundle rescheduleBundle = intent.getExtras();
					Bundle rescheduleNotificationBundle = rescheduleBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
			    	if(showBlockedNotificationStatusBarNotification){
					    if(rescheduleNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = rescheduleNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle rescheduleNotificationBundleSingle = rescheduleNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(rescheduleNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, 1, notificationType, rescheduleNotificationBundleSingle.getInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE), callStateIdle, rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_CONTACT_ID, -1), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_K9_EMAIL_URI), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_LINK_URL), false, Common.getStatusBarNotificationBundle(context, notificationType));
				    			}
							}			    			
						}
			    	}
			    	Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, notificationType, rescheduleNotificationBundle);
			    }
		    }
		}catch(Exception ex){
			Log.e("RescheduleBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}