package com.tfc.misguias;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.tfc.BubblesMap.view.MapLocation;
import com.tfc.BubblesMap.view.MapLocationViewer;
import com.tfc.BubblesMap.view.MapLocationsManager;
import com.tfc.BubblesMap.view.OnMapLocationClickListener;

public class GuideMap extends MapActivity implements OnMapLocationClickListener {
	
	private static final int ACTIVITY_LOCATION = 0;
	private static final int ACTIVITY_NEWLOCATION = 1;
	
	private MapLocationViewer mMapView;
    private MapController mMapController;
    
    private boolean isTraffic = true;
    
    private boolean isAllPoints = true;
    
    private static final int POI_BYPAGE = 10;
    
    private static final int DIALOG_UPLOAD = 0;
    private static final int DIALOG_NOUSER = 1;
    private static final int DIALOG_SEARCH = 2;
    private static final int DIALOG_SEARCH_LIST = 3;
    
    private static final int MYLOCATION_ID = Menu.FIRST;
    private static final int NEWPOI_ID = Menu.FIRST + 1;
    private static final int PREFERENCES_ID = Menu.FIRST + 2;
    private static final int SATELLITE_ID = Menu.FIRST + 3;
    private static final int SEARCH_ID = Menu.FIRST + 4;
    private static final int MYPOINTS_ID = Menu.FIRST + 5;
    
    private EditText mSearch;
    private List<Address> mListAddress = new ArrayList<Address>();
    private List<String> mListStringAddress = new ArrayList<String>();
        
    private boolean goToNewLocationInNextLocation = false;
    
    private Location lastLocation = null;
    
    private boolean moveToLocation = false;
    
    private long idCurrentLocation = -1;
    private String nameCurrentLocation ="";
    private float actualDistance=-1;
    
    private int page = 0;
    private int total = 0;
    private int pages = 0;
    
    private Location topLeft, bottomRight;
    
    public long idGuide;
    
    private TextView tvFound, tvPages, tvInfo;
    private ImageView ivIcoInfo; 
    private LinearLayout llMenu, llInfo, llButtonsNewPoint, llPagination; 
    
    private int groups[] = {1, 0, 0, 0, 0};
    
    private ImageView btnGroup1, btnGroup2, btnGroup3, btnGroup4, btnGroup5, btnMore,btnNear;
    
    private List <Entity> places;
   
    private int fromInfo = -1;
    public int ownList=-1;//propia es un 1, descargada es un 0
    
    private String groups2str() {
    	String out = "(";
    	for (int i=0; i<5; i++) {
    		out += groups[i];
    		if (i<4) out += ",";
    	}
    	out += ")";
    	return out;
    }
    
    public void showMenu() {
    	mMapView.getManager().unSelectedMapLocation();
    	//llMenu.setVisibility(View.VISIBLE);
    	
    	llInfo.setVisibility(View.GONE);
    	
    	llButtonsNewPoint.setVisibility(View.GONE);
    	llPagination.setVisibility(View.VISIBLE);
    }
    
    public void hideMenu() {
    	mMapView.getManager().unSelectedMapLocation();
    	llInfo.setVisibility(View.GONE);
    	llButtonsNewPoint.setVisibility(View.GONE);
    	llPagination.setVisibility(View.VISIBLE);
    }
    
    public void showInfo() {
    	
    	llInfo.setVisibility(View.VISIBLE);
    	
    	btnMore.setVisibility(View.VISIBLE);
    	
    	llButtonsNewPoint.setVisibility(View.GONE);
    	llPagination.setVisibility(View.VISIBLE);
    }

    public void showInfoNewLocation() {
    	Location loc = mMapView.getManager().getNewLocation();
		tvInfo.setText(getAddress(loc));
    	showInfo();
    	
    	btnMore.setVisibility(View.GONE);
    	
    	llButtonsNewPoint.setVisibility(View.VISIBLE);
    	llPagination.setVisibility(View.GONE);
    }
    
    public String getAddress(Location loc) {
    	Geocoder gc = new Geocoder(GuideMap.this);
    	try {
    		
    		List<Address> listAddress = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
    		if (listAddress.size()>0) {
    			Address address = listAddress.get(0);
    			return address.getAddressLine(0);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "";
    }
        
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_SEARCH:
        	mSearch = new EditText(this);
        	mSearch.setPadding(10, 10, 10, 10);
            return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.alert_dialog_search)
                .setView(mSearch)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
						search(mSearch.getText().toString());
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        case DIALOG_SEARCH_LIST:
        	
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	            android.R.layout.simple_spinner_dropdown_item, mListStringAddress);
        	
	        return new AlertDialog.Builder(this)
		        .setIcon(R.drawable.alert_dialog_icon)
		        .setTitle(R.string.you_say)
		        .setAdapter(adapter, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Address x = mListAddress.get(whichButton);
		            	if (x!=null) {
		            		mMapView.getController().setCenter(new GeoPoint((int)(x.getLatitude()*1E6), (int)(x.getLongitude()*1E6)));
		            		mMapController.setZoom(13);
		            	} else {
		            		Utils.showMessage(GuideMap.this, GuideMap.this.getString(R.string.no_search));
		            	}
		            }
		        })
		        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            }
		        })
		       .create();
        case DIALOG_UPLOAD:
            return new AlertDialog.Builder(GuideMap.this)
                .setTitle(R.string.type_location)
                .setItems(R.array.select_type_location, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {//no reconoce la localizacion en el simulador, probar desde android
                        	if (!ServiceGPS.isRunning()) {
                        		startService();
                        		Utils.showShortMessage(GuideMap.this, getString(R.string.search_gps));
                        	} else {
                        		moveMap2NewLocation(lastLocation);
                        	}
                        	goToNewLocationInNextLocation = true;
                        	mMapView.getManager().setTypeMarks(MapLocationsManager.TYPE_MARKS_NEW_FROM_GPS);
                        	showInfoNewLocation();
                        } else if (which==1) {
                        	mMapView.getManager().setTypeMarks(MapLocationsManager.TYPE_MARKS_NEW_FROM_FINGER);
                        	showInfoNewLocation();
                        }
                    }
                })
                .create();
        case DIALOG_NOUSER:
            return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.title_question_nouser)
                .setMessage(R.string.msg_question_nouser)
                .setPositiveButton(R.string.alert_dialog_preferences, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	goToPreferences();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        }
        return null;
    }
    /**
     * Recibe informacion del tipo de lista que estamos creando
     * 
     * 
     * 
     * 
     */
   
    
    
    
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		//Recuperamos clase singleton
		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
		idGuide = sgDatosLista.getIdGuide();
		ownList = sgDatosLista.getOwnList();
		idCurrentLocation =	sgDatosLista.getIdSelected(); 
		if (idCurrentLocation!=-1){
        	fromInfo=0;
		}
        if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(DataFramework.KEY_ID)) 
				idGuide = savedInstanceState.getLong(DataFramework.KEY_ID);
				//OwnList = savedInstanceState. ;añadir mas adelante, implica recuperar si es una ownlist
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				//idGuide = (extras.containsKey(DataFramework.KEY_ID)) ? extras.getLong(DataFramework.KEY_ID) : -1;
				//idCurrentLocation = (extras.containsKey("selectedPlace")) ? extras.getLong("selectedPlace") : -1;
				ownList = 1;
			} else {
				if (idGuide==-1){//instanciado singleton habrá que eliminar esto
					idGuide = -1;
					ownList = 0;
					
				}
			}
		}
        
        
        //Comprobamos el tipo de conexión a internet y si es demasiada lenta le mostramos un aviso
        if ( Utils.isConnectionSlow(GuideMap.this) ) {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    		alert.setTitle(R.string.speed_connection);  
    		alert.setMessage(R.string.speed_text_connection);  
    		alert.setPositiveButton(R.string.change_now, new DialogInterface.OnClickListener() {  
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 Intent lIntent = new Intent(Settings.ACTION_SETTINGS); 			  
         			 startActivity(lIntent);  
                 }  
           	});  
    		alert.setNegativeButton(R.string.no_change, new DialogInterface.OnClickListener() {  
    			public void onClick(DialogInterface dialog, int whichButton) {  
    				//nada  
    			}
    		});  
    		alert.setCancelable(false);
    		alert.show();
        }
        
        topLeft = null;
        bottomRight = null;
        
        FrameLayout frameMapContainer = (FrameLayout) this.findViewById(R.id.map_container);
        
        //mMapView = new MapLocationViewer(this, "0aF-1bxSehpw5LfdmFoMqeNxCbbQDwAw7fEG7Tg"); Caducó
        mMapView = new MapLocationViewer(this, "0aF-1bxSehpzt5JpRMxAIWseewD8hhbJ_oS9HVQ");
        
        mMapView.setBuiltInZoomControls(true);
		
		mMapView.getManager().setOnMapLocationClickListener(this);
		
		frameMapContainer.addView(mMapView);
				
		mMapController = mMapView.getController();
		
		mMapView.setSatellite(false);
		
		tvFound = (TextView) this.findViewById(R.id.txt_found);
		tvPages = (TextView) this.findViewById(R.id.txt_page);
		tvInfo = (TextView) this.findViewById(R.id.txt_info);
		ivIcoInfo = (ImageView) this.findViewById(R.id.ico_info);
		
		llInfo = (LinearLayout) this.findViewById(R.id.ll_info);
		llButtonsNewPoint = (LinearLayout) this.findViewById(R.id.ll_buttons_new_point);
		llPagination = (LinearLayout) this.findViewById(R.id.ll_pagination);
		
		llInfo.setVisibility(View.GONE);
		
		ImageView btnPrevious = (ImageView) this.findViewById(R.id.img_page_left);
		
		btnPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page>0) {
					showMenu();
					page--;
					try {
						readLocations();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		ImageView btnNext = (ImageView) this.findViewById(R.id.img_page_right);
		
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page<pages-1) {
					showMenu();
					page++;
					try {
						readLocations();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			}
			
		});
		
		ImageView btnNear = (ImageView) this.findViewById(R.id.img_next_place);
		
		btnNear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lastLocation!=null) {
					try {
						nearestLocation();
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Utils.showMessage(GuideMap.this, GuideMap.this.getString(R.string.no_gps_near));
				}	
			}
			
		});
		
		
		
		
		
		
		
		btnMore = (ImageView) this.findViewById(R.id.btn_more);
		
		llInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMapView.getManager().getTypeMarks() == MapLocationsManager.TYPE_MARKS_LOCATIONS) {
					//Si viene de haber pulsado el anterior
					if (fromInfo==0){
						
						finish();
					}else{
						Intent i = new Intent(GuideMap.this, InfoLocation.class);
						i.putExtra("idSelected", idCurrentLocation);
						i.putExtra("ownList", ownList);
						i.putExtra("locationGPS", lastLocation);
						i.putExtra("fromMap", 0);
						startActivityForResult(i, ACTIVITY_LOCATION);
					}
				} else {
					Intent i = new Intent(GuideMap.this, NewLocation.class);
					Location loc = mMapView.getManager().getNewLocation();
					//ECS Included the list id
					i.putExtra("address", getAddress(loc));
					i.putExtra("location", loc);
					i.putExtra("idGuide", idGuide);
					i.putExtra("ownList", ownList);
					startActivityForResult(i, ACTIVITY_NEWLOCATION);
				}
			}
			
		});
		
		TextView tvAcceptNewPoint = (TextView) this.findViewById(R.id.btn_accept_new_point);
		
		tvAcceptNewPoint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToNewLocationInNextLocation = false;
				Intent i = new Intent(GuideMap.this, NewLocation.class);
				Location loc = mMapView.getManager().getNewLocation();
				//ECS 10/12/2011
				i.putExtra("idGuide", idGuide);//Sending also the list id FALLANDO 10/12
				i.putExtra("address", getAddress(loc));
				i.putExtra("location", loc);
				startActivityForResult(i, ACTIVITY_NEWLOCATION);
			}
			
		});

		TextView tvCancelNewPoint = (TextView) this.findViewById(R.id.btn_cancel_new_point);
		
		tvCancelNewPoint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelNewPoint();
			}
			
		});
		
		//Cargamos las categorias por defecto
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if ( preference.getBoolean("checkbox_PuntosLimpios", true) == true ) {
			groups[0] = 1;	
			//btnGroup1.setImageResource(R.drawable.ico_1_on);
		} else {
			groups[0] = 0;
			//btnGroup1.setImageResource(R.drawable.ico_1_off);
		}		
		
		if ( preference.getBoolean("checkbox_Pilas", false) == true ) {
			groups[1] = 2;	
			//btnGroup2.setImageResource(R.drawable.ico_2_on);
		} else {
			groups[1] = 0;
			//btnGroup2.setImageResource(R.drawable.ico_2_off);
		}
		
		if ( preference.getBoolean("checkbox_DenunciaAmbiental", false) == true ) {
			groups[2] = 3;	
			//btnGroup3.setImageResource(R.drawable.ico_3_on);
		} else {
			groups[2] = 0;
			//btnGroup3.setImageResource(R.drawable.ico_3_off);
		}
		
		if ( preference.getBoolean("checkbox_Ropa", false) == true ) {
			groups[3] = 4;	
			//btnGroup4.setImageResource(R.drawable.ico_4_on);
		} else {
			groups[3] = 0;
			//btnGroup4.setImageResource(R.drawable.ico_4_off);
		}
		
		if ( preference.getBoolean("checkbox_Otros", false) == true ) {
			groups[4] = 5;	
			//btnGroup5.setImageResource(R.drawable.ico_5_on);
		} else {
			groups[4] = 0;
			//btnGroup5.setImageResource(R.drawable.ico_5_off);
		}
		
		//Si estan desactivadas todas las categorias, como mï¿½nimo mostramos la de los puntos limpios
		if ( (groups[0] == 0) && (groups[1] == 0) && (groups[2] == 0) && (groups[3] == 0) && (groups[4] == 0) ) {
			groups[0] = 1;	
			//btnGroup1.setImageResource(R.drawable.ico_1_on);
		}
		
		setPositionInMap();
		if (idCurrentLocation!=-1){
			//Nos posicionamos en el sitio actual
			Entity currentPlace   = new Entity("tbl_places", idCurrentLocation);
			Location loc = new Location(LocationManager.GPS_PROVIDER);
			loc.setLatitude(currentPlace.getDouble("latitude"));
			loc.setLongitude(currentPlace.getDouble("longitude"));
			moveToLocation(loc);
			showInfo();
			
			
			System.out.println("funciona y el current location es: "+idCurrentLocation);
		}
			
		
    }
    
    private void setPositionInMap() {
		LocationManager locationmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationmanager.getBestProvider(criteria,true);
		Location location = null;
		if (provider != null) { 
			location = locationmanager.getLastKnownLocation(provider);
			if ( location != null ) {
				mMapView.getController().setCenter(new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6)));
	    		mMapController.setZoom(8);
	    		topLeft = mMapView.getManager().getLocationTopLeft();
		        bottomRight = mMapView.getManager().getLocationBottomRight();
		        newLocation(location);
	    		moveToLocation(location);
			} else {
				mMapView.getController().setCenter(new GeoPoint((int)(40.407*1E6), (int)(-3.68*1E6)));
	    		mMapController.setZoom(14);
			}
		} else {
			mMapView.getController().setCenter(new GeoPoint((int)(40.407*1E6), (int)(-3.68*1E6)));
    		mMapController.setZoom(6);
		}
		startService();
		
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, MYLOCATION_ID, 0, this.getString(R.string.my_location))
        	.setIcon(android.R.drawable.ic_menu_mylocation);
        menu.add(0, SATELLITE_ID, 0, R.string.menu_satellite)
			.setIcon(android.R.drawable.ic_menu_mapmode);
        menu.add(0, SEARCH_ID, 0, this.getString(R.string.search))
			.setIcon(android.R.drawable.ic_menu_search);
        menu.add(0, NEWPOI_ID, 0, this.getString(R.string.new_poi))
    		.setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MYPOINTS_ID, 0, this.getString(R.string.my_points))
			.setIcon(android.R.drawable.ic_menu_myplaces);
        menu.add(0, PREFERENCES_ID, 0, this.getString(R.string.preferences))
			.setIcon(android.R.drawable.ic_menu_preferences);
        
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case MYLOCATION_ID:
        	if (lastLocation!=null) {
        		moveToLocation(lastLocation);
        	} else {
				moveToLocation = true;
        	}
			if (!ServiceGPS.isRunning()) {
				startService();
			}
            return true;
        case SATELLITE_ID:
        	if (isTraffic) {
        		isTraffic = false;
        		item.setTitle(R.string.menu_traffic);
            	mMapView.setTraffic(false);
            	mMapView.setSatellite(true);
        	} else {
        		isTraffic = true;
        		item.setTitle(R.string.menu_satellite);
        		mMapView.setTraffic(true);
            	mMapView.setSatellite(false);
        	}
            return true;
        case MYPOINTS_ID:
        	try {
				if (!Utils.isUser(this).equals("")) {
					if (isAllPoints) {
						isAllPoints = false;
						item.setTitle(R.string.all_points);
						Utils.showMessage(this, this.getString(R.string.msg_mypoint));
					} else {
						isAllPoints = true;
						item.setTitle(R.string.my_points);
						Utils.showMessage(this, this.getString(R.string.msg_allpoint));
					}
					try {
						readLocations();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Utils.showMessage(this, this.getString(R.string.msg_nouser));
				}
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            return true;
        case SEARCH_ID:
        	showDialog(DIALOG_SEARCH);
        	return true;
        case PREFERENCES_ID:
        	goToPreferences();
        	return true;
        case NEWPOI_ID:
        	try {
        		//ECS COMENTAREMOS ESTO, SE PODRÁN AÑADIR PUNTOS SIEMPRE QUE SE ESTÉ CON UNA LISTA 
				if (Utils.isUser(this).equals("")) {
					showDialog(DIALOG_UPLOAD);
				} else {
					showDialog(DIALOG_UPLOAD);
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void search(String text) {
		Geocoder gc = new Geocoder(this);
		try {
			mListAddress.clear(); 
			mListStringAddress.clear();
			
			mListAddress = gc.getFromLocationName(text, 5);
			if (mListAddress.size()<=0) {
				Utils.showMessage(this, this.getString(R.string.no_search));
			} else if (mListAddress.size()==1) {
				Address x = mListAddress.get(0);
				mMapView.getController().setCenter(new GeoPoint((int)(x.getLatitude()*1E6), (int)(x.getLongitude()*1E6)));
	    		mMapController.setZoom(13);
	    		topLeft = mMapView.getManager().getLocationTopLeft();
		        bottomRight = mMapView.getManager().getLocationBottomRight();		        
	    		try {
	    			readLocations();
	    		} catch (XmlPullParserException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
			} else {
				for (int i=0; i<mListAddress.size(); i++) {
					String address = "";
					for (int j=0; j<=mListAddress.get(0).getMaxAddressLineIndex(); j++) {
						address += mListAddress.get(i).getAddressLine(j) + " ";
					}
					mListStringAddress.add(address);
				}
				showDialog(DIALOG_SEARCH_LIST);
			}

		} catch (Exception e) {
		
		} 
    }
    
    private void goToPreferences() {
    	Intent i = new Intent(this, Preferences.class);
		startActivity(i);
    }
    
    private boolean startService() {
    	if (Utils.getGPSStatus(this)) {
    		Intent svc = new Intent(this, ServiceGPS.class);
            startService(svc);	
            Utils.showShortMessage(this, this.getString(R.string.search_location));
            return true;
    	} else {
    		Utils.showShortMessage(this, this.getString(R.string.no_gps));
    		return false;
    	}
    	
    }
    
    private void stopService() {
    	if (ServiceGPS.isRunning()) {
    		Intent svc = new Intent(this, ServiceGPS.class);
    		stopService(svc);
    		mMapView.getManager().hideCurrentLocation();
    	}
    }
    
    public void moveMap2NewLocation(Location loc) {
    	mMapView.getManager().setNewLocation(loc);
		mMapView.getController().setCenter(Utils.Location2Geopoint(loc));
		mMapView.getController().setZoom(17);
		mMapView.showInfoNewLocation();
    }
    
    public void newLocation(Location loc) {
    	lastLocation = loc;
		mMapView.getManager().setCurrentLocation(loc);
		mMapView.refresh();
		if (goToNewLocationInNextLocation) {
			moveMap2NewLocation(loc);
		} else {
	    	if (moveToLocation) {
	    		moveToLocation(lastLocation);
	    		moveToLocation = false;
	    	}
		}
    } 
    
    private void moveToLocation(Location loc) {
    	Location loc2 = loc;
		mMapView.getController().animateTo(Utils.Location2Geopoint(loc2));
    	//mMapView.getController().setCenter(Utils.Location2Geopoint(loc));
		mMapView.getController().setZoom(15);
		mMapView.getManager().showCurrentLocation();
		try {
			Utils.showShortMessage(this, this.getString(R.string.show_near_location));
			readLocations();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void moveToLocationZoom(Location loc){
    	int minLat = Integer.MAX_VALUE;
    	int maxLat = Integer.MIN_VALUE;
    	int minLon = Integer.MAX_VALUE;
    	int maxLon = Integer.MIN_VALUE;
    	
    	Location ownPosition =  lastLocation;;
    	GeoPoint ownPoint = Utils.Location2Geopoint(ownPosition);
    	GeoPoint point = Utils.Location2Geopoint(loc);

    	int lat = point.getLatitudeE6();
        int lon = point.getLongitudeE6();
        
        int ownLat = ownPoint.getLatitudeE6();
        int ownLon = ownPoint.getLongitudeE6();

        maxLat = Math.max(lat, ownLat);
        minLat = Math.min(lat, ownLat);
        maxLon = Math.max(lon, ownLon);
        minLon = Math.min(lon, ownLon);


        double fitFactor = 1.5;
        mMapView.getController().zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor), (int)(Math.abs(maxLon - minLon) * fitFactor));

   		mMapView.getController().animateTo(new GeoPoint( (maxLat + minLat)/2, 
   				(maxLon + minLon)/2 )); 

		
    }
    
    public void cancelNewPoint() {
    	goToNewLocationInNextLocation = false;
    	mMapView.getManager().setTypeMarks(MapLocationsManager.TYPE_MARKS_LOCATIONS);
		showMenu();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        ServiceGPS.unregisterActivity();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
		//Recuperamos clase singleton
		SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
		idCurrentLocation =	sgDatosLista.getIdSelected(); 
        if (idCurrentLocation==-1){
        	fromInfo=-1;
        	hideMenu();
        }
		ServiceGPS.registerActivity(this);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void OnMapLocationClick(MapLocation mapLocation, boolean wasSelected) {
		if (wasSelected) {
			showMenu();
		} else {

			if (mapLocation.getType() != MapLocation.TYPE_CURRENTPOSITION) {
				try {
					readLocation(mapLocation.getId());
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (lastLocation!=null) {
				mapLocation.setTitle(nameCurrentLocation.toUpperCase()+" a " + Utils.formatDistance(this, mapLocation.getLocation().distanceTo(lastLocation)));
				actualDistance = mapLocation.getLocation().distanceTo(lastLocation);
			}else{
				mapLocation.setTitle(nameCurrentLocation.toUpperCase());
			}
		}
		
	}
	
	private void readLocation(int id) throws XmlPullParserException, IOException {
		idCurrentLocation = id;
		if (ownList!=1){// si se trata de una lista publica
			String url = "http://www.dondereciclar.com/api_info_location.php?id=" + id;
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
						if (x.getName().equals("location")) {
							int idGroup = this.getResources().getIdentifier(
	        					"com.tfc.misguias:drawable/ico_"+x.getAttributeValue(null, "group_id")+"_on", null, null);
							ivIcoInfo.setImageResource(idGroup);
						}
						if (x.getName().equals("street")) {
							tvInfo.setText(x.nextText());
						}
	        		
					}
					eventType = x.next();
				}
			
			} catch (Exception e) {
			
			}
		}
			else{// Se trata de una lista privada

				Entity e = new Entity("tbl_places", (long) id);
				System.out.println(e.getString("name"));
    			System.out.println(e.getString("longitude"));
    			System.out.println("Tipo_"+e.getString("type_id"));
    			System.out.println(e.getString("latitude"));
    			System.out.println(e.getString("description"));
    			System.out.println(e.getString("address"));
    			nameCurrentLocation = e.getString("name");
    			tvInfo.setText(e.getString("address")+" "+e.getString("description"));
			
			}
			showInfo();
		}
	
/*	private void readLocations() throws XmlPullParserException, IOException {
		
		try{
			String url = "http://www.dondereciclar.com/api_locations.php?latitude1=" + topLeft.getLatitude() 
									+ "&latitude2=" + bottomRight.getLatitude() 
									+ "&longitude1=" + bottomRight.getLongitude() 
									+ "&longitude2=" + topLeft.getLongitude()
									+ "&page=" + page
									+ "&categories=" + groups2str();
			if (lastLocation!=null) {
				url += "&current_latitude=" + lastLocation.getLatitude() 
						+ "&current_longitude=" + lastLocation.getLongitude();
			}
			if (!isAllPoints) {
				url += "&user_id=" + Utils.idUser(this);
			}
			HttpGet request = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(request);
			String xml = EntityUtils.toString(httpResponse.getEntity());
			try {
				
				mMapView.getManager().clear();
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser x = factory.newPullParser();
				
				x.setInput( new StringReader ( xml ) );
						
				int eventType = x.getEventType();
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	
		        	if ( eventType == XmlPullParser.START_TAG ) {
		        		if (x.getName().equals("locations")) {
		        			total = Integer.parseInt(x.getAttributeValue(null, "total"));
		        			pages = total/POI_BYPAGE;
		        			if (total%POI_BYPAGE!=0) pages++;
		        		}
		        		
		        		if (x.getName().equals("location")) {
		        			Location loc = new Location(LocationManager.GPS_PROVIDER);
		        			loc.setLatitude(Double.parseDouble(x.getAttributeValue(null, "latitude")));
		        			loc.setLongitude(Double.parseDouble(x.getAttributeValue(null, "longitude")));
		        			MapLocation ml = new MapLocation(mMapView, loc, Integer.parseInt(x.getAttributeValue(null, "group")));
		        			ml.setId(Integer.parseInt(x.getAttributeValue(null, "id")));
		        			mMapView.getManager().addMapLocation(ml);
		        		}
		        	}
		        	
		        	if ( eventType == XmlPullParser.END_TAG ) {
		        		
		        	}
		        	
		        	eventType = x.next();
		        }				
				
			} catch (Exception e) {
				
			}
			
	        tvFound.setText(total + " " + this.getString(R.string.found));
	        if (pages<=0) {
	        	tvPages.setText(this.getString(R.string.no_points));
	        } else {
	        	tvPages.setText(this.getString(R.string.page) + " " + (page+1) + "/" + pages);
	        }
	        mMapView.refresh();
		} catch(ClientProtocolException e){
			
		} catch(IOException e){
			
		} 
		
	}*/
	public void nearestLocation() throws XmlPullParserException, IOException{
		Location nearLoc = null;
		float minDistance= Float.MAX_VALUE;
		float mDistance= Float.MIN_VALUE;
		Entity entNear =null;
		if (idCurrentLocation!=-1){
			
			mDistance = actualDistance;
		}
			places  = DataFramework.getInstance().getEntityList("tbl_places","guide_id=" + idGuide);
			try {
				
				Iterator<Entity> iter = places.iterator();
				int posicion=1;
				//borramos antes de mostrar los puntos
	
				
	    		while (iter.hasNext()){
	    			Entity ent = (Entity)iter.next();
	    			Location loc = new Location(LocationManager.GPS_PROVIDER);
	    			loc.setLatitude(ent.getDouble("latitude"));
	    			loc.setLongitude(ent.getDouble("longitude"));
	    			Location lastLocation2 = lastLocation;
					if ((loc.distanceTo(lastLocation2)<minDistance)&&(mDistance<loc.distanceTo(lastLocation2))){
	    				minDistance = loc.distanceTo(lastLocation2);
						nearLoc=loc;
	    				entNear = ent;
	    			}
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (nearLoc!=null){
				MapLocation ml = new MapLocation(mMapView, nearLoc,entNear.getInt("type_id"),entNear.getString("name"));
				ml.setId(entNear.getId());
				mMapView.getManager().addMapLocation(ml);
				readLocation(ml.getId());
				//Ampliamos mapa para que se vean ambos puntos
				moveToLocationZoom(nearLoc);
				ml.setTitle(nameCurrentLocation.toUpperCase()+" a " + Utils.formatDistance(this, ml.getLocation().distanceTo(lastLocation)));
				actualDistance = ml.getLocation().distanceTo(lastLocation);
				mMapView.getManager().setHitMapLocation(mMapView, ml);
			}else{
				//Se ha llegado al punto más lejano
				Utils.showMessage(this, this.getString(R.string.lastLocation));
				actualDistance=-1;
			}
	}
	
	
	
	
	
	
	
	
	
	private void readLocations()throws XmlPullParserException, IOException{
		//Leemos el xml de los propios recursos de la bbdd
		try {
		    DataFramework.getInstance().open(this, "com.tfc.misguias");
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		//List<Entity> categories = DataFramework.getInstance().getEntityList("personal", "categoria_id = 3", "fecha asc");
		places  = DataFramework.getInstance().getEntityList("tbl_places","guide_id=" + idGuide);
		try {
			
			Iterator<Entity> iter = places.iterator();
			int posicion=1;
			//borramos antes de mostrar los puntos
			mMapView.getManager().clear();
			
    		while (iter.hasNext()){
    			Entity ent = (Entity)iter.next();
    			System.out.println(ent.getString("name"));
    			System.out.println(ent.getString("longitude"));
    			System.out.println(ent.getString("latitude"));
    			System.out.println(ent.getString("description"));
    			System.out.println(ent.getString("address"));
    			System.out.println("tipo: "+ent.getString("type_id"));
    			System.out.println(ent.getId());
    			Location loc = new Location(LocationManager.GPS_PROVIDER);
    			loc.setLatitude(ent.getDouble("latitude"));
    			loc.setLongitude(ent.getDouble("longitude"));
    			//ECS 16/10/11 FALTA SABER QUE ES EL GROUP Y EMULARLO, TAMBIEN EL ID Y YA SE REPRESENTARA
    			//El group será el tipo del punto, el id debe tomarlo del xml de la tabla
    			
    			//Si el punto es el current se pinta en cualquier caso

    			
    			
    			if (page!=0){
    				int top = pages*POI_BYPAGE;
    				if (((posicion>(top-POI_BYPAGE))&&(posicion<top))){
    					MapLocation ml = new MapLocation(mMapView, loc,ent.getInt("type_id"),ent.getString("name"));
    					ml.setId(ent.getId());
    					mMapView.getManager().addMapLocation(ml);   					

    				}else{
    					
    				}
    			}
    			else{
    				if ((posicion<POI_BYPAGE)){
    					MapLocation ml = new MapLocation(mMapView, loc,ent.getInt("type_id"),ent.getString("name"));
    					ml.setId(ent.getId());
    					mMapView.getManager().addMapLocation(ml);
    				}
    			}
    			//Muestra en el caso de ser un acceso directo a punto
    			if ((ent.getId()==idCurrentLocation)&&(fromInfo==0)){
					MapLocation ml = new MapLocation(mMapView, loc,ent.getInt("type_id"),ent.getString("name"));
					ml.setId(ent.getId());
					mMapView.getManager().addMapLocation(ml);
					readLocation(ml.getId());
					if (lastLocation!=null){
						ml.setTitle(nameCurrentLocation.toUpperCase()+" a " + Utils.formatDistance(this, ml.getLocation().distanceTo(lastLocation)));
						actualDistance = ml.getLocation().distanceTo(lastLocation);
					}else{
						ml.setTitle(nameCurrentLocation.toUpperCase());
					}
					mMapView.getManager().setHitMapLocation(mMapView, ml);
    			}
    			posicion++;
    			//Contamos el numero de puntos
    			
    		}
    		total=places.size();
			pages = total/POI_BYPAGE;
        	if (total%POI_BYPAGE!=0) pages++;
        	
			
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        tvFound.setText(total + " " + this.getString(R.string.found));
        if (pages<=0) {
        	tvPages.setText(this.getString(R.string.no_points));
        } else {
        	tvPages.setText(this.getString(R.string.page) + " " + (page+1) + "/" + pages);
        }
        mMapView.refresh();
	
	}
	
	@Override
	public void OnMapChanged(Location topLeft, Location bottomRight) {
		page = 0;
		this.bottomRight = bottomRight;
		this.topLeft = topLeft;

		try {
			readLocations();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mMapView.getManager().getSelectedMapLocation()==null) {
				back();
			} else {
				showMenu();
				mMapView.refresh();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

    
    public void back() {
    	setResult(RESULT_OK);
        finish();
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode){
    	case ACTIVITY_NEWLOCATION:
    		cancelNewPoint();
			try {
				readLocations();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		mMapView.refresh();
    		break;
    	case ACTIVITY_LOCATION:
    		if (resultCode == RESULT_OK) {
    			showMenu();
    			try {
					readLocations();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			mMapView.refresh();
    		}
    		break;
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();

		stopService();
	}

	public int getOwnList() {
		return ownList;
	}

	public void setOwnList(int oList) {
		ownList = oList;
	}
	
	
    
}