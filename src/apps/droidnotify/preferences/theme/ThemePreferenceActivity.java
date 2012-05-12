package apps.droidnotify.preferences.theme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class displays the themes for a user to select from.
 * 
 * @author Camille Sévigny
 */
public class ThemePreferenceActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private ArrayList<ThemeView> _themeViews = null;
	private ThemeViewFlipper _themeViewFlipper = null;
	private LoadThemesAsyncTask _currentLoadThemesAsyncTask = null;
	private final Object _loadThemesAsyncTaskLock = new Object();
	private ProgressBar _themeProgressBar  = null;
	private TextView _okTextView = null;
	private TextView _moreTextView = null;
	private MotionEvent _downMotionEvent = null;

	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * This function intercepts all the touch events.
	 * In here we decide what to pass on to child items and what to handle ourselves.
	 * 
	 * @param motionEvent - The touch event that occurred.
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent){
	    switch (motionEvent.getAction()){
	        case MotionEvent.ACTION_DOWN:{
		        //Keep track of the starting down-event.
		        _downMotionEvent = MotionEvent.obtain(motionEvent);
		        break;
	        }
	        case MotionEvent.ACTION_UP:{
	            //Consume if necessary and perform the fling / swipe action if it has been determined to be a fling / swipe.
	        	float deltaX = motionEvent.getX() - _downMotionEvent.getX();
		        if(Math.abs(deltaX) > new ViewConfiguration().getScaledTouchSlop()*2){
		        	if (deltaX < 0){
		        		_themeViewFlipper.showNext();
	           	    	return true;
					}else if (deltaX > 0){
						_themeViewFlipper.showPrevious();
	           	    	return true;
	               	}
		        }
	            break;
	        }
	    }
	    return super.dispatchTouchEvent(motionEvent);
	}	

	//================================================================================
	// Protected Methods
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
	    if (_debug) Log.v("ThemePreferenceActivity.onCreate()");
	    _context = getApplicationContext();
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.theme_preference_activity);
	    initLayoutItems();
	    setupButtons();
	}

	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    if (_debug) Log.v("NotificationActivity.onResume()");
	    super.onResume();
        synchronized (_loadThemesAsyncTaskLock) {
            if (_currentLoadThemesAsyncTask == null){
			    LoadThemesAsyncTask loadThemesAsyncTask = new LoadThemesAsyncTask();
			    _currentLoadThemesAsyncTask = loadThemesAsyncTask;
			    _currentLoadThemesAsyncTask.execute();
			}
        }
	}
	
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    if (_debug) Log.v("NotificationActivity.onPause()");
	    super.onPause();
	    synchronized (_loadThemesAsyncTaskLock) {
            if(_currentLoadThemesAsyncTask != null){
            	_currentLoadThemesAsyncTask.cancel(true);
            	_currentLoadThemesAsyncTask = null;
            }
        }
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Initialize the layout items.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("ThemePreferenceActivity.initLayoutItems()");
		_themeViewFlipper = (ThemeViewFlipper)findViewById(R.id.theme_view_flipper);
		_themeProgressBar = (ProgressBar)findViewById(R.id.theme_progress_bar);
		_okTextView = (TextView)findViewById(R.id.ok_button);
		_moreTextView = (TextView)findViewById(R.id.more_button);
	}
	
	/**
	 * Setup the activity buttons.
	 */
	private void setupButtons(){
		if (_debug) Log.v("ThemePreferenceActivity.setupButtons()");
		//OK Button.
		_okTextView.setBackgroundResource(R.drawable.preference_row_click);
		_okTextView.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
    			SharedPreferences.Editor editor = _preferences.edit();
    			editor.putString(Constants.APP_THEME_KEY, _themeViewFlipper.getThemePackage());
    			editor.commit();
        		finish();
        	}
		});
		//More Button.
		_moreTextView.setBackgroundResource(R.drawable.preference_row_click);
		_moreTextView.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
			    	String searchAppURL = "";
			    	if(Log.getShowAndroidRateAppLink()){
			    		searchAppURL = Constants.APP_SEARCH_ANDROID_URL;
			    	}else if(Log.getShowAmazonRateAppLink()){
			    		searchAppURL = Constants.APP_SEARCH_AMAZON_URL;
			    	}else{
			    		searchAppURL = "";
			    	}
		    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(searchAppURL)));
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Rate App Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_LONG).show();
		    	}
        	}
		});
	}
	
	/**
	 * Load the themes to choose from including installed theme packages.
	 */
	private void loadThemes(){
		if (_debug) Log.v("ThemePreferenceActivity.loadThemes()");
		_themeViews = new ArrayList<ThemeView>();
		//Load Included Themes.
		_themeViews.add(new ThemeView(_context, _themeViewFlipper, Constants.DARK_TRANSLUCENT_THEME));
		_themeViews.add(new ThemeView(_context, _themeViewFlipper, Constants.DARK_TRANSLUCENT_V2_THEME));
		_themeViews.add(new ThemeView(_context, _themeViewFlipper, Constants.DARK_TRANSLUCENT_V3_THEME));
		//Load Installed Themes.   
		try{
		    List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		    for(int i=0;i<packages.size();i++) {
				try{
			        PackageInfo packageInfo = packages.get(i);
			        String packageName = packageInfo.packageName;
			        if(packageName.startsWith(Constants.APP_THEME_PREFIX)){
			        	if (_debug) Log.v("ThemePreferenceActivity.loadThemes() Found Installed Package: " + packageName);
			        	_themeViews.add(new ThemeView(_context, _themeViewFlipper, packageName));
			        }
				}catch(Exception ex){
					Log.e("ThemePreferenceActivity.loadThemes() PACKAGE SPECIFIC ERROR: " + ex.toString());
				}
		    }
		}catch(Exception ex){
			Log.e("ThemePreferenceActivity.loadThemes() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Set the displayed view flipper theme to be the currently selected theme.
	 */
	private void setDisplayedTheme(){
		if (_debug) Log.v("ThemePreferenceActivity.setDisplayedTheme()");
		_themeViewFlipper.setDisplayedTheme(_preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT));
	}
	
	/**
	 * Load the ViewFlipper themes.
	 * 
	 * @author Camille Sévigny
	 */
	private class LoadThemesAsyncTask extends AsyncTask<Void, Void, Boolean> {
		
		/**
		 * Set up the theme loading view.
		 */
	    protected void onPreExecute() {
			if (_debug) Log.v("ThemePreferenceActivity.LoadThemesAsyncTask.onPreExecute()");
			_themeViewFlipper.setVisibility(View.GONE);
			_themeProgressBar.setVisibility(View.VISIBLE);
	    }
	    
	    /**
	     * Load the themes in the background.
	     */
	    protected Boolean doInBackground(Void... params) {
			if (_debug) Log.v("ThemePreferenceActivity.LoadThemesAsyncTask.doInBackground()");
			loadThemes();
			return true;
	    }
	    
	    /**
	     * Display the available themes.
	     */
		protected void onPostExecute(Boolean success) {
			if (_debug) Log.v("ThemePreferenceActivity.LoadThemesAsyncTask.onPostExecute()");
			int size = _themeViews.size();
			for(int i=0; i<size; i++){
				_themeViewFlipper.addTheme(_themeViews.get(i));
			}
			_themeViewFlipper.setVisibility(View.VISIBLE);
			_themeProgressBar.setVisibility(View.GONE);
		    setDisplayedTheme();
	    }
	}
	
}