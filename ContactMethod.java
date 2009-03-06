package com.sanbit.android.mystats;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

public class ContactMethod {

	public static class ContactMethodRow extends Object {
        public Long _id;
        public String data;
        public String type; 
        public String lable;
        public String kind;
    }
    
    
    private Context context;
	
    public ContactMethod(Context c){
    	context = c;
    }
    
    public List<ContactMethodRow> getPersonContactMethods(Long person_id) {
		List<ContactMethodRow> contactMethods = new ArrayList<ContactMethodRow>();
		Cursor cur = context.getContentResolver().query(android.provider.Contacts.ContactMethods.CONTENT_URI, null, android.provider.Contacts.ContactMethods.PERSON_ID+"="+person_id, null,null);
		//activity.startManagingCursor(cur);
		
		if(cur.first()){
	    	do{	
	    		ContactMethodRow row = new ContactMethodRow();
	    		row.data = cur.getString(cur.getColumnIndex(android.provider.Contacts.ContactMethodsColumns.DATA));
	    		row.kind = cur.getString(cur.getColumnIndex(android.provider.Contacts.ContactMethodsColumns.KIND));
	    		row.lable = cur.getString(cur.getColumnIndex(android.provider.Contacts.ContactMethodsColumns.LABEL));
	    		row.type = cur.getString(cur.getColumnIndex(android.provider.Contacts.ContactMethodsColumns.TYPE));
	    		contactMethods.add(row);
	    	}while(cur.next());
		}
		
		return contactMethods;
	}
    
    
}
