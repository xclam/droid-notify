package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.SMSBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiver extends BroadcastReceiver{

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives the incomming SMS message.
	 * This function starts the service that will handle the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiver.onReceive()");
		try{
			Intent smsBroadcastReceiverServiceIntent = new Intent(context, SMSBroadcastReceiverService.class);
			smsBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, smsBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e("SMSReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}