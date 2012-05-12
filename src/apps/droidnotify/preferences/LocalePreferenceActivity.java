package apps.droidnotify.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class LocalePreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
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
		if (_debug) Log.v("LocalePreferenceActivity.onSharedPreferenceChanged() Key: " + key);
		if(key.equals(Constants.LANGUAGE_KEY)){
			reloadPreferenceActivity();
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
	    if (_debug) Log.v("LocalePreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	    this.addPreferencesFromResource(R.xml.locale_preferences);
	    this.setContentView(R.layout.locale_preferences);
	}
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    if(_debug) Log.v("LocalePreferenceActivity.onResume()");
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    if(_debug) Log.v("LocalePreferenceActivity.onPause()");
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Reload Preference Activity
	 */
	public void reloadPreferenceActivity() {
		if (_debug) Log.v("LocalePreferenceActivity.reloadPreferenceActivity()");
		try{
		    Intent intent = getIntent();
		    overridePendingTransition(0, 0);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
		    overridePendingTransition(0, 0);
		    startActivity(intent);
		}catch(Exception ex){
			Log.e("LocalePreferenceActivity.reloadPreferenceActivity() ERROR: " + ex.toString());
		}
	}
	
}
