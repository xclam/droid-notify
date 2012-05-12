package apps.droidnotify.preferences.k9;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "K9 Customize" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class K9CustomizePreferenceActivity extends PreferenceActivity{
	
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
	    if (_debug) Log.v("K9CustomizePreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(getApplicationContext(), this);
	    this.addPreferencesFromResource(R.xml.k9_customize_preferences);
	    this.setContentView(R.layout.customize_preferences);
	}
	
}