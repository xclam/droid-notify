package apps.droidnotify.preferences.notifications;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Constants;

/**
 * A custom ListPreference class that handles custom LED color selection/creation.
 * 
 * @author Camille Sévigny
 */
public class LEDColorListPreference extends ListPreference{
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
    private SharedPreferences _preferences = null;
    private int _notificationType = -1;

	//================================================================================
	// Constructors
	//================================================================================
    
	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 */
	public LEDColorListPreference(Context context) {
		super(context);
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	}
   
	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 */
	public LEDColorListPreference(Context context, AttributeSet attrs) {
		  super(context, attrs);
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	}

	//================================================================================
	// Protected Methods
	//================================================================================
		
	/**
	 * Override the onDialogClosed method.
	 * 
	 * @param result - A boolean result that indicates how the dialog window was closed.
	 */
	@Override
	protected void onDialogClosed(boolean result) {
		super.onDialogClosed(result);
		if(result){
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.CUSTOM_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = -2;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_SMS;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_MMS;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_CALENDAR;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.K9_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_K9;
				showDialog();
			}
		}
	}
		
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the dialog window that allows the user to enter the led color they wish to have.
	 */
	private void showDialog() {
		int initialColor = 0;
		try {
			switch(_notificationType){
			    case -2:{
			    	initialColor = _preferences.getInt(Constants.CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_SMS:{
			    	initialColor = _preferences.getInt(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_MMS:{
			    	initialColor = _preferences.getInt(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_PHONE:{
			    	initialColor = _preferences.getInt(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_CALENDAR:{
			    	initialColor = _preferences.getInt(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_K9:{
			    	initialColor = _preferences.getInt(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
		    }
		}catch(Exception ex){
			initialColor =  Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT);
		}
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(_context, initialColor, new OnAmbilWarnaListener(){
		        public void onOk(AmbilWarnaDialog dialog, int color){
					SharedPreferences.Editor editor = _preferences.edit();
		        	switch(_notificationType){
					    case -2:{
					    	editor.putInt(Constants.CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, color);
					    }
					    case Constants.NOTIFICATION_TYPE_SMS:{
					    	editor.putInt(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, color);
					    }
					    case Constants.NOTIFICATION_TYPE_MMS:{
					    	editor.putInt(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, color);
					    }
					    case Constants.NOTIFICATION_TYPE_PHONE:{
					    	editor.putInt(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, color);
					    }
					    case Constants.NOTIFICATION_TYPE_CALENDAR:{
					    	editor.putInt(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, color);
					    }
					    case Constants.NOTIFICATION_TYPE_K9:{
					    	editor.putInt(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, color);
					    }
				    }
			        editor.commit();
					Toast.makeText(_context, _context.getString(R.string.preference_led_color_set), Toast.LENGTH_LONG).show();
		        }
		                
		        public void onCancel(AmbilWarnaDialog dialog){
		        	//Do Nothing.
		        }
		});
		dialog.show();
	}

}
