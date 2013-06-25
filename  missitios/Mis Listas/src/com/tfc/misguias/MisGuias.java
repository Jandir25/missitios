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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
public class MisGuias extends Activity{
	
	private static final int DIALOG_CREATEGUIDE = 0;
	private static final int ACTIVITY_SHOWGUIDE = 1;
	private static final int DIALOG_OPENGUIDE = 2;
	private static final int DIALOG_SHOWGUIDE = 3;
	private static final int ACTIVITY_OPENGUIDE = 4;
	private static final int ACTIVITY_GUIDELIST = 5;
	private static final int ACTIVITY_CREATEGUIDE = 6;
    /**
     * Crea una nueva ventana de dialogo
     * 
     * @param id Identificador
     * @return Dialog dialogo
     */
	/*Este dialog habra que quitarlo*/
    @Override
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_CREATEGUIDE:
        	//Leemos el xml de los propios recursos de la bbdd
    		try {
    		    DataFramework.getInstance().open(this, "com.tfc.misguias");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    		createGuide();
        case DIALOG_SHOWGUIDE:
        	//Leemos el xml de los propios recursos de la bbdd
    		try {
    		    DataFramework.getInstance().open(this, "com.tfc.misguias");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    		Intent j = new Intent(this, GuideList.class);
    		this.startActivityForResult(j, ACTIVITY_SHOWGUIDE);        	
        	break;
        	
        case DIALOG_OPENGUIDE:
        	//Leemos el xml de los propios recursos de la bbdd
    		try {
    		    DataFramework.getInstance().open(this, "com.tfc.misguias");
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
        	//Aqui abriremos una lista 
             	
            Intent k = new Intent(this, GuideList.class);
            startActivityForResult(k, ACTIVITY_GUIDELIST);
        	
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
    		this.startActivityForResult(i, ACTIVITY_SHOWGUIDE);
    	
    }

	/**Called when create a guide*/
    private void createGuide() {
    		Intent i = new Intent(this, NewGuide.class);
    		this.startActivityForResult(i, ACTIVITY_CREATEGUIDE);
    	
    }

    
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrance);
        
        
        Button btnNew = (Button) this.findViewById(R.id.newguide);        
        btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createGuide();
				//showDialog(DIALOG_CREATEGUIDE);
			}			
		});
        
        Button btnOpen = (Button) this.findViewById(R.id.openguide);        
        btnOpen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				showLists();				
			}			
		});
		
		btnNew.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Button img = (Button) MisGuias.this.findViewById(R.id.newguide);
//				switch (event.getAction()) {
//                	case MotionEvent.ACTION_DOWN:
//                		img.setImageResource(R.drawable.nuevo);
//                		break;
//                	case MotionEvent.ACTION_UP:
//                		img.setImageResource(R.drawable.nuevo_off);
//                		break;
//				}
				return false;
			}
			
		});
		
	   
		
    }
}
