package com.tfc.misguias;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;


public class RowPlacesWebAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Place> elements; 
    private long selectId = -1;
    private View viewSelectId = null;
    private RatingBar ratingBarPlace = null;
    
    private ImageView imageView;
	private Bitmap loadedImage;
	//private String imageHttpAddress = "http://192.168.1.13/misguias/FILEUPLOAD/places_images/thumbs/"; 
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de Lugares
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowPlacesWebAdapter(Context mContext, ArrayList<Place> elements)
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
        
	public int getPositionById(int id) {
        for (int i=0; i<getCount(); i++) {
        	if ( ((Entity)getItem(i)).getId() == id ) {
        		return i;
        	}
        }
        return -1;
	}
	
    
    /**
     * Descarga una imagen de la guía
     * 
     * 
     * */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);            
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
		Place placeSelected=elements.get(position);
		return placeSelected.getId();
	}
	
    /**
     * Devuelve la vista de la fila
     * 
     * @param position Posicion del elemento en la guía
     * @param convertView View
     * @param parent ViewGroup
     * @return Vista
     */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Place item = elements.get(position);
		long id = item.getId();
		long type_id = item.getType_id();
        View v = View.inflate(mContext, R.layout.place_row, null);
        String placeImage="";
        Drawable d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier("com.tfc.misguias:drawable/place_type_" + type_id, null, null));
        
        ImageView img = (ImageView)v.findViewById(R.id.icon);
        
        if (item.getImage().length()!=0){
        	placeImage=mContext.getResources().getString(R.string.ip_home_place_images)+item.getImage();	
        }
        else{
        	placeImage=mContext.getResources().getString(R.string.ip_home_place_images)+"no_disponible.jpg";
        }
        
        Bitmap imgs =getBitmapFromURL(placeImage);
        img.setImageBitmap(Bitmap.createScaledBitmap(imgs, 60, 60, false));
        
        
        TextView title = (TextView)v.findViewById(R.id.name);       
        title.setText(item.getName());
        
        TextView adress = (TextView)v.findViewById(R.id.address);       
        adress.setText(item.getAddress());
        
        TextView description = (TextView)v.findViewById(R.id.description);       
        description.setText(item.getDescription());
        
        //TextView puntuation = (TextView)v.findViewById(R.id.puntuation);
        float puntu = item.getPuntuacion(); 
        //puntuation.setText(Float.toString(puntu));
        
        RatingBar ratingBarPlace = (RatingBar)v.findViewById (R.id.ratingBarPlace);
        ratingBarPlace.setRating(puntu);
            
        
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