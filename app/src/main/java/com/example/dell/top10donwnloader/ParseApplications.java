package com.example.dell.top10donwnloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }
    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentRecord= null;
        boolean inEntry = false;
        boolean gotImage = false;
        String textValue = "";
        try{
            XmlPullParserFactory factory =  XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            StringReader stringReader = new StringReader(xmlData);
            xpp.setInput(stringReader);
            int eventType = xpp.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting Tag for "+ tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        else if(("image".equalsIgnoreCase(tagName))&& inEntry){
                            String imageResolution = xpp.getAttributeValue(null,"height");
                            if(imageResolution != null){
                                gotImage = "53".equalsIgnoreCase(imageResolution);
                            }
                    }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending Tag for  "+tagName);
                        if(inEntry){
                            if("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentRecord);
                                inEntry = false;
                            }
                            else if("name".equalsIgnoreCase(tagName)){
                                currentRecord.setName(textValue);
                            }
                            else if("artist".equalsIgnoreCase(tagName)){
                                currentRecord.setArtist(textValue);
                            }
                            else if("releaseDate".equalsIgnoreCase(tagName)){
                                currentRecord.setReleaseDate(textValue);
                            }
                            else if("summary".equalsIgnoreCase(tagName)){
                                currentRecord.setSummary(textValue);
                            }
                            else if("image".equalsIgnoreCase(tagName)) {
                                if (gotImage) {
                                    currentRecord.setImageUrl(textValue);
                                }
                            }
                        }
                        break;
                        default:
                }
                eventType = xpp.next();
            }
            for(FeedEntry app:applications){
                Log.d(TAG, "parse: *******************");
                Log.d(TAG,app.toString());
            }
        }
        catch (Exception e){
            status = false;

        }
        return status;
    }
}
