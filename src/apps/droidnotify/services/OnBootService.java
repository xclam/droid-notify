package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class OnBootService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public OnBootService() {
		super("OnBootBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("OnBootBroadcastReceiverService.OnBootBroadcastReceiverService()");
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
		if (_debug) Log.v("OnBootBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("OnBootBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Start Calendar Alarms
		    if(preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
		    	CalendarCommon.startCalendarAlarmManager(context, System.currentTimeMillis() + (5 * 60 * 1000));
			}
		}catch(Exception ex){
			Log.e("OnBootBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}