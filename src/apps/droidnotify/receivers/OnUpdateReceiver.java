package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.OnUpdateService;
import apps.droidnotify.services.WakefulIntentService;

public class OnUpdateReceiver extends BroadcastReceiver {

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
    
	/**
	 * Receives a notification that the app was updated.
	 * This function starts the service that will handle the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("OnUpdateReceiver.onReceive()");
		try{		
			if(intent.getDataString().contains("apps.droidnotify")){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, OnUpdateService.class));
			}
		}catch(Exception ex){
			Log.e("OnUpdateReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}