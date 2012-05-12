package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.K9AlarmReceiver;

public class K9BroadcastReceiverService extends WakefulIntentService {
	
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
	public K9BroadcastReceiverService() {
		super("K9BroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("K9BroadcastReceiverService.K9BroadcastReceiverService()");
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
		if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if K9 notifications are disabled.
		    if(!preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() K9 Notifications Disabled. Exiting...");
				return;
			}
			if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() IntentAction: " + intent.getAction());
			//Schedule task x seconds after the broadcast.
			//This time is set by the users advanced preferences. 1 seconds is the default value.
			//This should allow enough time to pass for the email db to be written to.
			long timeoutInterval = Long.parseLong(preferences.getString(Constants.K9_TIMEOUT_KEY, "1")) * 1000;
			String intentActionText = intent.getAction() + "/" + String.valueOf(System.currentTimeMillis());
			long alarmTime = System.currentTimeMillis() + timeoutInterval;
			Common.startAlarm(context, K9AlarmReceiver.class, intent.getExtras(), intentActionText, alarmTime);
		}catch(Exception ex){
			Log.e("K9BroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}