package com.sanbit.android.mystats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Menu.Item;

public class About extends Activity {
	private static final int HOME_VIEW=1;
	private static final int HOME = Menu.FIRST;
	
	@Override
	  public void onCreate(Bundle icicle) {
	   super.onCreate(icicle);
	   setContentView(R.layout.about);
	   
	   
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
}
