package com.geekool.dondereciclar;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class Utils {

    /**
     * Convierte de Location a Geopoint
     * 
     */
    
    static public GeoPoint Location2Geopoint(Location loc) {
    	return new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
    }
    
    /**
     * Convierte de Geopoint a Location 
     * 
     */
    
    static public Location Geopoint2Location(GeoPoint geo) {
    	Location loc = new Location(LocationManager.GPS_PROVIDER);
    	loc.setLatitude((double)geo.getLatitudeE6()/1E6);
		loc.setLongitude((double)geo.getLongitudeE6()/1E6);
    	return loc;
    }
    
    /**
     * Comprueba si el GPS esta activido en el dispositivo
     * 
     * @return savedInstanceState
     */
    
    static public boolean getGPSStatus(Activity act)
    {
    	String allowedLocationProviders =
    		Settings.System.getString(act.getContentResolver(),
    		Settings.System.LOCATION_PROVIDERS_ALLOWED);
     
    	if (allowedLocationProviders == null) {
    		allowedLocationProviders = "";
    	}
     
    	return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER) || allowedLocationProviders.contains(LocationManager.NETWORK_PROVIDER);
    }	
    
    /**
     * Muestra un mensaje
     * 
     * @param msg Mensaje
     * 
     */
    
    public static void showMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_LONG).show();
    }
    
    /**
     * Muestra un mensaje
     * 
     * @param msg Mensaje
     * 
     */
    
    public static void showShortMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Devuelve una distancia en formato metros/km
     * 
     * @param distance Distancia
     * @retrun Texto
     * 
     */
	
	static public String formatDistance(Context context, float distance) {
		if (distance<500) {
			return Utils.round(distance, 2) + " " + context.getResources().getString(R.string.metres);
		}
		return Utils.round(distance/1000, 2) + " " + context.getResources().getString(R.string.kilometres);
	}
	
    
    /**
     * Redondea a un numeto de digitos determinado un valor
     * 
     * @param val Valor
     * @param places Numero de digitos
     * @return Nuevo valor
     * 
     */
	
	static public float round(float val, int places) {
		long factor = (long)Math.pow(10,places);
		val = val * factor;
		long tmp = Math.round(val);
		return (float)tmp / factor;
	} 
	
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }
    
    public static Boolean isConnectionSlow (Context cnt) {
    	ConnectivityManager connec =  (ConnectivityManager)cnt.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( connec.getNetworkInfo(0).isConnectedOrConnecting() ) {
          if ( connec.getNetworkInfo(0).getSubtype() == 3 ) { 
        	  //Conectado por 3G (UMTS)
        	  return false;
          } else if ( connec.getNetworkInfo(0).getSubtype() == 1 ) {
        	  //Conectado por 2G (GRPS)
        	  return true;
          } else {
        	  //Otro tipo de conexion
        	  return false;
          }
        } else if ( connec.getNetworkInfo(1).isConnectedOrConnecting() ) {
        	//Conectado por WIFI
        	return false;
        } else {
        	//Sin conexión a Internet
        	return true;
        }    	
    }
    
	public static String isUser(Context c) throws XmlPullParserException, IOException {
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(c);
        
        String name = preference.getString("edittext_username", "");
        String pass = "";
		try {
			pass = Utils.MD5(preference.getString("edittext_pass", ""));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "http://www.dondereciclar.com/api_verify_user.php?nick=" +  name + "&pass=" + pass;
		
		HttpGet request = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse = client.execute(request);
		String xml = EntityUtils.toString(httpResponse.getEntity());
		try {
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser x = factory.newPullParser();
			
			x.setInput( new StringReader ( xml ) );
					
			int eventType = x.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if ( eventType == XmlPullParser.START_TAG ) {
	        		if (x.getName().equals("user")) {
	        			if (x.getAttributeValue(null, "exist").equals("true")) {
	        				return name;
	        			} else {
	        				return "";
	        			}
	        		}
	        		
	        	}
	        	eventType = x.next();
	        }
			
		} catch (Exception e) {
			
		}
		
		return "";
		
	}
	
	public static int idUser(Context c) throws XmlPullParserException, IOException {
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(c);
        
        String name = preference.getString("edittext_username", "");
        String pass = "";
		try {
			pass = Utils.MD5(preference.getString("edittext_pass", ""));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "http://www.dondereciclar.com/api_verify_user.php?nick=" +  name + "&pass=" + pass;
		
		HttpGet request = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse = client.execute(request);
		String xml = EntityUtils.toString(httpResponse.getEntity());
		try {
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser x = factory.newPullParser();
			
			x.setInput( new StringReader ( xml ) );
					
			int eventType = x.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if ( eventType == XmlPullParser.START_TAG ) {
	        		if (x.getName().equals("user")) {
	        			if (x.getAttributeValue(null, "exist").equals("true")) {
	        				if (!x.getAttributeValue(null, "id").equals("")) {
	        					return Integer.parseInt(x.getAttributeValue(null, "id"));
	        				}
	        			} else {
	        				return -1;
	        			}
	        		}
	        		
	        	}
	        	eventType = x.next();
	        }
			
		} catch (Exception e) {
			
		}
		
		return -1;
		
	}
	
	static public String getNameForImage() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		Date currentTime = new Date();
		return formatter.format(currentTime);
	}
	
}
