package apps.droidnotify.preferences.calendar;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.R;

/**
 * A Preference that displays a list of entries as
 * a dialog and allows multiple selections
 * 
 * This preference will store a string into the SharedPreferences. This string will be the values selected
 * from the setEntryValues(CharSequence[]) array.
 */
public class SelectCalendarListPreference extends ListPreference {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
    private Context _context = null;
    private boolean[] _clickedDialogEntryIndices = null;

	//================================================================================
	// Constructors
	//================================================================================
    
    /**
     * Class Constructor.
     * 
     * @param context - Context
     */
    public SelectCalendarListPreference(Context context) {
        this(context, null);
        _debug = Log.getDebug();
        if (_debug) Log.v("SelectCalendarListPreference(Context context)");
        _context = context;
    }
    
    /**
     * Class Constructor.
     * 
     * @param context - Context
     * @param attrs - AttributeSet
     */
    public SelectCalendarListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        _debug = Log.getDebug();
        if (_debug) Log.v("SelectCalendarListPreference(Context context, AttributeSet attrs)");
        _context = context;
    }
 
	//================================================================================
	// Public Methods
	//================================================================================
    
    /**
     * Set the entry values to the ListPreference object.
     * 
     * @param entries
     */
    @Override
    public void setEntries(CharSequence[] entries) {
    	super.setEntries(entries);
    	if (_debug) Log.v("SelectCalendarListPreference.setEntries()");
    	_clickedDialogEntryIndices = new boolean[entries.length];
    }
 
    /**
     * Set the entryValues values to the ListPreference object.
     * 
     * @param entries
     */
    @Override
    public void setEntryValues(CharSequence[] entryValues) {
    	super.setEntryValues(entryValues);
    	if (_debug) Log.v("SelectCalendarListPreference.setEntryValues()");
    	_clickedDialogEntryIndices = new boolean[entryValues.length];
    }
    
    /**
     * Cusstom work done to initialize the ListPreference.
     * Read the calendars and add the names and IDs to the ListPreference.
     * 
     * @param builder - Dialog Builder.
     */
    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
    	if (_debug) Log.v("SelectCalendarListPreference.onPrepareDialogBuilder()");
    	String availableCalendarsInfo = CalendarCommon.getAvailableCalendars(_context);
    	if(availableCalendarsInfo == null){
    		Toast.makeText(_context, _context.getString(R.string.app_android_calendars_not_found_error), Toast.LENGTH_LONG).show();
    		return;
    	}
    	String[] calendarsInfo = availableCalendarsInfo.split(",");
    	ArrayList<String> calendarEntries = new ArrayList<String>();
    	ArrayList<String> calendarEntryValues = new ArrayList<String>();
    	for(String calendarInfo : calendarsInfo){
    		String[] calendarInfoArray = calendarInfo.split("\\|");
    		calendarEntryValues.add(calendarInfoArray[0]);
    		calendarEntries.add(calendarInfoArray[1]);
    	}
    	CharSequence[] entries = calendarEntries.toArray(new String[] {});
    	CharSequence[] entryValues = calendarEntryValues.toArray(new String[] {});
    	setEntries(entries);
    	setEntryValues(entryValues);
        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, _clickedDialogEntryIndices, 
                new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int index, boolean value) {
						_clickedDialogEntryIndices[index] = value;
					}
        });
    }
    
	//================================================================================
	// Private Methods
	//================================================================================

    /**
     * Parse the stored values into an array.
     * 
     * @param value - The currently stored preference value.
     * 
     * @return String[] - An array of parsed values.
     */
    private String[] parseStoredValue(String value) {
    	if (_debug) Log.v("SelectCalendarListPreference.parseStoredValue() value: " + value);
    	if(value == null){
    		return null;
    	}else if(value.equals("")){
			return null;
		}else{
			return value.split("\\|");
		}
    }
    
    /**
     * Read the stored preference value and set the current ListPreference to the values stored.
     */
    private void restoreCheckedEntries() {
    	if (_debug) Log.v("SelectCalendarListPreference.restoreCheckedEntries()");
    	CharSequence[] entryValues = getEntryValues();
    	String[] preferenceValues = parseStoredValue(getValue());
    	if (preferenceValues != null) {
    		int preferenceValuesLength = preferenceValues.length;
        	for (int j=0; j<preferenceValuesLength; j++) {
        		String preferenceValue = preferenceValues[j].trim();
        		int entryValuesLength = entryValues.length;
            	for (int i=0; i<entryValuesLength; i++) {
            		CharSequence entry = entryValues[i];
                	if (entry.equals(preferenceValue)) {
            			_clickedDialogEntryIndices[i] = true;
            			break;
            		}
            	}
        	}
    	}else{
    		//Default to all value.
    		int dialogEntryLength = _clickedDialogEntryIndices.length;
    		for (int i=0; i<dialogEntryLength; i++) {
        		_clickedDialogEntryIndices[i] = true;
        	}
    	}
    }

    /**
     * Called when the Preference Dialog box is closed.
     * Store our custom preferences in a string and save it.
     * 
     * @param positiveResult - Either "OK" or "Cancel". Only do work if "OK".
     */
	@Override
    protected void onDialogClosed(boolean positiveResult) {
		if (_debug) Log.v("SelectCalendarListPreference.onDialogClosed()");
    	CharSequence[] entryValues = getEntryValues();
        if (positiveResult && entryValues != null) {
        	StringBuffer value = new StringBuffer();
        	int entryValuesLength = entryValues.length;
        	for (int i=0; i<entryValuesLength; i++) {
        		if (_clickedDialogEntryIndices[i]) {
        			if(!value.toString().equals("")){
        				value.append("|");
        			}
        			value.append(entryValues[i]);
        		}
        	}
            if (callChangeListener(value)) {
            	setValue(value.toString());
            }
        }
    }
	
}