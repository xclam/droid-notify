package apps.droidnotify;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application{

	private static Context _context;

    public void onCreate(){
        super.onCreate();
        _context = this.getApplicationContext();
    }

    public static Context getContext() {
        return _context;
    }
    
}