package com.tfc.misguias;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dataframework.Entity;
import com.tfc.misguias.R;

public class RowListWebAdapter extends BaseAdapter {

    
	private ArrayList<Guide> elements; 
    private int R_layout_IdView; 
    private Context mContext;
	
	private ImageView imageView;
	private Bitmap loadedImage;
	private String imageHttpAddress = "http://192.168.1.12/misguias/FILEUPLOAD/guide_images/thumbs/"; 
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de categorías para listas
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowListWebAdapter(Context mContext, ArrayList<Guide> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
       
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
	
    void downloadFile(String imageHttpAddress) {
        URL imageUrl = null;
        try {
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
            imageView.setImageBitmap(loadedImage);
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), "Error cargando la imagen: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
     * Devuelve la vista de la fila
     * 
     * @param position Posicion del elemento en la lista
     * @param convertView View
     * @param parent ViewGroup
     * @return Vista
     */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Guide item = (Guide) elements.get(position);
		    long id = item.getId();
			long creator_id = item.getCreator();
		    long category_id = item.getCategory_id();
			View v = View.inflate(mContext, R.layout.list_row, null);
			Drawable d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier("com.tfc.misguias:drawable/category_" + category_id, null, null));
			String guideImage="";
	        ImageView img = (ImageView)v.findViewById(R.id.icon);
	        //img.setImageDrawable(d);
	        if (item.getIcon().length()!=0){
	        	guideImage=imageHttpAddress+item.getIcon();	
	        }
	        else{
	        	guideImage=imageHttpAddress+"no_disponible.jpg";
	        }
	        Bitmap imgs =getBitmapFromURL(guideImage);
	        img.setImageBitmap(Bitmap.createScaledBitmap(imgs, 60, 60, false));
	        TextView title = (TextView)v.findViewById(R.id.title);       
	        title.setText(item.getTitle());
	        TextView date = (TextView)v.findViewById(R.id.date);       
	        date.setText(item.getDate());
		        
	    return v;
		}
	
	
	
}