package apps.droidnotify.preferences.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "Phone Notifications" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class PhonePreferenceActivity extends PreferenceActivity{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
	
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
	    if (_debug) Log.v("PhonePreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.missed_calls_preferences);
	    this.setContentView(R.layout.missed_calls_preferences);
	    setupCustomPreferences();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	private void setupCustomPreferences(){
	    if (_debug) Log.v("PhonePreferenceActivity.setupCustomPreferences()");
		//Status Bar Notification Settings Preference/Button
		Preference statusBarNotificationSettingsPref = (Preference)findPreference(Constants.SETTINGS_STATUS_BAR_NOTIFICATIONS_PREFERENCE);
		statusBarNotificationSettingsPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, PhoneStatusBarNotificationsPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e("PhonePreferenceActivity() Status Bar Notifications Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Customize Preference/Button
		Preference customizePref = (Preference)findPreference(Constants.SETTINGS_CUSTOMIZE_PREFERENCE);
		customizePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, PhoneCustomizePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e("PhonePreferenceActivity() Customize Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
}