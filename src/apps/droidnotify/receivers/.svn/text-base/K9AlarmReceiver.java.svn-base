package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import apps.droidnotify.log.Log;
import apps.droidnotify.services.K9AlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for a broadcast of a new read K9 email request.
 * 
 * @author Camille Sévigny
 */
public class K9AlarmReceiver extends BroadcastReceiver {
	
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
		_debug = Log.getDebug();;
		if (_debug) Log.v("K9AlarmReceiver.onReceive()");
		try{
			Intent k9AlarmBroadcastReceiverServiceIntent = new Intent(context, K9AlarmBroadcastReceiverService.class);
		    k9AlarmBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
		    k9AlarmBroadcastReceiverServiceIntent.setAction(intent.getAction());
			WakefulIntentService.sendWakefulWork(context, k9AlarmBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e("K9AlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}
