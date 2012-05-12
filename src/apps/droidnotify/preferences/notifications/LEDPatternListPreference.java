package apps.droidnotify.preferences.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Constants;

/**
 * A custom ListPreference class that handles custom LED pattern selection/creation.
 * 
 * @author Camille Sévigny
 */
public class LEDPatternListPreference extends ListPreference {
	
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
	public LEDPatternListPreference(Context context) {
		super(context);
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	}

	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 * @param attrs
	 */
	public LEDPatternListPreference(Context context, AttributeSet attrs) {
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
		if (result) {
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT).equals(Constants.CUSTOM_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = -2;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT).equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_SMS;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT).equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_MMS;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT).equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_CALENDAR;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT).equals(Constants.K9_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE)) {
				_notificationType = Constants.NOTIFICATION_TYPE_K9;
				showDialog();
			}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the dialog window that allows the user to enter the vibrate pattern they wish to have.
	 */
	private void showDialog() {
	    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.led_pattern_dialog, null);
	    String ledCustomPattern = null;
	    switch(_notificationType){
		    case -2:{
		    	ledCustomPattern = _preferences.getString(Constants.CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT);
		    }
		    case Constants.NOTIFICATION_TYPE_SMS:{
		    	 ledCustomPattern = _preferences.getString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT);
		    }
		    case Constants.NOTIFICATION_TYPE_MMS:{
		    	ledCustomPattern = _preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT);
		    }
		    case Constants.NOTIFICATION_TYPE_PHONE:{
		    	ledCustomPattern = _preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT);
		    }
		    case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	ledCustomPattern = _preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT);
		    }
		    case Constants.NOTIFICATION_TYPE_K9:{
		    	ledCustomPattern = _preferences.getString(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT);
		    }
	    }
	    String[] ledCustomPatternArray = ledCustomPattern.split(",");
		final EditText customOnLEDPatternEditText = (EditText) view.findViewById(R.id.ledPatternOnEditText);
		final EditText customOffLEDPatternEditText = (EditText) view.findViewById(R.id.ledPatternOffEditText);
		customOnLEDPatternEditText.setText(ledCustomPatternArray[0]);
		customOffLEDPatternEditText.setText(ledCustomPatternArray[1]);
		AlertDialog.Builder ledPatternAlertBuilder = new AlertDialog.Builder(_context);
        try{
        	ledPatternAlertBuilder.setIcon(android.R.drawable.ic_dialog_info);
        }catch(Exception ex){
        	//Don't set the icon if this fails.
        }
		ledPatternAlertBuilder.setTitle(R.string.led_pattern_title);
		ledPatternAlertBuilder.setView(view);
		ledPatternAlertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String customLEDPattern = customOnLEDPatternEditText.getText() + "," + customOffLEDPatternEditText.getText();
				if (validateLEDPattern(customLEDPattern)) {
					SharedPreferences.Editor editor = _preferences.edit();
		        	switch(_notificationType){
					    case -2:{
					    	editor.putString(Constants.CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, customLEDPattern);
					    }
					    case Constants.NOTIFICATION_TYPE_SMS:{
					    	editor.putString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, customLEDPattern);
					    }
					    case Constants.NOTIFICATION_TYPE_MMS:{
					    	editor.putString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, customLEDPattern);
					    }
					    case Constants.NOTIFICATION_TYPE_PHONE:{
					    	editor.putString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, customLEDPattern);
					    }
					    case Constants.NOTIFICATION_TYPE_CALENDAR:{
					    	editor.putString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, customLEDPattern);
					    }
					    case Constants.NOTIFICATION_TYPE_K9:{
					    	editor.putString(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY, customLEDPattern);
					    }
				    }
		            editor.commit();
					Toast.makeText(_context, _context.getString(R.string.preference_led_pattern_set), Toast.LENGTH_LONG).show();
		        } else {
		        	Toast.makeText(_context, _context.getString(R.string.preference_led_pattern_error), Toast.LENGTH_LONG).show();
		        }
			}
		});
		ledPatternAlertBuilder.show();
	}
	
	/**
	 * Parse an led pattern and verify if it's valid or not.
	 * 
	 * @param ledPattern - The led pattern to verify.
	 * 
	 * @return boolean - Returns True if the led pattern is valid.
	 */
	private boolean validateLEDPattern(String ledPattern){
		String[] ledPatternArray = ledPattern.split(",");
		int arraySize = ledPatternArray.length;
	    for (int i = 0; i < arraySize; i++) {
	    	long ledLength = 0;
	    	try {
	    		ledLength = Long.parseLong(ledPatternArray[i].trim());
	    	} catch (Exception ex) {
	    		return false;
	    	}
	    	if(ledLength < 0){
	    		return false;
	    	}
	    }
		return true;
	}
	
}