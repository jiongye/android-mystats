package com.sanbit.android.mystats;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sanbit.android.mystats.ContactAlarm.ContactAlarmRow;
import com.sanbit.android.mystats.Person.PersonRow;

public class UserReport extends Activity {
	private static final int ACTIVITY_VIEW=0;
	private static final int HOME_VIEW=1;
	private TextView titleText1;
	private Long personId;
	private ContactAlarm contactAlarm;
	private String personName;
	private String receiver;
	private ListView callList;
	private static final String LOG = "UserReport";
	private static final int ALARM_MENU = Menu.FIRST + 1;
	private static final int NO_DAYS = Menu.FIRST + 2; //as in don't use it
	private static final int THREE_DAYS = Menu.FIRST + 3;
	private static final int SEVEN_DAYS = Menu.FIRST + 4;
	private static final int FOURTEEN_DAYS = Menu.FIRST + 5;
	private static final int THIRTY_DAYS  = Menu.FIRST + 6;
	private static final int CONTACT_LIST = Menu.FIRST + 7;
	private static final int HOME = Menu.FIRST + 8;
	private Button timePeriod;
	private String timeSql;
	private Calendar today;
	private TextView alarmStatus;
	
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		
		Bundle extras = getIntent().getExtras();
		setContentView(R.layout.user_report); 
		
		titleText1 = (TextView) findViewById(R.id.title1);
		ImageView image = (ImageView) findViewById(R.id.image);
		image.setImageResource(R.drawable.personal);
	
        if (extras != null && !extras.isEmpty()) {
    		
    		if (extras.containsKey("person_photo") && extras.getString("person_photo") != null){
    			String photoPath = extras.getString("person_photo");
    			Log.v("XXXXQ",photoPath);
    			//ContentURI cu = new ContentURI("content://images/media/"+photoPath);
    			//InputStream is = getContentResolver().openInputStream(cu);
    		}
            if (extras.containsKey("person_name")) {
            	personName = extras.getString("person_name");
                titleText1.append(personName);
            }
            
            if (extras.containsKey("person_id")){
            	personId = extras.getLong("person_id");
            }
            if (extras.containsKey("receiver")){
            	receiver = extras.getString("receiver");
            }
            if (extras.containsKey("notification_id")){
              NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
              nm.cancel(extras.getInt("notification_id"));
            }
        }
        
        
        today = Calendar.getInstance();
        today.set(Calendar.HOUR,0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        
        timePeriod = (Button) findViewById(R.id.report_interval);
        timePeriod.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(UserReport.this)
                        .setTitle(R.string.time_interval)
                        .setItems(R.array.report_interval, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.report_interval);
                                long start=0l;
                                long end=0l;
                                switch(which) {
                                case 0:
                                	break;
                                case 1:
                                	start = today.getTime().getTime() - (today.get(Calendar.DAY_OF_WEEK )-1) * 3600*24*1000;
                                	end = today.getTime().getTime() + 3600*24*1000;
                                	break;
                                case 2:
                                	end = today.getTime().getTime() - (today.get(Calendar.DAY_OF_WEEK )-1) * 3600*24*1000;
                                	start = end - 3600*24*7*1000;
                                	
                                	break;
                            	case 3:
                            		start = today.getTime().getTime() - (today.get(Calendar.DAY_OF_MONTH)-1) * 3600*24*1000;
                            		end = today.getTime().getTime() + 3600*24*1000;                                		
                            		break;
                            	case 4:
                            		end = today.getTime().getTime() - (today.get(Calendar.DAY_OF_MONTH)-1) * 3600*24*1000;
                            		start = end - 3600*24*1000*30;
                            		break;
                                }	
                                timePeriod.setText("Time Period: " + items[which]);
                            	
                                if(start != 0){
                                	timeSql=" time >= '" + start + "' and time < '" + end + "' ";
                                }else {
                                	timeSql = null;
                                }
                                populateCallHistory();
                            }
                        })
                        .show();
            }
        });    
                
		populateCallHistory();
      
	}
	
	@Override
  public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      menu.add(0, CONTACT_LIST, "Contacts");
      if(personId != null && personId != 0){
    	  SubMenu sub = menu.addSubMenu(1, ALARM_MENU, "Set Contact Alarm");
    	  sub.add(0, NO_DAYS, "Off");
    	  sub.add(0, THREE_DAYS, "3 days");
    	  sub.add(0, SEVEN_DAYS, "7 days");
    	  sub.add(0, FOURTEEN_DAYS, "14 days");
    	  sub.add(0, THIRTY_DAYS, "30 days");
      }
      menu.add(0, HOME, "Home");
      return true;
  }
  
  @Override
  public boolean onMenuItemSelected(int featureId, Item item) {
      super.onMenuItemSelected(featureId, item);
      int alarmDays = -1; //default is off
      Intent i = null;
      switch(item.getId()) {
      case NO_DAYS:
        alarmDays = 0;
        break;
      case THREE_DAYS:
        alarmDays = 3;
        break;    
      case SEVEN_DAYS:
        alarmDays = 7;
      	break;
      case FOURTEEN_DAYS:
        alarmDays = 14;
        break;
      case THIRTY_DAYS:
        alarmDays = 30;
        break;
      case CONTACT_LIST:
    	i = new Intent(this, ContactsActivity.class);
        startSubActivity(i, ACTIVITY_VIEW);  
    	break;
      case HOME:
      	i = new Intent(this, HomeActivity.class);
        startSubActivity(i, HOME_VIEW);  
      	break;
      }

      if (alarmDays >= 0){
        if(personId != null && personId != 0){
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
          }else if (alarmDays > 0){ //no alarm so create it and is not select the "off" option
            ContactAlarmRow contactAlarmRow = new ContactAlarmRow();
            contactAlarmRow.person_id = personId;
            contactAlarmRow.active = 1;
            contactAlarmRow.interval = alarmDays;
            contactAlarm.save(contactAlarmRow);
          }
          populateCallHistory();
        }else{
          //there is no person, what should we do, display an error message
          
        }
      }  
      return true;
  }
	
	
	
	void populateCallHistory(){ 
		alarmStatus = (TextView) findViewById(R.id.alarm_status);     		
		contactAlarm = new ContactAlarm(this);
		ImageView alert = (ImageView) findViewById(R.id.alert);
		alert.setVisibility(View.INVISIBLE);
		alarmStatus.setText("");
		if(personId != null && personId != 0){
			List<ContactAlarmRow> alarms = contactAlarm.find("person_id = "+personId);
			if(alarms.size() == 1){
	            ContactAlarmRow contactAlarmRow = alarms.get(0);
	            Person person = new Person(this);
	            PersonRow personRow = person.findById(personId);
	            Long now = Calendar.getInstance().getTime().getTime();
				Long diff = now - personRow.last_call_timestamp;
				Long alarmTime = diff - contactAlarmRow.interval * 86400000;
				if(alarmTime > 0 && contactAlarmRow.active > 0){
					alert.setVisibility(View.VISIBLE);
					alert.setImageResource(R.drawable.alert);
					alarmStatus.setText(Helper.timeStampToString(alarmTime) + " Over Due!\n(Interval: " + contactAlarmRow.interval + " days)" );
				}
	         }
		} 
        
		
		String[] columns = new String[] { "phone_type", "time", "count" }; 
		String query;
		//Log.i(LOG,receiver);
		if (personId != null && personId != 0){ // This means we got a record with a corresponding  contact
			query = "person_id = \"" +personId+"\"";
		}else{
			query = "receiver = \"" +receiver+"\"";
		}
		
		if (timeSql != null){
			query += " and " + timeSql;
		}
		
    Record record = new Record(this);
  	Cursor c = record.query("select max(_id) as _id, phone_type, max(time) as time, count(*) as count, sum(duration) as duration, receiver from records where " +  query +  " group by receiver, phone_type order by count(*) desc",null);
    startManagingCursor(c);
    
    c.first();
    int totalCount = 0;
    int totalTime = 0;
	if(c.count() > 0){
		do{
			totalCount += c.getInt(3);
			totalTime += c.getInt(4);
		} while(c.next());
	}
    
    callList = (ListView) findViewById(R.id.frequency_list);
    ListView talkList = (ListView) findViewById(R.id.talk_time_list);
    TextView totalCall = (TextView) findViewById(R.id.call_frequency);
	TextView talkTime = (TextView) findViewById(R.id.talk_time);
	totalCall.setText("Total Calls: " + totalCount + " Times");
	talkTime.setText("Total Talk Time: " + Helper.timeToString(totalTime));
	
    if(c.count() == 0) {
    	
    	Phone phone = new Phone(this);
    	c = phone.getPhonesCursor(personId);
    	TextView noCommunication = (TextView) findViewById(R.id.no_communication);
    	noCommunication.setText("No Communication with This Contact!");
    	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.call_history,c,new String[]{"type"},new int[]{R.id.call_number});
    	adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
    		public boolean setViewValue(View v, Cursor c, int col) {
    			TextView tv = (TextView) v;
    			String type;
    			int value = c.getInt(col);
    			
    			if (value == 6){
    				type = c.getString(c.getColumnIndex(android.provider.Contacts.PhonesColumns.LABEL));
    			}else{
    				type = Helper.phoneType()[value];
    			}
    			
    			tv.setText(type);
    			return true; // indicate that you set the text
    		}
    	});
    	callList.setAdapter(adapter);
    	talkList.setAdapter(adapter);
    }else{
    	
    	int[] phones = new int[] {R.id.call_number, R.id.call_date, R.id.call_value}; 
    	
    	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.call_history,c,columns,phones);
    	adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
    		public boolean setViewValue(View v, Cursor c, int col) {
    			TextView tv = (TextView) v;
    			String value = c.getString(col);
    		
    			if (col == 2) { 
    				tv.setVisibility(1);
    				tv.setText("Last call: " + DateUtils.dateString(c.getLong(col)).toString()); 
    			}else if (col == 3) {
    				tv.setText(" (" + value + " Times)"); 
    			}else { 
    				tv.setText(value); 
    			}
    			return true; // indicate that you set the text
    		}
    	});
    
    	callList.setAdapter(adapter);
    	
    	
    	String[] columns2 = new String[] { "phone_type", "duration" }; 
    	int[] phones2 = new int[] {R.id.call_number, R.id.call_value};
    
    	SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,R.layout.call_history,c,columns2,phones2);
    	adapter2.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
    		public boolean setViewValue(View v, Cursor c, int col) {
    			TextView tv = (TextView) v;
    			String value = c.getString(col);
    			if (col == 4) {
    				int seconds = c.getInt(col);
    				tv.setText(Helper.timeToString(seconds)); 
    			}else { 
    				tv.setText(value); 
    			}
    			return true; // indicate that you set the text
    		}
    	});
    	talkList.setAdapter(adapter2);
		}
	}
	
}
