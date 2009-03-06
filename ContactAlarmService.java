package com.sanbit.android.mystats;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.os.Parcel;

import android.app.AlertDialog;
import android.view.View.OnClickListener;
import android.content.DialogInterface;

import android.os.SystemClock;
import java.util.ArrayList;
import android.util.Log;
import java.util.List;
import android.widget.Toast;
import android.os.Handler;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.Context;

import com.sanbit.android.mystats.Person.PersonRow;
import com.sanbit.android.mystats.Helper;

import com.sanbit.android.mystats.ContactAlarm.ContactAlarmRow;

public class ContactAlarmService extends Service implements Runnable {
  private static final String LOG = "ContactAlarmService";
  private ContactAlarm contactAlarm;
  private Person person;
  final Handler mHandler = new Handler(); 
  
  
  @Override
  protected void onCreate() {
  	  contactAlarm = new ContactAlarm(this);
  	  person = new Person(this);
      // Start up the thread running the service.  Note that we create a
      // separate thread because the service normally runs in the process's
      // main thread, which we don't want to block.
       Log.d(LOG,"HWWWWWWW");
      Thread thr = new Thread(null, this, "ContactAlarmService");
      thr.start();
  }
  
  @Override
  public IBinder onBind(Intent intent){
    return null;
  }
  


    protected void processAlarms(){
      List<ContactAlarmRow> alarms = contactAlarm.find("active > 0");
      Long time = System.currentTimeMillis();
      boolean alarms_exist = false;
      NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      for(ContactAlarmRow alarm : alarms){
        PersonRow personRow = person.findById(alarm.person_id);

        if(personRow != null){
          //if((time - personRow.last_call_timestamp) > alarm.interval * 86400000){
 
            alarms_exist = true;
            Intent intent = new Intent(this,UserReport.class);
            intent.putExtra("last_call_timestamp",personRow.last_call_timestamp);
            intent.putExtra("person_id", personRow._id);
            intent.putExtra("person_name", personRow.name);
            int id = Integer.parseInt("69"+personRow._id); //try to generate a unique number!!
            intent.putExtra("notification_id",id);
            Notification notification = new Notification(this,R.drawable.alarm22,"Contact Alarm",0L,"Alarm for "+personRow.toString(),"Last contact: " +Helper.timeAgo(personRow.last_call_timestamp), intent,R.drawable.sanbit,"Sanbit",intent);
            nm.cancel(id);
            nm.notify(id,notification);
          //} 
        }else{
          //exception or log error, how do we have an alarm with a user without an id? the id could have changed
        }
      }
    }

  
  //@Override
  public void run(){
    mHandler.post(mUpdate);
  }
  final Runnable mUpdate = new Runnable() {
      public void run() {
        processAlarms();
        stopSelf();
      }
  };
    
}