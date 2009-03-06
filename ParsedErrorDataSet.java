package com.sanbit.android.mystats;

public class ParsedErrorDataSet {
     private String extractedString = null;
     private int extractedInt = 0;

     public String getExtractedString() {
          return extractedString;
     }
     public void setExtractedString(String extractedString) {
       if(extractedString != null){
         if(this.extractedString == null){
           this.extractedString = extractedString;
         }else{
           this.extractedString += "\n"+extractedString;       
         }
       }
     }
     
     public String toString(){
       return this.extractedString;
     }
}