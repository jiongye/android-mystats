package com.sanbit.android.mystats;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.os.IBinder;
import android.util.Log;


public class ContactAlarmIntentReceiver extends IntentReceiver {
    private static final String LOG = "ContactAlarmIntentReceiver";
   
    public void onReceiveIntent(Context context, Intent intent) {

      Log.d(LOG,"starting alarm service...");
      context.startService(new Intent(context,SyncService.class), null);
      Intent contactAlarmIntent = new Intent(context,ContactAlarmService.class);
      context.startService(contactAlarmIntent,null);
      
    }
}