package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.GenericNotificationService;
import apps.droidnotify.services.WakefulIntentService;
import apps.droidnotify.common.Constants;

public class GenericNotificationReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification from an external source.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("GenericNotificationReceiver.onReceive()");
		try{		
			Intent genericNotificaitonIntent = new Intent(context, GenericNotificationService.class);
			genericNotificaitonIntent.putExtras(intent.getExtras());
			genericNotificaitonIntent.putExtra(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_GENERIC);
			WakefulIntentService.sendWakefulWork(context, genericNotificaitonIntent);
		}catch(Exception ex){
			Log.e("GenericNotificationReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}