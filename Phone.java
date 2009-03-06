package com.sanbit.android.mystats;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

public class Phone {
    public static class PhoneRow extends Object {
        public Long _id;
        public String number;
        public String type; 
        public String lable;
        
        public String toString(){
          return number.toString();
        }
    }
    
    
    private Context context;
	
    public Phone(Context c){
    	context = c;
    }
    
    //TODO: add cursor handling code since startManagingCursor can't be used here
    public List<PhoneRow> getPersonPhones(Long person_id) {
    	List<PhoneRow> phones = new ArrayList<PhoneRow>();
    	Cursor cur = getPhonesCursor(person_id);
		//activity.startManagingCursor(cur);
		
		if(cur.first()){
	    	do{	
	    		PhoneRow row = new PhoneRow();
	    		row.number = cur.getString(cur.getColumnIndex(android.provider.Contacts.PhonesColumns.NUMBER));
	    		row.type = cur.getString(cur.getColumnIndex(android.provider.Contacts.PhonesColumns.TYPE));
	    		row.lable = cur.getString(cur.getColumnIndex(android.provider.Contacts.PhonesColumns.LABEL));
	    		row._id = cur.getLong(cur.getColumnIndex(android.provider.Contacts.Phones._ID));
	    		phones.add(row);
	    	}while(cur.next());
		}
		return phones;
	}
    
    public Cursor getPhonesCursor(Long person_id) {
    	Cursor cur = context.getContentResolver().query(android.provider.Contacts.Phones.CONTENT_URI,null, android.provider.Contacts.Phones.PERSON_ID+"="+person_id, null,null);
    	return cur;
    }
	
}
