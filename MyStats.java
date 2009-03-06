package com.sanbit.android.mystats;


import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class MyStats extends ListActivity {
	
    private static final int ACTIVITY_VIEW=0;
    public static final int CALL_FREQUENCY = 0;
    public static final int MOST_TALK_TIME = 1;
    public static final int NOT_IN_ADDRESS_BOOK = 2;
    public static final int NO_COMMUNICATION = 3;
    private static final int CHECK_COOKIE=6;
    public static final int NO_RECENT_COMMUNICATION = 4;
    public static final int HOME_VIEW=7;

    private static final int HOME_ID = Menu.FIRST;

    private static final String LOG = "Mystats";
    private List<String> items;

	  
    @Override
    public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      items = new ArrayList<String>();
      
      setContentView(R.layout.main);
      items.add("Most Frequently contacted Contacts"); //0
      items.add("Contacts spent the most talk time with"); //1
      items.add("Numbers not in my Contacts List");   //2
      items.add("Contacts never communicated with"); //3
      items.add("Recent Communications with contacts"); //4
      
      ArrayAdapter<String> reports = new ArrayAdapter<String>(this, R.layout.reports_row, items);
      setListAdapter(reports);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      menu.add(0, HOME_ID, "Home");
      return true;
    }
    
    
    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        super.onMenuItemSelected(featureId, item);
        Intent i = null;
        switch(item.getId()) {
        case HOME_ID:
        	i = new Intent(this, HomeActivity.class);
            startSubActivity(i, HOME_VIEW);
          break;    
        }
        
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReportActivity.class);
        i.putExtra("title", items.get(position)); 
        i.putExtra("report", position);
        startSubActivity(i, ACTIVITY_VIEW);
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
      super.onActivityResult(requestCode, resultCode, data, extras);
      
      switch(requestCode) {
      case 0:
          //String title = extras.getString(KEY_TITLE);
          break;
      }  
    }
    
}