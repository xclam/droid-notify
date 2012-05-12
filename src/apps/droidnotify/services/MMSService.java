package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.sms.SMSCommon;

/**
 * This class handles the work of processing incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSService() {
		super("MMSReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSReceiverService.MMSReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("MMSReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			Bundle mmsNotificationBundle = SMSCommon.getMMSMessagesFromDisk(context);
			if(mmsNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_MMS);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, mmsNotificationBundle);
				Common.startNotificationActivity(context, bundle);
			}else{
				if (_debug) Log.v("MMSReceiverService.doWakefulWork() No new MMSs were found. Exiting...");
			}
		}catch(Exception ex){
			Log.e("MMSReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}