package com.tfc.misguias;



import java.util.List;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

public class EditGuide extends ListActivity {

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
				searchIdCategory = -1;
				searchIdGroup = -1;
				searchDate = -1;
				selectId = -1;
				selectPosition = -1;
				orderList = -1;
			}
			final List <Entity> lists  = DataFramework.getInstance().getEntityList("tbl_places");
        }
	}
	
    @Override
    protected void onResume() {
        super.onResume();		
		fillData();
		
    }
	
	/**
     * Rellena la lista con las rutas de la base de datos
     * 
     */
    
    private void fillData() {
    	try {//tiene que ser un RowPlacesAdapter
    		//List <Entity> places  = DataFramework.getInstance().getEntityList("tbl_places","list_id=" + selectId);
	    	places = new RowPlacesAdapter(this, DataFramework.getInstance().getEntityList("tbl_places","list_id=" + selectId));
	    	places.setSelectId(selectId);
	        setListAdapter(places);
	        if (selectId>=0) {
	        	this.getListView().setSelection(places.getPositionById(selectId));
	        }
	        
	        //TextView total = (TextView)findViewById(R.id.total_routes);
	        this.setTitle(this.getString(R.string.list_elements) + " (" + places.getCount() + " " +  this.getString(R.string.of) + " " + DataFramework.getInstance().getEntityList("tbl_places","list_id=" + selectId).size() + ")");
	        	        
    	} catch (Exception e) {
    		System.out.println("ERROR: "+e.getMessage());
    	}
    	        
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
    
}
