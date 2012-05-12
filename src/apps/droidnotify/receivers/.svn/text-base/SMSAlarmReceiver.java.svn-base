package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.SMSAlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for scheduled MMS notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class SMSAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSAlarmReceiver.onReceive()");
		try{
			Intent smsAlarmBroadcastReceiverServiceIntent = new Intent(context, SMSAlarmBroadcastReceiverService.class);
		    smsAlarmBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, smsAlarmBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e("SMSAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}