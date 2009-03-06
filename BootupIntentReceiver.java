package com.sanbit.android.mystats;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.content.SharedPreferences;
import android.util.Log;
import android.app.AlarmManager;
import java.util.Calendar;

public class BootupIntentReceiver extends IntentReceiver {
    private static final String LOG = "BootupIntentReceiver";

    //This method is called to perform intent specific processing
    public void onReceiveIntent(Context context, Intent intent) {
      BootupIntentReceiver.bootup(context);
    }
    
    public static void bootup(Context context){
      Log.d(LOG,"Booting up sanbit services"); 
      Calendar today = Calendar.getInstance();
      today.set(Calendar.HOUR,12);
      today.set(Calendar.MINUTE, 0);
      today.set(Calendar.SECOND,0);
      today.set(Calendar.MILLISECOND,0);
      Long noon = today.getTime().getTime();
      AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
      
      Intent contactAlarmIntent = new Intent(context, ContactAlarmIntentReceiver.class);
      //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,noon, 86400000, contactAlarmIntent); //once a day at noon
      am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,Calendar.getInstance().getTime().getTime(),86400000, contactAlarmIntent); //for android DEMO
      //TODO: please refactor this horrible code, it is repeated in 2 places, here and in preferences:
      SharedPreferences settings = context.getSharedPreferences(Preference.PREFS, 0);
      SharedPreferences.Editor editor = settings.edit();
      Long uploadInterval = settings.getInt("uploadInterval", 1440) * 60000L;
      Intent fileUploadIntent = new Intent(context, FileUploadIntentReceiver.class);
      am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,noon, uploadInterval, fileUploadIntent);
      
      
      //set this to run every ten minutes for now for the demo
      Intent syncIntent = new Intent(context, SyncIntentReceiver.class);
      am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,Calendar.getInstance().getTime().getTime(), 60000, syncIntent);

    }
    
    
}