package com.sanbit.android.mystats;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.net.Uri;


public class HomeActivity extends Activity implements OnClickListener {
	private static final int VIEW_REPORTS=0;
	private static final int VIEW_CONTACTS=1;
	private static final int VIEW_PREFERENCE=2;
	private static final int VIEW_ABOUT=3;
	private static final int VIEW_ALARM=4;
	private static final int VIEW_SYNC=5;
	
	private ImageButton reportsButton;
	private ImageButton contactsButton;
	private ImageButton preferencesButton;
	private ImageButton alarmsButton;
	private ImageButton aboutButton;
	private ImageButton syncButton;
	private ImageButton onlineButton;
	
	private SharedPreferences settings;
  private SharedPreferences.Editor editor;
	
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    startService(new Intent(this,SyncService.class), null);
    setContentView(R.layout.home);
    
    reportsButton = (ImageButton) findViewById(R.id.reports);
    reportsButton.setOnClickListener(this);
    
    contactsButton = (ImageButton) findViewById(R.id.contacts);
    contactsButton.setOnClickListener(this);
    
    preferencesButton = (ImageButton) findViewById(R.id.preferences);
    preferencesButton.setOnClickListener(this);
    
    aboutButton = (ImageButton) findViewById(R.id.about);
    aboutButton.setOnClickListener(this);
    
    alarmsButton = (ImageButton) findViewById(R.id.alarms);
    alarmsButton.setOnClickListener(this);
    
    syncButton = (ImageButton) findViewById(R.id.sync);
    syncButton.setOnClickListener(this);
    
    onlineButton = (ImageButton) findViewById(R.id.online);
    onlineButton.setOnClickListener(this);
    
    
    settings = getSharedPreferences(Preference.PREFS, 0);
    editor = settings.edit();
    if(settings.getBoolean("initialized",false) == false){
    	//initialization code
    	editor.putBoolean("initialized",true);
    	editor.commit();
    	TestData testData = new TestData(this);
    }
    
    
    BootupIntentReceiver.bootup(this);
    startService(new Intent(this,SyncService.class), null);
    startService(new Intent(this,ContactAlarmService.class), null);
  }
  
  public void onClick(View v) {
	  Intent i;
    if (v == reportsButton){
      i = new Intent(this, MyStats.class);
      startSubActivity(i, VIEW_REPORTS);
    }else if (v == contactsButton){
      i = new Intent(this, ContactsActivity.class);
    	startSubActivity(i, VIEW_CONTACTS);  
    }else if (v == preferencesButton){
      i = new Intent(this, Preference.class);
    	startSubActivity(i, VIEW_PREFERENCE);  
    }else if (v == aboutButton){
      i = new Intent(this, About.class);
      startSubActivity(i, VIEW_ALARM);
    }else if (v == alarmsButton){
      i = new Intent(this, ManageAlarmActivity.class);
      startSubActivity(i, VIEW_ABOUT);  
    }else if (v == syncButton){
      i = new Intent(this, Login.class);
      startSubActivity(i, VIEW_SYNC);
    }else if (v == onlineButton){
     i = new Intent("android.intent.action.VIEW", Uri.parse(Login.URL+"/logs/list"));
     startSubActivity(i, VIEW_SYNC);
    }
  }

  
}