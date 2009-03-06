package com.sanbit.android.mystats;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ErrorHandler extends DefaultHandler{

     // ===========================================================
     // Fields
     // ===========================================================
     
     private boolean in_outertag = false;
     private boolean in_innertag = false;
     private boolean in_mytag = false;
     
     private ParsedErrorDataSet myParsedErrorDataSet = new ParsedErrorDataSet();

     // ===========================================================
     // Getter & Setter
     // ===========================================================

     public ParsedErrorDataSet getParsedData() {
          return this.myParsedErrorDataSet;
     }


     /** Gets be called on opening tags like:
      * <tag>
      * Can provide attribute(s), when xml was like:
      * <tag attribute="attributeValue">*/
     @Override
     public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
          if (localName.equals("message")) {
            myParsedErrorDataSet.setExtractedString(atts.getValue("thenumber"));
          }
     }
     

     
     /** Gets be called on the following structure:
      * <tag>characters</tag> */
     @Override
    public void characters(char ch[], int start, int length) {

      myParsedErrorDataSet.setExtractedString(new String(ch, start, length));

    }
}