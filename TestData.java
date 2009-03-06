package com.sanbit.android.mystats;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.sanbit.android.mystats.ContactAlarm.ContactAlarmRow;
import com.sanbit.android.mystats.ContactAlarm;

public class TestData{
  private Context c;
  private static final String LOG = "TestData";
  private Person person;
  private Record record;
  private ContactAlarm contactAlarm;
  public TestData(Context context){
    c = context;
    person = new Person(c);
    record = new Record(c);
    contactAlarm = new ContactAlarm(c);
    deleteAll();
    Integer id;
    String phone;
    phone = "12345";
    id = ic("Jason","Sanbit",phone,null);
    if(id != null){
      record.createRow("",phone,"ic",1206509760000L,666,id,"Home");
      record.createRow("",phone,"ic",1206709760000L,66634,id,"Home");
      record.createRow("",phone,"ic",1206599760000L,6665,id,"Home");
      record.createRow("",phone,"ic",1206237721000L,66612,id,"Home");
      record.createRow("",phone,"ic",1206235721000L,66127,id,"Home");
    }
    phone = "90210";
    id=ic("John","Sanbit",phone,null);
    if(id != null){
      record.createRow("",phone,"ic",1206504760000L,616,id,"Home");
      record.createRow("",phone,"ic",1207509760000L,66322,id,"Home");
      record.createRow("",phone,"ic",1206309760000L,36437,id,"Home");
      record.createRow("",phone,"ic",1206237721000L,65326,id,"Home");
      record.createRow("",phone,"ic",1206237421000L,6634,id,"Home");
      record.createRow("",phone,"ic",1206217721000L,3627,id,"Home");
      record.createRow("",phone,"ic",1207237721000L,6566,id,"Home");
      record.createRow("",phone,"ic",1206235821000L,66312,id,"Home");
      record.createRow("",phone,"ic",1206227721000L,3627,id,"Home");
    }
    phone = "8675309";
    id=ic("Serge","google",phone,android.provider.Contacts.Phones.MOBILE_TYPE);
    if(id != null){
      record.createRow("",phone,"ic",1205237721000L,66,id,"Home");
      record.createRow("",phone,"ic",1206217721000L,612,id,"Home");
      record.createRow("",phone,"ic",1206237721000L,667,id,"Home");
    }
    phone = "18005551212";
    id=ic("Mike","techcrunch",phone,android.provider.Contacts.Phones.MOBILE_TYPE);
    if(id != null){
      record.createRow("",phone,"ic",1206235721000L,66,id,"Home");
      record.createRow("",phone,"oc",1206237721000L,6662,id,"Home");
      record.createRow("",phone,"ic",1207237721000L,627,id,"Home");
    }
    
    phone = "6178575212";
    id=ic("Andy","NU",phone,android.provider.Contacts.Phones.MOBILE_TYPE);
    if(id != null){
      record.createRow("",phone,"ic",1205237721000L,1166,id,"Home");
      record.createRow("",phone,"ic",1206237721000L,6662,id,"Home");
      record.createRow("",phone,"oc",1206237721000L,627,id,"Home");
      ContactAlarmRow car =new ContactAlarmRow();
      car.person_id = id+0L;
      car.active=0;
      car.interval = 14;
      contactAlarm.save(car);
    }
    
    phone = "8578575212";
    id=ic("Micheal","Angstrom",phone,android.provider.Contacts.Phones.MOBILE_TYPE);
    if(id != null){

      record.createRow("",phone,"ic",1204237721000L,9662,id,"Home");
      record.createRow("",phone,"ic",1206235721000L,127,id,"Home");
      ContactAlarmRow car =new ContactAlarmRow();
      car.person_id = id+0L;
      car.active=1;
      car.interval = 30;
      contactAlarm.save(car);
    }
    
    record.createRow("","911","oc",1203237721000L,1227,0,"");
    record.createRow("","911","ic",1205237721000L,1427,0,"");
    record.createRow("","1237632","ic",1206237721000L,1297,0,"");
    record.createRow("","54122","oc",1207217721000L,1274,0,"");
    
    id = ic("Cousin Bobby","no job!","917123127",null);
    id = ic("Uncle Fester","Cemetery","54315645",null);
    
  } 
  
  public void insertRecords(Integer id){

    
  }
  
  public void insertAlarms(){
    
  }
  
  public void deleteAll(){
    record.execQuery("delete from records;");
    record.execQuery("delete from contact_alarms;");
  }
  
  public Integer ic(String name,String company,String number,Integer type){
    ContentValues v = new ContentValues();
    v.put(android.provider.Contacts.People.NAME, name);
    v.put(android.provider.Contacts.People.COMPANY, company);
    Uri p = c.getContentResolver().insert(android.provider.Contacts.People.CONTENT_URI, v);
    if(p != null){
      ContentValues n  = new ContentValues();
      List<String> pathList = p.getPathSegments(); 
      String pathLeaf = pathList.get(pathList.size() -1 );
      n.put(android.provider.Contacts.Phones.PERSON_ID,pathLeaf);
      n.put(android.provider.Contacts.Phones.NUMBER,number);
      if(type != null){
        n.put(android.provider.Contacts.Phones.TYPE,type);
      }
      Uri phoneUpdate = c.getContentResolver().insert(android.provider.Contacts.Phones.CONTENT_URI,n);
      Log.d(LOG,"pathleaf:"+pathLeaf);
      return Integer.parseInt(pathLeaf);
    }else{
      return null;
    }
  }
  
}