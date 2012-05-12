package apps.droidnotify.preferences.sms;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "SMS Customize" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class SMSCustomizePreferenceActivity extends PreferenceActivity{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
	
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
	    if (_debug) Log.v("SMSCustomizePreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(getApplicationContext(), this);
	    this.addPreferencesFromResource(R.xml.sms_customize_preferences);
	    this.setContentView(R.layout.customize_preferences);
	}
	
}