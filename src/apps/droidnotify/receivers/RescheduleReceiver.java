package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.RescheduleService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for a rescheduled notification.
 * 
 * @author Camille Sévigny
 */
public class RescheduleReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification that a rescheduled notification is ready to be shown.
	 * This function starts the service that will handle the work or reschedules the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("RescheduleReceiver.onReceive()");
		try{
			Intent rescheduleBroadcastReceiverServiceIntent = new Intent(context, RescheduleService.class);
		    rescheduleBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, rescheduleBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e("RescheduleReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}