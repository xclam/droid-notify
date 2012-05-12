package apps.droidnotify.preferences.custom;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import apps.droidnotify.R;

/**
 * The ImageListPreference class responsible for displaying an image for each
 * item within the list.
 * 
 * @author Camille Sévigny
 */
public class ImageListPreference extends ListPreference {

	//================================================================================
    // Properties
    //================================================================================
	
	private int[] _resourceIds = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Constructor of the ImageListPreference. 
	 * Initializes the custom images.
	 * 
	 * @param context application context.
	 * @param attrs custom xml attributes.
	 */
	public ImageListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageList);
		String[] imageNames = context.getResources().getStringArray(typedArray.getResourceId(typedArray.getIndexCount()-1, -1));
		_resourceIds = new int[imageNames.length];
		for (int i=0;i<imageNames.length;i++) {
			String imageName = imageNames[i].substring(imageNames[i].lastIndexOf('/') + 1, imageNames[i].lastIndexOf('.'));
			_resourceIds[i] = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
		}
		typedArray.recycle();
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * 
	 */
	protected void onPrepareDialogBuilder(Builder builder) {
		int index = findIndexOfValue(getSharedPreferences().getString(getKey(), "0"));
		ListAdapter listAdapter = null;
		listAdapter = new ImageArrayAdapter(getContext(), R.layout.listitem, this.getEntries(), _resourceIds, index);
		// Order matters.
		builder.setAdapter(listAdapter, this);
		super.onPrepareDialogBuilder(builder);
	}

}
