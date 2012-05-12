package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.preferences.calendar.CalendarPreferenceActivity;
import apps.droidnotify.preferences.k9.K9PreferenceActivity;
import apps.droidnotify.preferences.phone.PhonePreferenceActivity;
import apps.droidnotify.preferences.sms.MMSPreferenceActivity;
import apps.droidnotify.preferences.sms.SMSPreferenceActivity;

/**
 * This is the "Notifications" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class NotificationsPreferenceActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private TextView _smsSettingsRow = null;	
	private TextView _mmsSettingsRow = null;	
	private TextView _missedCallsSettingsRow = null;	
	private TextView _calendarSettingsRow = null;	
	private TextView _k9SettingsRow = null;	
	private TextView _twitterSettingsRow = null;
	private TextView _facebookSettingsRow = null;
	private TextView _moreSettingsRow = null;

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
	    if (_debug) Log.v("NotificationsPreferenceActivity.onCreate()");
		_context = this;
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.notification_preference_activity);
	    initLayoutItems();
	    setupRowAttributes();
	    setupRowActivities();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Initialize the layout items.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("NotificationsPreferenceActivity.initLayoutItems()");		
		_smsSettingsRow = (TextView)findViewById(R.id.row_sms);
		_mmsSettingsRow = (TextView)findViewById(R.id.row_mms);
		_missedCallsSettingsRow = (TextView)findViewById(R.id.row_missed_calls);
		_calendarSettingsRow = (TextView)findViewById(R.id.row_calendar);	
		_k9SettingsRow = (TextView)findViewById(R.id.row_k9);
		_twitterSettingsRow = (TextView)findViewById(R.id.row_twitter);
		_facebookSettingsRow = (TextView)findViewById(R.id.row_facebook);
		_moreSettingsRow = (TextView)findViewById(R.id.row_more);
	}
	
	/**
	 * Set up each preference row's attributes (background style etc.)
	 */
	private void setupRowAttributes(){
		if (_debug) Log.v("NotificationsPreferenceActivity.setupRowAttributes()");	
		_smsSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_mmsSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_missedCallsSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_calendarSettingsRow.setBackgroundResource(R.drawable.preference_row_click);	
		_k9SettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_twitterSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_facebookSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_moreSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
	}

	/**
	 * Attach the click events to the preference rows.
	 */
	private void setupRowActivities(){
		if (_debug) Log.v("NotificationsPreferenceActivity.Activities()");
		//SMS Button
		_smsSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, SMSPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() SMS Button ERROR: " + ex.toString());
		    	}
        	}
		});
		//MMS Button
		_mmsSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, MMSPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() MMS Button ERROR: " + ex.toString());
		    	}
        	}
		});		
		//Missed Calls Button
		_missedCallsSettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, PhonePreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() Missed Calls Button ERROR: " + ex.toString());
		    	}
	    	}
		});		
		//Calendar Button
		_calendarSettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, CalendarPreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() Calendar Button ERROR: " + ex.toString());
		    	}
	    	}
		});		
		//K9 Button
		_k9SettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{
		    		startActivity(new Intent(_context, K9PreferenceActivity.class));
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() K9 Button ERROR: " + ex.toString());
		    	}
	    	}
		});		
		//Twitter Button
		_twitterSettingsRow.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
		    	try{	    		
	    			Bundle bundle = new Bundle();
	    			bundle.putInt(Constants.DIALOG_UPGRADE_TYPE, Constants.DIALOG_FEATURE_PRO_ONLY);
			    	Intent upgradeActivityIntent = new Intent(_context, UpgradePreferenceActivity.class);
			    	upgradeActivityIntent.putExtras(bundle);
		    		startActivity(upgradeActivityIntent);
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() Twitter Button ERROR: " + ex.toString());
		    	}
	    	}
		});
		//Facebook Button
		_facebookSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{	    		
	    			Bundle bundle = new Bundle();
	    			bundle.putInt(Constants.DIALOG_UPGRADE_TYPE, Constants.DIALOG_FEATURE_PRO_ONLY);
			    	Intent upgradeActivityIntent = new Intent(_context, UpgradePreferenceActivity.class);
			    	upgradeActivityIntent.putExtras(bundle);
		    		startActivity(upgradeActivityIntent);
		    	}catch(Exception ex){
	 	    		Log.e("NotificationsPreferenceActivity() Facebook Button ERROR: " + ex.toString());
		    	}
        	}
		});
		//More Button
		if(Common.packageExists(_context, "apps.droidnotifyplus")){
			_moreSettingsRow.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view){
			    	try{
			    		Intent intent = new Intent();
			    		intent.setComponent(new ComponentName("apps.droidnotifyplus", "apps.droidnotifyplus.preferences.selectnotifications.SelectNotificationsPreferenceActivity"));
			    		startActivity(intent);
			    	}catch(Exception ex){
		 	    		Log.e("NotificationsPreferenceActivity() More Button ERROR: " + ex.toString());
			    	}
	        	}
			});
		}else{
			_moreSettingsRow.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view){
			    	try{	    		
			    		startActivity(new Intent(_context, AddOnsActivity.class));
			    	}catch(Exception ex){
		 	    		Log.e("NotificationsPreferenceActivity() More Button ERROR: " + ex.toString());
			    	}
	        	}
			});
		}
	}
	
}

