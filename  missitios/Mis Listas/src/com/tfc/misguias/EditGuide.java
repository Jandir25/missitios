package com.tfc.misguias;



import java.util.List;

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
	
	private long searchIdCategory = -1;
	private long searchIdGroup = -1;
	private long searchDate = -1;
	private long orderList = -1;
	 
	
	private RowPlacesAdapter places;
	private long selectId=-1;
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
			
		
        setContentView(R.layout.list_edit);
        
        if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("selectId")) selectId = savedInstanceState.getLong("selectId");
			if (savedInstanceState.containsKey("selectPostion")) selectPosition = savedInstanceState.getInt("selectPostion");
			
        } else {
        	Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				selectId = (extras.containsKey("selectId")) ? extras.getLong("selectId") : -1;
				selectPosition = (extras.containsKey("selectPosition")) ? extras.getInt("selectPosition") : -1;
				
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
			final List <Entity> guide  = DataFramework.getInstance().getEntityList("tbl_places");
        }
	}
	
    @Override
    protected void onResume() {
        super.onResume();		
		fillData();
		
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
    
}
