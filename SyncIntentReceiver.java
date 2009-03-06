package com.sanbit.android.mystats;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.util.Log;


public class SyncIntentReceiver extends IntentReceiver {
    private static final String LOG = "SyncIntentReceiver";
   
    public void onReceiveIntent(Context context, Intent intent) {
      Log.d(LOG,"starting sync service...");
      Intent syncIntent = new Intent(context,SyncService.class);
      context.startService(syncIntent,null);
      
    }
}