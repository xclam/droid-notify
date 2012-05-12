package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.MMSAlarmReceiver;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class MMSBroadcastReceiverService extends WakefulIntentService {
	
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
	public MMSBroadcastReceiverService() {
		super("MMSBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSBroadcastReceiverService.MMSBroadcastReceiverService()");
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
		if (_debug) Log.v("MMSBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("MMSBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("MMSBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if MMS notifications are disabled.
		    if(!preferences.getBoolean(Constants.MMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("MMSBroadcastReceiverService.doWakefulWork() MMS Notifications Disabled. Exiting...");
				return;
			}
			//Schedule mms task x seconds after the broadcast.
			//This time is set by the users advanced preferences. 40 seconds is the default value.
			//This should allow enough time to pass for the mms inbox to be written to.
			long timeoutInterval = Long.parseLong(preferences.getString(Constants.MMS_TIMEOUT_KEY, "40")) * 1000;
			String intentActionText = "apps.droidnotify.alarm/MMSAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
			long alarmTime = System.currentTimeMillis() + timeoutInterval;
			Common.startAlarm(context, MMSAlarmReceiver.class, null, intentActionText, alarmTime);
		}catch(Exception ex){
			Log.e("MMSBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}