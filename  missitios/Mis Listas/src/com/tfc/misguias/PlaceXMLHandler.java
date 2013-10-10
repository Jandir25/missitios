package com.tfc.misguias;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PlaceXMLHandler extends DefaultHandler {
    private ArrayList<Place> places;
    private String tempVal;
    // to maintain context
    private Place place;
 
    public PlaceXMLHandler() {
    	places = new ArrayList<Place>();
    }
 
    public ArrayList<Place> getPlaces() {
        return places;
    }
    

}
