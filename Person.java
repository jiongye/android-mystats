package com.sanbit.android.mystats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

public class Person {
    public static class PersonRow extends Object implements Comparable{
        public Long _id;
        public String name;
        public String time; //this is wrong!!
        public String photo;
        public String company;
        public String notes;
        public String title;
        
        //the following columns don't really make sense here, the Person class was originally a representation of a row of Data from android.provider.Contacts.People
        //but now we are adding in other data to pass between activities, later on figure out how to maybe refactor this
        public String receiver;
        public Integer measure;
        public Integer talk_time;
        public Integer talk_frequency;
        public Long last_call_timestamp;
        
        public String toString(){
        	return name;
        }
        
        public int compareTo(Object anotherPersonRow) throws ClassCastException {
          if (!(anotherPersonRow instanceof PersonRow))
            throw new ClassCastException("A PersonRow object expected.");
          String anotherPersonRowName = ((PersonRow) anotherPersonRow).name;  
          return this.name.compareTo(anotherPersonRowName);    
          }
        
        public static Comparator FrequencyComparator = new Comparator() {
          public int compare(Object personRow, Object anotherPersonRow) {
            Integer personRowFrequency = ((PersonRow)personRow).talk_frequency;
            Integer anotherPersonRowFrequency = ((PersonRow)anotherPersonRow).talk_frequency;
            if(personRowFrequency == null){
              personRowFrequency = 0;
            }
            if(anotherPersonRowFrequency == null){
              anotherPersonRowFrequency = 0;
            }
            return anotherPersonRowFrequency.compareTo(personRowFrequency);
           }
        };
        
        public static Comparator TimeComparator = new Comparator() {
          public int compare(Object personRow, Object anotherPersonRow) {
            Integer personRowTime = ((PersonRow)personRow).talk_time;
            Integer anotherPersonRowTime = ((PersonRow)anotherPersonRow).talk_time;
            if(personRowTime == null){
              personRowTime = 0;
            }
            if(anotherPersonRowTime == null){
              anotherPersonRowTime = 0;
            }
            return anotherPersonRowTime.compareTo(personRowTime);
           }
        };
        
        public static Comparator MeasureComparator = new Comparator() {
          public int compare(Object personRow, Object anotherPersonRow) {
            Integer measure = ((PersonRow)personRow).measure;
            Integer anotherMeasure = ((PersonRow)anotherPersonRow).measure;
            if(measure == null){
              measure = 0;
            }
            if(anotherMeasure == null){
              anotherMeasure = 0;
            }
            return anotherMeasure.compareTo(measure);
           }
        };
        
        public static Comparator LastCallComparator = new Comparator() {
            public int compare(Object personRow, Object anotherPersonRow) {
              Long personRowLastCall = ((PersonRow)personRow).last_call_timestamp;
              Long anotherPersonRowLastCall = ((PersonRow)anotherPersonRow).last_call_timestamp;
              if(personRowLastCall == null){
                personRowLastCall = 0L;
              }
              if(anotherPersonRowLastCall == null){
                anotherPersonRowLastCall = 0L;
              }
              return anotherPersonRowLastCall.compareTo(personRowLastCall);
             }
        };
        
        
    }
    
    private Context context;
	  private String[] columns = new String[] { 
			android.provider.BaseColumns._ID,
			android.provider.Contacts.PeopleColumns.NAME,
			android.provider.Contacts.PeopleColumns.PHOTO,
			android.provider.Contacts.PeopleColumns.LAST_TIME_CONTACTED
		};
		
    public Person(Context c){
    	context = c;
    }
	
	public List<PersonRow> find(String idsSql){
			Cursor c = context.getContentResolver().query(android.provider.Contacts.People.CONTENT_URI, columns, idsSql, null, null);
			//context.startManagingCursor(c);
			int idColumn = c.getColumnIndex(android.provider.Contacts.People._ID);
			int nameColumn = c.getColumnIndex(android.provider.Contacts.PeopleColumns.NAME);
			int photoColumn = c.getColumnIndex(android.provider.Contacts.PeopleColumns.PHOTO);
			ArrayList<PersonRow> ret = new ArrayList<PersonRow>();
			if(c.first()){
				  do{
					  PersonRow row = new PersonRow();
					  row.name = c.getString(nameColumn);
					  row.photo = c.getString(photoColumn);
					  row._id = c.getLong(idColumn);
					  ret.add(row);
				  }while(c.next());
				  
			}
			return ret;
	}
	
	public PersonRow findById(Long id){
		Cursor c = context.getContentResolver().query(android.provider.Contacts.People.CONTENT_URI, columns, "people._id="+id.toString(), null, null);
		//context.startManagingCursor(c);
		int idColumn = c.getColumnIndex(android.provider.Contacts.People._ID);
		int nameColumn = c.getColumnIndex(android.provider.Contacts.PeopleColumns.NAME);
		int photoColumn = c.getColumnIndex(android.provider.Contacts.PeopleColumns.PHOTO);
		PersonRow row = null;
		Record record = new Record(context);
		record.getLastCallTime(id, "");
		
		if(c.first()){
			row = new PersonRow();
			row.name = c.getString(nameColumn);
			row.last_call_timestamp = record.getLastCallTime(id, "");
			row.photo = c.getString(photoColumn);
			row._id = c.getLong(idColumn);
		}
		return row;
	}
	
	public List<PersonRow> getAllPeople(){
		List<PersonRow> people = new ArrayList<PersonRow>();
		
		Cursor cur = context.getContentResolver().query(android.provider.Contacts.People.CONTENT_URI, null, null,null, android.provider.Contacts.PeopleColumns.NAME + " ASC");
		//activity.startManagingCursor(cur);
		Log.d(LOG,"ASDS");
		if(cur.first()){
	    	do{
	    		PersonRow row = new PersonRow();
	    		row.company = cur.getString(cur.getColumnIndex(android.provider.Contacts.PeopleColumns.COMPANY));
	    		row.name = cur.getString(cur.getColumnIndex(android.provider.Contacts.PeopleColumns.NAME));
	    		row.notes = cur.getString(cur.getColumnIndex(android.provider.Contacts.PeopleColumns.NOTES));
	    		row.title = cur.getString(cur.getColumnIndex(android.provider.Contacts.PeopleColumns.TITLE));
	    		row._id = cur.getLong(cur.getColumnIndex(android.provider.Contacts.People._ID));
	    		people.add(row);
	    	}while(cur.next());
		}
		
		return people;
	}

}
