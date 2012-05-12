package apps.droidnotify.preferences.k9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is a download activity to inform the user that they need to install a compatible K-9 email client.
 * 
 * @author Camille Sévigny
 */
public class K9DownloadPreferenceActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private RelativeLayout _buttonRelativeLayout = null;
	private TextView _contentTextView = null;
	private TextView _k9ButtonTextView = null;
	private TextView _kaitenButtonTextView = null;
	private TextView _okButtonTextView = null;

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
	    if (_debug) Log.v("K9DownloadPreferenceActivity.onCreate()");
	    _context = getApplicationContext();
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.k9_download_preference_activity);
	    initLayoutItems();
	}
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application context.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("K9DownloadPreferenceActivity.initLayoutItems()");
		//Find Views.
		_buttonRelativeLayout = (RelativeLayout)findViewById(R.id.button_relative_layout);
		_contentTextView = (TextView)findViewById(R.id.content_text);
		_k9ButtonTextView = (TextView)findViewById(R.id.k9_button);	
		_kaitenButtonTextView = (TextView)findViewById(R.id.kaiten_button);	
		_okButtonTextView = (TextView)findViewById(R.id.ok_button);			
		_k9ButtonTextView.setBackgroundResource(R.drawable.preference_row_click);	
		_kaitenButtonTextView.setBackgroundResource(R.drawable.preference_row_click);	
		_okButtonTextView.setBackgroundResource(R.drawable.preference_row_click);	
		final String k9DownloadURL;
		final String kaitenDownloadURL;
		String descriptionText = null;
		boolean displayDownloadButtons = true;
		if(Log.getShowAndroidRateAppLink()){
			displayDownloadButtons = true;
			descriptionText = _context.getString(R.string.package_k9_not_found);
			k9DownloadURL = Constants.K9_MAIL_ANDROID_URL;
			kaitenDownloadURL = Constants.KAITEN_MAIL_ANDROID_URL;
        }else if(Log.getShowAmazonRateAppLink()){
        	displayDownloadButtons = true;
			descriptionText = _context.getString(R.string.package_k9_not_found);
			k9DownloadURL = Constants.K9_MAIL_AMAZON_URL;
			kaitenDownloadURL = Constants.KAITEN_MAIL_AMAZON_URL;
        }else{
			displayDownloadButtons = false;
			descriptionText = _context.getString(R.string.package_k9_not_found_generic);			
			k9DownloadURL = Constants.K9_MAIL_ANDROID_URL;
			kaitenDownloadURL = Constants.KAITEN_MAIL_ANDROID_URL;
        }
		_contentTextView.setText(descriptionText);
        if(displayDownloadButtons){			
        	ImageView rowDivider = (ImageView)findViewById(R.id.ok_button_divider);
        	rowDivider.setVisibility(View.GONE);
        	_buttonRelativeLayout.setVisibility(View.VISIBLE);
        	_okButtonTextView.setVisibility(View.GONE);
        	_k9ButtonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(k9DownloadURL)));
		    		finish();
	        	}
	        });        	
        	_kaitenButtonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(kaitenDownloadURL)));
		    		finish();
	        	}
	        });
		}else{			
			ImageView rowDivider = (ImageView)findViewById(R.id.k9_button_divider);
			rowDivider.setVisibility(View.GONE);
        	_buttonRelativeLayout.setVisibility(View.GONE);
        	_okButtonTextView.setVisibility(View.VISIBLE);
        	_okButtonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		finish();
	        	}
	        });
		}
	}
	
}