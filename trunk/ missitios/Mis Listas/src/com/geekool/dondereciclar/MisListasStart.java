package com.geekool.dondereciclar;

import java.util.List;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
public class MisListasStart extends Activity{
	
	private static final int DIALOG_CREATELIST = 0;
	private static final int ACTIVITY_NEWLIST = 1;
	
    /**
     * Crea una nueva ventana de dialogo
     * 
     * @param id Identificador
     * @return Dialog dialogo
     */
	
    @Override
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_CREATELIST:
        	//Leemos el xml de los propios recursos de la bbdd
    		try {
    		    DataFramework.getInstance().open(this, "com.geekool.dondereciclar");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
        	//Aqui crearemos una nueva lista y la insertaremos en la bd
        	final List <Entity> categories  = DataFramework.getInstance().getEntityList("tbl_places");
        	
        	RowCategoryAdapter adapter = new RowCategoryAdapter(this, categories, true);
        	return new AlertDialog.Builder(this)
	        .setIcon(R.drawable.alert_dialog_icon)
	        .setTitle(R.string.select_category)
	        .setAdapter(adapter, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//createRoute(categories.get(whichButton).getId());
	            }
	        })
	        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        })
	       .create();
        
        
        
        }
        return null;
    }
	
	
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrance);
        
        
        ImageButton btnNew = (ImageButton) this.findViewById(R.id.newlist);
        
        btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_CREATELIST);			
			}
			
		});
		
		btnNew.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageButton img = (ImageButton) MisListasStart.this.findViewById(R.id.newlist);
				switch (event.getAction()) {
                	case MotionEvent.ACTION_DOWN:
                		img.setImageResource(R.drawable.nuevo);
                		break;
                	case MotionEvent.ACTION_UP:
                		img.setImageResource(R.drawable.nuevo_off);
                		break;
				}
				return false;
			}
			
		});
		
	   
		
    }
}
