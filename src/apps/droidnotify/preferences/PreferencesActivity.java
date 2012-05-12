package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.services.OnFirstRunService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This is the applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class PreferencesActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
    private SharedPreferences _preferences = null;
    private LinearLayout _enableAppLinearLayout = null;
    private CheckBox _enableAppCheckBox = null;
	private TextView _basicSettingsRow = null;	
	private TextView _localeSettingsRow = null;	
	private TextView _screenSettingsRow = null;	
	private TextView _customizeSettingsRow = null;	
	private TextView _notificationsSettingsRow = null;	
	private TextView _privacySettingsRow = null;
	private TextView _advancedSettingsRow = null;	
	private TextView _rateAppSettingsRow = null;	
	private TextView _emailDeveloperSettingsRow = null;	
	private TextView _aboutSettingsRow = null;	
	private TextView _upgradeSettingsRow = null;
	private String _currentLanguage = null;

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
	    if (_debug) Log.v("PreferencesActivity.onCreate()");
	    _context = this;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.preference_activity);
	    initLayoutItems();
	    setupRowAttributes();
	    setupRowActivities();	
	    Common.setInLinkedAppFlag(_context, false);
    	Common.setInQuickReplyAppFlag(_context, false);
    	_currentLanguage = _preferences.getString(Constants.LANGUAGE_KEY, Constants.LANGUAGE_DEFAULT);
    	setupFirstRun();
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    if(_debug) Log.v("PreferencesActivity.onResume()");
	    //Restart activity if the language was changed.
	    if(!_currentLanguage.equals(_preferences.getString(Constants.LANGUAGE_KEY, Constants.LANGUAGE_DEFAULT))){
	    	reloadPreferenceActivity();
	    }
	    super.onResume();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Initialize the layout items.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("PreferencesActivity.initLayoutItems()");	
		_enableAppLinearLayout = (LinearLayout)findViewById(R.id.enable_app_linear_layout);
		_enableAppCheckBox = (CheckBox)findViewById(R.id.enable_app_checkbox);
		_basicSettingsRow = (TextView)findViewById(R.id.row_basic);
		_localeSettingsRow = (TextView)findViewById(R.id.row_locale);
		_screenSettingsRow = (TextView)findViewById(R.id.row_screen);
		_customizeSettingsRow = (TextView)findViewById(R.id.row_customize);
		_notificationsSettingsRow = (TextView)findViewById(R.id.row_notifications);
		_privacySettingsRow = (TextView)findViewById(R.id.row_privacy);
		_advancedSettingsRow = (TextView)findViewById(R.id.row_advanced);
		_rateAppSettingsRow = (TextView)findViewById(R.id.row_rate_app);
		_emailDeveloperSettingsRow = (TextView)findViewById(R.id.row_email_developer);
		_aboutSettingsRow = (TextView)findViewById(R.id.row_about);
		_upgradeSettingsRow = (TextView)findViewById(R.id.row_upgrade);
	}
	
	/**
	 * Set up each preference row's attributes (background style etc.)
	 */
	private void setupRowAttributes(){
		if (_debug) Log.v("PreferencesActivity.setupRowAttributes()");	
		_enableAppLinearLayout.setBackgroundResource(R.drawable.preference_row_click);
		_basicSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_localeSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_screenSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_customizeSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_notificationsSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_privacySettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_advancedSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_rateAppSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_emailDeveloperSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_aboutSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_upgradeSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
	}

	/**
	 * Attach the click events to the preference rows.
	 */
	private void setupRowActivities(){
		if (_debug) Log.v("PreferencesActivity.Activities()");
		//Enable App Button. Initialize this checkbox.
		boolean appEnabled = _preferences.getBoolean(Constants.APP_ENABLED_KEY, true);
		_enableAppCheckBox.setChecked(appEnabled);
		enableUserPreferences(appEnabled);
		//Setup onClickListener
		_enableAppLinearLayout.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		boolean appEnabled = !_enableAppCheckBox.isChecked();
		    		_enableAppCheckBox.setChecked(appEnabled);
		    		SharedPreferences.Editor editor = _preferences.edit();
		    		editor.putBoolean(Constants.APP_ENABLED_KEY, appEnabled);
		    		editor.commit();
		    		//Enable/Disable all other buttons.
		    		enableUserPreferences(appEnabled);
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Enable App Button ERROR: " + ex.toString());
		    	}
        	}
		});
		//Set onCheckedChangeListener
		_enableAppCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() { 
			public void onCheckedChanged(CompoundButton checkBox, boolean isChecked){
	    		_enableAppCheckBox.setChecked(isChecked);
	    		SharedPreferences.Editor editor = _preferences.edit();
	    		editor.putBoolean(Constants.APP_ENABLED_KEY, isChecked);
	    		editor.commit();
	    		//Enable/Disable all other buttons.
	    		enableUserPreferences(isChecked);
			}
		}); 
		//Basic Button
		_basicSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, BasicPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Basic Button ERROR: " + ex.toString());
		    	}
        	}
		});
		//Locale Button
		_localeSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, LocalePreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Locale Button ERROR: " + ex.toString());
		    	}
        	}
		});		
		//Screen Button
		_screenSettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, ScreenPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Screen Button ERROR: " + ex.toString());
		    	}
	    	}
		});		
		//Customize Button
		_customizeSettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, CustomizePreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Customize Button ERROR: " + ex.toString());
		    	}
	    	}
		});		
		//Notifications Button
		_notificationsSettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, NotificationsPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Notifications Button ERROR: " + ex.toString());
		    	}
	    	}
		});		
		//Privacy Button
		_privacySettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, PrivacyPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Privacy Button ERROR: " + ex.toString());
		    	}
	    	}
		});
		//Advanced Button
		_advancedSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, AdvancedPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Advanced Button ERROR: " + ex.toString());
		    	}
        	}
		});		
		//Rate This App Preference/Button		
		boolean displayRateAppRow = true;
		if(!Log.getShowAndroidRateAppLink() && !Log.getShowAmazonRateAppLink()){
			displayRateAppRow = false;
		}
		if(displayRateAppRow){
			_rateAppSettingsRow.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view){
			    	try{
				    	String rateAppURL = "";
				    	if(Log.getShowAndroidRateAppLink()){
				    		rateAppURL = Constants.APP_ANDROID_URL;
				    	}else if(Log.getShowAmazonRateAppLink()){
				    		rateAppURL = Constants.APP_AMAZON_URL;
				    	}else{
				    		rateAppURL = "";
				    	}
			    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rateAppURL)));
			    	}catch(Exception ex){
		 	    		Log.e("PreferencesActivity() Rate App Button ERROR: " + ex.toString());
		 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_LONG).show();
			    	}
	           }
			});
		}else{
			ImageView rowDividerRateApp = (ImageView)findViewById(R.id.row_divider_rate_app);
			rowDividerRateApp.setVisibility(View.GONE);
			_rateAppSettingsRow.setVisibility(View.GONE);
		}
		//Email Developer Button
		_emailDeveloperSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
			    	Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
			    	sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Droid Notify Lite - App Feedback");
		    		startActivity(sendEmailIntent);
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Email Developer Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_email_app_error), Toast.LENGTH_LONG).show();
		    	}
           }
		});
		//About Button
		_aboutSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, AboutPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() About Button ERROR: " + ex.toString());
		    	}
        	}
		});
		//Upgrade Button
		boolean displayUpgradeRow = true;
		if(!Log.getShowAndroidRateAppLink() && !Log.getShowAmazonRateAppLink()){
			displayUpgradeRow = false;
		}
		if(displayUpgradeRow){
			_upgradeSettingsRow.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view){
			    	try{
			    		Bundle bundle = new Bundle();
						bundle.putInt(Constants.DIALOG_UPGRADE_TYPE, Constants.DIALOG_UPGRADE);
				    	Intent upgradeActivityIntent = new Intent(_context, UpgradePreferenceActivity.class);
				    	upgradeActivityIntent.putExtras(bundle);
			    		startActivity(upgradeActivityIntent);
			    	}catch(Exception ex){
		 	    		Log.e("PreferencesActivity() Upgrade Button ERROR: " + ex.toString());
			    	}
	        	}
			});
		}else{
			ImageView rowDividerUpgrade = (ImageView)findViewById(R.id.row_divider_upgrade);
			rowDividerUpgrade.setVisibility(View.GONE);
			_upgradeSettingsRow.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Enable or disable the user preferences.
	 * 
	 * @param appEnabled - The flag to set the user preferences with.
	 */
	private void enableUserPreferences(boolean appEnabled){
		if (_debug) Log.v("PreferencesActivity.enableUserPreferences()");
		_basicSettingsRow.setEnabled(appEnabled);
		_localeSettingsRow.setEnabled(appEnabled);
		_screenSettingsRow.setEnabled(appEnabled);
		_customizeSettingsRow.setEnabled(appEnabled);
		_notificationsSettingsRow.setEnabled(appEnabled);
		_privacySettingsRow.setEnabled(appEnabled);
		_advancedSettingsRow.setEnabled(appEnabled);
	}
	
	/**
	 * Reload Preference Activity
	 */
	public void reloadPreferenceActivity() {
		if (_debug) Log.v("PreferencesActivity.reloadPreferenceActivity()");
		try{
		    Intent intent = getIntent();
		    overridePendingTransition(0, 0);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
		    overridePendingTransition(0, 0);
		    startActivity(intent);
		}catch(Exception ex){
			Log.e("PreferencesActivity.reloadPreferenceActivity() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup the app when it is first run.
	 */
	private void setupFirstRun(){
		if (_debug) Log.v("PreferencesActivity.setupFirstRun()");
		try{
			if(!_preferences.getBoolean(Constants.FIRST_RUN_KEY, false)){	
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(_context).edit();
				editor.putBoolean(Constants.FIRST_RUN_KEY, true);
				editor.commit();
				WakefulIntentService.sendWakefulWork(_context, new Intent(_context, OnFirstRunService.class));
			}
		}catch(Exception ex){
			Log.e("PreferencesActivity.setupFirstRun() ERROR: " + ex.toString());
		}
	}
	
}
