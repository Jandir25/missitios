package com.tfc.misguias;

import java.util.List;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;
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
import android.content.Intent;
public class MisListasStart extends Activity{
	
	private static final int DIALOG_CREATELIST = 0;
	private static final int ACTIVITY_SHOWLIST = 1;
	private static final int DIALOG_OPENLIST = 2;
	private static final int DIALOG_SHOWLIST = 3;
	private static final int ACTIVITY_OPENLIST = 4;
	private static final int ACTIVITY_LISTLISTS = 5;
	
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
    		    DataFramework.getInstance().open(this, "com.tfc.misguias");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
        	//Aqui crearemos una nueva lista y la insertaremos en la bd
        	final List <Entity> categories  = DataFramework.getInstance().getEntityList("tbl_types");
        	
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
        
        case DIALOG_SHOWLIST:
        	//Leemos el xml de los propios recursos de la bbdd
    		try {
    		    DataFramework.getInstance().open(this, "com.tfc.misguias");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    		Intent i = new Intent(this, GuideList.class);
    		this.startActivityForResult(i, ACTIVITY_SHOWLIST);
        	
        	
        	
        	
        case DIALOG_OPENLIST:
        	//Leemos el xml de los propios recursos de la bbdd
    		try {
    		    DataFramework.getInstance().open(this, "com.tfc.misguias");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
        	//Aqui abriremos una lista 
             	
            Intent j = new Intent(this, GuideList.class);
            startActivityForResult(j, ACTIVITY_LISTLISTS);
        	
//        	RowListAdapter adapterOpen = new RowListAdapter(this, lists);
//        	return new AlertDialog.Builder(this)
//	        .setIcon(R.drawable.alert_dialog_icon)
//	        .setTitle(R.string.select_list)
//	        .setAdapter(adapterOpen, new DialogInterface.OnClickListener() {
//	            public void onClick(DialogInterface dialog, int whichButton) {
//	            	openList(lists.get(whichButton).getId());
//	            	
//	            }
//	        })
//	        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//	            public void onClick(DialogInterface dialog, int whichButton) {
//	            }
//	        })
//	       .create();
        
        
        }
        return null;
    }
	
	/**Called when open a list*/
    private void showLists() {
    		Intent i = new Intent(this, GuideList.class);
    		this.startActivityForResult(i, ACTIVITY_SHOWLIST);
    	
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
        
        ImageButton btnOpen = (ImageButton) this.findViewById(R.id.openlist);        
        btnOpen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLists();				
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
