package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "Upgrade" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class UpgradePreferenceActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private TextView _titleTextView = null;
	private TextView _contentTextView = null;
	private TextView _buttonTextView = null;
	private int _upgradeType = -1;

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
	    if (_debug) Log.v("UpgradePreferenceActivity.onCreate()");
	    _context = getApplicationContext();
	    _upgradeType = getIntent().getExtras().getInt(Constants.DIALOG_UPGRADE_TYPE);
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.upgrade_preference_activity);
	    initLayoutItems();
	}
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application context.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("UpgradePreferenceActivity.initLayoutItems()");
		//Find Views.
		_titleTextView = (TextView)findViewById(R.id.header_title);
		_contentTextView = (TextView)findViewById(R.id.content_text);
		_buttonTextView = (TextView)findViewById(R.id.button);
		_buttonTextView.setBackgroundResource(R.drawable.preference_row_click);	
		final String upgradeURL;
		String titleText = null;
		String descriptionText = null;
		String buttonText = null;
		boolean displayUpgradeButton = true;
		if(_upgradeType == Constants.DIALOG_FEATURE_PRO_ONLY){
			titleText = _context.getString(R.string.upgrade_to_pro);
			if(Log.getShowAndroidRateAppLink()){
				displayUpgradeButton = true;
				descriptionText = _context.getString(R.string.upgrade_description_text);
				buttonText = _context.getString(R.string.upgrade_now_text);
				upgradeURL = Constants.APP_PRO_ANDROID_URL;
	        }else if(Log.getShowAmazonRateAppLink()){
				displayUpgradeButton = true;
				descriptionText = _context.getString(R.string.upgrade_description_text);
				buttonText = _context.getString(R.string.upgrade_now_text);
				upgradeURL = Constants.APP_PRO_AMAZON_URL;
	        }else{
				displayUpgradeButton = false;
				descriptionText = _context.getString(R.string.upgrade_no_market_description_text);
				buttonText = _context.getString(R.string.close);
				upgradeURL = Constants.APP_PAYPAL_URL;
	        }
		}else if(_upgradeType == Constants.DIALOG_UPGRADE){
			titleText = _context.getString(R.string.upgrade);
			if(Log.getShowAndroidRateAppLink()){
				displayUpgradeButton = true;
				descriptionText = _context.getString(R.string.upgrade_direct_description_text);
				buttonText = _context.getString(R.string.upgrade_now_text);
				upgradeURL = Constants.APP_PRO_ANDROID_URL;
	        }else if(Log.getShowAmazonRateAppLink()){
				displayUpgradeButton = true;
				descriptionText = _context.getString(R.string.upgrade_direct_description_text);
				buttonText = _context.getString(R.string.upgrade_now_text);
				upgradeURL = Constants.APP_PRO_AMAZON_URL;
	        }else{
				upgradeURL = Constants.APP_PRO_ANDROID_URL;
	        }
		}else{
			upgradeURL = Constants.APP_PRO_ANDROID_URL;
        }
		_titleTextView.setText(titleText);
		_contentTextView.setText(descriptionText);
		_buttonTextView.setText(buttonText);	
        if(displayUpgradeButton){
        	_buttonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(upgradeURL)));
		    		finish();
	        	}
	        });
		}else{
			_buttonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		finish();
	        	}
	        });
		}
	}
	
}