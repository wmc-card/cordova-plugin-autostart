package com.tonikorin.cordova.plugin.autostart;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import com.tonikorin.cordova.plugin.autostart.AutoStart;
import de.appplant.cordova.plugin.background.ForegroundService;
import android.util.Log;

public class AppStarter {

    public static final int BYPASS_USERPRESENT_MODIFICATION = -1;
    private static final String CORDOVA_AUTOSTART = "cordova_autostart";

    public void run(Context context, Intent intent, int componentState) {
	     this.run(context, intent, componentState, false);
    }

    public void run(Context context, Intent intent, int componentState, boolean onAutostart) {
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("My notification")
            .setContentText("Hello World!");

        NotificationManager mNotificationManager =

            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManager.notify().

        mNotificationManager.notify(001, mBuilder.build());	    
	    
	    
	    
        // Enable or Disable UserPresentReceiver (or bypass the modification)
        //Log.d("Cordova AppStarter", "UserPresentReceiver component, new state:" + String.valueOf(componentState));

        if( componentState != BYPASS_USERPRESENT_MODIFICATION ) {
            ComponentName receiver = new ComponentName(context, UserPresentReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, componentState, PackageManager.DONT_KILL_APP);
        }

        // Starting your app...
        //Log.d("Cordova AppStarter", "STARTING APP...");
        SharedPreferences sp = context.getSharedPreferences(AutoStart.PREFS, Context.MODE_PRIVATE);
        String packageName = context.getPackageName();
        String activityClassName = sp.getString(AutoStart.ACTIVITY_CLASS_NAME, "");
        if( !activityClassName.equals("") ){
            //Log.d("Cordova AppStarter", className);
            Intent activityIntent = new Intent();
            //activityIntent.setClassName(
                //context, String.format("%s.%s", packageName, activityClassName));
		activityIntent.setClassName("de.appplant.cordova.plugin.background", "de.appplant.cordova.plugin.background.ForegroundService");
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (onAutostart) {
              activityIntent.putExtra(CORDOVA_AUTOSTART, true);
            }
            context.startActivity(activityIntent);
        }
        // Start a service in the background.
        String serviceClassName = sp.getString(AutoStart.SERVICE_CLASS_NAME, "");
        //String servicePackageName = serviceClassName.substring(0, serviceClassName.lastIndexOf("."));
String servicePackageName = "";
try {
    servicePackageName = serviceClassName.substring(0, serviceClassName.lastIndexOf("."));

} catch (Exception e) {
    //TODO: handle exception
    Log.d("MyDebug", "Error in setting servicePackageName");
}
        if ( !serviceClassName.equals("") ) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClassName("de.appplant.cordova.plugin.background", "de.appplant.cordova.plugin.background.ForegroundService");
            if ( onAutostart ) {
                serviceIntent.putExtra(CORDOVA_AUTOSTART, true);
            }
            context.startService(serviceIntent);
        }
    }
}
