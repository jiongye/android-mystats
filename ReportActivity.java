package com.sanbit.android.mystats;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sanbit.android.mystats.Person.PersonRow;

public class ReportActivity extends ListActivity {
	private TextView titleText;
	private TextView dateText;
	private Record record;
	private Integer reportId;
	private ArrayAdapter people;
	private List<PersonRow> people_rows;
	private String reportName;
	private static final String LOG = "ReportActivity";
	private static final int HOME=Menu.FIRST + 1;
	private static final int HOME_VIEW=0;
	private Button timePeriod;
	private String timeSql;
	private Calendar today;
	
	
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		
		record = new Record(this);
		
		setContentView(R.layout.report);
		titleText = (TextView) findViewById(R.id.title);

            String title = getIntent().getStringExtra("title");
           
            if (title != null) {
                titleText.append(title);
            }
            reportId = getIntent().getIntExtra("report",0); //0 is not the correct default

            today = Calendar.getInstance();
            today.set(Calendar.HOUR,0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND,0);
            today.set(Calendar.MILLISECOND,0);
            
            timePeriod = (Button) findViewById(R.id.report_interval);
            timePeriod.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(ReportActivity.this)
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
                                    displayReport();
                                }
                            })
                            .show();
                }
            });    
        
            displayReport();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, HOME, "Home");
        return true;
    }
    
    
    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        super.onMenuItemSelected(featureId, item);
 
        switch(item.getId()) {
        case HOME:
        	Intent i = new Intent(this, HomeActivity.class);
        	startSubActivity(i, HOME_VIEW);
        	break;
        }
        
        return true;
    }
	
	private void displayReport(){
		switch(reportId) {
        case MyStats.CALL_FREQUENCY:
        	callFrequency(null);
        	break;
        case MyStats.NO_COMMUNICATION:
        	noCommunication();
        	break;
        case MyStats.MOST_TALK_TIME:
        	mostTalkTime();
        	break;
        case MyStats.NOT_IN_ADDRESS_BOOK:
        	notInAddressBook();
        	break;
        case MyStats.NO_RECENT_COMMUNICATION:
        	noRecentCommunication();
        	break;
        }
		
		people = new ArrayAdapter<PersonRow>(this,R.layout.reports_row,people_rows){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				PersonRow person =getItem(position);
				TextView resultView = null;
				if (null == convertView ||!(convertView instanceof TextView)){
					resultView = new TextView(super.getContext());
				}
				
				
				if(person.last_call_timestamp == null){
				  resultView.setText(""+(position+1) + ". " + person.name);
				}else {
					String dateString = DateUtils.dateString(person.last_call_timestamp).toString();
					if (reportId == MyStats.MOST_TALK_TIME) {
						resultView.setText(""+(position+1) + ". " + person.name + " " + Helper.timeToString(person.measure) + "\n    " + dateString + Helper.timeAgo(person.last_call_timestamp));
					}else{
						
						resultView.setText(""+(position+1) + ". " + person.name + " (" + person.measure + " times) \n    " + dateString + Helper.timeAgo(person.last_call_timestamp));
					}
				}
				resultView.setPadding(1, 10, 1, 10);
				resultView.setTextSize(20);
				
				return resultView;
			}
		};
		setListAdapter(people);
	}
	
	private void noCommunication(){
		
		reportName = "No Communication";
		String sql = "select distinct(person_id) from records";
		if (timeSql != null){
			sql += " where " + timeSql;
		}
		
		Cursor c = record.query(sql);
		String idsSql = null;
		c.first();
		if(c.count() > 0){
			idsSql = "";
			do{
				idsSql += "" +c.getLong(0) +",";
			} while(c.next());
			idsSql = "people._id NOT IN (" + idsSql.substring(0, idsSql.length()-1) + ")";
		}
		Person person = new Person(this);
		people_rows = person.find(idsSql);		
				
	}
	
	private void mostTalkTime(){
		String sql = "select person_id,sum(duration),receiver,max(time) from records ";
		if (timeSql != null){
			sql += " where " + timeSql;
		}
		sql += " group by person_id order by sum(duration) DESC";
		people_rows = matchDataToContacts(sql,true);
	}
	
	private void notInAddressBook(){
		Record record = new Record(this);
		String sql="";
		if (timeSql != null){
			sql = " and " + timeSql;
		}
		Cursor c = record.query("select receiver,max(time), count(*) from records where person_id IS NULL OR person_id = 0 " + sql + " GROUP BY receiver");
		people_rows = new ArrayList<PersonRow>();
		if(c.first()){
			do{
			  Person.PersonRow row = new Person.PersonRow();
			  row.name = c.getString(0);
			  row.receiver = c.getString(0);
			  row.last_call_timestamp = c.getLong(1);
			  row.measure = c.getInt(2);
			  people_rows.add(row);
			}while(c.next());
		}
	}
	
	//what people have I not spoken to recently (don't include people who I've never contacted)
	private void noRecentCommunication(){
		String sql = "select person_id,count(*),receiver,max(time) from records ";
		if (timeSql != null){
			sql += " where " + timeSql;
		}
		sql += " group by person_id order by MAX(time) DESC";
		people_rows = matchDataToContacts(sql,true);
	}
	
	//the option time can be set to "w" (week) or "m" (month)  the default is month if nothing is given
	private void callFrequency(String frequency){
		//get sql data according to dates
		String sql = "select person_id,count(*),receiver,max(time) from records ";
		if (timeSql != null){
			sql += " where " + timeSql;
		}
		sql += " group by receiver order by count(*) DESC";
		people_rows = matchDataToContacts(sql,true);
		Collections.sort(people_rows,Person.PersonRow.MeasureComparator);
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, UserReport.class);
        
        i.putExtra("title", titleText.getText()); 

        //TODO: cleanup,eventually we don't need to pass all this report info
        i.putExtra("report_id", reportId);
        i.putExtra("report_name",reportName);
        i.putExtra("last_call_timestamp",people_rows.get(position).last_call_timestamp);
        i.putExtra("receiver",people_rows.get(position).receiver);
        i.putExtra("report_position",position);
        i.putExtra("person_measure",people_rows.get(position).measure);
        i.putExtra("person_name", people_rows.get(position).name);
        i.putExtra("person_id", people_rows.get(position)._id);
        i.putExtra("person_photo", people_rows.get(position).photo);
        startSubActivity(i, 0);
        
    }
    
    //TODO: this is the wrong class for this to appear in, this should be refactored later, maybe moved to Person?
    //a simple metjod to match the sql from records into people records
    protected ArrayList<PersonRow> matchDataToContacts(String sql, Boolean inOrOut){
      	Record record = new Record(this);
    		Cursor c = record.query(sql);
    		String idsSql = null;
    		c.first();
    		if(c.count() > 0){
    			idsSql = "";
    			do{
    				idsSql += "" + c.getLong(0) + ",";
    			}while(c.next());
    			if(inOrOut == true){
    			  idsSql = "people._id IN (" + idsSql.substring(0, idsSql.length()-1) + ")";
    			}else{
    			  idsSql = "people._id NOT IN (" + idsSql.substring(0, idsSql.length()-1) + ")";
  			  }
    		}

    		String[] columns = new String[] { 
    				android.provider.BaseColumns._ID,
    				android.provider.Contacts.PeopleColumns.NAME,
    				android.provider.Contacts.PeopleColumns.PHOTO
    			};
    		Cursor contactCursor = getContentResolver().query(android.provider.Contacts.People.CONTENT_URI, columns, idsSql, null, null);
    		startManagingCursor(contactCursor);
    		int idColumn = contactCursor.getColumnIndex(android.provider.Contacts.People._ID);
    		int nameColumn = contactCursor.getColumnIndex(android.provider.Contacts.PeopleColumns.NAME);
    		int photoColumn = contactCursor.getColumnIndex(android.provider.Contacts.PeopleColumns.PHOTO);
    		ArrayList<PersonRow> people = new ArrayList<PersonRow>();
    		Phone phone = new Phone(this);
    		if(c.first()){
    			  do{
    				  
    				  contactCursor.first();
    				  Integer count = null;
    				  do{
    				    if(c.getLong(0) == 0L){ //0 is for people with no contact info TODO: this code is very fragile, dont rely on the id being 0, check if receiver matches
    				      Person.PersonRow row = new Person.PersonRow();
    				      count = c.getInt(1);  //careful of changing above code, this index will also change
    				      row.name = c.getString(2);
    				      row.measure = c.getInt(1);
    				      row.last_call_timestamp = c.getLong(3);
        				  row._id = 0L;
        				  row.receiver = c.getString(2);
        				  people.add(row);
    						  break;
    					  }else if(c.getLong(0) == contactCursor.getLong(idColumn)){ //we found a receiver with a corresponding contact
    					    Person.PersonRow row = null; //new Person.PersonRow();
    					    for (PersonRow person : people){
    					      if(contactCursor.getLong(idColumn) == person._id){
    					        row = person;
    					        row.measure += c.getInt(1);
    					        if(c.getInt(3) > row.last_call_timestamp){
    					          row.last_call_timestamp = c.getLong(3);
  					          	}
    					        break;
    					      }
    					    }
    					    if(row == null){
    					      row = new Person.PersonRow();
    					      row.name = contactCursor.getString(nameColumn);
          				  row._id = contactCursor.getLong(idColumn);
          				  row.last_call_timestamp = c.getLong(3);
          				  row.measure = c.getInt(1);
          				  row.photo = contactCursor.getString(photoColumn);
          				  people.add(row);
    					    }
        				  
    						  break;
    					  }

    				  }while(contactCursor.next());

    			  }while(c.next());

    		}
    		return people;
    }

}
