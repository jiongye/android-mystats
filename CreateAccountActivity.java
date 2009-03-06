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
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class CreateAccountActivity extends Activity implements OnClickListener{
	
	private boolean success = false;
	private static EditText username;
	private static EditText password;
	private static EditText passwordConfirmation;
	private static EditText email;
	private static TextView failReasonView;
	private static String failReason;
	private static String COOKIE_NAME;
	private static String COOKIE_VALUE;
	private static String COOKIE_DOMAIN;
	private static final String LOG = "createAccountActivity";
	private static PostMethod method;
	private static HttpClient client;
	private static String redirectLocation;
  
	
	@Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    
    setContentView(R.layout.create_account);
		
		failReasonView = (TextView) findViewById(R.id.fail_reason);
		if(failReason != null){
		  failReasonView.setText(failReason);
		}
		
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		passwordConfirmation = (EditText) findViewById(R.id.password_confirm);
		Button createButton = (Button) findViewById(R.id.create_account);
		
		createButton.setOnClickListener(this);
			
	}
	
	
	public void onClick(View v) {

		  //try creating, parse error message

			client = new HttpClient();
			Cookie userCookie = new Cookie();
    
			//Create a new HttpState container
	    HttpState initialState = new HttpState();
	    initialState.addCookie(userCookie);
	    client.setState(initialState);
	        
      method = new PostMethod(Login.URL+"/users/create.xml");
      
      //method.setFollowRedirects(true); 
      method.addParameter("user[login]", username.getText().toString());
      method.addParameter("user[password]", password.getText().toString());
      method.addParameter("user[password_confirmation]", passwordConfirmation.getText().toString());
      method.addParameter("user[email]", email.getText().toString());
      
      //TODO: why doesnt setRequestHeader work?,we should use that instead of create.xml
      //method.setRequestHeader("Content-type","text/xml; charset=ISO-8859-1");
      
      int statusCode=0;
      try{
          statusCode = client.executeMethod(method);
          Log.d(LOG,"STATUSCODE:"+statusCode);
          Header locationHeader = method.getResponseHeader("location");
          if (locationHeader != null) {
            redirectLocation = locationHeader.getValue();
            Log.d(LOG,"REDIRECT:"+redirectLocation);
          }
          if(redirectLocation != null && statusCode != 200){
           //currently if we get redirected, that means we logged in, otherwise there are errors
           //success = Login.saveCookie(this,client);
           method.releaseConnection();  
          }else{
            String response = method.getResponseBodyAsString();
            Log.d(LOG,"RESPONSE:"+response);
            /* Get a SAXParser from the SAXPArserFactory. */
            try{
              SAXParserFactory spf = SAXParserFactory.newInstance();
              SAXParser sp = spf.newSAXParser();
              
              /* Get the XMLReader of the SAXParser we created. */
              XMLReader xr = sp.getXMLReader();
              /* Create a new ContentHandler and apply it to the XML-Reader*/
              ErrorHandler errorHandler = new ErrorHandler();
              xr.setContentHandler(errorHandler);

               xr.parse(new InputSource(method.getResponseBodyAsStream())); 
              /* Parsing has finished. */
              
              /* Our ExampleHandler now provides the parsed data to us. */
              ParsedErrorDataSet parsedErrorDataSet = errorHandler.getParsedData();
              Log.d(LOG,parsedErrorDataSet.toString());
              failReason = parsedErrorDataSet.toString();
            } catch (Exception e) { 
              Log.d(LOG,"FUCK");
              Log.d(LOG,e.toString());
             failReason = "invalid response from the server, please try again";
             
            }
          }
      }
      catch(IOException e) {
        failReason = "Lost connection, please try again";
      }
      
      
	  if(failReason != null){
	    //try to create an account again
		  failReasonView.setText(failReason);
	  }else{
	    //everything succeded
	    boolean success = Login.saveCookie(this,client);
	    Toast.makeText(this, "Account created successfully!",Toast.LENGTH_LONG ).show();
	    startService(new Intent(this,FileUploadService.class), null);
	    startSubActivity(new Intent(this, HomeActivity.class), 0);  
	  }

	  
	}
	

}
