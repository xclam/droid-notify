package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.DateFormat;

import apps.droidnotify.log.Log;
import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Constants;

public class OnFirstRunService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
    private SharedPreferences _preferences = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public OnFirstRunService(){
		super("OnFirstRunService");
		_debug = Log.getDebug();
		if (_debug) Log.v("OnFirstRunService.OnFirstRunService()");
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
		if (_debug) Log.v("OnFirstRunService.doWakefulWork()");
		try{	   
			_context = this.getApplicationContext();
		    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			startCalendarAlarmManager(System.currentTimeMillis() + (60 * 1000));
			checkSystemDateTimeFormat();
		}catch(Exception ex){
			Log.e("OnFirstRunService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Start the Calendar Alarm Manager.
	 * 
	 * @param alarmStartTime - The time to start the alarm.
	 */
	private void startCalendarAlarmManager(long alarmStartTime){
		if (_debug) Log.v("OnFirstRunService.startCalendarAlarmManager()");
		//Make sure that this user preference has been set and initialized.
		initUserCalendarsPreference();
		//Schedule the reading of the calendar events.
		CalendarCommon.startCalendarAlarmManager(_context, alarmStartTime);
	}
	
	/**
	 * Initializes the calendars which will be checked for event notifications.
	 * This sets the user preference to check all available calendars.
	 */
	private void initUserCalendarsPreference(){
		if (_debug) Log.v("OnFirstRunService.initUserCalendarsPreference()");
    	String availableCalendarsInfo = CalendarCommon.getAvailableCalendars(_context);
    	if(availableCalendarsInfo == null){
    		return;
    	}
    	//Only initialize the calendars if the user preference doesn't exist yet.
    	if(_preferences.getString(Constants.CALENDAR_SELECTION_KEY, null) == null){
	    	String[] calendarsInfo = availableCalendarsInfo.split(",");
	    	StringBuilder calendarSelectionPreference = new StringBuilder();
	    	for(String calendarInfo : calendarsInfo){
	    		String[] calendarInfoArray = calendarInfo.split("\\|");
	    		if(!calendarSelectionPreference.toString().equals("")) calendarSelectionPreference.append("|");
	    		calendarSelectionPreference.append(calendarInfoArray[0]);
	    	}
	    	SharedPreferences.Editor editor = _preferences.edit();
	    	editor.putString(Constants.CALENDAR_SELECTION_KEY, calendarSelectionPreference.toString());
	    	editor.commit();
    	}
	}	
	
	/**
	 * A first time installation check and update of the Date & Time format settings.
	 */
	private void checkSystemDateTimeFormat(){
		if (_debug) Log.v("OnFirstRunService.checkSystemDateTimeFormat()");
		try{
			SharedPreferences.Editor editor = _preferences.edit();
			String systemDateFormat = Settings.System.getString(_context.getContentResolver(), Settings.System.DATE_FORMAT);
		    String systemHourFormat = Settings.System.getString(_context.getContentResolver(), Settings.System.TIME_12_24);
		    if(systemDateFormat != null && !systemDateFormat.equals("")){
		    	if(systemDateFormat.equals("MM-dd-yyyy")){
		    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_0));
		    	}else if(systemDateFormat.equals("dd-MM-yyyy")){
		    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_6));
		    	}else if(systemDateFormat.equals("yyyy-MM-dd")){
		    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_12));
		    	}
		    }else{
		    	systemDateFormat = String.valueOf(DateFormat.getDateFormatOrder(_context));
		    	if(systemDateFormat.equals("Mdy")){
		    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_0));
		    	}else if(systemDateFormat.equals("dMy")){
		    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_6));
		    	}else if(systemDateFormat.equals("yMd")){
		    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_12));
		    	}
		    }
		    if(systemHourFormat != null && !systemHourFormat.equals("")){
			    if(systemHourFormat.equals("12")){
					editor.putString(Constants.TIME_FORMAT_KEY, String.valueOf(Constants.TIME_FORMAT_12_HOUR));
			    }else{
					editor.putString(Constants.TIME_FORMAT_KEY, String.valueOf(Constants.TIME_FORMAT_24_HOUR));
			    }
		    }
			editor.commit();
		}catch(Exception ex){
    		Log.e("MainPreferenceActivity.checkSystemDateTimeFormat() ERROR: " + ex.toString());
    	}	
	}
		
}