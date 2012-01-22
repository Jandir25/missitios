package com.geekool.dondereciclar;

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
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.javielinux.BubblesMap.view.MapLocation;
import com.javielinux.BubblesMap.view.MapLocationViewer;
import com.javielinux.BubblesMap.view.MapLocationsManager;
import com.javielinux.BubblesMap.view.OnMapLocationClickListener;

public class MisListas extends MapActivity implements OnMapLocationClickListener {
	
	private static final int ACTIVITY_LOCATION = 0;
	private static final int ACTIVITY_NEWLOCATION = 1;
	
	private MapLocationViewer mMapView;
    private MapController mMapController;
    
    private boolean isTraffic = true;
    
    private boolean isAllPoints = true;
    
    private static final int POI_BYPAGE = 15;
    
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
    
    private int idCurrentLocation = -1;
    
    private int page = 0;
    private int total = 0;
    private int pages = 0;
    
    private Location topLeft, bottomRight;
    
    public long idList;
    
    private TextView tvFound, tvPages, tvInfo;
    private ImageView ivIcoInfo; 
    private LinearLayout llMenu, llInfo, llButtonsNewPoint, llPagination; 
    
    private int groups[] = {1, 0, 0, 0, 0};
    
    private ImageView btnGroup1, btnGroup2, btnGroup3, btnGroup4, btnGroup5, btnMore;
    
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
    	llMenu.setVisibility(View.VISIBLE);
    	llInfo.setVisibility(View.GONE);
    	
    	llButtonsNewPoint.setVisibility(View.GONE);
    	llPagination.setVisibility(View.VISIBLE);
    }
    
    public void showInfo() {
    	llMenu.setVisibility(View.GONE);
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
    	Geocoder gc = new Geocoder(MisListas.this);
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
		            		Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.no_search));
		            	}
		            }
		        })
		        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            }
		        })
		       .create();
        case DIALOG_UPLOAD:
            return new AlertDialog.Builder(MisListas.this)
                .setTitle(R.string.type_location)
                .setItems(R.array.select_type_location, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {//no reconoce la localizacion en el simulador, probar desde android
                        	if (!ServiceGPS.isRunning()) {
                        		startService();
                        		Utils.showShortMessage(MisListas.this, getString(R.string.search_gps));
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
        if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(DataFramework.KEY_ID)) idList = savedInstanceState.getLong(DataFramework.KEY_ID);
			
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				idList = (extras.containsKey(DataFramework.KEY_ID)) ? extras.getLong(DataFramework.KEY_ID) : -1;
				
			} else {
				idList = -1;
				
			}
		}
        
        
        //Comprobamos el tipo de conexión a internet y si es demasiada lenta le mostramos un aviso
        if ( Utils.isConnectionSlow(MisListas.this) ) {
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
		llMenu = (LinearLayout) this.findViewById(R.id.ll_menu);
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
		
		btnGroup1 = (ImageView) this.findViewById(R.id.group1);
		
		btnGroup1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (groups[0]==0) {
					groups[0]=1;
					btnGroup1.setImageResource(R.drawable.ico_1_on);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.punto_limpio) + " activado");
				} else {
					groups[0]=0;
					btnGroup1.setImageResource(R.drawable.ico_1_off);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.punto_limpio) + " desactivado");
				}
				page = 0;
				try {
					readLocations();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		btnGroup2 = (ImageView) this.findViewById(R.id.group2);
		
		btnGroup2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (groups[1]==0) {
					groups[1]=2;
					btnGroup2.setImageResource(R.drawable.ico_2_on);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.pilas) + " activado");
				} else {
					groups[1]=0;
					btnGroup2.setImageResource(R.drawable.ico_2_off);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.pilas) + " desactivado");
				}
				page = 0;
				try {
					readLocations();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		btnGroup3 = (ImageView) this.findViewById(R.id.group3);
		
		btnGroup3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (groups[2]==0) {
					groups[2]=3;
					btnGroup3.setImageResource(R.drawable.ico_3_on);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.denuncia) + " activado");
				} else {
					groups[2]=0;
					btnGroup3.setImageResource(R.drawable.ico_3_off);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.denuncia) + " desactivado");
				}
				page = 0;
				try {
					readLocations();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		btnGroup4 = (ImageView) this.findViewById(R.id.group4);
		
		btnGroup4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (groups[3]==0) {
					groups[3]=4;
					btnGroup4.setImageResource(R.drawable.ico_4_on);	
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.ropa) + " activado");
				} else {
					groups[3]=0;
					btnGroup4.setImageResource(R.drawable.ico_4_off);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.ropa) + " desactivado");
				}
				page = 0;
				try {
					readLocations();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		btnGroup5 = (ImageView) this.findViewById(R.id.group5);
		
		btnGroup5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (groups[4]==0) {
					groups[4]=5;
					btnGroup5.setImageResource(R.drawable.ico_5_on);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.otro) + " activado");
				} else {
					groups[4]=0;
					btnGroup5.setImageResource(R.drawable.ico_5_off);
					Utils.showMessage(MisListas.this, MisListas.this.getString(R.string.otro) + " desactivado");
				}
				page = 0;
				try {
					readLocations();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		btnMore = (ImageView) this.findViewById(R.id.btn_more);
		
		llInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMapView.getManager().getTypeMarks() == MapLocationsManager.TYPE_MARKS_LOCATIONS) {
					Intent i = new Intent(MisListas.this, InfoLocation.class);
					i.putExtra("id", idCurrentLocation);
					i.putExtra("locationGPS", lastLocation);
			        startActivityForResult(i, ACTIVITY_LOCATION);
				} else {
					Intent i = new Intent(MisListas.this, NewLocation.class);
					Location loc = mMapView.getManager().getNewLocation();
					//ECS Included the list id
					i.putExtra("address", getAddress(loc));
					i.putExtra("location", loc);
					startActivityForResult(i, ACTIVITY_NEWLOCATION);
				}
			}
			
		});
		
		TextView tvAcceptNewPoint = (TextView) this.findViewById(R.id.btn_accept_new_point);
		
		tvAcceptNewPoint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToNewLocationInNextLocation = false;
				Intent i = new Intent(MisListas.this, NewLocation.class);
				Location loc = mMapView.getManager().getNewLocation();
				//ECS 10/12/2011
				i.putExtra("idList", idList);//Sending also the list id FALLANDO 10/12
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
			btnGroup1.setImageResource(R.drawable.ico_1_on);
		} else {
			groups[0] = 0;
			btnGroup1.setImageResource(R.drawable.ico_1_off);
		}		
		
		if ( preference.getBoolean("checkbox_Pilas", false) == true ) {
			groups[1] = 2;	
			btnGroup2.setImageResource(R.drawable.ico_2_on);
		} else {
			groups[1] = 0;
			btnGroup2.setImageResource(R.drawable.ico_2_off);
		}
		
		if ( preference.getBoolean("checkbox_DenunciaAmbiental", false) == true ) {
			groups[2] = 3;	
			btnGroup3.setImageResource(R.drawable.ico_3_on);
		} else {
			groups[2] = 0;
			btnGroup3.setImageResource(R.drawable.ico_3_off);
		}
		
		if ( preference.getBoolean("checkbox_Ropa", false) == true ) {
			groups[3] = 4;	
			btnGroup4.setImageResource(R.drawable.ico_4_on);
		} else {
			groups[3] = 0;
			btnGroup4.setImageResource(R.drawable.ico_4_off);
		}
		
		if ( preference.getBoolean("checkbox_Otros", false) == true ) {
			groups[4] = 5;	
			btnGroup5.setImageResource(R.drawable.ico_5_on);
		} else {
			groups[4] = 0;
			btnGroup5.setImageResource(R.drawable.ico_5_off);
		}
		
		//Si estan desactivadas todas las categorias, como mï¿½nimo mostramos la de los puntos limpios
		if ( (groups[0] == 0) && (groups[1] == 0) && (groups[2] == 0) && (groups[3] == 0) && (groups[4] == 0) ) {
			groups[0] = 1;	
			btnGroup1.setImageResource(R.drawable.ico_1_on);
		}
		
		setPositionInMap();
		
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
	    		mMapController.setZoom(15);
	    		topLeft = mMapView.getManager().getLocationTopLeft();
		        bottomRight = mMapView.getManager().getLocationBottomRight();
		        newLocation(location);
	    		moveToLocation(location);
			} else {
				mMapView.getController().setCenter(new GeoPoint((int)(40.407*1E6), (int)(-3.68*1E6)));
	    		mMapController.setZoom(6);
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
    	mMapView.getController().setCenter(Utils.Location2Geopoint(loc));
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
			if (lastLocation!=null) {
				mapLocation.setTitle("a " + Utils.formatDistance(this, mapLocation.getLocation().distanceTo(lastLocation)));
			}
			if (mapLocation.getType() != MapLocation.TYPE_CURRENTPOSITION) {
				try {
					readLocation(mapLocation.getId());
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void readLocation(int id) throws XmlPullParserException, IOException {
		idCurrentLocation = id;
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
	        					"com.geekool.dondereciclar:drawable/ico_"+x.getAttributeValue(null, "group_id")+"_on", null, null);
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
	private void readLocations()throws XmlPullParserException, IOException{
		//Leemos el xml de los propios recursos de la bbdd
		try {
		    DataFramework.getInstance().open(this, "com.geekool.dondereciclar");
		} catch (Exception e) {
		    e.printStackTrace();
		}

		
		
		
		
		
		
		//List<Entity> categories = DataFramework.getInstance().getEntityList("personal", "categoria_id = 3", "fecha asc");
		List <Entity> places  = DataFramework.getInstance().getEntityList("tbl_places","list_id=" + idList);
		try {
			
			Iterator iter = places.iterator();
    		while (iter.hasNext()){
    			Entity ent = (Entity)iter.next();
    			System.out.println(ent.getString("name"));
    			System.out.println(ent.getString("longitude"));
    			System.out.println(ent.getString("latitude"));
    			Location loc = new Location(LocationManager.GPS_PROVIDER);
    			loc.setLatitude(ent.getDouble("latitude"));
    			loc.setLongitude(ent.getDouble("longitude"));
    			//ECS 16/10/11 FALTA SABER QUE ES EL GROUP Y EMULARLO, TAMBIEN EL ID Y YA SE REPRESENTARA
    			//El group será el tipo del punto, el id debe tomarlo del xml de la tabla
    			MapLocation ml = new MapLocation(mMapView, loc,1);
    			ml.setId(1);
    			mMapView.getManager().addMapLocation(ml);
    		}
			
			
			//mMapView.getManager().clear();
			

			
		} catch (Exception e) {
			
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
	
	
    
}