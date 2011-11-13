package com.geekool.dondereciclar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class ServiceGPS extends Service {

	private static MisListas map;
	
	private LocationManager mLocationManager;
	private MyLocationListener mLocationListener;
	
	private static boolean running = false;

    	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		_startService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_shutdownService();
	}
	
	private void _startService() {
		
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			running = true;
			long timeRegister = 1000;
			float distanceRegister = 5;
								
			mLocationListener = new MyLocationListener();
			mLocationManager.removeUpdates(mLocationListener);
			
			mLocationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, timeRegister, distanceRegister, mLocationListener);
		} else {
			
			if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				running = true;
				long timeRegister = 1000;
				float distanceRegister = 5;
									
				mLocationListener = new MyLocationListener();
				mLocationManager.removeUpdates(mLocationListener);
				
				mLocationManager.requestLocationUpdates(
		                LocationManager.NETWORK_PROVIDER, timeRegister, distanceRegister, mLocationListener);
			}
			
		}
	}
	
	private void _shutdownService() {
		running = false;
		if(mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);
	}
		
    private class MyLocationListener implements LocationListener 
    {
    	
        /**
         * Metodo que se ejecuta cada vez que cambia la localizacion del GPS
         * 
         * @param loc Objeto Location
         * 
         */
    	
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
            	if (running) {
            		sendLocation(loc);
            	}
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
    
	public void sendLocation(Location loc) {
    	if (map!=null) {
    		map.newLocation(loc);
    	}
	}   
	
    static boolean isRunning() {
    	return running;
    }
    
    static void registerActivity(MisListas m) {
    	map = m;
    }
    
    static void unregisterActivity() {
    	map = null;
    }

}
