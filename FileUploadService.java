package com.sanbit.android.mystats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.json.JSONException;
import org.json.JSONStringer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.sanbit.android.mystats.ContactMethod.ContactMethodRow;
import com.sanbit.android.mystats.Person.PersonRow;
import com.sanbit.android.mystats.Phone.PhoneRow;
import com.sanbit.android.mystats.Record.Row;

public class FileUploadService extends Service implements Runnable {

  final Handler mHandler = new Handler();
	public File recordFile;
	public File contactFile;
	private Record record;
	private static String redirectLocation;
	private static final String LOG = "FileUploadService";
	

  @Override
  protected void onCreate() {
  	  record = new Record(this);
      // Start up the thread running the service.  Note that we create a
      // separate thread because the service normally runs in the process's
      // main thread, which we don't want to block.
      Thread thr = new Thread(null, this, "FileUploadService");
      thr.start();
  }
  
  @Override
  public IBinder onBind(Intent intent){
    return null;
  }
  
  //@Override
  public void run(){
    mHandler.post(mUpdate);
  }
  
  final Runnable mUpdate = new Runnable() {
    public void run() {
        
        try {
          record.collectNewStats();
          upload();
        } catch (Exception e) {
          	  	 Log.d(LOG,"BBBerror:"+e.toString());
          //we want to keep some kind of error log file
        }
        stopSelf();
    }
  };

  
	public void createReportFile() {		
		List<Row> rows = record.find(null);
		
		try {
			Log.e("file upload","create temp file");
	        // Create temp file.
	        recordFile = File.createTempFile("record.csv", ".tmp");
	    
	        // Write to temp file
	        BufferedWriter out = new BufferedWriter(new FileWriter(recordFile));
	        String line;
	         Log.d(LOG,"XXXXXWWWWWWWW:");
	        for (int i = 0; i < rows.size(); i++) {
	        	line = "";
	        	line += rows.get(i).profile + "|";
	        	line += rows.get(i).receiver + "|";
	        	line += rows.get(i).method + "|";
	        	line += rows.get(i).personId + "|";
	        	line += rows.get(i).duration + "|";
	        	line += rows.get(i).time + "\n";
	        	out.write(line);
	        	 Log.d(LOG,"RLINE:"+line.toString());
	        }
	        out.close();
	    } catch (IOException e) {
	      	  	 Log.d(LOG,"CCCerror:"+e.toString());
	    }

	}
	
public void checkCookie(){
  
}

public void upload(){
  Log.d(LOG,"I am running?");
  checkCookie();
     	 Log.d(LOG,"TRRRRdo upload");
  createReportFile();
   	 Log.d(LOG,"TYYYYdo upload");
 // createContactsFile();
  	 Log.d(LOG,"XXXXXdo upload");
  doUpload();
  removeTempFiles();
}
public void removeTempFiles(){
  
}
	
public void createContactsFile() {		
		
		try {
			Log.e("file upload","create contact yaml file");
			contactFile = File.createTempFile("contact.json", ".tmp");
	    
	        // Write to temp file
	        BufferedWriter out = new BufferedWriter(new FileWriter(contactFile));
	        
	        Person person = new Person(this);
			List<PersonRow> people = person.getAllPeople();
	        Log.d(LOG,"here");
			JSONStringer js = new JSONStringer();
			try {
				js.array();
				Long person_id;
				for (int i = 0; i < people.size(); i++) {
					person_id = people.get(i)._id;
					JSONStringer phoneJS = new JSONStringer(); //create phone json object
					JSONStringer contactJS = new JSONStringer(); //create contactmethod json object
					
					//prepare phone json object
					Phone phone = new Phone(this);
					List<PhoneRow> phones = phone.getPersonPhones(person_id);
					phoneJS.array();
					for(int j=0; j<phones.size(); j++){
						int type = Integer.parseInt(phones.get(j).type);
						phoneJS.object()
							.key("number").value(phones.get(j).number)
							.key("type").value(Helper.phoneType()[type])
							.key("lable").value(phones.get(j).lable)
							.key("id").value(phones.get(j)._id)
						.endObject();
					}
					   Log.d(LOG,"hereXXXX");
					phoneJS.endArray();
					
					//prepare contact method json object
					ContactMethod contactMethod = new ContactMethod(this);
					List<ContactMethodRow> contactMethods = contactMethod.getPersonContactMethods(person_id);
					contactJS.array();
					for(int j=0; j<contactMethods.size(); j++){
						int contactKind = Integer.parseInt(contactMethods.get(j).kind);
						int contactType = Integer.parseInt(contactMethods.get(j).type);
						String type = Helper.contactMethodType(contactKind)[contactType];
						contactJS.object()
							.key("data").value(contactMethods.get(j).data)
							.key("type").value(type)
							.key("lable").value(contactMethods.get(j).lable)
							.key("id").value(contactMethods.get(j)._id)
							.key("kind").value(Helper.contactMethodKind()[contactKind-1])
						.endObject();
					}
					contactJS.endArray();
					   Log.d(LOG,"hereYYYY");
					js.object()
	        			.key("name").value(people.get(i).name)
	        			.key("id").value(person_id)
	        			.key("company").value(people.get(i).company)
	        			.key("title").value(people.get(i).title)
	        			.key("notes").value(people.get(i).notes)
	        			.key("phones").value(phoneJS)
	        			.key("contact_methods").value(contactJS)
	        		.endObject();
				}
				js.endArray();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
					  	 Log.d(LOG,"DDDerror:"+e.toString());
				e.printStackTrace();
			}
			
			out.write(js.toString());
	        out.close();
	    } catch (IOException e) {
	    		  	 Log.d(LOG,"TTTTerror:"+e.toString());
	    }

	}
	
	public void doUpload(){
	  SharedPreferences settings = getSharedPreferences(Preference.PREFS, 0);
	  String cookieName = settings.getString("cookie_name", "");
    String cookieValue = settings.getString("cookie_value", "");
    String cookieDomain = settings.getString("cookie_domain", "");
    if(cookieName.equals("")) {
      Toast.makeText(this, "Unable to Sync, please login to Sanbit.com",Toast.LENGTH_SHORT).show();
      //TODO: fill in, we get here because there is no valid cookie,meaning we probably didnt login
    }else{
      Cookie userCookie = new Cookie(cookieDomain, cookieName, cookieValue, "/", -1, false);
      HttpState initialState = new HttpState();
      initialState.addCookie(userCookie);
		  HttpClient client = new HttpClient();
		  client.setState(initialState);
		  PostMethod filePost = new PostMethod(Login.URL+"/logs/sms"); 
		  FilePart recordFilePart = null;
		  FilePart contactFilePart = null;
		  try {
		  	recordFilePart = new FilePart(recordFile.getName(), recordFile);
		  	//contactFilePart = new FilePart(contactFile.getName(), contactFile);
		  } catch (FileNotFoundException e) {
		  	//what should we do?
		  	Log.d(LOG,"status:"+e.toString());
		  }
      
		  //Part[] parts = { new StringPart("version", "value"), filePart };
		  Part[] parts = { recordFilePart }; 
		  //Part[] parts = { recordFilePart, contactFilePart }; 
		  filePost.setRequestEntity( new MultipartRequestEntity(parts, filePost.getParams()) ); 
		  Integer status = null;
		  try {
		  	status = client.executeMethod(filePost);
		  	Header locationHeader = filePost.getResponseHeader("location");
        if (locationHeader != null) {
          redirectLocation = locationHeader.getValue();
          Log.d(LOG,"REDIRECT:"+redirectLocation);
          if(redirectLocation == "/login"){
            
          // we need to pop up some status to fix this
          }
        }
		  } catch (HttpException e) {
		  	 Log.d(LOG,"status:"+e.toString());
		  } catch (IOException e){
		  	 Log.d(LOG,"AAAerror:"+e.toString());
		  }
		  Login.saveCookie(this,client);

		  //if(status == 200){
		    Log.d(LOG,"status:"+status.toString());
		    Toast.makeText(this, "Data Synced to Sanbit.com !",Toast.LENGTH_SHORT).show();   
		  //}
		   
		  recordFile.delete();
		  contactFile.delete();
	  }
	}
}
