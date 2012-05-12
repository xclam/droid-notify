package apps.droidnotify.services;

import android.content.Intent;

import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.log.Log;

/**
 * This class handles the checking of the users calendars.
 * 
 * @author CommonsWare edited by Camille Sévigny
 *
 */
public class CalendarAlarmReceiverService extends WakefulIntentService {
    
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class constructor.
	 */
	public CalendarAlarmReceiverService() {
		super("CalendarAlarmReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarAlarmReceiverService.CalendarAlarmReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * This service function should read the users calendar events for the next 25 hours and start alarms for each one individually.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("CalendarAlarmReceiverService.doWakefulWork()");
		//Read the users calendar(s) and events.
		CalendarCommon.readCalendars(getApplicationContext());
	}

}