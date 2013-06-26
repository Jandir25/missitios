package com.tfc.misguias;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

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
	private Vector<ParsedCategoryDataSet> categories;
	private String categories_url = "http://localhost/misguias/categories_xml.php";
	

	//Es una lista de categorias
	private RowListAdapter lists;
	private long selectId=-1;
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
        try {
        	DataFramework.getInstance().open(this, "com.tfc.misguias");
		} catch (Exception e) {
			e.printStackTrace();
		}
		final List <Entity> lists  = DataFramework.getInstance().getEntityList("tbl_guides");
        this.setTitle(this.getString(R.string.guide_categories));
        
        setContentView(R.layout.guide_categories);
        


    

    } 
    
    public void loadCategories() {
    	 
        try {
     
            // Url del archivo XML
            URL url = new URL(categories_url);
     
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
     
            XMLReader xr = sp.getXMLReader();
            // Utilizamos nuestro propio parseador (CategoryHandler)
            CategoryHandler myExampleHandler = new CategoryHandler();
            xr.setContentHandler(myExampleHandler);
     
            InputSource is = new InputSource(url.openStream());
            // Le indicamos la codificación para evitar errores
            is.setEncoding("UTF-8");
            xr.parse(is);
     
            // Asignamos al vector categories los datos parseados
            categories = myExampleHandler.getParsedData();
     
         } catch (Exception e) {
              // Ha ocurrido algún error
              //Log.e("Ideas4All", "Error", e);
         }        
     
    }
    
}
