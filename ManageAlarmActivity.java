package com.sanbit.android.mystats;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sanbit.android.mystats.ContactAlarm.ContactAlarmRow;
import com.sanbit.android.mystats.Person.PersonRow;


public class ManageAlarmActivity extends ListActivity{
	
	private static final int CONTACT_LIST = Menu.FIRST ;
	private static final int HOME = Menu.FIRST + 1;
	
	private static final int ACTIVITY_VIEW=0;
	private static final int HOME_VIEW=1;
	private Long personId;
	private ContactAlarm contactAlarm;
	private Cursor c;
	private SimpleCursorAdapter simpleCursorAdapter;
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.manage_alarm_activity);
		
		simpleCursorAdapter = getAdapter();
		setListAdapter(simpleCursorAdapter);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      menu.add(0, CONTACT_LIST, "Contacts");
      menu.add(0, HOME, "Home");
      return true;
  }
  
  @Override
  public boolean onMenuItemSelected(int featureId, Item item) {
      super.onMenuItemSelected(featureId, item);
      Intent i = null;
      switch(item.getId()) {
      case CONTACT_LIST:
    	i = new Intent(this, ContactsActivity.class);
        startSubActivity(i, ACTIVITY_VIEW);  
    	break;
      case HOME:
      	i = new Intent(this, HomeActivity.class);
        startSubActivity(i, HOME_VIEW);  
      	break;
      }
      return true;
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, final int position, long id) {
	  super.onListItemClick(l, v, position, id);
	  new AlertDialog.Builder(ManageAlarmActivity.this)
      .setTitle(R.string.edit_alarm)
      .setItems(R.array.alarm_options, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
        	  int alarmDays = -1; 
        	  switch(which) {
              case 0:
                alarmDays = 0;
                break;
              case 1:
                alarmDays = 3;
                break;    
              case 2:
                alarmDays = 7;
              	break;
              case 3:
                alarmDays = 14;
                break;
              case 4:
                alarmDays = 30;
                break;
              }
        	  
        	  c.moveTo(position);
        	  personId = c.getLong(1);
        	  
        	  List<ContactAlarmRow> alarms = contactAlarm.find("person_id = "+personId);
        	  if(alarms.size() == 1){
        	  	  ContactAlarmRow contactAlarmRow = alarms.get(0);
        	  	  if(alarmDays == 0){
        	  		  contactAlarmRow.active = 0;
        	      }else{
        	    	  contactAlarmRow.active = 1;
        	    	  contactAlarmRow.interval = alarmDays;
        	      }
        	      contactAlarm.update(contactAlarmRow);
        	  }
        	  
        	  simpleCursorAdapter = getAdapter();
        	  setListAdapter(simpleCursorAdapter);
        	  
          }
      })
      .show();
      
  }

  public SimpleCursorAdapter getAdapter(){
	  contactAlarm = new ContactAlarm(this);
	  c = contactAlarm.getAlarms();
	  startManagingCursor(c);
		
	  String[] columns = new String[] { "_id", "person_id","interval","active"}; 
	   int[] alarms = new int[] {R.id.status ,R.id.name, R.id.interval, R.id.active};
	   final Person person = new Person(this);
	   
	   SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.alarm_row,c,columns,alarms);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			public boolean setViewValue(View v, Cursor c, int col) {
				TextView tv = (TextView) v;
				int value = c.getInt(col);
				
				if (col == 1 || col == 0) { 
					personId = c.getLong(col);
					PersonRow personRow = person.findById(personId);
					if (col == 1){
						tv.setText(personRow.toString() + "      ");
					}else{
						if (c.getInt(3) == 1){
							Long now = Calendar.getInstance().getTime().getTime();
							Long diff = now - personRow.last_call_timestamp;
							Long alarmTime = diff - c.getLong(2) * 86400000;
							if(alarmTime > 0){
								tv.setText(Helper.timeStampToString(alarmTime) + " Over Due!");
							}else{
								tv.setText(Helper.timeStampToString(alarmTime*-1) + " Left!");
							}
						}
					}
				}else if (col == 2) {
					tv.setText(",   Interval: " + value + " Days"); 
				}else if (col == 3){ 
					String active = "OFF";
					if (value == 1){
						active = "ON";
					}
					tv.setText("   Alarm: " + active); 
				}
				return true; // indicate that you set the text
			}
		});
	
		return adapter;
  }
  
}