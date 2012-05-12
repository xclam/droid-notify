package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.PhoneAlarmReceiver;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class PhoneBroadcastReceiverService extends WakefulIntentService {
	
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
	public PhoneBroadcastReceiverService() {
		super("PhoneBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneBroadcastReceiverService.PhoneBroadcastReceiverService()");
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
		if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if missed call notifications are disabled.
		    if(!preferences.getBoolean(Constants.PHONE_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Missed Call Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
			TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    int callState = telemanager.getCallState();
		    if(callState == TelephonyManager.CALL_STATE_IDLE){
		    	if(preferences.getInt(Constants.PREVIOUS_CALL_STATE_KEY, TelephonyManager.CALL_STATE_IDLE) != TelephonyManager.CALL_STATE_RINGING){
		    		if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Previous call state not 'CALL_STATE_RINGING'. Exiting...");
		    		setCallStateFlag(preferences, callState);
		    		return;
		    	}else{
		    		if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Previous call state 'CALL_STATE_RINGING'. Missed Call Occurred");
		    	}
				//Schedule phone task x seconds after the broadcast.
				//This time is set by the users advanced preferences. 5 seconds is the default value.
				//This should allow enough time to pass for the phone log to be written to.
				long timeoutInterval = Long.parseLong(preferences.getString(Constants.CALL_LOG_TIMEOUT_KEY, "5")) * 1000;
				String intentActionText = "apps.droidnotify.alarm/PhoneAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
				long alarmTime = System.currentTimeMillis() + timeoutInterval;
				Common.startAlarm(context, PhoneAlarmReceiver.class, null, intentActionText, alarmTime);
		    }else if(callState == TelephonyManager.CALL_STATE_RINGING){
		    	if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Phone Ringing. Exiting...");
		    }else if(callState == TelephonyManager.CALL_STATE_OFFHOOK){
		    	if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Phone Call In Progress. Exiting...");
		    }else{
		    	if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Unknown Call State. Exiting...");
		    }
		    setCallStateFlag(preferences, callState);
	    }catch(Exception ex){
			Log.e("PhoneBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set the phone state flag.
	 */
	private void setCallStateFlag(SharedPreferences preferences, int callState){
		if (_debug) Log.v("PhoneBroadcastReceiverService.setCallStateFlag() callState: " + callState);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.PREVIOUS_CALL_STATE_KEY, callState);
		editor.commit();
	}
		
}