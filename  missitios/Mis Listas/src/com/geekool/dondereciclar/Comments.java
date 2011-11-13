package com.geekool.dondereciclar;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Comments extends ListActivity {
	
	private static final int BACK_ID = Menu.FIRST;
	
	private int id = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.comments);
        
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("id")) id = savedInstanceState.getInt("id");
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				id = (extras.containsKey("id")) ? extras.getInt("id") : -1;
			} else {
				id = -1;
			}
		}
		
		RowCommentsAdapter comments = new RowCommentsAdapter(this, id); 
        setListAdapter(comments);
		
    }
    
    /**
     * Crea el menu
     * 
     * @param menu Menu
     * @return Boleano
     * 
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, BACK_ID, 0,  R.string.back)
			.setIcon(android.R.drawable.ic_menu_revert);
        return true;
    }

    /**
     * Se ejecuta al pulsar un boton del menu
     * 
     * @param featureId
     * @param item boton pulsado del menu
     * @return Si se ha pulsado una opcion
     * 
     */
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case BACK_ID:
        	setResult(RESULT_OK);
            finish();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
}
