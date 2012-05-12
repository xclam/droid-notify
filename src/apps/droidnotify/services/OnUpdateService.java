package apps.droidnotify.services;

import android.content.Intent;

import apps.droidnotify.log.Log;

public class OnUpdateService extends WakefulIntentService {
	
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
	public OnUpdateService() {
		super("OnUpdateService.OnUpdateService()");
		_debug = Log.getDebug();
		if (_debug) Log.v("OnUpdateService.OnUpdateService()");
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
	protected void doWakefulWork(Intent intent){
		if (_debug) Log.v("OnUpdateService.doWakefulWork()");
		try{

		}catch(Exception ex){
			Log.e("OnUpdateService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}