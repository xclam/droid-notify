package apps.droidnotify.preferences.phone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "Phone Status Bar Notifications" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class PhoneStatusBarNotificationsPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
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
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (_debug) Log.v("PhoneStatusBarNotificationsPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
		if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateVibratePreferences();
		}
	}
	
	//================================================================================
	// Protected Methods
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
	    if (_debug) Log.v("PhoneStatusBarNotificationsPreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	    this.addPreferencesFromResource(R.xml.missed_calls_status_bar_notifications_preferences);
	    this.setContentView(R.layout.status_bar_notifications_preferences);
	}			
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    if(_debug) Log.v("PhoneStatusBarNotificationsPreferenceActivity.onResume()");
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    if(_debug) Log.v("PhoneStatusBarNotificationsPreferenceActivity.onPause()");
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Updates the vibrate preferences based on the vibrate setting.
	 */
	private void updateVibratePreferences(){
		if (_debug) Log.v("PhoneStatusBarNotificationsPreferenceActivity.updateVibratePreferences()");
		try{
			ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
			CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
			if(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
				if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
				if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
			}else{
				if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
				if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
			}
		}catch(Exception ex){
			Log.e("PhoneStatusBarNotificationsPreferenceActivity.updateVibratePreferences() ERROR: " + ex.toString());
		}
	}
	
}