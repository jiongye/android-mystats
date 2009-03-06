package com.sanbit.android.mystats;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.sanbit.android.mystats.Person.PersonRow;

//Activity for us to display a list of all the available contacts
public class ContactsActivity extends ListActivity {
  private static final String LOG = "ContactsActivity";
  private static final int HOME_VIEW=1;
  private static final int FAVORITE_MENU_ID = Menu.FIRST + 1;
  private static final int HOME = Menu.FIRST + 2;
  private ArrayList<PersonRow> people;
  private ArrayAdapter peopleAdapter;
  private Button sortOder;
	
  protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.contacts_list);
		
		sortOder = (Button) findViewById(R.id.sort_order);
		sortOder.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(ContactsActivity.this)
                        .setTitle(R.string.sort_order)
                        .setItems(R.array.sort_contact_order, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.sort_contact_order);
                                
                                switch(which) {
                                case 0:
                                	Collections.sort(people,Person.PersonRow.FrequencyComparator);
                                	break;
                                case 2:
                                	Collections.sort(people,Person.PersonRow.TimeComparator);
                                	break;
                                case 3:
                                	Collections.sort(people, Person.PersonRow.LastCallComparator);
                                	break;
                            	case 1:
                            		Collections.sort(people);
                            		break;
                                }	
                                sortOder.setText("Sorted by " + items[which]);
                            	setListAdapter(peopleAdapter);
                            }
                        })
                        .show();
            }
        });

		
		
		String sql = "select person_id,count(*),sum(duration),receiver,max(time) from records group by receiver ORDER BY SUM(duration)";
		Record record = new Record(this);
		Cursor c = record.query(sql);
		startManagingCursor(c);
		int idColumn = 0;
		int frequencyColumn = 1;
		int timeColumn = 2;
		int receiverColumn = 3;
		int lastCallTimestampColumn = 4;

		String[] columns = new String[] { 
			android.provider.BaseColumns._ID,
			android.provider.Contacts.PeopleColumns.NAME,
			android.provider.Contacts.PeopleColumns.PHOTO
		};
    Cursor contactCursor = getContentResolver().query(android.provider.Contacts.People.CONTENT_URI, columns, null, null, null);
    int peopleNameColumn = contactCursor.getColumnIndex(android.provider.Contacts.PeopleColumns.NAME);
    int peopleIdColumn = contactCursor.getColumnIndex(android.provider.Contacts.People._ID);
    startManagingCursor(contactCursor);
    people = new ArrayList<PersonRow>();
    Phone phone = new Phone(this);
    
    //TODO: refactor or optimize this 
    if(contactCursor.first()){
			  do{
				  c.first();
				  boolean has_matching_record = false;
				  Integer talk_time = null;
				  Integer talk_frequency = null;
				  do{
					  if(c.getLong(idColumn) == contactCursor.getLong(peopleIdColumn)){ //we found a receiver with a corresponding contact
					    Person.PersonRow row = null;
					    for (PersonRow person : people){
					      if(contactCursor.getLong(idColumn) == person._id){
					        row = person;
					        row.talk_frequency += c.getInt(frequencyColumn);
					        row.talk_time += c.getInt(timeColumn);
					        if(c.getLong(lastCallTimestampColumn) > row.last_call_timestamp){
  					        row.last_call_timestamp = c.getLong(lastCallTimestampColumn);
					        }
					        break;
					      }
					    }
					    if(row == null){
					      row = new Person.PersonRow();
					      row.talk_time = c.getInt(timeColumn);
					      row.talk_frequency = c.getInt(frequencyColumn);
  						  row.name = c.getString(peopleNameColumn);
  						  row.last_call_timestamp = c.getLong(lastCallTimestampColumn);
  						  row.receiver = c.getString(receiverColumn);
					      row.name = contactCursor.getString(peopleNameColumn);
					      row._id = contactCursor.getLong(idColumn);
					      people.add(row);
					    }
    				  has_matching_record = true;
						  break;  
					  }

				  }while(c.next());
				  
				  if(has_matching_record == false){ //no matching record so just create an empty one with numbers
				    Person.PersonRow row = new Person.PersonRow();
				    row.name = contactCursor.getString(peopleNameColumn);
				    row._id = contactCursor.getLong(idColumn);
				    people.add(row); 
				  }

			  }while(contactCursor.next());

		}
    Collections.sort(people);
    peopleAdapter = new ArrayAdapter<PersonRow>(this, R.layout.contacts_activity,R.id.contact_row, people);
    setListAdapter(peopleAdapter);
    
	}
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
      super.onListItemClick(l, v, position, id);
      Intent i = new Intent(this, UserReport.class);
      i.putExtra("person_id", people.get(position)._id);
      i.putExtra("person_name", people.get(position).name);
      i.putExtra("receiver", people.get(position).receiver);
      startSubActivity(i, 0);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      //menu.add(0, FAVORITE_MENU_ID, "favorite");
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
  
  
  
}