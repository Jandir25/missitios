package com.tfc.misguias;

import java.io.File;
import java.io.StringReader;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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


public class GuideCategories extends ListActivity {
	
	//Atributos de la lista de categorias

	//private String categories_url = "http://192.168.1.13/misguias/categories_xml.php";
	

	//Es una lista de categorias
	private RowGuideCategoryAdapter lists;
	private long selectId=-1;
	private int selectPostion=-1;
	
	private View viewFilter;
	private TextView filterTextCategory;
	private TextView filterTextGroup;
	private TextView filterTextDate;
	private TextView filterTextOrder;
	
	private ProgressDialog pd = null;
	
	private static final int ACTIVITY_SHOW = 0;
	
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
        this.setTitle(this.getString(R.string.guide_categories));
        setContentView(R.layout.guide_categories); 
        
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
    	ArrayList<GuideCategory> miLista = new ArrayList<GuideCategory>();  ;
    	
        try {
        	String url = this.getResources().getString(R.string.ip_home_category_xml);
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
	        			GuideCategory nCategory = new GuideCategory();
	        			nCategory.setDescription(x.getAttributeValue(null, "description"));
	        			String id = x.getAttributeValue(null, "id");
	        			nCategory.setId(Long.parseLong(id));
	        			nCategory.setName(x.getAttributeValue(null, "name"));
	        			nCategory.setIcon(x.getAttributeValue(null, "icon"));
	        			miLista.add(nCategory);
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
        lists = new RowGuideCategoryAdapter(this, miLista,false);
        setListAdapter(lists);
        //return miLista;
    }
    

    

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
       	//lists.clearSelectId();
        
        selectId = ((GuideCategory)lists.getItem(position)).getId();
        selectPostion = position;
        
        lists.setSelectId(selectId);
        lists.setViewSelectId(v);
        
        TextView t = (TextView) v.findViewById(R.id.name_cat);
		t.setTextColor(Color.rgb(0xea, 0xea,0x9c));
        
        //showDialog(DIALOG_ITEM);
		//Abre tabbed
		SingletonDatosLista sdLista = SingletonDatosLista.getInstance();
		sdLista.setIdGuide(selectId);
		int ownList= 1;//Hay que declararlo de otra manera
		sdLista.setOwnList(ownList);
		
		
		Intent i = new Intent(this, GuideListWeb.class);
		i.putExtra(DataFramework.KEY_ID, selectId);
		i.putExtra("ownList", 2);
		i.putExtra("category_id", selectId);
		i.putExtra("idSelected", selectId);
		this.startActivityForResult(i, ACTIVITY_SHOW);
    }
    
}
