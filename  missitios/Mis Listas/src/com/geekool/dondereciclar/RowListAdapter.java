package com.geekool.dondereciclar;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.Entity;

public class RowListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Entity> elements; 
    private boolean hasIcon = false;
	
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de listas
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowListAdapter(Context mContext, List<Entity> elements, boolean hasIcon)
    {
        this.mContext = mContext;
        this.elements = elements;
        this.hasIcon = hasIcon;
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
		long list_id = item.getId();
        View v = View.inflate(mContext, R.layout.category_row, null);

        if (hasIcon) {
	        Drawable d;
	
	        if (list_id<0) {
	        	d = mContext.getResources().getDrawable(R.drawable.category_0);
	        } else {
	        	d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier("com.geekool.dondereciclar:drawable/category_" + list_id, null, null));
	        }
	        
	        ImageView img = (ImageView)v.findViewById(R.id.icon_cat);
	        img.setImageDrawable(d);
        }
        
        TextView name = (TextView)v.findViewById(R.id.name_cat);       
        name.setText(item.getString("title"));
        
        return v;
	}

}