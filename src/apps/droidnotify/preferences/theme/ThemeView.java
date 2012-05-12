package apps.droidnotify.preferences.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;

/**
 * This class is the view which the ViewFlipper displays for each notification.
 * 
 * @author Camille Sévigny
 */
public class ThemeView extends LinearLayout {

	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private int _notificationType = -1;
	private String _themePackageName = null;
	private Resources _resources = null;
	
	private ThemeViewFlipper _themeViewFlipper = null;
	
	private LinearLayout _notificationWindowLinearLayout = null;	

	private LinearLayout _buttonLinearLayout = null;
	private LinearLayout _imageButtonLinearLayout = null;
	
	private TextView _themeNameTextView = null;
	private TextView _contactNameTextView = null;
	private TextView _contactNumberTextView = null;
	private TextView _notificationCountTextView = null;
	private TextView _notificationInfoTextView = null;
	private TextView _notificationDetailsTextView = null;
	private TextView _mmsLinkTextView = null;
	
	private ImageView _notificationIconImageView = null;
	private ImageView _photoImageView = null;

	private ImageView _rescheduleButton = null;
	private ImageView _ttsButton = null;
	
	private Button _previousButton = null;
	private Button _nextButton = null;	
	
	private Button _dismissButton = null;
	private Button _deleteButton = null;
	private Button _callButton = null;
	private Button _replyButton = null;
	private Button _viewButton = null;
	
	private ImageButton _dismissImageButton = null;
	private ImageButton _deleteImageButton = null;
	private ImageButton _callImageButton = null;
	private ImageButton _replyImageButton = null;
	private ImageButton _viewImageButton = null;
	
	private ProgressBar _photoProgressBar = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * Class Constructor.
     */	
	public ThemeView(Context context, ThemeViewFlipper themeViewFlipper, String packageName){
	    super(context);
	    _debug = Log.getDebug();;
	    if (_debug) Log.v("ThemeView.ThemeView()");
	    _context = context;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    _themeViewFlipper = themeViewFlipper;
	    _themePackageName = packageName;
		View.inflate(context, R.layout.theme_notification, this);
	    initLayoutItems();
		setupLayoutTheme();
	    setupViewHeaderButtons();
	    setupViewButtons();
	    populateViewInfo();
	}

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getThemePackage(){
		return _themePackageName;
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application's Context.
	 */
	private void initLayoutItems(){
		if (_debug) Log.v("ThemeView.initLayoutItems()");
		
		_notificationWindowLinearLayout = (LinearLayout) findViewById(R.id.notification_linear_layout);
	    _buttonLinearLayout = (LinearLayout) findViewById(R.id.button_linear_layout);
	    _imageButtonLinearLayout = (LinearLayout) findViewById(R.id.image_button_linear_layout);
		
	    _themeNameTextView = (TextView) findViewById(R.id.theme_name_text_view);
		_contactNameTextView = (TextView) findViewById(R.id.contact_name_text_view);
		_contactNumberTextView = (TextView) findViewById(R.id.contact_number_text_view);
		_notificationCountTextView = (TextView) findViewById(R.id.notification_count_text_view);
		_notificationInfoTextView = (TextView) findViewById(R.id.notification_info_text_view);	    
		_notificationDetailsTextView = (TextView) findViewById(R.id.notification_details_text_view);
		_mmsLinkTextView = (TextView) findViewById(R.id.mms_link_text_view);
		
		_notificationIconImageView = (ImageView) findViewById(R.id.notification_type_icon_image_view);
		_photoImageView = (ImageView) findViewById(R.id.contact_photo_image_view);

		_rescheduleButton = (ImageView) findViewById(R.id.reschedule_button_image_view);
		_ttsButton = (ImageView) findViewById(R.id.tts_button_image_view);
		
    	_previousButton = (Button) findViewById(R.id.previous_button);
		_nextButton = (Button) findViewById(R.id.next_button);		

		_dismissButton = (Button) findViewById(R.id.dismiss_button);
		_deleteButton = (Button) findViewById(R.id.delete_button);
		_callButton = (Button) findViewById(R.id.call_button);
		_replyButton = (Button) findViewById(R.id.reply_button);
		_viewButton = (Button) findViewById(R.id.view_button);
		
		_dismissImageButton = (ImageButton) findViewById(R.id.dismiss_image_button);
		_deleteImageButton = (ImageButton) findViewById(R.id.delete_image_button);
		_callImageButton = (ImageButton) findViewById(R.id.call_image_button);
		_replyImageButton = (ImageButton) findViewById(R.id.reply_image_button);
		_viewImageButton = (ImageButton) findViewById(R.id.view_image_button);
		
		_photoProgressBar = (ProgressBar) findViewById(R.id.contact_photo_progress_bar);

		_notificationDetailsTextView.setMovementMethod(new ScrollingMovementMethod());
	}
	
	/**
	 * Setup the layout graphical items based on the current theme.
	 */
	private void setupLayoutTheme(){
		if (_debug) Log.v("ThemeView.setupLayoutTheme() _themePackageName: " + _themePackageName);
		Drawable layoutBackgroundDrawable = null;
		Drawable rescheduleDrawable = null;
		Drawable ttsDrawable = null;
		int notificationCountTextColorID = 0;
		int headerInfoTextcolorID = 0;
		int contactNameTextColorID = 0;
		int contactNumberTextColorID = 0;
		int bodyTextColorID = 0;
		int buttonTextColorID = 0;
		String themeName = null;
		if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
			_resources = _context.getResources();
			if(_themePackageName.equals(Constants.DARK_TRANSLUCENT_THEME)){
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel);
				themeName = _context.getString(R.string.notify_theme) + " - " + _context.getString(R.string.default_v1);
			}else if(_themePackageName.equals(Constants.DARK_TRANSLUCENT_V2_THEME)){
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel_v2);
				themeName = _context.getString(R.string.notify_theme) + " - " + _context.getString(R.string.default_v2);
			}else if(_themePackageName.equals(Constants.DARK_TRANSLUCENT_V3_THEME)){
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel_v3);
				themeName = _context.getString(R.string.notify_theme) + " - " + _context.getString(R.string.default_v3);
			}
			rescheduleDrawable = _resources.getDrawable(R.drawable.ic_reschedule);
			ttsDrawable = _resources.getDrawable(R.drawable.ic_tts);
			notificationCountTextColorID = _resources.getColor(R.color.notification_count_text_color);	
			headerInfoTextcolorID = _resources.getColor(R.color.header_info_text_color);	
			contactNameTextColorID = _resources.getColor(R.color.contact_name_text_color);	
			contactNumberTextColorID = _resources.getColor(R.color.contact_number_text_color);	
			bodyTextColorID = _resources.getColor(R.color.body_text_color);
			buttonTextColorID = _resources.getColor(R.color.button_text_color);	
		}else{
			try{
				_resources = _context.getPackageManager().getResourcesForApplication(_themePackageName);
				layoutBackgroundDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/background_panel", null, null));
				rescheduleDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_reschedule", null, null));
				ttsDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_tts", null, null));
				notificationCountTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/notification_count_text_color", null, null));
				headerInfoTextcolorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/header_info_text_color", null, null));	
				contactNameTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/contact_name_text_color", null, null));	
				contactNumberTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/contact_number_text_color", null, null));
				bodyTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/body_text_color", null, null));
				buttonTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/button_text_color", null, null));
				themeName = _resources.getString(_resources.getIdentifier(_themePackageName + ":string/app_name_desc", null, null));
			}catch(NameNotFoundException ex){
				Log.e("ThemeView.setupLayoutTheme() Loading Theme Package ERROR: " + ex.toString());
				_themePackageName = Constants.DARK_TRANSLUCENT_THEME;
				_resources = _context.getResources();
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel);
				rescheduleDrawable = _resources.getDrawable(R.drawable.ic_reschedule);
				ttsDrawable = _resources.getDrawable(R.drawable.ic_tts);
				notificationCountTextColorID = _resources.getColor(R.color.notification_count_text_color);	
				headerInfoTextcolorID = _resources.getColor(R.color.header_info_text_color);	
				contactNameTextColorID = _resources.getColor(R.color.contact_name_text_color);	
				contactNumberTextColorID = _resources.getColor(R.color.contact_number_text_color);	
				bodyTextColorID = _resources.getColor(R.color.body_text_color);
				buttonTextColorID = _resources.getColor(R.color.button_text_color);	
				themeName = _context.getString(R.string.notify_theme) + " - " + _context.getString(R.string.default_v1);
			}
		}
		
		_notificationWindowLinearLayout.setBackgroundDrawable(layoutBackgroundDrawable);
		
		_notificationCountTextView.setTextColor(notificationCountTextColorID);
		_notificationInfoTextView.setTextColor(headerInfoTextcolorID);
		_contactNameTextView.setTextColor(contactNameTextColorID);
		_contactNumberTextView.setTextColor(contactNumberTextColorID);
		
		_notificationDetailsTextView.setTextColor(bodyTextColorID);
		_notificationDetailsTextView.setLinkTextColor(bodyTextColorID);
		_mmsLinkTextView.setTextColor(bodyTextColorID);
		
		_previousButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NAV_PREV));
		_nextButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NAV_NEXT));
				
		_dismissButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_deleteButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_callButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_replyButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_viewButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		

		_dismissButton.setTextColor(buttonTextColorID);
		_deleteButton.setTextColor(buttonTextColorID);		
		_callButton.setTextColor(buttonTextColorID);		
		_replyButton.setTextColor(buttonTextColorID);		
		_viewButton.setTextColor(buttonTextColorID);
		
		_dismissImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_deleteImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_callImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_replyImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));		
		_viewImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
		
		_rescheduleButton.setImageDrawable(rescheduleDrawable);
		_ttsButton.setImageDrawable(ttsDrawable);
		
		_themeNameTextView.setText(themeName);
		
	}
	
	/**
	 * Get the StateListDrawable object for the certain button types associated with the current theme.
	 * 
	 * @param buttonType - The button type we want to retrieve.
	 * 
	 * @return StateListDrawable - Returns a Drawable that contains the state specific images for this theme.
	 */
	private StateListDrawable getThemeButton(int buttonType){
		if (_debug) Log.v("ThemeView.getThemeButton() ButtonType: " + buttonType);
		StateListDrawable stateListDrawable = new StateListDrawable();
		switch(buttonType){
			case Constants.THEME_BUTTON_NORMAL:{
				if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
					stateListDrawable.addState(new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.button_pressed));
					stateListDrawable.addState(new int[] {android.R.attr.state_enabled}, _resources.getDrawable(R.drawable.button_normal));
					stateListDrawable.addState(new int[] {}, _resources.getDrawable(R.drawable.button_disabled));
				}else{
					stateListDrawable.addState(new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_pressed", null, null)));
					stateListDrawable.addState(new int[] {android.R.attr.state_enabled}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_normal", null, null)));
					stateListDrawable.addState(new int[] {}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_disabled", null, null)));
				}
				return stateListDrawable;
			}
			case Constants.THEME_BUTTON_NAV_PREV:{
				if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
					stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.button_navigate_prev_pressed));
					stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.button_navigate_prev_normal));
				}else{
					stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_prev_pressed", null, null)));
					stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_prev_normal", null, null)));
				}
				return stateListDrawable;
			}
			case Constants.THEME_BUTTON_NAV_NEXT:{
				if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
					stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.button_navigate_next_pressed));
					stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.button_navigate_next_normal));
				}else{
					stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_next_pressed", null, null)));
					stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_next_normal", null, null)));
				}
				return stateListDrawable;
			}
		}
		return null;
	}
	
	private void setupViewHeaderButtons(){
		if (_debug) Log.v("ThemeView.setupViewHeaderButtons()");
		try{
			//Previous Button
			_previousButton.setVisibility(View.VISIBLE);
			_previousButton.setOnClickListener(
				new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v("Previous Button Clicked()");
				    	_themeViewFlipper.showPrevious();
				    }
				}
			);
			//Next Button
			_nextButton.setVisibility(View.VISIBLE);
			_nextButton.setOnClickListener(
				new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v("Next Button Clicked()");
				    	_themeViewFlipper.showNext();
				    }
				}
			);
			//TTS Button
			if(_preferences.getBoolean(Constants.DISPLAY_TEXT_TO_SPEECH_KEY, true)){
				_ttsButton.setVisibility(View.VISIBLE);
			}else{
				_ttsButton.setVisibility(View.GONE);
			}
			//Reschedule Button
			if(_preferences.getBoolean(Constants.DISPLAY_RESCHEDULE_BUTTON_KEY, true)){
				_rescheduleButton.setVisibility(View.VISIBLE);
			}else{
				_rescheduleButton.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			Log.e("ThemeView.setupViewHeaderButtons() ERROR: " + ex.toString());
		}
	}

	/**
	 * Sets up the ThemeView's buttons.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setupViewButtons(){
		if (_debug) Log.v("ThemeView.setupViewButtons()");
		try{
			boolean usingImageButtons = false;
			String buttonDisplayStyle = _preferences.getString(Constants.BUTTON_DISPLAY_STYLE_KEY, Constants.BUTTON_DISPLAY_STYLE_DEFAULT);
			//Show the LinearLayout of the specified button style (ImageButton vs Button)
			if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_ICON_ONLY)){
				usingImageButtons = true;
				_buttonLinearLayout.setVisibility(View.GONE);
		    	_imageButtonLinearLayout.setVisibility(View.VISIBLE);
			}else{
				usingImageButtons = false;
				_buttonLinearLayout.setVisibility(View.VISIBLE);
		    	_imageButtonLinearLayout.setVisibility(View.GONE);
			}
			//Default all buttons to be hidden.
			_dismissButton.setVisibility(View.GONE);
			_deleteButton.setVisibility(View.GONE);
			_callButton.setVisibility(View.GONE);
			_replyButton.setVisibility(View.GONE);
			_viewButton.setVisibility(View.GONE);
			_dismissImageButton.setVisibility(View.GONE);
			_deleteImageButton.setVisibility(View.GONE);
			_callImageButton.setVisibility(View.GONE);
			_replyImageButton.setVisibility(View.GONE);
			_viewImageButton.setVisibility(View.GONE);
			//Set button font size.
			float buttonTextSize = Float.parseFloat(_preferences.getString(Constants.BUTTON_FONT_SIZE_KEY, Constants.BUTTON_FONT_SIZE_DEFAULT));
			_dismissButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
			_deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
			_callButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
			_replyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
			_viewButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
			//Setup the views buttons based on the notification type.
			setupViewSMSButtons(usingImageButtons, _notificationType);
			setupButtonIcons(usingImageButtons, _notificationType, buttonDisplayStyle);
		}catch(Exception ex){
			Log.e("ThemeView.setupViewButtons() ERROR: " + ex.toString());
		}
	}

	/**
	 * Setup the view buttons for SMS & MMS notifications.
	 * 
	 * @param usingImageButtons - True if the user wants to use buttons with icons only.
	 * @param notificationType - The notification type.
	 */
	private void setupViewSMSButtons(boolean usingImageButtons, int notificationType){
		if (_debug) Log.v("ThemeView.setupViewSMSButtons()");
		try{
			if(usingImageButtons){
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.SMS_DISPLAY_DISMISS_BUTTON_KEY, true)){		
		    		_dismissImageButton.setVisibility(View.VISIBLE);
		    	}else{		
		    		_dismissImageButton.setVisibility(View.GONE);
		    	}
				// Delete Button
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_DELETE_BUTTON_KEY, true)){
		    		_deleteImageButton.setVisibility(View.VISIBLE);
		    	}else{
		    		_deleteImageButton.setVisibility(View.GONE);
		    	}
				// Reply Button;
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_REPLY_BUTTON_KEY, true)){
					_replyImageButton.setVisibility(View.VISIBLE);
		    	}else{
		    		_replyImageButton.setVisibility(View.GONE);
		    	}
			}else{
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.SMS_DISPLAY_DISMISS_BUTTON_KEY, true)){		
		    		_dismissButton.setVisibility(View.VISIBLE);
		    	}else{		
		    		_dismissButton.setVisibility(View.GONE);
		    	}
				// Delete Button
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_DELETE_BUTTON_KEY, true)){
		    		_deleteButton.setVisibility(View.VISIBLE);
		    	}else{
		    		_deleteButton.setVisibility(View.GONE);
		    	}
				// Reply Button
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_REPLY_BUTTON_KEY, true)){
		    		_replyButton.setVisibility(View.VISIBLE);
		    	}else{
		    		_replyButton.setVisibility(View.GONE);
		    	}
			}
		}catch(Exception ex){
			Log.e("ThemeView.setupViewSMSButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup and load the notification specific button icons.
	 */
	private void setupButtonIcons(boolean usingImageButtons, int notificationType, String buttonDisplayStyle){
		if (_debug) Log.v("ThemeView.setupButtonIcons()");
		try{
			if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_TEXT_ONLY)){
				return;
			}
			Drawable dismissButtonIcon = null;
			Drawable deleteButtonIcon = null;
			Drawable replySMSButtonIcon = null;
			//Load the theme specific icons.
			if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
				dismissButtonIcon = _resources.getDrawable(R.drawable.ic_dismiss);
				deleteButtonIcon = _resources.getDrawable(R.drawable.ic_delete);
				replySMSButtonIcon = _resources.getDrawable(R.drawable.ic_conversation);
			}else{
				dismissButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_dismiss", null, null));
				deleteButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_delete", null, null));
				replySMSButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_conversation", null, null));
			}
			if(usingImageButtons){
				_dismissImageButton.setImageDrawable(dismissButtonIcon);
				_deleteImageButton.setImageDrawable(deleteButtonIcon);
				_replyImageButton.setImageDrawable(replySMSButtonIcon);
			}else{
				_dismissButton.setCompoundDrawablesWithIntrinsicBounds(dismissButtonIcon, null, null, null);
				_deleteButton.setCompoundDrawablesWithIntrinsicBounds(deleteButtonIcon, null, null, null);
				_replyButton.setCompoundDrawablesWithIntrinsicBounds(replySMSButtonIcon, null, null, null);
			}
		}catch(Exception ex){
			Log.e("ThemeView.setupButtonIcons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Populate the notification view with content from the actual Notification.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void populateViewInfo(){
		if (_debug) Log.v("ThemeView.populateViewInfo()");
		boolean loadContactPhoto = true;
		//Set the max lines property of the notification body.
		_notificationDetailsTextView.setMaxLines(Integer.parseInt(_preferences.getString(Constants.NOTIFICATION_BODY_MAX_LINES_KEY, Constants.NOTIFICATION_BODY_MAX_LINES_DEFAULT)));
		//Set the font size property of the notification body.
		_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
	    // Set from, number, message etc. views.
		//Show/Hide Contact Name
		_contactNameTextView.setText("Julie Miller");
		_contactNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.CONTACT_NAME_SIZE_KEY, Constants.CONTACT_NAME_SIZE_DEFAULT)));
		_contactNameTextView.setVisibility(View.VISIBLE);
		//Show/Hide Contact Number
		if(_preferences.getBoolean(Constants.CONTACT_NUMBER_DISPLAY_KEY, true)){
	    	_contactNumberTextView.setText(PhoneCommon.formatPhoneNumber(_context, "5555555555"));		    	
		    _contactNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.CONTACT_NUMBER_SIZE_KEY, Constants.CONTACT_NUMBER_SIZE_DEFAULT)));
		    _contactNumberTextView.setVisibility(View.VISIBLE);
		}else{
			_contactNumberTextView.setVisibility(View.GONE);
		}
		//Show/Hide Contact Photo
		if(_preferences.getBoolean(Constants.CONTACT_PHOTO_DISPLAY_KEY, true)){
			//Set Contact Photo Background
			int contactPhotoBackground = Integer.parseInt(_preferences.getString(Constants.CONTACT_PHOTO_BACKGKROUND_KEY, "0"));
			if(contactPhotoBackground == 1){
				_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_froyo);
			}else if(contactPhotoBackground == 2){
				_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_gingerbread);
			}else if(contactPhotoBackground == 3){
				_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_blue_steel);
			}else{
				_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_white);
			}
		}else{
			_photoImageView.setVisibility(View.GONE);
			_photoProgressBar.setVisibility(View.GONE);
			loadContactPhoto = false;
		}		
		if(_preferences.getBoolean(Constants.DISPLAY_NOTIFICATION_BODY_KEY, true)){
			_notificationDetailsTextView.setVisibility(View.VISIBLE);
		    //Load the notification message.
		    setNotificationMessage();
		}else{
			_notificationDetailsTextView.setVisibility(View.GONE);
		}
	    //Load the notification type icon & text into the notification.
	    setNotificationTypeInfo();
	    //Load the image from the users contacts.
    	if(loadContactPhoto){
			int contactPhotoSize = Integer.parseInt(_preferences.getString(Constants.CONTACT_PHOTO_SIZE_KEY, Constants.CONTACT_PHOTO_SIZE_DEFAULT));
			String contactPlaceholderImageIndex = _preferences.getString(Constants.CONTACT_PLACEHOLDER_KEY, Constants.CONTACT_PLACEHOLDER_DEFAULT);
	    	Bitmap contactPhotoPlaceholder = Common.getRoundedCornerBitmap(BitmapFactory.decodeResource(_context.getResources(), getContactPhotoPlaceholderResourceID(Integer.parseInt(contactPlaceholderImageIndex))), 5, true, contactPhotoSize, contactPhotoSize);
	    	_photoImageView.setImageBitmap(contactPhotoPlaceholder);
	    	_photoProgressBar.setVisibility(View.GONE);
	    	_photoImageView.setVisibility(View.VISIBLE);
    	}
	}
	
	/**
	 * Set the notification message. 
	 * This is specific to the type of notification that was received.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationMessage(){
		if (_debug) Log.v("ThemeView.setNotificationMessage()");
		int notificationAlignment = Gravity.LEFT;
	    _notificationDetailsTextView.setText("Theme test message.");
		if(_preferences.getBoolean(Constants.NOTIFICATION_BODY_CENTER_ALIGN_TEXT_KEY, false)){
			notificationAlignment = Gravity.CENTER_HORIZONTAL;
		}else{
			notificationAlignment = Gravity.LEFT;
		}
	    _notificationDetailsTextView.setGravity(notificationAlignment);
	}
	
	/**
	 * Set notification specific details into the header of the Notification.
	 * This is specific to the type of notification that was received.
	 * Details include:
	 * 		Icon,
	 * 		Icon Text,
	 * 		Date & Time,
	 * 		Etc...
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationTypeInfo(){
		if (_debug) Log.v("ThemeView.setNotificationTypeInfo()");
		Bitmap iconBitmap = null;
		// Update TextView that contains the image, contact info/calendar info, and timestamp for the Notification.
		String formattedTimestamp = Common.formatTimestamp(_context, Common.convertGMTToLocalTime(_context, System.currentTimeMillis(), true));
    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.notification_type_sms);
    	String receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
		if(_preferences.getBoolean(Constants.NOTIFICATION_TYPE_INFO_ICON_KEY, true)){
		    if(iconBitmap != null){
		    	_notificationIconImageView.setImageBitmap(iconBitmap);
		    	_notificationIconImageView.setVisibility(View.VISIBLE);
		    }
		}else{
			_notificationIconImageView.setVisibility(View.GONE);
		}
		_notificationInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_TYPE_INFO_FONT_SIZE_KEY, Constants.NOTIFICATION_TYPE_INFO_FONT_SIZE_DEFAULT)));
	    _notificationInfoTextView.setText(receivedAtText);
	}
	
	/**
	 * Get the contact photo placeholder image resource id.
	 * 
	 * @param index - The contact image index.
	 * 
	 * @return int - Returns the resource id of the image that corresponds to this index.
	 */
	private int getContactPhotoPlaceholderResourceID(int index){
		if (_debug) Log.v("ThemeView.getContactPhotoPlaceholderResourceID()");
		switch(index){
			case 1:{
				return R.drawable.ic_contact_picture_1;
			}
			case 2:{
				return R.drawable.ic_contact_picture_2;
			}
			case 3:{
				return R.drawable.ic_contact_picture_3;
			}
			case 4:{
				return R.drawable.ic_contact_picture_4;
			}
			case 5:{
				return R.drawable.ic_contact_picture_5;
			}
			case 6:{
				return R.drawable.ic_contact_picture_6;
			}
			case 7:{
				return R.drawable.ic_contact_picture_7;
			}
			case 8:{
				return R.drawable.ic_contact_picture_8;
			}
			case 9:{
				return R.drawable.ic_contact_picture_9;
			}
			case 10:{
				return R.drawable.ic_contact_picture_10;
			}
			case 11:{
				return R.drawable.ic_contact_picture_11;
			}
			case 12:{
				return R.drawable.ic_contact_picture_12;
			}
			default:{
				return R.drawable.ic_contact_picture_1;
			}
		}
	}
	
}