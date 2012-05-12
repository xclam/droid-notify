package apps.droidnotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class is the main control window that displays and moves the Notifications.
 * 
 * @author Camille Sévigny
 */
public class NotificationViewFlipper extends ViewFlipper {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private NotificationActivity _notificationActivity = null;
	private int _smsCount = 0;
	private int _mmsCount = 0;
	private int _missedCallCount = 0;
	private int _calendarCount = 0;
	private int _k9Count = 0;

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 * Class Constructor.
	 */
	public NotificationViewFlipper(Context context){
		super(context);
		_debug = Log.getDebug();
		if(_debug) Log.v("NotificationViewFlipper.NotificationViewFlipper()");
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		_notificationActivity = (NotificationActivity) context;
	}
	
	/**
	 * Class Constructor.
	 */	
	public  NotificationViewFlipper(Context context, AttributeSet attributes){
		super(context, attributes);
		_debug = Log.getDebug();
		if(_debug) Log.v("NotificationViewFlipper.NotificationViewFlipper()");
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		_notificationActivity = (NotificationActivity) context;
	}
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Add new View to the ViewFlipper.
	 * 
	 * @param notification - Notification to add to the ArrayList.
	 */
	public void addNotification(Notification notification){
		if(_debug) Log.v("NotificationViewFlipper.addNotification()");
		int notificationType = notification.getNotificationType();
		boolean duplicateFound = false;
		int totalNotifications = this.getChildCount();
		for (int i=0; i<totalNotifications; i++){
			Notification currentNotification = ((NotificationView) this.getChildAt(i)).getNotification();
			String notificationSentFromAddress = notification.getSentFromAddress();
			String currentSentFromAddress = currentNotification.getSentFromAddress();
			if(notification.getTimeStamp() == currentNotification.getTimeStamp()){
				if(notificationSentFromAddress == null && currentSentFromAddress == null){
					duplicateFound = true;
					//Update Notification Information
					currentNotification.setReminderPendingIntent(notification.getReminderPendingIntent());
					currentNotification.setRescheduleNumber(notification.getRescheduleNumber());
					break;
				}else if(notificationSentFromAddress != null && currentSentFromAddress != null && notificationSentFromAddress.equals(currentSentFromAddress)){
					duplicateFound = true;
					//Update Notification Information
					currentNotification.setReminderPendingIntent(notification.getReminderPendingIntent());
					currentNotification.setRescheduleNumber(notification.getRescheduleNumber());
					break; 
				}
			}else{
				//Special case for SMS messages.
				if(notificationType == Constants.NOTIFICATION_TYPE_SMS){
					if(notification.getMessageID() == currentNotification.getMessageID()){
						duplicateFound = true;
						//Update Notification Information
						currentNotification.setReminderPendingIntent(notification.getReminderPendingIntent());
						currentNotification.setRescheduleNumber(notification.getRescheduleNumber());
						break; 
					}
				}
			}
		}
		if(!duplicateFound){
			if(_preferences.getString(Constants.VIEW_NOTIFICATION_ORDER, Constants.NEWEST_FIRST).equals(Constants.OLDER_FIRST)){
				addView(new NotificationView(_context, notification));			
				if(_preferences.getBoolean(Constants.DISPLAY_NEWEST_NOTIFICATION, true)){
					setDisplayedChild(this.getChildCount() - 1);
				}else{
					setDisplayedChild(0);
				}
			}else{
				addView(new NotificationView(_context, notification), 0);
				if(_preferences.getBoolean(Constants.DISPLAY_NEWEST_NOTIFICATION, true)){
					setDisplayedChild(0);
				}else{
					setDisplayedChild(this.getChildCount() - 1);
				}
			}
			//Update the navigation information on the current View every time a new View is added.
			final View currentView = this.getCurrentView();
			updateView(currentView, this.getDisplayedChild(), 0);
			//Update specific type counts.
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					_missedCallCount++;
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					_smsCount++;
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					_mmsCount++;
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					_calendarCount++;
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					_k9Count++;
					break;
				}
			}
		}
	}

	/**
	 * Remove the current Notification.
	 */
	public void removeActiveNotification(boolean reschedule){
		if(_debug) Log.v("NotificationViewFlipper.removeActiveNotification()");
		try{
			_notificationActivity.stopTextToSpeechPlayback();
			Notification notification = this.getActiveNotification();
			if(notification == null){
				Log.e("NotificationViewFlipper.removeActiveNotification() Active Notification Is Null. Exiting...");
				return;
			}
			this.removeNotification(this.getDisplayedChild(), reschedule);
			//Clear the status bar notification.
	    	Common.clearNotification(_context, this, notification.getNotificationType());
		}catch(Exception ex){
			Log.e("NotificationViewFlipper.removeActiveNotification() ERROR: " + ex.toString());
		}
	}

	/**
	 * Return the active Notification.
	 * The active Notification is the current message.
	 * 
	 * @return Notification - The current Notification or null if no notifications exist.
	 */	
	public Notification getActiveNotification(){
		if(_debug) Log.v("NotificationViewFlipper.getActiveNotification()");
		try{
			return this.getChildCount() > 0 ? ((NotificationView) this.getCurrentView()).getNotification() : null;
		}catch(Exception ex){
			Log.e("NotificationViewFlipper.getActiveNotification() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Return the total notification count.
	 * 
	 * @return int - The number of current notifications.
	 */	
	public int getTotalNotifications(){
		if(_debug) Log.v("NotificationViewFlipper.getTotalNotifications()");
		return this.getChildCount();
	}
	
	/**
	 * Show the next Notification/View in the list.
	 */
	@Override
	public void showNext(){
		if(_debug) Log.v("NotificationViewFlipper.showNext()");
		int currentNotification = this.getDisplayedChild();
		if(currentNotification < this.getChildCount() - 1){
			setInAnimation(inFromRightAnimation());
			setOutAnimation(outToLeftAnimation());
			//Update the navigation information on the next view before we switch to it.
			final View nextView = this.getChildAt(currentNotification + 1);
			updateView(nextView, currentNotification + 1, 0);
			//Flip to next View.
			super.showNext();
		}
	}
	  
	/**
	 * Show the previous Notification/View in the list.
	 */
	@Override
	public void showPrevious(){
		if(_debug) Log.v("NotificationViewFlipper.showPrevious()");
		int currentNotification = this.getDisplayedChild();
		if(currentNotification > 0){
			setInAnimation(inFromLeftAnimation());
			setOutAnimation(outToRightAnimation());
			//Update the navigation information on the previous view before we switch to it.
			final View previousView = this.getChildAt(currentNotification - 1);
			updateView(previousView, currentNotification - 1, 0);
			//Flip to previous View.
			super.showPrevious();
		}
	}
	
	/**
	 * Display the delete dialog from the activity.
	 */
	public void showDeleteDialog(){
		if(_debug) Log.v("NotificationViewFlipper.showDeleteDialog()");
		_notificationActivity.showDeleteDialog();
	}
	
	/**
	 * Delete the current Notification.
	 */
	public void deleteMessage(){
		if(_debug) Log.v("NotificationViewFlipper.deleteMessage()");
		//Remove the notification from the ViewFlipper.
		Notification notification = getNotification(this.getDisplayedChild());
		if(notification == null){
			if(_debug) Log.v("NotificationViewFlipper.deleteMessage() Notification is null. Exiting...");
			return;
		}
		int notificationType = notification.getNotificationType();
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					this.removeActiveNotification(false);
				}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					this.removeActiveNotification(false);
				}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
					//Delete the current message from the users phone.
					//The notification will remove ALL messages for this thread from the phone for us.
					notification.deleteMessage();
					//Remove all Notifications with the thread ID.
					this.removeNotificationsByThread(notification.getThreadID());
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					this.removeActiveNotification(false);
				}else if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					this.removeActiveNotification(false);
				}else if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_THREAD)){
					//Delete the current message from the users phone.
					//The notification will remove ALL messages for this thread from the phone for us.
					notification.deleteMessage();
					//Remove all Notifications with the thread ID.
					this.removeNotificationsByThread(notification.getThreadID());
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				if(_preferences.getString(Constants.K9_DELETE_KEY, "0").equals(Constants.K9_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					this.removeActiveNotification(false);
				}else if(_preferences.getString(Constants.K9_DELETE_KEY, "0").equals(Constants.K9_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					this.removeActiveNotification(false);
				}
				break;
			}
		}
	}
	
	/**
	 * Check if there are any notifications of a certain type.
	 * 
	 * @param notificationType - The notification type.
	 * 
	 * @return boolean - Returns true if a notification of the supplied type is found.
	 */
	public boolean containsNotificationType(int notificationType){
		if(_debug) Log.v("NotificationViewFlipper.containsAnyNotificationType()");
		int totalNotifications = this.getChildCount();
		for (int i=0; i<totalNotifications; i++){
			if(((NotificationView) this.getChildAt(i)).getNotification().getNotificationType() == notificationType){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Reschedule a notification.
	 */
	public void rescheduleNotification(){
		long rescheduleInterval = Long.parseLong(_preferences.getString(Constants.RESCHEDULE_TIME_KEY, Constants.RESCHEDULE_TIME_DEFAULT)) * 60 * 1000;
		Notification notification = getActiveNotification();
    	Common.rescheduleNotification(_context, notification, System.currentTimeMillis() + rescheduleInterval, notification.getRescheduleNumber() + 0);
    	this.removeActiveNotification(true);
	}

	/**
	 * Get the smsCount property.
	 * 
	 * @return int - The current smsCount value.
	 */
	public int getSMSCount(){
		if(_debug) Log.v("NotificationViewFlipper.getSMSCount() SMSCount: " + _smsCount);
		return _smsCount;
	}

	/**
	 * Get the mmsCount property.
	 * 
	 * @return int - The current mmsCount value.
	 */
	public int getMMSCount(){
		if(_debug) Log.v("NotificationViewFlipper.getMMSCount() MMSCount: " + _mmsCount);
		return _mmsCount;
	}

	/**
	 * Get the missedCallCount property.
	 * 
	 * @return int - The current missedCallCount value.
	 */
	public int getMissedCallCount(){
		if(_debug) Log.v("NotificationViewFlipper.getMissedCallCount() MissedCallCount: " + _missedCallCount);
		return _missedCallCount;
	}

	/**
	 * Get the calendarCount property.
	 * 
	 * @return int - The current calendarCount value.
	 */
	public int getCalendarCount(){
		if(_debug) Log.v("NotificationViewFlipper.getCalendarCount() CalendarCount: " + _calendarCount);
		return _calendarCount;
	}

	/**
	 * Get the k9Count property.
	 * 
	 * @return int - The current k9Count value.
	 */
	public int getK9Count(){
		if(_debug) Log.v("NotificationViewFlipper.getK9Count() K9Count: " + _k9Count);
		return _k9Count;
	}
  	
  	/**
  	 * Dismiss all notifications.
  	 */
  	public void dismissAllNotifications(){
		if (_debug) Log.v("NotificationViewFlipper.dismissAllNotifications()");
		try{
			int totalNotifications = this.getChildCount();
			for (int i=0; i<totalNotifications; i++){
				removeActiveNotification(false);
			}
  		}catch(Exception ex){
  			Log.e("NotificationViewFlipper.dismissAllNotifications() ERROR: " + ex.toString());
  		}
  	}
	
	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Retrieve the Notification at the current index.
	 * 
	 * @param index - Index of a Notification in the View Flipper.
	 * 
	 * @return Notification - Return the notification located at the specified index.
	 */
	private Notification getNotification(int index){
		if(_debug) Log.v("NotificationViewFlipper.getNotification()");
		try{
			return ((NotificationView) this.getChildAt(index)).getNotification();
		}catch(Exception ex){
			Log.e("NotificationViewFlipper.getNotification() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	* Remove the Notification and its view.
	*
	* @param notificationNumber - Index of the notification to be removed.
	*/
	private void removeNotification(int index, boolean reschedule){
		if(_debug) Log.v("NotificationViewFlipper.removeNotification()");
		//Get the current notification object.
		Notification notification = getNotification(index);
		if(notification == null){
			if(_debug) Log.v("NotificationViewFlipper.removeNotification() Notification is null. Exiting...");
			return;
		}
    	//Cancel the notification reminder.
    	notification.cancelReminder();
		int notificationType = notification.getNotificationType();
		//Set notification as being viewed.
		if(!reschedule){
			setNotificationViewed(notification);
		}
    	//Update specific type counts.
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				_missedCallCount--;
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				_smsCount--;
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				_mmsCount--;
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				_calendarCount--;
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				_k9Count--;
				break;
			}
		}
		int viewCount = this.getChildCount();
		if(viewCount > 1){
			try{
				// Fade out current notification.
				setOutAnimation(_context, android.R.anim.fade_out);
				// If this is the last notification, slide in from left.
				if(index + 1 == viewCount){
					setInAnimation(inFromLeftAnimation());
					//Update the navigation information on the previous view before we switch to it.
					final View previousView = this.getChildAt(index - 1);
					updateView(previousView, index - 1, 1);
				}else{ // Else slide in from right.
					setInAnimation(inFromRightAnimation());
					//Update the navigation information on the next view before we switch to it.
					final View nextView = this.getChildAt(index + 1);
					updateView(nextView, index + 1, -1);
				}
				// Remove the view from the ViewFlipper.
				removeViewAt(index);
		    	//Update the status bar notifications.
				if(notificationType != Constants.NOTIFICATION_TYPE_GENERIC){
			    	updateStatusBarNotifications(notification.getNotificationType(), notification.getNotificationSubType());
				}
			}catch(Exception ex){
				if(_debug) Log.v("NotificationViewFlipper.removeNotification() [Total Notification > 1] ERROR: " + ex.toString());
			}
		}else{	
			try{
				// Remove the view from the ViewFlipper.
				removeViewAt(index);
				//Close the ViewFlipper and finish the Activity.
				_notificationActivity.finishActivity();
			}catch(Exception ex){
				if(_debug) Log.v("NotificationViewFlipper.removeNotification() [TotalNotification <= 1] ERROR: " + ex.toString());
			}
		}
	}
	
	/**
	 * Updates the View's navigation buttons and other dynamic info.
	 * 
	 * @param view - The View that we will update.
	 * @param viewIndex - The index of the view we want to update.
	 * @param indexAdjustment - The adjustment value which maps to a remove operation.
	 *                          IndexAdjustment = +1 - We are removing the last view from the ViewFolipper.
	 *                          IndexAdjustment = -1 - We are removing any view except the last view from the ViewFolipper.
	 */
	private void updateView(View view, int viewIndex, int indexAdjustment){
    	if(_debug) Log.v("NotificationViewFlipper.updateView()");
		RelativeLayout headerRelativeLayout = (RelativeLayout) view.findViewById(R.id.header_navigation);
		Button previousButton = (Button) view.findViewById(R.id.previous_button);
		TextView notificationCountTextView = (TextView) view.findViewById(R.id.notification_count_text_view);
		Button nextButton = (Button) view.findViewById(R.id.next_button);
		//if(_debug) Log.v("NotificationViewFlipper.updateView() viewIndex: " + viewIndex + " indexAdjustment: " + indexAdjustment);
    	int totalviews = this.getChildCount();
    	int currentView = viewIndex + 1;
    	boolean isFirstView = isFirstView(viewIndex + indexAdjustment);
    	boolean isLastView = isLastView(viewIndex + indexAdjustment);
    	//Special cases when a View is removed.
    	if(indexAdjustment > 0){ // Removing the last View.
    		totalviews--;
    		isLastView = true;
    		if(!isFirstView) isFirstView = isFirstView(viewIndex);
    	}else if(indexAdjustment < 0){ // Removing the first or other View.
    		totalviews--;
    		currentView += indexAdjustment;
    		if(!isLastView) isLastView = isLastView(viewIndex);
    	}
    	//Update the navigation buttons and notification count text.
    	if(isFirstView){
    		previousButton.setVisibility(View.INVISIBLE);
    	}else{
    		previousButton.setVisibility(View.VISIBLE);
    	}
    	notificationCountTextView.setText(String.valueOf(currentView) + "/" + String.valueOf(totalviews));
    	if(isLastView){
    		nextButton.setVisibility(View.INVISIBLE);
    	}else{
    		nextButton.setVisibility(View.VISIBLE);
    	}
    	//Hide notification header row if single notification.
    	if(_preferences.getBoolean(Constants.HIDE_SINGLE_MESSAGE_HEADER_KEY, false)){
	    	if(totalviews == 1){
	    		headerRelativeLayout.setVisibility(View.GONE);
	    	}else{
	    		headerRelativeLayout.setVisibility(View.VISIBLE);
	    	}
    	}
	}

	/**
	 * Determine if the index supplied is the last View in the list.
	 * 
	 * @param viewIndex - The index of the view for which we want to know the info about.
	 * 
	 * @return boolean - Returns true if the viewIndex is the last View in the ViewFlipper.
	 */
	private boolean isLastView(int viewIndex){
		if(_debug) Log.v("NotificationViewFlipper.isLastView()");
		return viewIndex == this.getChildCount() - 1;
	}
	  
	/**
	 * Determine if the index supplied is the first View in the list.
	 * 
	 * @param viewIndex - The index of the view for which we want to know the info about.
	 * 
	 * @return boolean - Returns true if the viewIndex is the first View in the ViewFlipper.
	 */
	private boolean isFirstView(int viewIndex){
		if(_debug) Log.v("NotificationViewFlipper.isFirsView()");
		return viewIndex == 0;
	}
	
	/**
	 * Set the notification as being viewed.
	 * Let the Notification object handle this method.
	 * 
	 * @param notification - The Notification to set as viewed.
	 */
	private void setNotificationViewed(Notification notification){
		if(_debug) Log.v("NotificationViewFlipper.setNotificationViewed()");
		notification.setViewed(true);
	}
	
	/**
	 * Remove all Notifications with this thread ID.
	 * 
	 * @param threadID - Thread ID of the Notifications to be removed.
	 */ 
	private void removeNotificationsByThread(long threadID){
		if(_debug) Log.v("NotificationViewFlipper.removeNotifications() Thread ID: " + threadID);
		//Must iterate backwards through this collection.
		//By removing items from the end, we don't have to worry about shifting index numbers as we would if we removed from the beginning.
		int totalNotifications = this.getChildCount();
		for(int i=totalNotifications-1; i>=0; i--){
			Notification notification = ((NotificationView) this.getChildAt(i)).getNotification();
			if(notification.getThreadID() == threadID){
				removeNotification(i, false);
			}
		}
		//Clear the status bar notification for SMS & MMS types.
		Common.clearNotification(_context, this, Constants.NOTIFICATION_TYPE_SMS);
    	Common.clearNotification(_context, this, Constants.NOTIFICATION_TYPE_MMS);
	}
	
	/**
	 * Update the status bar notification when a notification is removed from the ViewFlipper.
	 * 
	 * @param notificationType - The notification type that we are updating.
	 * @param notificationSubType - The notification sub type that we are updating.
	 */
	private void updateStatusBarNotifications(int notificationType, int notificationSubType){
		if(_debug) Log.v("NotificationViewFlipper.updateStatusBarNotifications()");
		int notificationTypecount = 1;
		String sentFromContactName = null;
		long sentFromContactID = -1;
		String sentFromAddress = null;
		String message = null;
		String k9EmailUri = null;
		String linkURL = null;
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				notificationTypecount = _missedCallCount;
				break;
		    }
			case Constants.NOTIFICATION_TYPE_SMS:{
				notificationTypecount = _smsCount;
				break;
		    }
			case Constants.NOTIFICATION_TYPE_MMS:{
				notificationTypecount = _mmsCount;
				break;
		    }
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				notificationTypecount = _calendarCount;
				break;
		    }
			case Constants.NOTIFICATION_TYPE_K9:{
				notificationTypecount = _k9Count;
				break;
		    }
		}
		if(notificationTypecount > 0){
			if(notificationTypecount == 1){
				Notification notification = getNotificationByType(notificationType);
				sentFromContactName = notification.getContactName();
				sentFromContactID = notification.getContactID();
				sentFromAddress = notification.getSentFromAddress();
				message = notification.getMessageBody();
				k9EmailUri = notification.getK9EmailUri();
				linkURL = notification.getLinkURL();
			}
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, notificationTypecount, notificationType, notificationSubType, true, sentFromContactName, sentFromContactID, sentFromAddress, message, k9EmailUri, linkURL, true, Common.getStatusBarNotificationBundle(_context, notificationType));
		}
	}
	
	/**
	 * Get a notification of the given type.
	 * 
	 * @param notificationType - The notification type we are searchign for.
	 * 
	 * @return Notification - Returns the first notification of this type that is found or null if none are found.
	 */
	private Notification getNotificationByType(int notificationType){
		if(_debug) Log.v("NotificationViewFlipper.getNotificationByType()");
		int totalNotifications = this.getChildCount();
		for(int i=totalNotifications-1; i>=0; i--){
			Notification notification = ((NotificationView) this.getChildAt(i)).getNotification();
			if(notification.getNotificationType() == notificationType){
				return notification;
			}
		}
		return null;
	}
	
	/**
	 * Animation of the moving of the a Notification that comes from the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation inFromRightAnimation(){
		if(_debug) Log.v("NotificationViewFlipper.inFromRightAnimation()");
		Animation inFromRight = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, +1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(350);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	  
	/**
	 * Animation of the moving of the a Notification that leaves to the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation outToLeftAnimation(){
		if(_debug) Log.v("NotificationViewFlipper.outToLeftAnimation()");
		Animation outtoLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, -1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(350);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	  
	/**
	 * Animation of the moving of the a Notification that comes from the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation inFromLeftAnimation(){
		if(_debug) Log.v("NotificationViewFlipper.inFromLeftAnimation()");
		Animation inFromLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, -1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(350);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	  
	/**
	 * Animation of the moving of the a Notification that leaves to the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation outToRightAnimation(){
		if(_debug) Log.v("NotificationViewFlipper.outToRightAnimation()");
		Animation outtoRight = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, +1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(350);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
}