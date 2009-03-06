package com.sanbit.android.mystats;

import java.io.IOException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.Header;
import android.content.Intent;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity implements OnClickListener{
	
	private boolean success = false;
	private static EditText USERNAME;
	private static EditText PASSWORD;
	private static TextView login_reason;
	private static String COOKIE_NAME;
	private static String COOKIE_VALUE;
	private static String COOKIE_DOMAIN;
	protected static String URL = "http://sanbit.com";
	private static final String LOG = "Login";
	private static PostMethod method;
	private static HttpClient client;
	private static String redirectLocation;
  private Button createButton;
	private Button confirmButton;
	
	@Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
      
    SharedPreferences settings = getSharedPreferences(Preference.PREFS, 0);
    COOKIE_NAME = settings.getString("cookie_name", "");
    COOKIE_VALUE = settings.getString("cookie_value", "");
    COOKIE_DOMAIN = settings.getString("cookie_domain", "");
    checkCookie();
	}
	
	
	private void checkCookie() {
		if(COOKIE_NAME.equals("")) {
			populateLoginScreen("Please Login");
		}else{
			//Cookie Name= ABCD   Value=00000   Path=/  MaxAge=-1   Secure=False
			
			
			Cookie userCookie = new Cookie(COOKIE_DOMAIN, COOKIE_NAME, COOKIE_VALUE, "/", -1, false);
      Log.d(LOG,"COOKIE:"+userCookie.toString());
			//Create a new HttpState container
	        HttpState initialState = new HttpState();
	        initialState.addCookie(userCookie);
	        
	        //create new client
	        client = new HttpClient();
	        //set the HttpState for the client
	        client.setState(initialState);

	        method = new PostMethod(URL+"/account/logged_in");
	        //method.setFollowRedirects(true); 
	        int statusCode=0;
	        
	        try {
				statusCode = client.executeMethod(method);
				Log.d(LOG,"STATUSCODE:"+statusCode);
				Header locationHeader = method.getResponseHeader("location");
        if (locationHeader != null) {
          redirectLocation = locationHeader.getValue();
          Log.d(LOG,"REDIRECT:"+redirectLocation);
        }
			} catch (HttpException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
	        Log.d(LOG,"XXX");
	        success = Login.saveCookie(this,client);

	        method.releaseConnection();
	        if(statusCode == 200){
	          uploadFile();
	        }else{
	          populateLoginScreen("Please Login");
	        }
	        
		}
		
	}
	
	private void loginToServer() {

        //Instantiate an HttpClient
        client = new HttpClient();

        //Instantiate a POST HTTP method
        method = new PostMethod(URL+"/account/create");
        //method.setFollowRedirects(true); 
        method.addParameter("login", USERNAME.getText().toString());
        method.addParameter("password", PASSWORD.getText().toString());
        method.addParameter("remember_me", "1");

        try{
            int statusCode = client.executeMethod(method);
            Log.d(LOG,"STATUSCODE:"+statusCode);
            Header locationHeader = method.getResponseHeader("location");
            if (locationHeader != null) {
              redirectLocation = locationHeader.getValue();
              Log.d(LOG,"REDIRECT:"+redirectLocation);
            }
            
            success = Login.saveCookie(this,client);
            //release connection
            method.releaseConnection();
        }
        catch(IOException e) {
            
        }
    }

	
	public static boolean saveCookie(Context context,HttpClient client) {
		//Get cookies stored in the HttpState for this instance of HttpClient
    Cookie[] cookies = client.getState().getCookies();
    SharedPreferences settings = context.getSharedPreferences(Preference.PREFS, 0);
	  SharedPreferences.Editor editor = settings.edit();
	  boolean success = false;
    for (int i = 0; i < cookies.length; i++) {
      Log.d(LOG,"name:"+cookies[i].getName());
      Log.d(LOG,"value:"+cookies[i].getValue());
    	
    	if(cookies[i].getName().equals("auth_token") && cookies[i].getValue() != ""){ //TODO: find a cleaner way to see if we are logged in
    	  success = true;
    	  editor.putString("cookie_name", cookies[i].getName());
    	  editor.putString("cookie_value", cookies[i].getValue());
    	  editor.putString("cookie_domain", cookies[i].getDomain());
    	  // Don't forget to commit your edits!!!
    	  success = true;
    	  editor.commit();
    	}
    }
    return success;   
	}
	
	private void populateLoginScreen(String reason) {
		setContentView(R.layout.login);
		
		if(reason != null){
		  login_reason = (TextView) findViewById(R.id.login_reason);
		  login_reason.setText(reason);
		}
		USERNAME = (EditText) findViewById(R.id.username);
		PASSWORD = (EditText) findViewById(R.id.password);
		confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(this);
		createButton = (Button) findViewById(R.id.create_account);
		createButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
    if (v == createButton){
      Intent i = new Intent(this,CreateAccountActivity.class);
	    startActivity(i);
    }else if (v == confirmButton){
    	loginToServer();
			uploadFile();
    }
  }
	
	private void uploadFile() {
		if(success){
		  startService(new Intent(this,FileUploadService.class), null);
			setResult(RESULT_OK);
      finish();
    }else{
      populateLoginScreen("Unable to login, please try again");
    }
	}
	
}
