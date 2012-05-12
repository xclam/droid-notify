package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;

/**
 * This class handles scheduled Missed Call notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class PhoneService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor
	 */
	public PhoneService() {
		super("PhoneService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneService.PhoneService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Missed Call.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("PhoneService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			Bundle missedCallNotificationBundle = PhoneCommon.getMissedCalls(context);
			if(missedCallNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PHONE);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, missedCallNotificationBundle);
				Common.startNotificationActivity(context, bundle);
			}else{
				if (_debug) Log.v("PhoneService.doWakefulWork() No missed calls were found. Exiting...");
			}
		}catch(Exception ex){
			Log.e("PhoneService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}