package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.OnBootService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for the OnBoot event from the users phone.
 * 
 * @author Camille Sévigny
 */
public class OnBootReceiver extends BroadcastReceiver {

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
    
	/**
	 * Receives a notification that the phone was restarted.
	 * This function starts the service that will handle the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("OnBootReceiver.onReceive()");
		try{
			WakefulIntentService.sendWakefulWork(context, new Intent(context, OnBootService.class));
		}catch(Exception ex){
			Log.e("OnBootReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}