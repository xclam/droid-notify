package apps.droidnotify.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.email.EmailCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;

public class ContactsCommon {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	
	//================================================================================
	// Public Methods
	//================================================================================	
	
	/**
	 * Get various contact info for a given phoneNumber.
	 * 
	 * @param context - The application context.
	 * @param incomingNumber -  - The phoneNumber to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByPhoneNumber(Context context, String incomingNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactsInfoByPhoneNumber()");
		Bundle contactInfoBundle = new Bundle();
		long contactID = -1;
		String contactName = "";
		long photoID = -1;
		String lookupKey = "";
		boolean contactExists = false;
		if (incomingNumber == null) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByPhoneNumber() Phone number provided is null. Exiting...");
			return null;
		}
		//Exit if the phone number is an email address.
		if (incomingNumber.contains("@")) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByPhoneNumber() Phone number provided appears to be an email address. Exiting...");
			return null;
		}
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?";
			final String[] selectionArgs = new String[] {"1"};
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()) { 
				String contactIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactNameTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				String lookupKeyTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); 
				final String[] phoneProjection = null;
				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
				final String[] phoneSelectionArgs = new String[] {contactIDTmp};
				final String phoneSortOrder = null;
				Cursor phoneCursor = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						phoneProjection, 
						phoneSelection, 
						phoneSelectionArgs, 
						phoneSortOrder); 
				while (phoneCursor.moveToNext()) { 
					String contactNumberTmp = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if(PhoneCommon.isPhoneNumberEqual(contactNumberTmp, incomingNumber)){
						contactID = Long.parseLong(contactIDTmp);
		    		  	contactName = contactNameTmp;
		    		  	if(photoIDTmp != null){
		    			  	photoID = Long.parseLong(photoIDTmp);
		    		  	}
		    		  	lookupKey = lookupKeyTmp;
		  		      	contactExists = true;
		  		      	break;
					}
				}
				phoneCursor.close(); 
				if(contactExists) break;
		   	}
			cursor.close();
			if(contactID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactsInfoByPhoneNumber() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given email.
	 * 
	 * @param context - The application context.
	 * @param incomingEmail - The email to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByEmail(Context context, String incomingEmail){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactsInfoByEmail()");
		Bundle contactInfoBundle = new Bundle();
		long contactID = -1;
		String contactName = "";
		long photoID = -1;
		String lookupKey = "";
		boolean contactExists = false;
		if (incomingEmail == null) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByEmail() Email provided is null. Exiting...");
			return null;
		}
		if (!incomingEmail.contains("@")) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByEmail() Email provided does not appear to be a valid email address. Exiting...");
			return null;
		}
		try{
			final String[] projection = null;
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()) { 
				String contactIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactNameTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				String lookupKeyTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				final String[] emailProjection = null;
				final String emailSelection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactIDTmp;
				final String[] emailSelectionArgs = null;
				final String emailSortOrder = null;
                Cursor emailCursor = context.getContentResolver().query(
                		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
                		emailProjection,
                		emailSelection, 
                        emailSelectionArgs, 
                        emailSortOrder);
                while (emailCursor.moveToNext()) {
                	String contactEmailTmp = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                	if(EmailCommon.removeEmailFormatting(incomingEmail).equals(EmailCommon.removeEmailFormatting(contactEmailTmp))){
						contactID = Long.parseLong(contactIDTmp);
		    		  	contactName = contactNameTmp;
		    		  	if(photoIDTmp != null){
		    			  	photoID = Long.parseLong(photoIDTmp);
		    		  	}
		    		  	lookupKey = lookupKeyTmp;
		  		      	contactExists = true;
		  		      	break;
					}
                }
                emailCursor.close();
                if(contactExists) break;
		   	}
			cursor.close();
			if(contactID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactsInfoByEmail() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given name.
	 * 
	 * @param context - The application context.
	 * @param incomingName - The name to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByName(Context context, String incomingName){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactsInfoByName() IncomingName: " + incomingName);
		Bundle contactInfoBundle = new Bundle();
		long contactID = -1;
		long photoID = -1;
		String lookupKey = "";
		if (incomingName == null || incomingName.equals("")) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByName() Name provided is null or empty. Exiting...");
			return null;
		}
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.DISPLAY_NAME + "=?";
			final String[] selectionArgs = new String[]{incomingName};
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()){
				contactID = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
				String photoIDtmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				if(photoIDtmp != null){
					photoID = Long.parseLong(photoIDtmp); 
				}
				lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				break;
		   	}
			cursor.close();
			if(contactID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
			contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, incomingName);
			if(photoID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactsInfoByName() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given contact ID.
	 * 
	 * @param context - The application context.
	 * @param incomingID - The name to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByID(Context context, long incomingID){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactsInfoByID() IncomingID: " + incomingID);
		Bundle contactInfoBundle = new Bundle();
		String contactName = "";
		long photoID = -1;
		String lookupKey = "";
		if (incomingID <= 0) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByID() ID provided is null or empty. Exiting...");
			return null;
		}
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts._ID + "=?";
			final String[] selectionArgs = new String[]{String.valueOf(incomingID)};
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()){
				contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDtmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				if(photoIDtmp != null){
					photoID = Long.parseLong(photoIDtmp); 
				}
				lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				break;
		   	}
			cursor.close();
			contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, incomingID);
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactsInfoByID() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given contact ID.
	 * 
	 * @param context - The application context.
	 * @param contactID - The contact ID to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByID(Context context, String contactID){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactsInfoByID() Contact ID: " + contactID);
		Bundle contactInfoBundle = new Bundle();
		String contactName = "";
		StringBuilder contactPhoneNumbers = null;
		StringBuilder contactEmailAddresses = null;
		long photoID = -1;
		String lookupKey = "";
		if (contactID == null || contactID.equals("")) {
			if (_debug) Log.v("ContactsCommon.getContactsInfoByID() Contact ID provided is null or empty. Exiting...");
			return null;
		}
		try{
			//Get Name
			final String[] projection = null;
			final String selection = ContactsContract.Contacts._ID + " = " + contactID;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()){ 
				contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDtmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				if(photoIDtmp != null){
					photoID = Long.parseLong(photoIDtmp); 
				}
				lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				break;
		   	}
			cursor.close();
			contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, Long.parseLong(contactID));
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID >= 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			//Get Phone Numbers
			final String[] phoneProjection = null;
			final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
			final String[] phoneSelectionArgs = null;
			final String phoneSortOrder = null;
			Cursor phoneCursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
					phoneProjection, 
					phoneSelection, 
					phoneSelectionArgs, 
					phoneSortOrder); 
			while (phoneCursor.moveToNext()) { 
				String contactNumberTmp = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            	if(contactPhoneNumbers == null){
            		contactPhoneNumbers = new StringBuilder(contactNumberTmp);            		
            	}else{
            		if(!contactPhoneNumbers.toString().contains(contactNumberTmp)){
            			contactPhoneNumbers.append("\n" + contactNumberTmp);
            		}
            	}
			}
			phoneCursor.close();
			if(contactPhoneNumbers != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_PHONE_NUMBERS, contactPhoneNumbers.toString());
			//Get Email Addresses
			final String[] emailProjection = null;
			final String emailSelection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID;
			final String[] emailSelectionArgs = null;
			final String emailSortOrder = null;
            Cursor emailCursor = context.getContentResolver().query(
            		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
            		emailProjection,
            		emailSelection, 
                    emailSelectionArgs, 
                    emailSortOrder);
            while (emailCursor.moveToNext()) {
            	String contactEmailTmp = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            	if(contactEmailAddresses == null){
            		contactEmailAddresses = new StringBuilder(contactEmailTmp);            		
            	}else{
            		if(!contactEmailAddresses.toString().contains(contactEmailTmp)){
                		contactEmailAddresses.append("\n" + contactEmailTmp);
            		}
            	}				
            }
            emailCursor.close();
            if(contactEmailAddresses != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_EMAIL_ADDRESSES, contactEmailAddresses.toString());
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactsInfoByID() ERROR: " + ex.toString());
			return null;
		}
	}	
	
	/**
	 * Get the contact name for a given contact ID.
	 * 
	 * @param context - The application context.
	 * @param contactID - The contact ID to search the contacts by.
	 * 
	 * @return String - Returns a String of the contact name.
	 */ 
	public static String getContactNameByID(Context context, String contactID){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactNameByID() Contact ID: " + contactID);
		String contactName = "";
		if (contactID == null || contactID.equals("")) {
			if (_debug) Log.v("ContactsCommon.getContactNameByID() Contact ID provided is null or empty. Exiting...");
			return null;
		}
		try{
			//Get Name
			final String[] projection = null;
			final String selection = ContactsContract.Contacts._ID + " = " + contactID;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()){ 
				contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				break;
		   	}
			cursor.close();
			return contactName;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactNameByID() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Get the contact ID for a given contact name.
	 * 
	 * @param context - The application context.
	 * @param contactID - The contact name to search the contacts by.
	 * 
	 * @return long - Returns the contact ID.
	 */ 
	public static long getContactIDByName(Context context, String contactName){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.getContactIDByName() Contact Name: " + contactName);
		long contactID = -1;
		if (contactName == null || contactName.equals("")) {
			if (_debug) Log.v("ContactsCommon.getContactIDByName() Contact Name provided is null or empty. Exiting...");
			return -1;
		}
		try{
			//Get Contact ID
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.DISPLAY_NAME + "=?";
			final String[] selectionArgs = new String[] {contactName};
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			while (cursor.moveToNext()){
				contactID = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				break;
		   	}
			cursor.close();
			return contactID;
		}catch(Exception ex){
			Log.e("ContactsCommon.getContactIDByName() ERROR: " + ex.toString());
			return -1;
		}
	}
	
	/**
	 * Start the intent to view a contact.
	 * 
	 * @param context - The application context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param contactID - The id of the contact we want to view.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactViewActivity(Context context, NotificationActivity notificationActivity, long contactID, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.startContactViewActivity()");
		try{
			if(contactID < 0){
				Toast.makeText(context, context.getString(R.string.app_android_contact_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("ContactsCommon.startContactViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to edit a contact.
	 * 
	 * @param context - The application context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param contactID - The id of the contact we want to edit.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactEditActivity(Context context, NotificationActivity notificationActivity, long contactID, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.startContactEditActivity()");
		try{
			if(contactID < 0){
				Toast.makeText(context, context.getString(R.string.app_android_contact_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("ContactsCommon.startContactEditActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}	
	
	/**
	 * Start the intent to add a contact.
	 * 
	 * @param context - The application context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param sentFromAddress - The address (email or phone) of the contact we want to add.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactAddActivity(Context context, NotificationActivity notificationActivity, String sentFromAddress, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("ContactsCommon.startContactAddActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			if(sentFromAddress.contains("@")){
				intent.putExtra(ContactsContract.Intents.Insert.EMAIL, sentFromAddress);
			}else{
				intent.putExtra(ContactsContract.Intents.Insert.PHONE, sentFromAddress);
			}
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("ContactsCommon.startContactAddActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
}
