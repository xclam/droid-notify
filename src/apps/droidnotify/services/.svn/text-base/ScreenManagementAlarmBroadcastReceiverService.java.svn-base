package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;

import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

public class ScreenManagementAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public ScreenManagementAlarmBroadcastReceiverService() {
		super("ScreenManagementAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("ScreenManagementAlarmBroadcastReceiverService.ScreenManagementAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("ScreenManagementAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			//Check to see if the user is in a linked app. If they are, do not release the wakelock or keyguard.
			Context context = this.getApplicationContext();
			if(Common.isUserInLinkedApp(context) || Common.isUserInQuickReplyApp(context)){
				//Do not release the wakelock or keyguard.
			}else{
				//Release the KeyguardLock & WakeLock
				Common.clearKeyguardLock();
				Common.clearWakeLock();
			}
		}catch(Exception ex){
			Log.e("ScreenManagementAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}