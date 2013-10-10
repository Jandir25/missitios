package com.tfc.BubblesMap.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.location.Location;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.tfc.misguias.Utils;

public class MapLocationsManager {
	
	public static final int TYPE_MARKS_LOCATIONS = 0;
	public static final int TYPE_MARKS_NEW_FROM_GPS = 1;
	public static final int TYPE_MARKS_NEW_FROM_FINGER = 2;
	
	private List<MapLocation> mapLocations;
	
	public OnMapLocationClickListener OnMapLocationClickListener = null;
	
	public static Paint innerPaint, borderPaint, textPaint;
		
	private MapLocation selectedLocation = null;
	
	private MapLocation mCurrentLocation = null;
	
	private MapLocationViewer mMapView;
	
	private GeoPoint gpoint00 = null;
	
	private boolean todoRefresh = true;
	
	private int zoom = -1;
	
	private int typeMarks = TYPE_MARKS_LOCATIONS;
	private MapLocation mNewMapLocation;
	
    public MapLocationsManager(MapLocationViewer mlv) {
    	mMapView = mlv;
    	mapLocations = new ArrayList<MapLocation>();
    	
		mCurrentLocation = new MapLocation(mMapView, null, MapLocation.TYPE_CURRENTPOSITION);
		mCurrentLocation.hide();
		addMapLocation(mCurrentLocation);
		
		mNewMapLocation = new MapLocation(mMapView, null, MapLocation.TYPE_NEW);
		mNewMapLocation.hide();
		addMapLocation(mNewMapLocation);
		
		innerPaint = new Paint();
		innerPaint.setARGB(255, 255, 255, 255);
		innerPaint.setAntiAlias(true);
		
		borderPaint = new Paint();
		borderPaint.setARGB(255, 0, 0, 0);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(4);
		
		textPaint = new Paint();
		textPaint.setARGB(255, 0, 0, 0);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(12);
		
		todoRefresh = true;
    	
	}
    
	
	public void setOnMapLocationClickListener(OnMapLocationClickListener OnMapLocationClickListener) {
		this.OnMapLocationClickListener = OnMapLocationClickListener;
	}
	
	public void addMapLocation(MapLocation ml) {
		mapLocations.add(ml);
	}
	
	public List<MapLocation> getMapLocations() {
		return mapLocations;
	}
	
	public MapLocation getSelectedMapLocation() {
		return selectedLocation;
	}
	
	public void unSelectedMapLocation() {
		if (selectedLocation!=null) selectedLocation.setSelected(false);
		selectedLocation = null;
	}
	
	public void clear() {
		mapLocations.clear();
		addMapLocation(mCurrentLocation);
	}

	
    public boolean verifyHitMapLocation(MapView mapView, MotionEvent event) {
    	if (event.getAction()==MotionEvent.ACTION_DOWN) {

    		if (typeMarks == TYPE_MARKS_NEW_FROM_FINGER) {
    			
    			GeoPoint auxGP = mMapView.getProjection().fromPixels((int)event.getX(),(int)event.getY());
    			mNewMapLocation.setLocation(Utils.Geopoint2Location(auxGP));
    			mMapView.showInfoNewLocation();
    			
    		} else if (typeMarks == TYPE_MARKS_LOCATIONS) {

		    	for (int i=mapLocations.size()-1; i>=0; i--) {
		    		MapLocation testLocation = mapLocations.get(i);
		    		
		    		if (testLocation.getLocation()!=null && testLocation.isVisible()) {
		    		
			    		Point p = new Point();
			    		
			    		mapView.getProjection().toPixels(testLocation.getGeoPoint(), p);
			    				    		
			    		if (testLocation.getHit(p.x, p.y, event.getX(),event.getY())) {
			    			testLocation.setSelected(true);
			    			if (selectedLocation == testLocation) {
			    				if (OnMapLocationClickListener!=null) {
			    					OnMapLocationClickListener.OnMapLocationClick(selectedLocation, true);
			    				}
			    			} else {
			    				unSelectedMapLocation();
			    				selectedLocation = testLocation;
			    				if (OnMapLocationClickListener!=null) {
			    					OnMapLocationClickListener.OnMapLocationClick(selectedLocation, false);
			    				}
			    			}
			    			return false;
			    	    }
			    		
		    		}
		    	    
		    	}    			
    			
    		}

    	}
    	//selectedLocation = null;
    	return false; 
    }
    
    //Finjiremos que se ha tocado la pantalla
    public boolean setHitMapLocation(MapView mapView,MapLocation mLocation ) {
    	selectedLocation = mLocation;
    	return false; 
    }
    
    
    
    private void changeMap() {   	
		if (OnMapLocationClickListener!=null) {
			OnMapLocationClickListener.OnMapChanged(
					Utils.Geopoint2Location(mMapView.getProjection().fromPixels(0, 0)), 
					Utils.Geopoint2Location(mMapView.getProjection().fromPixels(mMapView.getWidth(), mMapView.getHeight())));
		}
    }
    
    public Location getLocationTopLeft() {
		return Utils.Geopoint2Location(mMapView.getProjection().fromPixels(0, 0));
    }
    
    public Location getLocationBottomRight() {
		return Utils.Geopoint2Location(mMapView.getProjection().fromPixels(mMapView.getWidth(), mMapView.getHeight()));
    }
    
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    	
    	if (shadow) {
	    	if (gpoint00==null) {
	    		gpoint00 = mMapView.getProjection().fromPixels(0, 0);
	    	} 
	    		
    		if (mMapView.getZoomLevel() != zoom) {
    			todoRefresh = true;
    			zoom = mMapView.getZoomLevel();
    		}
    		
    		if (todoRefresh) {
	    		GeoPoint aux = mMapView.getProjection().fromPixels(0, 0);
	
				if (aux.equals(gpoint00)) {
					changeMap();
					todoRefresh = false;
				}
    		}
			
    	}
    	
    	if (typeMarks == TYPE_MARKS_LOCATIONS) {
			Iterator<MapLocation> iterator = getMapLocations().iterator();
	
			while(iterator.hasNext()) {	   
	    		MapLocation location = iterator.next();
	    		location.draw(canvas, mapView, shadow);
	    	}
			
			// dibujar bocadillo sólo si selected location esta vacio y el titulo esta vacio...
	    	if ( ( selectedLocation != null) && (!selectedLocation.getTitle().equals(""))) {
	    		selectedLocation.drawBubble(canvas, mapView, shadow);
	    	}
    	} else {
    		mNewMapLocation.draw(canvas, mapView, shadow);
    	}
		
		gpoint00 = mMapView.getProjection().fromPixels(0, 0);
		    	
    }
    
	
	public void showCurrentLocation() {
		mCurrentLocation.show();
	}
	
	
	public void hideCurrentLocation() {
		mCurrentLocation.hide();
	}
	
	public void setCurrentLocation(Location loc) {
		mCurrentLocation.setLocation(loc);
	}


	public void setTodoRefresh(boolean todoRefresh) {
		this.todoRefresh = todoRefresh;
	}


	public void setTypeMarks(int typeMarks) {
		this.typeMarks = typeMarks;
		if (typeMarks == TYPE_MARKS_LOCATIONS) {
			mNewMapLocation.hide();
		} else {
			GeoPoint auxGP = mMapView.getProjection().fromPixels(mMapView.getWidth()/2, mMapView.getHeight()/2);
			mNewMapLocation.setLocation(Utils.Geopoint2Location(auxGP));
			mNewMapLocation.show();
		}
		mMapView.refresh();
	}


	public int getTypeMarks() {
		return typeMarks;
	}
	
	public Location getNewLocation() {
		return mNewMapLocation.getLocation();
	}
	
	public void setNewLocation(Location loc) {
		mNewMapLocation.setLocation(loc);
	}

    
}
