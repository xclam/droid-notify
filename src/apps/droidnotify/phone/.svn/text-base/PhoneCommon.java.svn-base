package apps.droidnotify.phone;

import java.lang.reflect.Method;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.contacts.ContactsCommon;
import apps.droidnotify.log.Log;

public class PhoneCommon {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Query the call log and check for any missed calls.
	 * 
	 * @param context - The application context.
	 * 
	 * @return Bundle - Returns a Bundle that contain the missed call notification information.
	 */
	public static Bundle getMissedCalls(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.getMissedCalls()");
		Bundle missedCallNotificationBundle = new Bundle();
		Cursor cursor = null;
		try{
			int bundleCount = 0;
			Boolean missedCallFound = false;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String missedCallPreference = preferences.getString(Constants.PHONE_DISMISS_BUTTON_ACTION_KEY, "0");
			final String[] projection = new String[] {
					android.provider.CallLog.Calls._ID, 
					android.provider.CallLog.Calls.NUMBER, 
					android.provider.CallLog.Calls.DATE, 
					android.provider.CallLog.Calls.TYPE, 
					android.provider.CallLog.Calls.NEW};
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://call_log/calls"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()){ 
	    		String callLogID = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
	    		String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
	    		long timeStamp = cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
	    		timeStamp = Common.convertGMTToLocalTime(context, timeStamp, true);
	    		String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
	    		String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
	    		if(Integer.parseInt(callType) == Constants.PHONE_TYPE && Integer.parseInt(isCallNew) > 0){
	    			Bundle missedCallNotificationBundleSingle = new Bundle();
    				bundleCount++;
    				if (_debug) Log.v("PhoneCommon.getMissedCalls() Missed Call Found: " + callNumber);
    				Bundle missedCallContactInfoBundle = null;
    				if(isPrivateUnknownNumber(context, callNumber)){
    					if (_debug) Log.v("PhoneCommon.getMissedCalls() Is a private or unknown number.");
    				}else{
    					missedCallContactInfoBundle = ContactsCommon.getContactsInfoByPhoneNumber(context, callNumber);
    				}
    				if(missedCallContactInfoBundle == null){				
    					//Basic Notification Information.
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_CALL_LOG_ID, Long.parseLong(callLogID));
    					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, callNumber);
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
    					missedCallNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PHONE);
    				}else{				
    					//Basic Notification Information.
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_CALL_LOG_ID, Long.parseLong(callLogID));
    					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, callNumber);
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
    					missedCallNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PHONE);
    	    			//Contact Information.
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_CONTACT_ID, missedCallContactInfoBundle.getLong(Constants.BUNDLE_CONTACT_ID, -1));
    					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_CONTACT_NAME, missedCallContactInfoBundle.getString(Constants.BUNDLE_CONTACT_NAME));
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_PHOTO_ID, missedCallContactInfoBundle.getLong(Constants.BUNDLE_PHOTO_ID, -1));
    					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_LOOKUP_KEY, missedCallContactInfoBundle.getString(Constants.BUNDLE_LOOKUP_KEY));
    				}
    				missedCallNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(bundleCount), missedCallNotificationBundleSingle);
    				if(missedCallPreference.equals(Constants.PHONE_GET_LATEST)){
    					if (_debug) Log.v("PhoneCommon.getMissedCalls() Missed call found - Exiting");
    					break;
    				}
    				missedCallFound = true;
    			}else{
    				if(missedCallPreference.equals(Constants.PHONE_GET_RECENT)){
    					if (_debug) Log.v("PhoneCommon.getMissedCalls() Found first non-missed call - Exiting");
    					break;
    				}
    			}
	    		if(!missedCallFound){
	    			if (_debug) Log.v("PhoneCommon.getMissedCalls() Missed call not found - Exiting");
	    			break;
	    		}
	    	}
			missedCallNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, bundleCount);
		}catch(Exception ex){
			Log.e("PhoneCommon.getMissedCalls() ERROR: " + ex.toString());
			missedCallNotificationBundle = null;
		}finally{
			cursor.close();
		}
	    return missedCallNotificationBundle;
	}
	
	/**
	 * Delete a call long entry.
	 * 
	 * @param context - The current context of this Activity.
	 * @param callLogID - The call log ID that we want to delete.
	 * 
	 * @return boolean - Returns true if the call log entry was deleted successfully.
	 */
	public static boolean deleteFromCallLog(Context context, long callLogID){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.deleteFromCallLog()");
		try{
			if(callLogID < 0){
				if (_debug) Log.v("PhoneCommon.deleteFromCallLog() Call Log ID < 0. Exiting...");
				return false;
			}
			String selection = android.provider.CallLog.Calls._ID + " = " + callLogID;
			String[] selectionArgs = null;
			context.getContentResolver().delete(
					Uri.parse("content://call_log/calls"),
					selection, 
					selectionArgs);
			return true;
		}catch(Exception ex){
			Log.e("PhoneCommon.deleteFromCallLog() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Mark a call log entry as being viewed.
	 * 
	 * @param context - The current context of this Activity.
	 * @param callLogID - The call log ID that we want to delete.
	 * 
	 * @return boolean - Returns true if the call log entry was updated successfully.
	 */
	public static boolean setCallViewed(Context context, long callLogID, boolean isViewed){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.setCallViewed()");
		try{
			if(callLogID < 0){
				if (_debug) Log.v("PhoneCommon.setCallViewed() Call Log ID < 0. Exiting...");
				return false;
			}
			ContentValues contentValues = new ContentValues();
			if(isViewed){
				contentValues.put(android.provider.CallLog.Calls.NEW, 0);
			}else{
				contentValues.put(android.provider.CallLog.Calls.NEW, 1);
			}
			String selection = android.provider.CallLog.Calls._ID + " = " + callLogID;
			String[] selectionArgs = null;
			context.getContentResolver().update(
					Uri.parse("content://call_log/calls"),
					contentValues,
					selection, 
					selectionArgs);
			return true;
		}catch(Exception ex){
			Log.e("PhoneCommon.setCallViewed() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Place a phone call.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The phone number we want to send a message to.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the application can be launched.
	 */
	public static boolean makePhoneCall(Context context, NotificationActivity notificationActivity, String phoneNumber, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.makePhoneCall()");
		try{
			phoneNumber = PhoneCommon.removePhoneNumberFormatting(phoneNumber);
			if(phoneNumber == null){
				Toast.makeText(context, context.getString(R.string.app_android_phone_number_format_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + phoneNumber));
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
		    return true;
		}catch(Exception ex){
			Log.e("PhoneCommon.makePhoneCall() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_phone_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to view the phones call log.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startCallLogViewActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.startCallLogViewActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android.cursor.dir/calls");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("PhoneCommon.startCallLogViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_call_log_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Determines if the incoming number is a Private or Unknown number.
	 * 
	 * @param incomingNumber - The incoming phone number.
	 * 
	 * @return boolean - Returns true if the number is a Private number or Unknown number.
	 */
	public static boolean isPrivateUnknownNumber(Context context, String incomingNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.isPrivateUnknownNumber() IncomingNumber: " + incomingNumber);
		try{
			if(incomingNumber == null){
				if (_debug) Log.v("PhoneCommon.isPrivateUnknownNumber() IncomingNumber is null. Exiting...");
				return false;
			}else if(incomingNumber.length() > 4){
				if (_debug) Log.v("PhoneCommon.isPrivateUnknownNumber() IncomingNumber is >4 digits. Exiting...");
				return false;
			}
			int convertedNumber = Integer.parseInt(incomingNumber);
			if(convertedNumber < 1) return true;
		}catch(Exception ex){
			if (_debug) Log.v("PhoneCommon.isPrivateUnknownNumber() Integer Parse Error");
			return false;
		}
		return false;
	}
	
	/**
	 * Cancel the stock missed call notification.
	 * 
	 * @return boolean - Returns true if the stock missed call notification was cancelled.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean clearStockMissedCallNotification(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.clearStockMissedCallNotification()");
		try{
			try{
		        Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
		        Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
		        Object phoneService = getServiceMethod.invoke(null, "phone");
		        Class ITelephonyClass = Class.forName("com.android.internal.telephony.ITelephony");
		        Class<?> ITelephonyStubClass = null;
		        for(Class clazz : ITelephonyClass.getDeclaredClasses()){
		            if (clazz.getSimpleName().equals("Stub")){
		                ITelephonyStubClass = clazz;
		                break;
		            }
		        }
		        if (ITelephonyStubClass != null){
		            Class IBinderClass = Class.forName("android.os.IBinder");
		            Method asInterfaceMethod = ITelephonyStubClass.getDeclaredMethod("asInterface", IBinderClass);
		            Object iTelephony = asInterfaceMethod.invoke(null, phoneService);
		            if (iTelephony != null){
		                Method cancelMissedCallsNotificationMethod = iTelephony.getClass().getMethod("cancelMissedCallsNotification");
		                cancelMissedCallsNotificationMethod.invoke(iTelephony);
		            }else{
		            	Log.e("Telephony service is null, can't call cancelMissedCallsNotification.");
		            }
		        }else{
		            if (_debug) Log.v("Unable to locate ITelephony.Stub class.");
		        }
		    }catch (Exception ex){
		    	Log.e("PhoneCommon.clearStockMissedCallNotification() REFLECTION ERROR: " + ex.toString());
		    }
			//Send broadcast to NotiGo (If installed)
			Intent notiGoBroadcastIntent = new Intent();
			notiGoBroadcastIntent.setAction("thinkpanda.notigo.CLEAR_MISSED_CALL");
	        context.sendBroadcast(notiGoBroadcastIntent);
	        return true;
	    }catch (Exception ex){
	    	Log.e("PhoneCommon.clearStockMissedCallNotification() ERROR: " + ex.toString());
	    	return false;
	    }
	}
	
	/**
	 * Function to format phone numbers.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputPhoneNumber - Phone number to be formatted.
	 * 
	 * @return String - Formatted phone number string.
	 */
	public static String formatPhoneNumber(Context context, String inputPhoneNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneCommon.formatPhoneNumber()");
		try{
			if(inputPhoneNumber == null){
				if (_debug) Log.v("PhoneCommon.formatPhoneNumber() InputPhoneNumber is null. exiting...");
				return null;
			}
			if(inputPhoneNumber.equals(context.getString(R.string.private_number_text))){
				return inputPhoneNumber;
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			inputPhoneNumber = PhoneCommon.removePhoneNumberFormatting(inputPhoneNumber);
			StringBuilder outputPhoneNumber = new StringBuilder("");		
			int phoneNumberFormatPreference = Integer.parseInt(preferences.getString(Constants.PHONE_NUMBER_FORMAT_KEY, Constants.PHONE_NUMBER_FORMAT_DEFAULT));
			String numberSeparator = "-";
			if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_7 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_8 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_9 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_10 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_16){
				numberSeparator = ".";
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_11 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_12 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_13 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_14 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_17){
				numberSeparator = " ";
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_5){
				numberSeparator = "";
			}
			if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_1 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_7 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_11){
				if(inputPhoneNumber.length() >= 10){
					//Format ###-###-#### (e.g.123-456-7890)
					//Format ###-###-#### (e.g.123.456.7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_2 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_8 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_12){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-###-##### (e.g.12-345-67890)
					//Format ##-###-##### (e.g.12.345.67890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 5, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 5));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_3 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_9 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_13){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-###-##### (e.g.01-234-567890)
					//Format ##-###-##### (e.g.01.234.567890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 9, inputPhoneNumber.length() - 6));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 9));
					}else if(inputPhoneNumber.length() == 11){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 9));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 11, inputPhoneNumber.length() - 9));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 11));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_4 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_10 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_14){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-##-##-##-## (e.g.12-34-56-78-90)
					//Format ##-##-##-##-## (e.g.12.34.56.78.90)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 2, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length() - 2));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 6));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_15 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_16 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_17){
				if(inputPhoneNumber.length() >= 10){
					//Format ###-###-#### (e.g.012-3456-7890)
					//Format ###-###-#### (e.g.012.3456.7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_5){
				//Format ########## (e.g.1234567890)
				outputPhoneNumber.append(inputPhoneNumber);
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_6){
				if(inputPhoneNumber.length() >= 10){
					//Format (###) ###-#### (e.g.(123) 456-7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, ") ");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, "(");
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, " (");
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else{
				outputPhoneNumber.append(inputPhoneNumber);
			}
			return outputPhoneNumber.toString();
		}catch(Exception ex){
			Log.e("PhoneCommon.formatPhoneNumber() ERROR: " + ex.toString());
			return inputPhoneNumber;
		}
	}
	
	/**
	 * Compares the two strings. 
	 * If the second string is larger and ends with the first string, return true.
	 * If the first string is larger and ends with the second string, return true.
	 * 
	 * @param contactNumber - The address books phone number.
	 * @param incomingNumber - The incoming phone number.
	 * 
	 * @return - boolean - 	 If the second string is larger ends with the first string, return true.
	 *                       If the first string is larger ends with the second string, return true.
	 */
	public static boolean isPhoneNumberEqual(String contactNumber, String incomingNumber){
		_debug = Log.getDebug();
		//if (_debug) Log.v("PhoneCommon.isPhoneNumberEqual() ContactNumber: " + contactNumber + " IncomingNumber: " + incomingNumber);
		try{
			if(contactNumber == null || incomingNumber == null){
				if (_debug) Log.v("PhoneCommon.isPhoneNumberEqual() ContactNumber OR IncomingNumber IS NULL. Exiting...");
				return false;
			}
			//Remove any formatting from each number.
			contactNumber = PhoneCommon.removePhoneNumberFormatting(contactNumber);
			incomingNumber = PhoneCommon.removePhoneNumberFormatting(incomingNumber);
			//Remove any leading zero's from each number.
			contactNumber = removeLeadingZero(contactNumber);
			incomingNumber = removeLeadingZero(incomingNumber);	
			int contactNumberLength = contactNumber.length();
			int incomingNumberLength = incomingNumber.length();
			//Check to see if the contactNumber is not the empty string. If it is, return false.
			if(contactNumberLength < 1){
				return false;
			}
			//Iterate through the ends of both strings...backwards from the end of the string.
			if(contactNumberLength <= incomingNumberLength){
				if(!incomingNumber.endsWith(contactNumber)){
					return false;
				}
			}else{
				if(!contactNumber.endsWith(incomingNumber)){
					return false;
				}
			}
			return true;
		}catch(Exception ex){
			Log.e("PhoneCommon.isPhoneNumberEqual() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Remove all non-numeric items from the phone number.
	 * 
	 * @param phoneNumber - String of original phone number.
	 * 
	 * @return String - String of phone number with no formatting.
	 */
	public static String removePhoneNumberFormatting(String phoneNumber){
		//if (_debug) Log.v("PhoneCommon.removePhoneNumberFormatting()");
		phoneNumber = phoneNumber.replace(" ", "");
		phoneNumber = phoneNumber.replace("-", "");
		phoneNumber = phoneNumber.replace(".", "");
		phoneNumber = phoneNumber.replace(",", "");
		phoneNumber = phoneNumber.replace("(", "");
		phoneNumber = phoneNumber.replace(")", "");
		phoneNumber = phoneNumber.replace("/", "");
		phoneNumber = phoneNumber.replace("x", "");
		phoneNumber = phoneNumber.replace("X", "");
		return phoneNumber.trim();
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Remove the leading zero from a string.
	 * 
	 * @param inputNumber - The number to remove the leading zero from.
	 * 
	 * @return String - The number after we have removed the leading zero.
	 */
	private static String removeLeadingZero(String inputNumber){
		//if (_debug) Log.v("PhoneCommon.removeLeadingZero() InputNumber: " + inputNumber);
		if(inputNumber.substring(0, 1).equals("0")){
			//Do not edit number if the number is exactly 0.
			//Only update if there is more than 1 digit.
			if(inputNumber.length() > 1){
				return inputNumber.substring(1);
			}
		}
		return inputNumber;
	}
	
}
