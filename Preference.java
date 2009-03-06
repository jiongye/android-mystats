package com.sanbit.android.mystats;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;
import android.content.Intent;
import android.app.AlarmManager;
import android.os.SystemClock;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class Preference extends Activity implements OnClickListener{
	
	  public static final String PREFS = "Prefs";
    private RadioButton daily;
    private RadioButton threeDays;
    private RadioButton fiveDays;
    private RadioButton manually;
    protected static RadioGroup uploadIntervalRadioGroup; 
    protected static Integer uploadInterval; //default is 1 day
    private static final String LOG = "Preference";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Button confirmButton;
    private Button logoutButton;
    
	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.preference);
        uploadIntervalRadioGroup = (RadioGroup) findViewById(R.id.upload_interval);
        confirmButton = (Button) findViewById(R.id.confirm);
        logoutButton = (Button) findViewById(R.id.logout);
        
        settings = getSharedPreferences(PREFS, 0);
        editor = settings.edit();
        uploadInterval = settings.getInt("uploadInterval", 1440);

        daily = (RadioButton)this.findViewById(R.id.upload_daily);
        threeDays = (RadioButton)this.findViewById(R.id.three_days);
        fiveDays = (RadioButton)this.findViewById(R.id.five_days);
        manually = (RadioButton)this.findViewById(R.id.manually);

        switch (uploadInterval) {  
             case 4320 : threeDays.setChecked(true); break;
             case 7200: fiveDays.setChecked(true); break;
             case 0: manually.setChecked(true); break;
             default: daily.setChecked(true); //default is daily
         }
        
        confirmButton.setOnClickListener(this);
        
        logoutButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
    if (v == logoutButton){
      editor.putString("cookie_name", "");
  	  editor.putString("cookie_value", "");
  	  editor.putString("cookie_domain", "");
      editor.commit();
      Toast.makeText(this, "Logged out!!",Toast.LENGTH_SHORT).show();
      setResult(RESULT_OK);
      finish();
    }else if (v == confirmButton){
    	setResult(RESULT_OK);
      finish();
    }
  }
	
	@Override
    protected void onStop(){
       super.onStop();
    
       Integer checkedId = new Integer(uploadIntervalRadioGroup.getCheckedRadioButtonId());
       Integer timeInMinutes = 0;
       Log.d(LOG,checkedId.toString());
       switch (checkedId) {  
           case R.id.three_days: timeInMinutes = 4320; break;
           case R.id.five_days: timeInMinutes = 7200; break;
           case R.id.manually: timeInMinutes = 0; break;
           default: timeInMinutes = 1440; //default is daily
       }
       editor.putInt("uploadInterval", timeInMinutes);
       editor.commit();
       if(timeInMinutes > 0){ //if not set to manual
         Intent intent = new Intent(this, FileUploadIntentReceiver.class);
         long firstTime = SystemClock.elapsedRealtime();
         //firstTime += 2*1000; //run it once 2 seconds from now
         AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
         Long interval = timeInMinutes*60000L; //how often to run the service in milliseconds
         am.cancel(intent); //first cancel old alarm manager, then create a new one with the new time
         am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime, interval, intent);
       }else{
         //cancel the upload service service
         Intent intent = new Intent(this, FileUploadIntentReceiver.class);
         AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
         am.cancel(intent);
       }
       
    }	
}
