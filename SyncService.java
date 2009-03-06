package com.sanbit.android.mystats;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.os.Parcel;

public class SyncService extends Service implements Runnable {
    
	private Record record;
    @Override
    protected void onCreate() {
    	record = new Record(this);
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        Thread thr = new Thread(null, this, "SyncService");
        thr.start();
    }

    @Override
    protected void onDestroy() {
        record.close();
    }
    
    @Override
    public IBinder onBind(Intent intent){
      return null;
    }
	
    public void run() {
        
        try {
          record.collectNewStats();
        } catch (Exception ex) {
          //we want to keep some kind of error log file
        }
        this.stopSelf();
    }
	
    public IBinder getBinder() {
        return null;
    }
	
}
