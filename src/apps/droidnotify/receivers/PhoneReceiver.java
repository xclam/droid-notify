package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.PhoneBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for the phone state to change.
 * 
 * @author Camille Sévigny
 */
public class PhoneReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification that the phone state changed.
	 * This function starts the service that will handle the work or reschedules the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneReceiver.onReceive()");
		try{
			WakefulIntentService.sendWakefulWork(context, new Intent(context, PhoneBroadcastReceiverService.class));
		}catch(Exception ex){
			Log.e("PhoneReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}