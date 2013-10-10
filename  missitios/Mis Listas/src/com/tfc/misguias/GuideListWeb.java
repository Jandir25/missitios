package com.tfc.misguias;

import java.io.File;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

import com.tfc.misguias.R;


public class GuideListWeb extends ListActivity {
	
	//Atributos de la lista de categorias
	private static final int ACTIVITY_SHOW = 0;
	
	
	//private String categories_url = "http://192.168.1.13/misguias/categories_places_xml.php?category_id=";
	private Bitmap loadedImage;

	//Es una lista de categorias
	private RowListWebAdapter listAdapter;
	private Long category_id;
	private long selectId=-1;
	public long getSelectId() {
		return selectId;
	}


	public void setSelectId(long selectId) {
		this.selectId = selectId;
	}

	private int selectPostion=-1;
	
	private View viewFilter;
	private TextView filterTextCategory;
	private TextView filterTextGroup;
	private TextView filterTextDate;
	private TextView filterTextOrder;
	
	private ProgressDialog pd = null;
	
	private long searchIdCategory = -1;
	private long searchIdGroup = -1;
	private long searchDate = -1;
	private long orderList = -1;
	
	//ECS lo usare?
	//private ThreadExport threadExport = null;
	//private ThreadSimplify threadSimplify = null;
	

	

    /**
     * Primer metodo que se ejecuta al crear una Actividad
     * 
     * @param savedInstanceState
     */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(this.getString(R.string.guide_list)+" categoria");
        setContentView(R.layout.guide_list); 
 
        Bundle extras = getIntent().getExtras();  
        if (extras != null) {
			long category_id = (extras.containsKey("category_id")) ? extras.getLong("category_id") : -1;
			this.setCategory_id(category_id);
		}
        
    } 
    
    
    @Override
    protected void onResume() {
        super.onResume();		
        fillData();
		
    }
  
    /**
     * Rellena la lista con las categorias descargadas de la web
     * 
     */
    
    private void fillData() {
    	ArrayList<Guide> miLista = new ArrayList<Guide>();  ;
    	
        try {
        	String category_id2=String.valueOf(this.category_id);
        	String url = this.getString(R.string.ip_home_category)+category_id2;
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
	        		if (x.getName().equals("category")) {
	        			Guide guide = new Guide();
	        			guide.setDescription(x.getAttributeValue(null, "description"));
	        			String id = x.getAttributeValue(null, "id");
	        			guide.setId(Long.parseLong(id));
	        			String uno = x.getAttributeValue(null, "title");
	        			String n = new String(uno.getBytes("ISO-8859-1"));
	        			guide.setTitle(n);
	        			guide.setDate(x.getAttributeValue(null, "date"));
	        			guide.setCategory_id(category_id);
	        			String creator = x.getAttributeValue(null,"creator");
	        			guide.setCreator(Long.parseLong(creator));
	        			guide.setIcon(x.getAttributeValue(null, "image"));
	        			miLista.add(guide);
	        			System.out.println(x.getAttributeValue(null, "title"));
	        			}
	        		
	        		
	        	}
	        	
	        	if ( eventType == XmlPullParser.END_TAG ) {
	        		
	        	}
	        	
	        	eventType = x.next();
	        }				
			
		} catch (Exception e) {
			System.out.println(e);
		}
        listAdapter = new RowListWebAdapter(this, miLista);
        setListAdapter(listAdapter);
        //return miLista;
    }
    

    
    

	public Long getCategory_id() {
		return category_id;
	}


	public void setCategory_id(Long category_id2) {
		this.category_id = category_id2;
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        selectId = ((Guide)listAdapter.getItem(position)).getId();
        selectPostion = position;
               
        TextView t = (TextView) v.findViewById(R.id.title);
		t.setTextColor(Color.rgb(0xea, 0xea,0x9c));
        
        //showDialog(DIALOG_ITEM);
		//Abre tabbed
		SingletonDatosLista sdLista = SingletonDatosLista.getInstance();
		sdLista.setIdGuide(selectId);
		int ownList= 2;//Hay que declararlo de otra manera
		sdLista.setOwnList(ownList);
		
		
		Intent i = new Intent(this, GuideTab.class);
		i.putExtra(DataFramework.KEY_ID, selectId);
		i.putExtra("ownList", 2);
		i.putExtra("idSelected", selectId);
		this.startActivityForResult(i, ACTIVITY_SHOW);
    }


	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}
    
	
    
}
