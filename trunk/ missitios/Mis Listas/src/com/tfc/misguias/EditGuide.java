package com.tfc.misguias;



import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class EditGuide extends ListActivity {

	private static final int ACTIVITY_SHOW = 0;
	
	private String places_url = "http://192.168.1.12/misguias/places_xml.php?guide_id=";
	
	private long searchIdCategory = -1;
	private long searchIdGroup = -1;
	private long searchDate = -1;
	private long orderList = -1;
	 
	private static final long WEB_LIST  = 2;
	
	private RowPlacesAdapter places;
	private RowPlacesWebAdapter webPlaces;
	private long selectId=-1;
	private long ownList =-1;
	private int selectPosition=-1;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setTitle(this.getString(R.string.edit_list));
		try {
        	DataFramework.getInstance().open(this, "com.tfc.misguias");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Recuperamos clase singleton
		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
		selectId = sgDatosLista.getIdGuide();
		ownList = sgDatosLista.getOwnList();	
		
        setContentView(R.layout.list_edit);
        
        if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("selectId")) selectId = savedInstanceState.getLong("selectId");
			if (savedInstanceState.containsKey("selectId")) ownList = savedInstanceState.getLong("ownList");
			if (savedInstanceState.containsKey("selectPostion")) selectPosition = savedInstanceState.getInt("selectPostion");
			
        } else {
        	Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				selectId = (extras.containsKey("selectId")) ? extras.getLong("selectId") : -1;
				selectPosition = (extras.containsKey("selectPosition")) ? extras.getInt("selectPosition") : -1;
				ownList = (extras.containsKey("ownList")) ? extras.getInt("ownList") : -1;
				
			} else {
				if (selectId==-1){//instanciado singleton habrá que eliminar esto
					searchIdCategory = -1;
					searchIdGroup = -1;
					searchDate = -1;
					selectId = -1;
					selectPosition = -1;
					orderList = -1;
				}
			}
			if (ownList!=WEB_LIST){
				final List <Entity> guide  = DataFramework.getInstance().getEntityList("tbl_places");
			}else{
				//Guide from web site
				
			}
        }
	}
	
    @Override
    protected void onResume() {
        super.onResume();		
		if (ownList!=WEB_LIST){
			fillData();
        }else{
        	fillWebData();
        }
		
    }
	
	/**
     * Rellena la lista con los elementos de la base de datos
     * 
     */
    
    private void fillData() {
    	try {//tiene que ser un RowPlacesAdapter
    		//Recuperamos clase singleton
    		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
    		selectId = sgDatosLista.getIdGuide();
    		ownList = sgDatosLista.getOwnList();
    		
    		
    		places = new RowPlacesAdapter(this, DataFramework.getInstance().getEntityList("tbl_places","guide_id=" + selectId));
    		
	    	places.setSelectId(selectId);
	        setListAdapter(places);
	        if (selectId>=0) {
	        	this.getListView().setSelection(places.getPositionById(selectId));
	        }
	        
	        //TextView total = (TextView)findViewById(R.id.total_routes);
	        this.setTitle(this.getString(R.string.guide_elements) + " (" + places.getCount() + " " +  this.getString(R.string.of) + " " + DataFramework.getInstance().getEntityList("tbl_places","guide_id=" + selectId).size() + ")");
	        	        
    	} catch (Exception e) {
    		System.out.println("ERROR: "+e.getMessage());
    	}
    	        
    }
    
    /**
     * Se ejecuta cuando se pulsa sobre un elemento de la guía
     * 
     * @param l ListView
     * @param v View
     * @param position Posicion
     * @param id Identificador
     * 
     */
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        places.clearSelectId();
        
        selectId = ((Entity)places.getItem(position)).getId();
              
        places.setSelectId(selectId);
        places.setViewSelectId(v);
        
        TextView n = (TextView) v.findViewById(R.id.name);
		n.setTextColor(Color.rgb(0xea, 0xea,0x9c));
        
        showPlace(selectId);
    }
    
    
    /**
     * Se ejecuta la nueva Actividad para mostrar el lugar
     */
    
    private void showPlace(Long idSelected) {
    	if (idSelected>=0) {
    		Intent i = new Intent(this, InfoLocation.class);
    		i.putExtra(DataFramework.KEY_ID, idSelected);
    		i.putExtra("ownList", 1);
    		i.putExtra("idSelected", idSelected);
    		this.startActivityForResult(i, ACTIVITY_SHOW);
    	}
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
    
	
	private void fillWebData() {
    	ArrayList<Place> miLista = new ArrayList<Place>();  ;
    	
        try {
        	String guide_id=String.valueOf(this.selectId);
        	String url = places_url+guide_id;
        	HttpGet request = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(request);
			String xml = EntityUtils.toString(httpResponse.getEntity());
            // Url del archivo XML
        
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser x = factory.newPullParser();
			
			x.setInput( new StringReader ( xml ) );
					
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
	        			miLista.add(nPlace);
	        			System.out.println(x.getAttributeValue(null, "name"));
	        			}
	        		
	        		
	        	}
	        	
	        	if ( eventType == XmlPullParser.END_TAG ) {
	        		
	        	}
	        	
	        	eventType = x.next();
	        }				
			
		} catch (Exception e) {
			System.out.println(e);
		}
        webPlaces = new RowPlacesWebAdapter(this, miLista);
        setListAdapter(webPlaces);
        //return miLista;
    }
	
	
	
	
}
