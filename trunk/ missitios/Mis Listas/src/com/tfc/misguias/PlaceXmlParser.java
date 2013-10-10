package com.tfc.misguias;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PlaceXmlParser {
	 // create new Place object to hold data
	public ArrayList<Place> parse(StringReader reader) {
	ArrayList<Place> place = new ArrayList<Place>();
	
    try {
    	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser x = factory.newPullParser();
		
		x.setInput(reader);
				
		int eventType = x.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	
        	if ( eventType == XmlPullParser.START_TAG ) {
        		if (x.getName().equals("marker")) {
        			Place nPlace = new Place();
        			//Name
        			String name = x.getAttributeValue(null, "name");
        			String nName = new String(name.getBytes("ISO-8859-1"));
        			nPlace.setName(nName);
        			//Description
        			String description = x.getAttributeValue(null, "description");
        			String nDescription = new String(description.getBytes("ISO-8859-1"));
        			nPlace.setDescription(nDescription);
        			//Address
        			String address = x.getAttributeValue(null, "address");
        			String nAddress = new String(address.getBytes("ISO-8859-1"));
        			nPlace.setAddress(nAddress);
        			//image
        			String image = x.getAttributeValue(null, "image");
        			String nImage ="no_disponible.jpg";
        			if (!(image.equals(""))){
        				nImage = new String(image.getBytes("ISO-8859-1"));
        			}
        			nPlace.setImage(nImage);
        			//Comment
        			//String comment = x.getAttributeValue(null, "comment");
        			//String nComment = new String(comment.getBytes("ISO-8859-1"));
        			//nPlace.setComment(nComment);
        			//Id
        			String id = x.getAttributeValue(null, "_id");
        			nPlace.setId(Long.parseLong(id));
        			//Latitude
        			String latitude =x.getAttributeValue(null, "lat");
        			nPlace.setLatitude(Double.parseDouble(latitude));
        			//Longitude	        			 
        			String longitude =x.getAttributeValue(null, "lng");
        			nPlace.setLongitude(Double.parseDouble(longitude)); 
        			//Puntuation
        			String puntuation =x.getAttributeValue(null, "puntuation");
        			nPlace.setPuntuation(Float.parseFloat(puntuation));
        			//Type
        			String type_id =x.getAttributeValue(null, "type");
        			nPlace.setType_id(Long.parseLong(type_id));
        			//Price
        			String price =x.getAttributeValue(null, "price");
        			nPlace.setPrice(price);
        			System.out.println(name);
        			place.add(nPlace);
        			
        			}
        		
        		
        	}
        	
        	if ( eventType == XmlPullParser.END_TAG ) {
        		
        	}
        	
        	eventType = x.next();
        }				
    } catch (XmlPullParserException e) {
        place = null;
    	
	} catch (IOException e) {
		System.out.println(e);
	}

    // return Place object
    return place;
}
}
