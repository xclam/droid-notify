package apps.droidnotify.preferences.calendar;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "Calendar Customize" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class CalendarCustomizePreferenceActivity extends PreferenceActivity{
	
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
	    if (_debug) Log.v("CalendarCustomizePreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(getApplicationContext(), this);
	    this.addPreferencesFromResource(R.xml.calendar_customize_preferences);
	    this.setContentView(R.layout.customize_preferences);
	}
	
}