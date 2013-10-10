package com.tfc.misguias;

import com.android.dataframework.DataFramework;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class GuideTab extends TabActivity {
	/** Called when the activity is first created. */
	 private int tabSelected;
	 private int idSelected;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_tab);
 
        TabHost tabHost = getTabHost();
 
        // Tab for List
        TabSpec guideList = tabHost.newTabSpec("Lugares");
        // setting Title and Icon for the Tab
        guideList.setIndicator("Lugares", getResources().getDrawable(R.drawable.category_1));
        Intent placesIntent = new Intent(this, EditGuide.class);
        guideList.setContent(placesIntent);
 
        // Tab for Map
        TabSpec guideMap = tabHost.newTabSpec("Mapa");
        guideMap.setIndicator("Mapa", getResources().getDrawable(R.drawable.category_22));
        
        Intent mapIntent = new Intent(this, GuideMap.class);
        guideMap.setContent(mapIntent);
        
        //Selecting tab
        if (savedInstanceState != null) {
			//Deberia gestionarlo?
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				tabSelected = (extras.containsKey("tabSelected")) ? extras.getInt("tabSelected") : -1;
				idSelected = (extras.containsKey("selectedPlace")) ? extras.getInt("selectedPlace") : -1;
				if (idSelected!=-1){
					
					mapIntent.putExtra("selectedPlace", idSelected);
				}
			}
		}
       
        // Adding all TabSpec to TabHost
        tabHost.addTab(guideList); // Adding list tab
        tabHost.addTab(guideMap); // Adding map tab
        if (tabSelected!=-1){
        	tabHost.setCurrentTab(tabSelected);
        }
         
        getTabHost().setOnTabChangedListener(new OnTabChangeListener() {

        	@Override
        	public void onTabChanged(String tabId) {

        	int i = getTabHost().getCurrentTab();
        	if (i == 0) {
        		//Limpiar id
        		tabSelected=0;
        		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
        		sgDatosLista.setIdSelected(-1); 
        	}else if (i ==1){
        		//Limpiar id
        		tabSelected=1;
        		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
        		sgDatosLista.setIdSelected(-1); 
        		
        	}

        	  }
        	});


    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if (tabSelected==1){
    	SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
		sgDatosLista.setIdSelected(-1);
    	}
	}

}

	
