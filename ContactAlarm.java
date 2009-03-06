package com.sanbit.android.mystats;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sanbit.android.mystats.Person.PersonRow;

public class ContactAlarm {

	public static class ContactAlarmRow extends Object {
    public Long _id;
    public Long person_id;
    public Integer interval;
    public Integer active;  //  0<= false   0 > true
    public Long last_call_timestamp;

    public String toString(){
      return interval.toString();
    }

    
    //how many does since the alarm
    public String timeSince(){
      
      return null;
    }

    //TODO please make the method name better!
    public boolean isValidAlarm(){
      if(active == 1){
        Long time = System.currentTimeMillis();
        if((time - last_call_timestamp) > interval * 86400000){
          return true;
        }else{
          return false;
        }
      }else{
        return false;
      }
    }
    
  }
    
    
    private static final String DATABASE_NAME = "data.db";
    private static final String DATABASE_TABLE = "contact_alarms";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private Context context;
    private static final String LOG = "ContactAlarm";
    private String[] columns = new String[] {"_id","person_id","interval","active"};
	
    public ContactAlarm(Context ctx) {
      context = ctx;
      try {
        db = ctx.openDatabase(DATABASE_NAME, null);
      }catch (FileNotFoundException e) {
        try {
          db = ctx.createDatabase(DATABASE_NAME, DATABASE_VERSION,Context.MODE_PRIVATE,null);
          // TODO: this is not a good way to create the tables, please fix, there needs to be some wayto check if all the tables exist 
          //also properly create tables on package install
          db.execSQL(Record.DATABASE_CREATE);
          db.execSQL(Record.DATABASE_CONTACT_ALARM_CREATE);
        } catch (FileNotFoundException e1) {
          Log.e(LOG, e.toString());
          //we should never get here unless android fucks up permissions,which it has done before
          db = null;
        }
      }
    }
    
    public void save(ContactAlarmRow row){
      ContentValues values = new ContentValues();
      values.put("person_id",row.person_id);
      values.put("interval",row.interval);
      values.put("active", row.active);
      db.insert(DATABASE_TABLE, null, values);
    }
    
    public void destroy(ContactAlarmRow row){
      db.delete(DATABASE_TABLE, "person_id=" + row.person_id, null);
    }
    
    public void update(ContactAlarmRow row){
      ContentValues values = new ContentValues();
      values.put("person_id",row.person_id);
      values.put("interval",row.interval);
      values.put("active", row.active);
      db.update(DATABASE_TABLE, values, "_id=" + row._id, null);
    }
    
    public List<ContactAlarmRow> find(String where) {
		List<ContactAlarmRow> rows = new ArrayList<ContactAlarmRow>();
		Cursor cur = db.query(DATABASE_TABLE, columns, where, null, null, null, null);
		String[] columns = new String[] { android.provider.Contacts.PeopleColumns.LAST_TIME_CONTACTED};
		if(cur.first()){
	    do{
	    	ContactAlarmRow row = new ContactAlarmRow();
	    	row._id = cur.getLong(0);
	    	Log.d(LOG, "in contactlarm:"+cur.getLong(1));
	    	row.person_id = cur.getLong(1);
	    	row.interval = cur.getInt(2);
	    	row.active = cur.getInt(3);
	    	
	    	if(row.active == 1){
	    		Person person = new Person(context);
	    		PersonRow personRow = person.findById(row.person_id);
	    		if(personRow != null){
	    			row.last_call_timestamp =  personRow.last_call_timestamp;
	    		}
	    	}
	    	rows.add(row);
	    }while(cur.next());
		}
		return rows;
	}
    
    public Cursor getAlarms(){
    	Cursor cur = db.query(DATABASE_TABLE, columns, null, null, null, null, null);
    	return cur;
    }
    
}
