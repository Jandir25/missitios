package com.javielinux.BubblesMap.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Path.Direction;
import android.location.Location;

import com.geekool.dondereciclar.R;
import com.geekool.dondereciclar.Utils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/** Class to hold our location information */
public class MapLocation {

	public static final int TYPE_CURRENTPOSITION = 0;
	public static final int TYPE_PUNTOLIMPIO = 1;
	public static final int TYPE_PILAS = 2;
	public static final int TYPE_DENUNCIA = 3;
	public static final int TYPE_ROPA = 4;
	public static final int TYPE_OTROS = 5;
	public static final int TYPE_NEW = 6;
	
	public static final int PADDING_X = 10;
	public static final int PADDING_Y = 8;
	public static final int RADIUS_BUBBLES = 5;
	public static final int DISTANCE_BUBBLE = 15;
	public static final int SIZE_SELECTOR_BUBBLE = 10;
	
	private Location mLocation;
	private String mTitle = "";
	private MapLocationViewer mMapLocationView;
	private int mType = -1;
	private boolean mVisibility = true;
	
	private boolean selected = false;
	
	private int id;
	
	private Bitmap mDrawIcon, mShadowIcon, mDrawIconSelected;

    /**
     * Constructor - Una marca en el mapa
     * 
     * @param mapView MapLocationViewer
     * @param name Nombre de la marca
     * @param loc Localizacion de la marca en el mapa
     * @param type Tipo de marca
     */
	
	public MapLocation(MapLocationViewer mapView, Location loc, int type) {
		if (type==TYPE_PUNTOLIMPIO) {
			this.mTitle = mapView.getContext().getString(R.string.punto_limpio);			
		} else if (type==TYPE_PILAS) {
			this.mTitle = mapView.getContext().getString(R.string.pilas);		
		} else if (type==TYPE_DENUNCIA) {
			this.mTitle = mapView.getContext().getString(R.string.denuncia);			
		} else if (type==TYPE_ROPA) {
			this.mTitle = mapView.getContext().getString(R.string.ropa);		
		} else if (type==TYPE_OTROS) {
			this.mTitle = mapView.getContext().getString(R.string.otro);		
		} else if (type==TYPE_NEW) {
			this.mTitle = mapView.getContext().getString(R.string.new_poi);		
		}
		
		mMapLocationView = mapView;
		this.mLocation = loc;
		setType(type);
	}
/** sobrecargamos el constructior metiendole un titulo ad hoc**/	
	public MapLocation(MapLocationViewer mapView, Location loc, int type,String title) {
		if (type==TYPE_PUNTOLIMPIO) {
			this.mTitle = title;			
		} else if (type==TYPE_PILAS) {
			this.mTitle = mapView.getContext().getString(R.string.pilas);		
		} else if (type==TYPE_DENUNCIA) {
			this.mTitle = mapView.getContext().getString(R.string.denuncia);			
		} else if (type==TYPE_ROPA) {
			this.mTitle = mapView.getContext().getString(R.string.ropa);		
		} else if (type==TYPE_OTROS) {
			this.mTitle = mapView.getContext().getString(R.string.otro);		
		} else if (type==TYPE_NEW) {
			this.mTitle = mapView.getContext().getString(R.string.new_poi);		
		}
		
		mMapLocationView = mapView;
		this.mLocation = loc;
		setType(type);
	}	
	
	
    /**
     * Mostrarlo en el mapa
     */
	
	public void show() {
		mVisibility = true;
	}
	
    /**
     * Mostrarlo en el mapa
     */
	
	public void hide() {
		mVisibility = false;
	}
	
	public boolean isVisible() {
		return mVisibility;
	}
	
    /**
     * Establece el tipo de la marca
     * 
     * @param type Tipo de marca
     */
	
	private void setType(int type) {
		this.mType = type;	
		switch (mType) {
		case TYPE_CURRENTPOSITION:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.current_position);
			mDrawIconSelected = null;
			mShadowIcon = null;
			break;
		case TYPE_PUNTOLIMPIO:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark1_off);
			mDrawIconSelected = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark1_on);
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.markshadow);
			break;
		case TYPE_PILAS:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark2_off);
			mDrawIconSelected = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark2_on);
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.markshadow);
			break;
		case TYPE_DENUNCIA:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark3_off);
			mDrawIconSelected = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark3_on);
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.markshadow);
			break;
		case TYPE_ROPA:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark4_off);
			mDrawIconSelected = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark4_on);
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.markshadow);
			break;
		case TYPE_OTROS:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark5_off);
			mDrawIconSelected = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.mark5_on);
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.markshadow);
			break;
		case TYPE_NEW:
			mDrawIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.new_mark);
			mDrawIconSelected = null;
			mShadowIcon = BitmapFactory.decodeResource(mMapLocationView.getResources(),R.drawable.markshadow);
			break;
		}
		
	}
		
    /**
     * Devuelve la sombra del dibujo
     * 
     * @return Bitmap Sombra
     */
	
	public Bitmap getShadowIcon() {
		return mShadowIcon;
	}
	
    /**
     * Devuelve el tipo
     * 
     * @return int Tipo
     */
	
	public int getType() {
		return mType;
	}
	
    /**
     * Devuelve localizacion
     * 
     * @return Location Localizacion
     */
	
	public void setLocation(Location loc) {
		mLocation = loc;
	}
	
    /**
     * Devuelve localizacion
     * 
     * @return Location Localizacion
     */
	
	public Location getLocation() {
		return mLocation;
	}
	
    /**
     * Devuelve localizacion
     * 
     * @return GeoPoint Objeto GeoPoint
     */
	
	public GeoPoint getGeoPoint() {
		return new GeoPoint((int)(mLocation.getLatitude()*1E6), (int)(mLocation.getLongitude()*1E6));
	}
	
    /**
     * Devuelve el nombre
     * 
     * @return String Nombre
     */

	public void setTitle(String t) {
		if (mType==TYPE_PUNTOLIMPIO) {
			this.mTitle = mMapLocationView.getContext().getString(R.string.punto_limpio) + " " + t;
		} else if (mType==TYPE_PILAS) {
			this.mTitle = mMapLocationView.getContext().getString(R.string.pilas) + " " + t;			
		} else if (mType==TYPE_DENUNCIA) {
			this.mTitle = mMapLocationView.getContext().getString(R.string.denuncia) + " " + t;			
		} else if (mType==TYPE_ROPA) {
			this.mTitle = mMapLocationView.getContext().getString(R.string.ropa) + " " + t;			
		} else if (mType==TYPE_OTROS) {
			this.mTitle = mMapLocationView.getContext().getString(R.string.otro) + " " + t;			
		} else if (mType==TYPE_CURRENTPOSITION) {
			this.mTitle = mMapLocationView.getContext().getString(R.string.my_location);			
		}
	}
			
    /**
     * Devuelve el nombre
     * 
     * @return String Nombre
     */

	public String getTitle() {
		return mTitle;
	}
	
    /**
     * Devuelve el ancho del dibujo
     * 
     * @return int Ancho
     */
	
	public int getWidthIcon() {
		return mDrawIcon.getWidth();
	}
	
    /**
     * Devuelve el alto del dibujo
     * 
     * @return int Alto
     */
	
	public int getHeightIcon() {
		return mDrawIcon.getHeight();
	}
	
	public int getWidthText() {
		return (int)MapLocationsManager.textPaint.measureText(this.getTitle());
	}
	
	public int getHeightText() {
		return (int)MapLocationsManager.textPaint.descent()-(int)MapLocationsManager.textPaint.ascent();
	}
		
    /**
     * Devuelve el objeto RectF asociado al icono en la posicion (0,0)
     * 
     * @return Objeto RectF
     */
	
	public RectF getHRectFIcon() {
		return getHRectFIcon(0, 0);
	}
	
    /**
     * Devuelve el objeto RectF asociado al icono en la posicion 
     * enviada por parametro
     * 
     * @param offsetx Desplazamiento en X
     * @param offsety Desplazamiento en Y
     * @return Objeto RectF
     */
	
	public RectF getHRectFIcon(int offsetx, int offsety) {
		RectF rectf = new RectF();
		rectf.set(-mDrawIcon.getWidth()/2,-mDrawIcon.getHeight(),mDrawIcon.getWidth()/2,0);
		rectf.offset(offsetx, offsety);
		return rectf;
	}
	
    /**
     * Devuelve si ha sido pulsado el icono en el mapa
     * 
     * @param offsetx Desplazamiento en X
     * @param offsety Desplazamiento en Y
     * @param event_x Posicion X
     * @param event_y Posicion Y
     * @return Booleando
     */
	
	public boolean getHit(int offsetx, int offsety, float event_x, float event_y) {
	    if ( getHRectFIcon(offsetx, offsety).contains(event_x,event_y) ) {
	        return true;
	    }
	    return false;
	}
	
    /**
     * Dibuja la locacalizacion en el mapa
     * 
     * @param canvas Canvas sobre el que se dibuja
     * @param mapView Mapa
     * @param shadow Si es la sombra
     */
	
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (mVisibility && (getLocation()!=null)) {
			Point p = new Point();
			mapView.getProjection().toPixels(this.getGeoPoint(), p);
			
	    	if (shadow) {
	    		if (getShadowIcon()!=null) canvas.drawBitmap(this.getShadowIcon(), p.x, p.y - this.getShadowIcon().getHeight(),null);
	    	} else {
	    		if (isSelected() && mDrawIconSelected!=null)
	    			canvas.drawBitmap(mDrawIconSelected, p.x -mDrawIcon.getWidth()/2, p.y -mDrawIcon.getHeight(),null);
	    		else
	    			canvas.drawBitmap(mDrawIcon, p.x -mDrawIcon.getWidth()/2, p.y -mDrawIcon.getHeight(),null);
	    	}
		}
	}
	
	public void drawBubble(Canvas canvas, MapView mapView, boolean shadow) {
	    Point p = new Point();
	    mapView.getProjection().toPixels(Utils.Location2Geopoint(mLocation), p);
	    
	    int wBox = getWidthText()  + (PADDING_X*2);
	    int hBox = getHeightText() + (PADDING_Y*2); 
	    
	    RectF boxRect = new RectF(0, 0, wBox, hBox);
	    int offsetX = p.x - wBox/2;
	    int offsetY = p.y - hBox - this.getHeightIcon() - DISTANCE_BUBBLE;
	    boxRect.offset(offsetX, offsetY);
	    
	    Path pathBubble = new Path();
	    pathBubble.addRoundRect(boxRect, RADIUS_BUBBLES, RADIUS_BUBBLES, Direction.CCW);
	    pathBubble.moveTo(offsetX+(wBox/2)-(SIZE_SELECTOR_BUBBLE/2), offsetY+hBox);
	    pathBubble.lineTo(offsetX+(wBox/2), offsetY+hBox+SIZE_SELECTOR_BUBBLE);
	    pathBubble.lineTo(offsetX+(wBox/2)+(SIZE_SELECTOR_BUBBLE/2), offsetY+hBox);
	    
	    canvas.drawPath(pathBubble, MapLocationsManager.borderPaint);
	    canvas.drawPath(pathBubble, MapLocationsManager.innerPaint);
	
	    canvas.drawText(this.getTitle(), p.x-(getWidthText()/2),
	    		p.y-MapLocationsManager.textPaint.ascent()-this.getHeightIcon()-hBox+PADDING_Y - DISTANCE_BUBBLE, MapLocationsManager.textPaint);
	}

	public void setId(long l) {
		this.id = (int) l;
	}

	public int getId() {
		return id;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

}
