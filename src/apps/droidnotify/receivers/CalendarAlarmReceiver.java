package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.CalendarAlarmReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for scheduled notifications to check the users calendars.
 * 
 * @author Camille Sévigny
 */
public class CalendarAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification of a Calendar Event.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarAlarmReceiver.onReceive()");
		try{
			WakefulIntentService.sendWakefulWork(context, new Intent(context, CalendarAlarmReceiverService.class));
		}catch(Exception ex){
			Log.e("CalendarAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}