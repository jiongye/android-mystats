package com.sanbit.android.mystats;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.util.Log;

public class FileUploadIntentReceiver extends IntentReceiver {
    private static final String LOG = "FileUploadIntentReceiver";

    //This method is called to perform intent specific processing
    public void onReceiveIntent(Context context, Intent intent) {
      
      context.startService(new Intent(context,FileUploadService.class), null);
      //TODO: get the return status and display either a popup(fail) or toast (success)
      
      
      //Toast.makeText(context,"I love you!",5).show();
      //Creating an intent to display GrabImage activity
      //Intent viewImageIntent = new Intent();
      //viewImageIntent.setClassName(context, "br.eti.faces.grabimage.GrabImage");
      //viewImageIntent.setLaunchFlags(Intent.NEW_TASK_LAUNCH);
      //Displaying the activity based on intent parameters
      //context.startActivity(advertisingIntent);       
    }
}