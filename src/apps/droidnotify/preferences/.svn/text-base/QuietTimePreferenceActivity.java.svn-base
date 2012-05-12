package apps.droidnotify.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class QuietTimePreferenceActivity extends PreferenceActivity{
	
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
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("QuietTimePreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    this.addPreferencesFromResource(R.xml.quiet_time_preferences);
	    this.setContentView(R.layout.quiet_time_preferences);
	    setupCustomPreferences();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	private void setupCustomPreferences(){
	    if (_debug) Log.v("QuietTimePreferenceActivity.setupCustomPreferences()");
		//Export Preferences Preference/Button
		Preference quietTimePref = (Preference)findPreference("quiet_time_blackout_period_preference");
		quietTimePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
		    		showQuietTimePeriodDialog();
		    	}catch(Exception ex){
	 	    		Log.e("QuietTimePreferenceActivity() Quiet Time Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
	}
	
	/**
	 * Display the dialog window that allows the user to set the quiet time hours.
	 */
	private void showQuietTimePeriodDialog() {
		if (_debug) Log.v("MainPreferenceActivity.showQuietTimePeriodDialog()");
	    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.quiet_time_period_dialog, null);
		final TimePicker startTimePicker = (TimePicker) view.findViewById(R.id.start_time_picker);
		final TimePicker stopTimePicker = (TimePicker) view.findViewById(R.id.stop_time_picker);
		//Sets the view format based on the users time format preference.
		if(_preferences.getString(Constants.TIME_FORMAT_KEY, Constants.TIME_FORMAT_DEFAULT).equals(Constants.TIME_FORMAT_24_HOUR)){
			startTimePicker.setIs24HourView(true);
			stopTimePicker.setIs24HourView(true);
		}else{
			startTimePicker.setIs24HourView(false);
			stopTimePicker.setIs24HourView(false);
		}
		//Initialize the TimePickers
		String startTime = _preferences.getString(Constants.QUIET_TIME_START_TIME_KEY, "");
		String stopTime = _preferences.getString(Constants.QUIET_TIME_STOP_TIME_KEY, "");
		if(!startTime.equals("")){
			String[] startTimeArray = startTime.split("\\|");
			if(startTimeArray.length == 2){
				startTimePicker.setCurrentHour(Integer.parseInt(startTimeArray[0]));
				startTimePicker.setCurrentMinute(Integer.parseInt(startTimeArray[1]));
			}
		}
		if(!stopTime.equals("")){
			String[] stopTimeArray = stopTime.split("\\|");
			if(stopTimeArray.length == 2){
				stopTimePicker.setCurrentHour(Integer.parseInt(stopTimeArray[0]));
				stopTimePicker.setCurrentMinute(Integer.parseInt(stopTimeArray[1]));
			}
		}
		//Build & Display Dialog
		AlertDialog.Builder quietTimePeriodAlertBuilder = new AlertDialog.Builder(_context);		
	    try{
	    	quietTimePeriodAlertBuilder.setIcon(android.R.drawable.ic_dialog_info); 
        }catch(Exception ex){
        	//Don't set the icon if this fails.
        }
		quietTimePeriodAlertBuilder.setTitle(R.string.preference_quiet_time_quiet_period_title);
		quietTimePeriodAlertBuilder.setView(view);
		quietTimePeriodAlertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SharedPreferences.Editor editor = _preferences.edit();
	        	editor.putString(Constants.QUIET_TIME_START_TIME_KEY, startTimePicker.getCurrentHour() + "|" + startTimePicker.getCurrentMinute());
	        	editor.putString(Constants.QUIET_TIME_STOP_TIME_KEY, stopTimePicker.getCurrentHour() + "|" + stopTimePicker.getCurrentMinute());
	            editor.commit();
				Toast.makeText(_context, _context.getString(R.string.preference_quiet_time_period_set), Toast.LENGTH_LONG).show();
			}
		});
		quietTimePeriodAlertBuilder.show();
	}
	
}