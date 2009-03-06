package com.sanbit.android.mystats;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.SharedPreferences;
import android.util.Log;

public class Record {
    class Row extends Object {
        public String profile;
        public Long _id;
        public String receiver;
        public Long personId;
        public String method;
        public Integer duration;
        public Long time;
        
        public String toString(){
        	return profile;
        }
        
    }

    protected static final String DATABASE_CREATE =
        "CREATE TABLE IF NOT EXISTS records (_id integer primary key autoincrement, "
            + "profile VARCHAR,receiver VARCHAR, method VARCHAR, blurb VARCHAR, duration INT,"
            + "person_id INT,time INT, stored BOOLEAN default 1, phone_type VARCHAR, "
            + "UNIQUE (receiver,method,time) ON CONFLICT IGNORE);"; //keep in mind data will be ignored!!
    protected static final String DATABASE_CONTACT_ALARM_CREATE =
            " CREATE TABLE IF NOT EXISTS contact_alarms (_id integer primary key autoincrement, person_id integer, "
            + "interval integer,active BOOLEAN default 1);";
    

    private static final String DATABASE_NAME = "data.db";
    private static final String DATABASE_TABLE = "records";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private Context context;
    private static final String LOG = "Record";
    
	private String[] columns = new String[] { "profile","_id","receiver","person_id","method","duration","time","stored" };

    public Record(Context ctx) {
        context = ctx;
        try {
            db = ctx.openDatabase(DATABASE_NAME, null);
        } catch (FileNotFoundException e) {
            try {
                db = ctx.createDatabase(DATABASE_NAME, DATABASE_VERSION,Context.MODE_PRIVATE,null);
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DATABASE_CONTACT_ALARM_CREATE);
            } catch (FileNotFoundException e1) {
            	//we should never get here unless android fucks up permissions,which it has done before
               db = null;
               
            }
        }
    }


	public void close() {
        db.close();
    }
    
    public SQLiteDatabase db(){ return db;} //convenience method, get rid of later
    
    public Cursor query(String query){
      return query(query,null);
    }
    
    public Cursor query(String query,String something){
      Log.d(LOG, query);
      return db.rawQuery(query,null);
      
    }
    public void execQuery(String sql){
      Log.d(LOG, sql);
      db.execSQL(sql);
    }
    
    public void createRow(String profile, String receiver,String method,Long time,Integer duration, Integer person_id, String phoneType) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("profile", profile);
        initialValues.put("receiver",receiver);
        initialValues.put("method",method);
        initialValues.put("time", time);
        //if (blurb != null) initialValues.put("blurb", blurb);
        if(duration != null) initialValues.put("duration", duration);
        if(person_id != null) initialValues.put("person_id", person_id);
        initialValues.put("duration", duration);
        initialValues.put("phone_type", phoneType);
        db.insert(DATABASE_TABLE, null, initialValues);
    }

    public void deleteRow(long rowId) {
        db.delete(DATABASE_TABLE, "rowid=" + rowId, null);
    }
    
    public List<Row> find(String where){
    	ArrayList<Row> ret = new ArrayList<Row>();
    	try {
            Cursor c =
                db.query(DATABASE_TABLE, columns, where, null, null, null, null);
            int numRows = c.count();
            c.first();
            for (int i = 0; i < numRows; ++i) {
                Row row = new Row();
                //TODO how can I reference the column index? this method can easily get bugs by changing around the order
                //{ "profile","_id","receiver","person_id","method","duration","time","stored" }
                row._id = c.getLong(1);
                row.profile = c.getString(0);
                row.receiver = c.getString(2);
                row.method = c.getString(4);
                row.duration = c.getInt(5);
                row.personId = c.getLong(3);
                row.time = c.getLong(6);
                ret.add(row);
                c.next();
            }
        } catch (SQLException e) {
            Log.e("booga", e.toString());
        }
    	return ret;
    }

    public List<Row> fetchAllRows() {
        ArrayList<Row> ret = new ArrayList<Row>();
        try {
            Cursor c =
                db.query(DATABASE_TABLE, columns, null, null, null, null, null);
            int numRows = c.count();
            c.first();
            for (int i = 0; i < numRows; ++i) {
                Row row = new Row();
               // row.rowId = c.getLong(0);
               // row.title = c.getString(1);
               // row.body = c.getString(2);
                ret.add(row);
                c.next();
            }
        } catch (SQLException e) {
            Log.e("booga", e.toString());
        }
        return ret;
    }

    public Row fetchRow(long rowId) {
        Row row = new Row();
        Cursor c =
            db.query(true, DATABASE_TABLE, new String[] {
                "rowid", "title", "body"}, "rowid=" + rowId, null, null,
                null, null);
        if (c.count() > 0) {
            c.first();
            //row.rowId = c.getLong(0);
            //row.title = c.getString(1);
            //row.body = c.getString(2);
            return row;
        } else {
            row._id = new Long(-1);
        }
        return row;
    }
    
    public void updateRow(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put("title", title);
        args.put("body", body);
        db.update(DATABASE_TABLE, args, "rowid=" + rowId, null);
    }
    
    public void updateStoredStatus(long rowId){
        ContentValues args = new ContentValues();
        args.put("stored", "t");
        db.update(DATABASE_TABLE, args, "rowid=" + rowId, null);
    }
    
    public Long getLastCallTime(Long personId, String receiver){
    	Long lastCall = -1l;
    	String where = "";
    	String groupBy = "";
    	if (personId != null && personId != 0){ // This means we got a record with a corresponding  contact
    		where = "person_id = \"" +personId+"\"";
    		groupBy = "person_id";
		}else{
			where = "receiver = \"" +receiver+"\"";
			groupBy = "receiver";
		}
    	
    	Cursor cur = query("select max(time) as time from records where " +  where +  " group by "+ groupBy, null);
    	if (cur.first()){
    		lastCall = cur.getLong(0);
    	}
    	return lastCall;
    }
    
    public void collectNewStats(){
      SharedPreferences settings = context.getSharedPreferences(Preference.PREFS, 0);
  	  SharedPreferences.Editor editor = settings.edit();
      //Long lastInsert = settings.getLong("lastInsert", 0L); //for the competition, we will reinsert every row to make sure we don't miss anything
      Long lastInsert = 0L;
      Log.d(LOG," running collectNewStats");
    	Cursor c = context.getContentResolver().query( 
                android.provider.CallLog.Calls.CONTENT_URI, 
                null, "date > " + lastInsert.toString() , null, 
                android.provider.CallLog.Calls.DATE + " DESC"); 
    	//context.startManagingCursor(c);
    	
        int numberColumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER); 
        int dateColumn = c.getColumnIndex(android.provider.CallLog.Calls.DATE);
        int durationColumn = c.getColumnIndex(android.provider.CallLog.Calls.DURATION);
        int personIdColumn = c.getColumnIndex(android.provider.CallLog.Calls.PERSON_ID);
        int phoneTypecolumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER_TYPE);
        // type can be: Incoming, Outgoing or Missed 
        int typeColumn = c.getColumnIndex(android.provider.CallLog.Calls.TYPE);
        String profile = android.os.SystemProperties.get("gsm.sim.line1.number");
        //TODO normalize the profile number in case different devices return different formats (ie + -)
    		  if(c.first()){
    			  do{
    				  String callerPhoneNumber = c.getString(numberColumn).trim(); 
              long callDate = c.getLong(dateColumn); 
              int callType = c.getInt(typeColumn); 
              int personId = c.getInt(personIdColumn);
              int duration = c.getInt(durationColumn);
              String phoneType = c.getString(phoneTypecolumn);
              String method = "uc"; //unknown call method
              switch(callType){ 
              case android.provider.CallLog.Calls.INCOMING_TYPE: 
                	method = "ic"; 
                   break; 
              case android.provider.CallLog.Calls.MISSED_TYPE: 
                method = "mc";
                   break; 
              case android.provider.CallLog.Calls.OUTGOING_TYPE: 
                   method = "oc";
                   break;              
              }
              Long callLong = new Long(callDate);  //TODO: faster to use a primitive,change this
              if(callLong > lastInsert){
                //TODO: also add some kind of check to only update lastInsert if the current callLong is valid data to insert into the db
                lastInsert = callLong;
              }
              createRow(profile,callerPhoneNumber,method,callDate,duration,personId, phoneType);
    			  }while(c.next());
    			  
    		  }
    		  
    		  editor.putLong("lastInsert", lastInsert);
    		  editor.commit();
      
    }

}


