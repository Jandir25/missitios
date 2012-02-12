package com.geekool.dondereciclar;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;


public class RowPlacesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Entity> elements; 
    private long selectId = -1;
    private View viewSelectId = null;
	
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de Lugares
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowPlacesAdapter(Context mContext, List<Entity> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
       
    }
    
    
	public int getPositionById(long id) {
        for (int i=0; i<getCount(); i++) {
        	if ( ((Entity)getItem(i)).getId() == id ) {
        		return i;
        	}
        }
        return -1;
	}
    
    
    
    
    
    /**
     * Numero de elementos en la lista
     * 
     * @return Numero de elementos
     */
    
	@Override
	public int getCount() {
		return elements.size();
	}
	
    /**
     * Devuelve un objeto de la lista
     * 
     * @param position Posicion del elemento en la lista
     * @return Objeto
     */

	@Override
	public Object getItem(int position) {
        return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
    /**
     * Devuelve la vista de la fila
     * 
     * @param position Posicion del elemento en la lista
     * @param convertView View
     * @param parent ViewGroup
     * @return Vista
     */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = elements.get(position);
		long id = item.getId();
		long type_id = item.getEntity("type_id").getId();
        View v = View.inflate(mContext, R.layout.place_row, null);

        Drawable d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier("com.geekool.dondereciclar:drawable/category_" + type_id, null, null));
        
        ImageView img = (ImageView)v.findViewById(R.id.icon);
        img.setImageDrawable(d);
        
        TextView title = (TextView)v.findViewById(R.id.name);       
        title.setText(item.getString("name"));
        
        TextView adress = (TextView)v.findViewById(R.id.address);       
        adress.setText(item.getString("address"));
        
        TextView description = (TextView)v.findViewById(R.id.description);       
        description.setText(item.getString("description"));
        
        TextView puntuation = (TextView)v.findViewById(R.id.puntuation);       
        puntuation.setText(item.getString("puntuation"));
            
        if (selectId==id) {
        	viewSelectId = v;
        	title.setTextColor(Color.rgb(0xea, 0xea,0x9c));
        }
        return v;
	}
	
	
	public void setSelectId(long selectId) {
		this.selectId = selectId;
	}

	public void setViewSelectId(View viewSelectId) {
		this.viewSelectId = viewSelectId;
	}
	
    /**
     * Limpiar fila seleccionada
     * 
     */
    
    public void clearSelectId() {
    	if ( (selectId>=0) && (viewSelectId!=null)) {
   			TextView t = (TextView) viewSelectId.findViewById(R.id.name);
    		t.setTextColor(Color.WHITE);
    		viewSelectId = null;
    		selectId = -1;
    	}
    }

}