package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.ScreenManagementAlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for a screen timeout alarm.
 * 
 * @author Camille Sévigny
 */
public class ScreenManagementAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification of a Screen Management Alarm.
	 * This function removes the KeyguardLock and WakeLock help by this application.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("ScreenManagementAlarmReceiver.onReceive()");
		try{
			Intent screenManagementAlarmBroadcastReceiverServiceIntent = new Intent(context, ScreenManagementAlarmBroadcastReceiverService.class);
		    screenManagementAlarmBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, screenManagementAlarmBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e("ScreenManagementAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}
