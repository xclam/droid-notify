package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.CalendarNotificationAlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for scheduled Calendar Event notifications that we want to display.
 * 
 * @author Camille S�vigny
 */
public class CalendarNotificationAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification that the Calendar should be checked.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarNotificationAlarmReceiver.onReceive()");
		try{
		    Intent calendarNotificationAlarmBroadcastReceiverServiceIntent = new Intent(context, CalendarNotificationAlarmBroadcastReceiverService.class);
		    calendarNotificationAlarmBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, calendarNotificationAlarmBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e("CalendarNotificationAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}