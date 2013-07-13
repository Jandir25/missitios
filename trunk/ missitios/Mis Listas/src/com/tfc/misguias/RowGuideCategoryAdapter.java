package com.tfc.misguias;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.Entity;
import com.tfc.misguias.R;

public class RowGuideCategoryAdapter extends BaseAdapter {

	private ArrayList<GuideCategory> elements; 
    private int R_layout_IdView; 
    private Context mContext;
    private boolean hasIcon = false;
    private long selectId = -1;
    private View viewSelectId = null;
	
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de categorías para listas
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowGuideCategoryAdapter(Context mContext, ArrayList<GuideCategory> elements, boolean hasIcon)
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
	
	public void setSelectId(long selectId) {
		this.selectId = selectId;
	}

	public void setViewSelectId(View viewSelectId) {
		this.viewSelectId = viewSelectId;
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
		GuideCategory item = (GuideCategory) elements.get(position);
	   long category_id = item.getId();
		View v = View.inflate(mContext, R.layout.category_row, null);
		 Drawable d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier("com.tfc.misguias:drawable/category_"+category_id, null, null));
		 ImageView img = (ImageView)v.findViewById(R.id.icon_cat);
	        img.setImageDrawable(d);
	        
	        TextView name = (TextView)v.findViewById(R.id.name_cat);       
	        name.setText(item.getName());
	        
	        
    return v;
	}

}