package com.sanbit.android.mystats;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.widget.Toast;
import android.util.Log;

public class CallPopupIntentReceiver extends IntentReceiver {
    private static final String LOG = "callPopupIntentReceiver";

    //This method is called to perform intent specific processing
    public void onReceiveIntent(Context context, Intent intent) {

      //Logging intent execution
      Log.d(LOG, "Starting app...");
      //Toast.makeText(context,"I love you!",5).show();
      //Creating an intent to display GrabImage activity
      //Intent viewImageIntent = new Intent();
      //viewImageIntent.setClassName(context, "br.eti.faces.grabimage.GrabImage");
      //viewImageIntent.setLaunchFlags(Intent.NEW_TASK_LAUNCH);
      
      //Displaying the activity based on intent parameters
      //context.startActivity(advertisingIntent);       
    }
}