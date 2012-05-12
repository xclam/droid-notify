package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.MMSBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * This function starts the service that will handle the work or reschedules the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSReceiver.onReceive()");
		try{
			WakefulIntentService.sendWakefulWork(context, new Intent(context, MMSBroadcastReceiverService.class));
		}catch(Exception ex){
			Log.e("MMSReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}