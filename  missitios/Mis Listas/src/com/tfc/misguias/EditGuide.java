package com.tfc.misguias;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditGuide extends ListActivity {

	private static final int ACTIVITY_SHOW = 0;
	
	//private String places_url = "http://192.168.1.13/misguias/places_xml.php?guide_id=";
	//static final String URL = "http://192.168.1.13/misguias/places_xml.php?guide_id=1";
	private long searchIdCategory = -1;
	private long searchIdGroup = -1;
	private long searchDate = -1;
	private long orderList = -1;
	
	ListView mListView;
	
	private static final long WEB_LIST  = 2;
	private static final long OWN_LIST  = 1;

	private static final int DIALOG_KEY = 0;
	
	private RowPlacesAdapter places;
	private RowPlacesWebAdapter webPlaces;
	private long selectId=-1;
	private long ownList =-1;
	private int selectPosition=-1;

	private ProgressDialog mProgressDialog;

	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_KEY:                                                               // 1
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Retrieving recipes...");                       // 3
            mProgressDialog.setCancelable(false);                                      // 4
            return mProgressDialog;
        }
        return null;
    }
	
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
		mListView = (ListView) findViewById(R.layout.list_edit );
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
		        // URL to the XML data
		        String strUrl = this.getString(R.string.ip_home_guide)+selectId;
		 
		        // Creating a new non-ui thread task to download xml data
		        DownloadTask downloadTask = new DownloadTask();
		 
		        // Starting the download process
		        downloadTask.execute(strUrl);
		 
				
			}
			
			
        }
	}
	
    @Override
    protected void onResume() {
        super.onResume();		
		if (ownList!=WEB_LIST){
			fillData();
        }else{
        	//fillWebData();
        	//GetRSSDataTask task = new GetRSSDataTask();
        	//task.execute();
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
        
        if (ownList!=WEB_LIST){
        	places.clearSelectId();       
            selectId = ((Entity)places.getItem(position)).getId();
                  
            places.setSelectId(selectId);
            places.setViewSelectId(v);
            
            TextView n = (TextView) v.findViewById(R.id.name);
    		n.setTextColor(Color.rgb(0xea, 0xea,0x9c));
            
            showPlace(selectId,1);
        }else{
        	webPlaces.clearSelectId();
        	//Recuperamos clase singleton y se asignamos el sitio seleccionado
    		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
    		
        	selectId=webPlaces.getItemId(position);
        	sgDatosLista.setIdPlaceSelected(selectId);
        	webPlaces.setSelectId(selectId);
        	webPlaces.setViewSelectId(v);
            
            TextView n = (TextView) v.findViewById(R.id.name);
    		n.setTextColor(Color.rgb(0xea, 0xea,0x9c));
            
            showPlace(selectId,2);
        	//places es un ArrayList<Place>
        }
        
    }
    
    
    /**
     * Se ejecuta la nueva Actividad para mostrar el lugar
     */
    
    private void showPlace(Long idSelected,int ownList) {
    	if (idSelected>=0) {
    		Intent i = new Intent(this, InfoLocation.class);
    		i.putExtra(DataFramework.KEY_ID, idSelected);
    		i.putExtra("ownList", ownList);
    		i.putExtra("idSelected", idSelected);
    		this.startActivityForResult(i, ACTIVITY_SHOW);
    	}
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
    
	
	private ArrayList<Place> fillWebData() {
    	ArrayList<Place> miLista = new ArrayList<Place>();  ;
    	
        try {
        	String guide_id=String.valueOf(this.selectId);
        	String url = this.getString(R.string.ip_home_guide)+guide_id;
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
        //webPlaces = new RowPlacesWebAdapter(this, miLista);
        //setListAdapter(webPlaces);
        return miLista;
    }


	 private class GetRSSDataTask extends AsyncTask<String, Void, ArrayList<Place> > {
	       
		 
	        @Override
	        protected void onPreExecute() {
	            mProgressDialog.show();                                                          // 1
	        }
	        
		 
			@Override
	        protected ArrayList<Place> doInBackground(String... urls) {
	        	ArrayList<Place> myList = new ArrayList<Place>();
	            return myList;
	        }
	         
	        @Override
	        protected void onPostExecute(ArrayList<Place> result) {

                mProgressDialog.dismiss();  

	        }
	    }  
	
	 /** A method to download xml data from url */
	    private String downloadUrl(String strUrl) throws IOException{
	        String data = "";
	        InputStream iStream = null;
	        try{
	            URL url = new URL(strUrl);
	 
	            // Creating an http connection to communicate with url
	            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	 
	            // Connecting to url
	            urlConnection.connect();
	 
	            // Reading data from url
	            iStream = urlConnection.getInputStream();
	 
	            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
	 
	            StringBuffer sb  = new StringBuffer();
	 
	            String line = "";
	            while( ( line = br.readLine())  != null){
	                sb.append(line);
	            }
	 
	            data = sb.toString();
	 
	            br.close();
	 
	        }catch(Exception e){
	            Log.d("Exception while downloading url", e.toString());
	        }finally{
	            iStream.close();
	        }
	        return data;
	    }
	    
	    /** AsyncTask to download xml data */
	    private class DownloadTask extends AsyncTask<String, Integer, String>{
	        String data = null;
	        @Override
	        protected String doInBackground(String... url) {
	            try{
	                data = downloadUrl(url[0]);
	            }catch(Exception e){
	                Log.d("Background Task",e.toString());
	            }
	            return data;
	        }
	 
	        @Override
	        protected void onPostExecute(String result) {
	 
	            // The parsing of xml data is done in a non-ui thread
	            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
	 
	            // Start parsing xml data
	            listViewLoaderTask.execute(result);
	        }
	    }
	    
	    /** AsyncTask to parse xml data and load ListView */
	    private class ListViewLoaderTask extends AsyncTask<String, Void, RowPlacesWebAdapter>{
	 
	        StringReader reader;
	        private final ProgressDialog dialog=new ProgressDialog(EditGuide.this);
	        // Doing the parsing of xml data in a non-ui thread

			@Override
			protected void onPreExecute()
			{
			dialog.setMessage("Cargando Sitios ...");
			dialog.show();
			dialog.setCancelable(false);
			}
				        
	        
	        
	        @Override
	        protected RowPlacesWebAdapter doInBackground(String... strXml) {
	            try{
	                reader = new StringReader(strXml[0]);
	            }catch(Exception e){
	                Log.d("XML Exception1",e.toString());
	            }
	 
	            // Instantiating xml parser class
	            PlaceXmlParser placeXmlParser = new PlaceXmlParser();
	 
	            // A list object to store the parsed places 
	            ArrayList<Place> places = null;
	 
	            try{
	                // Getting the parsed data as a List construct
	                places = placeXmlParser.parse(reader);
	                
	            }catch(Exception e){
	                Log.d("Exception",e.toString());
	            }
	 
	
	 
	            // Instantiating an adapter to store each items
	            // R.layout.listview_layout defines the layout of each item
	            RowPlacesWebAdapter adapter = new RowPlacesWebAdapter(getBaseContext(), places);
	 
	            return adapter;
	        }
	 
	        /** Invoked by the Android when "doInBackground" is executed */
	        @Override
	        protected void onPostExecute(RowPlacesWebAdapter adapter) {
	 
	            // Setting adapter for the listview
	            //mListView.setAdapter(adapter);
	            setListAdapter(adapter);
	            //Asignamos la lista a la variable
	            webPlaces=adapter;
	            if(dialog.isShowing() == true)
	            {
	            dialog.dismiss();
	            }
	            
	            for(int i=0;i<adapter.getCount();i++){
	               /* HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
	                String imgUrl = (String) hm.get("flag_path");
	                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();
	 
	                HashMap<String, Object> hmDownload = new HashMap<String, Object>();
	                hm.put("flag_path",imgUrl);
	                hm.put("position", i);
	 
	                // Starting ImageLoaderTask to download and populate image in the listview
	                imageLoaderTask.execute(hm);*/
	            }
	        }
	    }

}	

