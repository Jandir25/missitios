package com.tfc.misguias;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class InfoLocation extends ListActivity {
	
	private static final int ACTIVITY_NEWLOCATION = 1;
	
	private static final int DIALOG_NEW_COMMENT = 0;
	private static final int DIALOG_USER = 1;
	private static final int DIALOG_DELETE = 2;
	
	private static final int HEIGHT_IMAGE = 250;
	
	private int id = -1;
	private long idSelected = -1;
	private int ownList = -1;
	private String httpImage = "";
	private Location mLocationGPS = null;
	private double mLatitudeGPS, mLongitudeGPS;
	private String mLatitude = "0", mLongitude = "0";
	
	private TextView locTitle;
	private ImageView icoLoc,icoGoMap;
	private int fromMap	=	-1;
	
	private EditText mComment;
	
	private int mUserId = -1;
	
	private LinearLayout llMoreAction = null;
	
	private RowInfoLocationAdapter adapter;
	
	private ListActivity gListActivity;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_NEW_COMMENT:
        	mComment = new EditText(this);
        	mComment.setPadding(10, 10, 10, 10);
            return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.alert_dialog_comment)
                .setView(mComment)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	try {
							saveComment(mComment.getText().toString());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
		case DIALOG_USER:
		    return new AlertDialog.Builder(this)
		        .setTitle(R.string.select_action)
		        .setItems(R.array.select_action_user_point, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
	                    if (which==0) {
	    					Intent i = new Intent(InfoLocation.this, NewLocation.class);
	    					i.putExtra("id", getId());
	    					startActivityForResult(i, ACTIVITY_NEWLOCATION);
		                } else if (which==1) {
		                	showDialog(DIALOG_DELETE);
		                }
		            }
		        })
		        .create();
        case DIALOG_DELETE:
            return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.title_question_delete)
                .setMessage(R.string.question_delete)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	try {
							deletePOI();
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
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

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.location);
        
        mLocationGPS = null;
        mLatitudeGPS = 0;
        mLongitudeGPS = 0;
        
        gListActivity = this;
        
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("id")) id = savedInstanceState.getInt("id");
			if (savedInstanceState.containsKey("locationGPS")) {
				mLocationGPS = (Location) savedInstanceState.get("location");
				
				if (mLocationGPS!=null) {
					mLatitudeGPS = mLocationGPS.getLatitude();
					mLongitudeGPS = mLocationGPS.getLongitude();
				}
			}
			if (savedInstanceState.containsKey("latitudeGPS")) mLatitudeGPS = savedInstanceState.getDouble("latitudeGPS");
			if (savedInstanceState.containsKey("ownList")) ownList = savedInstanceState.getInt("ownList");
			if (savedInstanceState.containsKey("idSelected")) idSelected = savedInstanceState.getLong("idSelected");
			if (savedInstanceState.containsKey("longitudeGPS")) mLongitudeGPS = savedInstanceState.getDouble("longitudeGPS");
			if (savedInstanceState.containsKey("fromMap")) fromMap = savedInstanceState.getInt("fromMap");
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				if (extras.containsKey("locationGPS")) {
					mLocationGPS = (Location)extras.get("locationGPS");
					
					if (mLocationGPS!=null) {
						mLatitudeGPS = mLocationGPS.getLatitude();
						mLongitudeGPS = mLocationGPS.getLongitude();
					}
				}
				
				id = (extras.containsKey("id")) ? extras.getInt("id") : -1;
				idSelected = (extras.containsKey("idSelected")) ? extras.getLong("idSelected") : -1;
				ownList = (extras.containsKey("ownList")) ? extras.getInt("ownList") : -1;
				if (extras.containsKey("fromMap")) fromMap = extras.getInt("fromMap");
			    if (extras.containsKey("latitudeGPS")) mLatitudeGPS = extras.getDouble("latitudeGPS");
				if (extras.containsKey("longitudeGPS")) mLongitudeGPS = extras.getDouble("longitudeGPS");
			} else {
				id = -1;
			}
		}
		
		locTitle = (TextView) this.findViewById(R.id.loc_title);
		
		icoLoc = (ImageView) this.findViewById(R.id.ico_loc);
		
		icoGoMap = (ImageView) this.findViewById(R.id.btn_goMap);
				
		llMoreAction = (LinearLayout) this.findViewById(R.id.ll_more_actions);
		
		fillListView();
		
		ImageView icoGoto = (ImageView)findViewById(R.id.ico_goto);
		icoGoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Utils.getGPSStatus(InfoLocation.this)) {
					if ( (mLatitudeGPS!=0) && (mLongitudeGPS!=0) ) {
						Uri uri = Uri.parse("http://maps.google.com/maps?saddr=" + mLatitudeGPS + "," + mLongitudeGPS + "&daddr=" + mLatitude + "," + mLongitude + "");
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
						startActivity(intent);
					} else {
						Utils.showShortMessage(InfoLocation.this, InfoLocation.this.getString(R.string.no_gps));						
					}
		    	} else {
		    		Utils.showShortMessage(InfoLocation.this, InfoLocation.this.getString(R.string.no_gps));
		    	}
			}
			
		});
		
		
		
		ImageView icoGoMap = (ImageView)findViewById(R.id.btn_goMap);
		
		icoGoMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Recuperamos clase singleton
				SingletonDatosLista sgDatosLista = SingletonDatosLista.getInstance();
				sgDatosLista.setIdSelected(idSelected);
				goToPlace(mLatitude,mLongitude);
			}
			
		});
		
		ImageView icoShare = (ImageView)findViewById(R.id.ico_share);
		icoShare.setOnClickListener(OnClickListener_IcoShare);
		
		ImageView icoMoreActions = (ImageView) this.findViewById(R.id.ico_more_actions);
		
		icoMoreActions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_USER);
			}
			
		});
		
    }
    
    private void goToPlace(String mLatitude,String mLongitude) {
    	if (mLatitude != null) {
    	    if (fromMap==0){
    	    	finish();
    	    }else{
    	    	Intent iOpenMap = new Intent(this, GuideTab.class);
    	    	iOpenMap.putExtra("tabSelected", 1);    	    
    	    	iOpenMap.putExtra("selectedPlace", idSelected);
    	    	startActivity(iOpenMap);
    	    }
    	}
    }
    
    
    private OnClickListener OnClickListener_IcoShare = new OnClickListener()
    {	      	
		public void onClick(View v)
        { 		
			final Intent SendIntent = new Intent(Intent.ACTION_SEND); 			  
			SendIntent.putExtra(Intent.EXTRA_SUBJECT, gListActivity.getString(R.string.subject_share)); 
			SendIntent.putExtra(Intent.EXTRA_TEXT, gListActivity.getString(R.string.text_share) + " http://www.dondereciclar.com/index.php?id=" + id);
			SendIntent.setType("text/plain");
			startActivity(Intent.createChooser(SendIntent, "GuideMap"));
        }
    };
    
    private void fillListView() {
		adapter = new RowInfoLocationAdapter(this,idSelected,ownList);
		adapter.createListView();
		setListAdapter(adapter);
    }
    
	public int readLocation() throws XmlPullParserException, IOException {
		
		List<Map<String,?>> infoLocation = new LinkedList<Map<String,?>>();
		Bitmap bmpImage = null;
		if (ownList != -1){
			if (id!=-1){
				idSelected = (long)id;//Para el acceso desde la lista de lugares
			}
			Entity e = new Entity("tbl_places", idSelected);
			int idIcon = this.getResources().getIdentifier(
					"com.tfc.misguias:drawable/ico_"+e.getInt("type_id")+"_on", null, null);
			icoLoc.setImageResource(idIcon);
			System.out.println(e.getString("name"));
			System.out.println(e.getString("longitude"));
			System.out.println(e.getString("latitude"));
			System.out.println(e.getString("description"));
			System.out.println(e.getString("address"));
			System.out.println(e.getString("comment"));
			/*tvInfo.setText(e.getString("address")+" "+e.getString("description"));*/
			locTitle.setText(e.getString("name"));
			mLatitude = e.getString("latitude");
			mLongitude = e.getString("latitude");
			if ( (mLatitudeGPS!=0) && (mLongitudeGPS!=0) ) {
				Location loc1 = new Location(LocationManager.GPS_PROVIDER);
				loc1.setLatitude(mLatitudeGPS);
				loc1.setLongitude(mLongitudeGPS);
				Location loc2 = new Location(LocationManager.GPS_PROVIDER);
				loc2.setLatitude(Double.parseDouble(mLatitude));
				loc2.setLongitude(Double.parseDouble(mLongitude));
							
				infoLocation.add(RowInfoLocationAdapter.createInfoItem( this.getString(R.string.distance), Utils.formatDistance(this, loc1.distanceTo(loc2) )));
			}
			/*int idGroup = this.getResources().getIdentifier(
					"com.tfc.misguias:drawable/ico_"+x.getAttributeValue(null, "group_id")+"_on", null, null);
			icoLoc.setImageResource(idGroup);*/
			infoLocation.add(RowInfoLocationAdapter.createInfoItem(this.getString(R.string.address), e.getString("address")));
			infoLocation.add(RowInfoLocationAdapter.createInfoItem(this.getString(R.string.description), e.getString("description")));
			infoLocation.add(RowInfoLocationAdapter.createInfoItem(this.getString(R.string.comment), e.getString("comment")));
			adapter.addSection(this.getString(R.string.info_point), new SimpleAdapter(this, infoLocation, R.layout.list_complex,
					new String[] { RowInfoLocationAdapter.INFOITEM_TITLE, RowInfoLocationAdapter.INFOITEM_INFO }, new int[] { R.id.list_complex_title, R.id.list_complex_info }));
			adapter.addSection(this.getString(R.string.comments), new ArrayAdapter<String>(this,
					R.layout.list_item_small, new String[] { this.getString(R.string.no_comments) }));
			return 0;
		}		
		
		else{
				
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
	        				        			
	        			locTitle.setText(x.getAttributeValue(null, "group").toString());
	        			mLatitude = x.getAttributeValue(null, "latitude");
	        			mLongitude = x.getAttributeValue(null, "longitude");
	        			
	        			mUserId = Integer.parseInt(x.getAttributeValue(null, "user-id"));
	        				        			
	        			int idGroup = this.getResources().getIdentifier(
	        					"com.tfc.misguias:drawable/ico_"+x.getAttributeValue(null, "group_id")+"_on", null, null);
	        			icoLoc.setImageResource(idGroup);
	        		}
	        		if (x.getName().equals("address")) {
	        			infoLocation.add(RowInfoLocationAdapter.createInfoItem(this.getString(R.string.address), x.nextText()));
	        		}
	        		if (x.getName().equals("description")) {
	        			infoLocation.add(RowInfoLocationAdapter.createInfoItem(this.getString(R.string.description), x.nextText()));
	        		}
	        		
	        		if (x.getName().equals("image")) {
						try {
							httpImage = "http://www.dondereciclar.com/photos/"+x.nextText();
							URL urlImage = new URL(httpImage);
							URLConnection conn = urlImage.openConnection();
							conn.connect();
							InputStream is = conn.getInputStream();
							BufferedInputStream bis = new BufferedInputStream(is);
							Options opt = new Options();
							opt.inSampleSize = 2;
							Bitmap bm = BitmapFactory.decodeStream(bis, null, opt);
							bis.close();
							is.close();
							
							int height = HEIGHT_IMAGE;
							int width = (HEIGHT_IMAGE * bm.getWidth()) / bm.getHeight();
							
							if (width>height) {
								width = HEIGHT_IMAGE;
								height = (HEIGHT_IMAGE * bm.getHeight()) / bm.getWidth();
								bmpImage = Bitmap.createScaledBitmap(bm, width, height, false);
							} else {
								bmpImage = Bitmap.createScaledBitmap(bm, width, height, false);
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
	        		}
	        		
	        	}
	        	eventType = x.next();
	        }
			
		} catch (Exception e) {
			
		}
		
		if ( (mLatitudeGPS!=0) && (mLongitudeGPS!=0) ) {
			Location loc1 = new Location(LocationManager.GPS_PROVIDER);
			loc1.setLatitude(mLatitudeGPS);
			loc1.setLongitude(mLongitudeGPS);
			Location loc2 = new Location(LocationManager.GPS_PROVIDER);
			loc2.setLatitude(Double.parseDouble(mLatitude));
			loc2.setLongitude(Double.parseDouble(mLongitude));
						
			infoLocation.add(RowInfoLocationAdapter.createInfoItem( this.getString(R.string.distance), Utils.formatDistance(this, loc1.distanceTo(loc2) )));
		}
		
		if (mUserId==Utils.idUser(this)) {
			llMoreAction.setVisibility(View.VISIBLE);
		}
		
		int lastPosition = infoLocation.size()+1;
		
		adapter.addSection(this.getString(R.string.info_point), new SimpleAdapter(this, infoLocation, R.layout.list_complex,
				new String[] { RowInfoLocationAdapter.INFOITEM_TITLE, RowInfoLocationAdapter.INFOITEM_INFO }, new int[] { R.id.list_complex_title, R.id.list_complex_info }));
		
		if (bmpImage!=null) {
			adapter.addSection(this.getString(R.string.image), new ImageAdapter(this, bmpImage));
			lastPosition += 2;
		}
		
		return lastPosition-1;
		}
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (adapter.positionsButtons.containsKey(position)) {
        	String action = "";
        	for(Object item : adapter.positionsButtons.keySet()) {
        		if (item.equals(position)) {
        			action = adapter.positionsButtons.get(item);
        		}
        	}
        	
        	if (action.equals("publish")) {
        		try {
					if (!Utils.isUser(this).equals("")) {
						showDialog(DIALOG_NEW_COMMENT);
					} else {
						Utils.showMessage(this, getString(R.string.msg_nouser));
					}
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	} else if (action.equals("more_comments")) {
        		Intent i = new Intent(this, Comments.class);
				i.putExtra("id", this.id);
		        startActivity(i);
        	} else if (action.equals("inappropriate")) {
            	Intent msg=new Intent(Intent.ACTION_SEND);
            	
            	String[] recipients={"contacto@dondereciclar.com"};  
            	msg.putExtra(Intent.EXTRA_EMAIL, recipients);
            	
            	String body = getString(R.string.pre_inappropriate_body);
            	body += ": http://www.dondereciclar.com/?id=" + id + "\n\n";
            	body += getString(R.string.post_inappropriate_body);
            	msg.setType("plain/text"); 
           		msg.putExtra(Intent.EXTRA_TEXT, body);  
            	msg.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.inappropriate_subject));            	    	
            	startActivity(Intent.createChooser(msg, "Enviar"));
            	
        	}
        	
        	
        }
        
    }
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putDouble("longitudeGPS", mLongitudeGPS);
        outState.putDouble("latitudeGPS", mLatitudeGPS);
        outState.putInt("id", id);
        
    }
    
	private void saveComment(String comment) throws XmlPullParserException, IOException {
		
		Utils.showMessage(this, this.getString(R.string.upload_comment));
		
		String url = "http://www.dondereciclar.com/api_save_comment.php";
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preference.getString("edittext_username", "");
        String pass = "";
		try {
			pass = Utils.MD5(preference.getString("edittext_pass", ""));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpClient httpclient = new DefaultHttpClient();  
		HttpPost httppost = new HttpPost(url);  
		try {  
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);  
			nameValuePairs.add(new BasicNameValuePair("key", "abcde"));
			nameValuePairs.add(new BasicNameValuePair("nick", name));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			nameValuePairs.add(new BasicNameValuePair("location_id", ""+id));
			nameValuePairs.add(new BasicNameValuePair("comment", comment));  
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
			HttpResponse httpResponse = httpclient.execute(httppost);  
			
			String xml = EntityUtils.toString(httpResponse.getEntity());
			try {
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser x = factory.newPullParser();
				
				x.setInput( new StringReader ( xml ) );
						
				int eventType = x.getEventType();
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	if ( eventType == XmlPullParser.START_TAG ) {
		        		if (x.getName().equals("response")) {
		        			if (x.getAttributeValue(null, "save").equals("true")) {
		        				Utils.showMessage(this, this.getString(R.string.save_comment));
		        				fillListView();
		        			} else {
		        				Utils.showMessage(this, this.getString(R.string.upload_end_error));
		        			}
		        		}
		        		
		        	}
		        	eventType = x.next();
		        }
				
			} catch (Exception e) {
				
			}
			
		} catch (ClientProtocolException e) {  
			
		} catch (IOException e) {
			
		} 
		
	}
	
	private void deletePOI() throws XmlPullParserException, IOException {
				
		String url = "http://www.dondereciclar.com/api_deletepoi.php";
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preference.getString("edittext_username", "");
        String pass = "";
		try {
			pass = Utils.MD5(preference.getString("edittext_pass", ""));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpClient httpclient = new DefaultHttpClient();  
		HttpPost httppost = new HttpPost(url);  
		try {  
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);  
			nameValuePairs.add(new BasicNameValuePair("key", "abcde"));
			nameValuePairs.add(new BasicNameValuePair("nick", name));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			nameValuePairs.add(new BasicNameValuePair("id", ""+id));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
			HttpResponse httpResponse = httpclient.execute(httppost);  
			
			String xml = EntityUtils.toString(httpResponse.getEntity());
			try {
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser x = factory.newPullParser();
				
				x.setInput( new StringReader ( xml ) );
						
				int eventType = x.getEventType();
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	if ( eventType == XmlPullParser.START_TAG ) {
		        		if (x.getName().equals("response")) {
		        			if (x.getAttributeValue(null, "save").equals("true")) {
		        				Utils.showMessage(this, this.getString(R.string.delete_end));
		        				setResult(RESULT_OK);
		        		        finish();
		        			} else {
		        				Utils.showMessage(this, this.getString(R.string.delete_end_error));
		        			}
		        		}
		        		
		        	}
		        	eventType = x.next();
		        }
				
			} catch (Exception e) {
				
			}
			
		} catch (ClientProtocolException e) {  
			
		} catch (IOException e) {
			
		} 
		
	}
	

    public int getId() {
    	return id;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode){
    	case ACTIVITY_NEWLOCATION:
    		fillListView();
    		break;
        }
    }
    
}
