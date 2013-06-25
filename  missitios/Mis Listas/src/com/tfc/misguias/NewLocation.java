package com.tfc.misguias;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.tfc.misguias.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

public class NewLocation extends Activity {

	private static final int DIALOG_PHOTO = 0;
	
	private static final int ACTIVITY_SELECTIMAGE = 0;
	
	private static final int HEIGHT_IMAGE_INFORMATION_SMALL = 80;
	
	private ThreadUploadLocation threadUpload;
	
	private String mImageUpload = "";
	private boolean hasImageUpload = false;
	
	private Location mLocation = null;
	private String mAddress = "";
	private String mName = "";
	private TextView tvAddress;
	private EditText tName, tCommentAddress, tDescription, tContact, tMoreInfo;
	private ImageView photo;
	private Spinner spType;
	private RatingBar rbPlace;
	
	private double mLatitude, mLongitude;
	
	private ProgressDialog pd = null;
	
	//ECS
	private long idGuide = -1;
	private List <Entity> types;

	
	private int id = -1;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
			case DIALOG_PHOTO:
			    return new AlertDialog.Builder(this)
			        .setTitle(R.string.select_action)
			        .setItems(R.array.select_action_photo, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
		                    if (which==0) {
								Intent intent = new Intent();
								intent.setAction(android.content.Intent.ACTION_VIEW);
								File file = new File(mImageUpload);
								intent.setDataAndType(Uri.fromFile(file), "image/*");
								startActivity(intent); 
			                } else if (which==1) {
			                	delImageUpload();
			                }
			            }
			        })
			        .create();
        }
        return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);
		//Leemos el xml de los propios recursos de la bbdd
		try {
		    DataFramework.getInstance().open(this, "com.tfc.misguias");
		} catch (Exception e) {
		    e.printStackTrace();
		}
        mLocation = null;
        mLatitude = 0;
        mLongitude = 0;
        id = -1;
        
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("id")) id = savedInstanceState.getInt("id");
			//ECS Getting the idGuide 
			if (savedInstanceState.containsKey("idGuide")) idGuide = savedInstanceState.getLong("idGuide");
			if (savedInstanceState.containsKey("location")) {
				mLocation = (Location) savedInstanceState.get("location");
				mLatitude = mLocation.getLatitude();
				mLongitude = mLocation.getLongitude();
			}
			if (savedInstanceState.containsKey("address")) mAddress = savedInstanceState.getString("address");
			if (savedInstanceState.containsKey("latitude")) mLatitude = savedInstanceState.getDouble("latitude");
			if (savedInstanceState.containsKey("longitude")) mLongitude = savedInstanceState.getDouble("longitude");
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				id = (extras.containsKey("id")) ? extras.getInt("id") : -1;
				idGuide = (extras.containsKey("idGuide")) ? extras.getLong("idGuide") : -1;
				if (extras.containsKey("location")) {
					mLocation = (Location)extras.get("location");
					mLatitude = mLocation.getLatitude();
					mLongitude = mLocation.getLongitude();
				}
				mAddress = (extras.containsKey("address")) ? extras.getString("address") : "";
				if (extras.containsKey("latitude")) mLatitude = extras.getDouble("latitude");
				if (extras.containsKey("longitude")) mLongitude = extras.getDouble("longitude");
			}
		}
		//ECS New field: name
		tName = (EditText)this.findViewById(R.id.text_name);
		tvAddress = (TextView) this.findViewById(R.id.text_address);		
		tCommentAddress = (EditText) this.findViewById(R.id.text_commentaddress);
		tDescription = (EditText) this.findViewById(R.id.text_description);
		tContact = (EditText) this.findViewById(R.id.text_contact);
		tMoreInfo = (EditText) this.findViewById(R.id.text_more_info);
		spType = (Spinner) this.findViewById(R.id.type);
		rbPlace = (RatingBar) this.findViewById(R.id.ratingBarPlace);
		//ECS Spinner formed by the table_types
		/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.select_type_points, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);*/
		types = DataFramework.getInstance().getEntityList("tbl_types");
		ArrayAdapter<Entity> adapter = new ArrayAdapter<Entity>(this, android.R.layout.simple_spinner_dropdown_item, types);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(adapter);
		
		
		photo = (ImageView) this.findViewById(R.id.photo);
		
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasImageUpload) showDialog(DIALOG_PHOTO);
			}
			
		});
        
		tvAddress.setText(mAddress);
		
		ImageView btnSave = (ImageView) this.findViewById(R.id.ico_save);
		
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
			}
			
		});
		
		ImageView btnPhoto = (ImageView) this.findViewById(R.id.ico_photo);
		
		btnPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadPhoto();
			}
			
		});
		
		ImageView btnCancel = (ImageView) this.findViewById(R.id.ico_cancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
			
		});
		
		populateFields();
				
    }
    
    private void save() {
        pd = new ProgressDialog(this);
        pd.setIcon(R.drawable.alert_dialog_icon);
        pd.setTitle(R.string.alert_dialog_upload);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(3);
        pd.setCancelable(false);
        pd.setMessage(getResources().getString(R.string.dialog_upload_preparing));
        pd.show();
        //sustituiremos el threadUpLocation por una insercion en la bbdd
        threadUpload = new ThreadUploadLocation(this, id);
        threadUpload.setIdList(idGuide);
        threadUpload.setLatitude(mLatitude);
        threadUpload.setLongitude(mLongitude);
        threadUpload.setAddress(mAddress);
        threadUpload.setName(tName.getText().toString());
        threadUpload.setCommentAddress(tCommentAddress.getText().toString());
        threadUpload.setDescription(tDescription.getText().toString());
        threadUpload.setContact(tContact.getText().toString());
        threadUpload.setMoreInfo(tMoreInfo.getText().toString());
        threadUpload.setType(spType.getSelectedItemPosition()+1);        
        threadUpload.setPuntuation(rbPlace.getRating());
    	Thread thread = new Thread(threadUpload);
		thread.start();    
        
    }
    
    public void endSave(boolean correctly) {
    	pd.dismiss();
    	pd = null;
    	if (correctly) {
    		Utils.showMessage(this, getResources().getString(R.string.upload_end));
    		cancel();
    	} else {
    		Utils.showMessage(this, getResources().getString(R.string.upload_end_error));
    	}
    }
    
    public void writeInProgressDialog(String msg) {
    	if (pd!=null) pd.setMessage(msg);
    }
    
    public void incrementProgress() {
    	if (pd!=null) pd.incrementProgressBy(1);
    }
    
    private void uploadPhoto() {
    	Utils.showMessage(this, getString(R.string.take_photo));
    	Intent i = new Intent(Intent.ACTION_PICK) ;
    	i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
    					 MediaStore.Images.Media.CONTENT_TYPE);
    	startActivityForResult(i, ACTIVITY_SELECTIMAGE);
    }

	private void cancel() {
    	setResult(RESULT_OK);
        finish();
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putDouble("longitude", mLongitude);
        outState.putDouble("latitude", mLatitude);
        outState.putString("address", mAddress);
        
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
               
		switch (requestCode){
    	case ACTIVITY_SELECTIMAGE:
    		if( resultCode != 0 ) {
	    		Cursor c = managedQuery(intent.getData(),null,null,null,null);
	    		if( c.moveToFirst() ) {
	    			setImageUpload(c.getString(1));
	    		}
	    		c.close();
    		}
    		break;
        }
    }

	public String getImageUpload() {
		return mImageUpload;
	}
	
	public void delImageUpload() {
		mImageUpload = "";
		hasImageUpload = false;
		photo.setImageBitmap(null);
		photo.setVisibility(View.GONE);
	}
	
	public void setImageUpload(String file) {
		mImageUpload = file;
		hasImageUpload = true;
		File f = new File(file);
		if (f.exists()) {

			Options opt = new Options();
			opt.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(file, opt);
			int height = HEIGHT_IMAGE_INFORMATION_SMALL;
			int width = (HEIGHT_IMAGE_INFORMATION_SMALL * bm.getWidth()) / bm.getHeight();
			
			Bitmap bm2;
						
			if (width>height) {
				width = HEIGHT_IMAGE_INFORMATION_SMALL;
				height = (HEIGHT_IMAGE_INFORMATION_SMALL * bm.getHeight()) / bm.getWidth();
				bm2 = Bitmap.createScaledBitmap(bm, width, height, false);
			} else {
				bm2 = Bitmap.createScaledBitmap(bm, width, height, false);
			}
			
			photo.setImageBitmap(bm2);
			photo.setVisibility(View.VISIBLE);

		}
		
	}
	
	public void setImageHttpUpload(String file) {
		mImageUpload = "";
		hasImageUpload = false;
		
		String httpImage = "http://www.dondereciclar.com/photos/"+file;
		try {
			URL urlImage = new URL(httpImage);
			URLConnection conn;
			conn = urlImage.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Options opt = new Options();
			opt.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeStream(bis, null, opt);
			bis.close();
			is.close();
			
			int height = HEIGHT_IMAGE_INFORMATION_SMALL;
			int width = (HEIGHT_IMAGE_INFORMATION_SMALL * bm.getWidth()) / bm.getHeight();
			Bitmap bmpImage = null;
			if (width>height) {
				width = HEIGHT_IMAGE_INFORMATION_SMALL;
				height = (HEIGHT_IMAGE_INFORMATION_SMALL * bm.getHeight()) / bm.getWidth();
				bmpImage = Bitmap.createScaledBitmap(bm, width, height, false);
			} else {
				bmpImage = Bitmap.createScaledBitmap(bm, width, height, false);
			}
			
			photo.setImageBitmap(bmpImage);
			photo.setVisibility(View.VISIBLE);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	public void populateFields() {
		if (id>0) {
			try{
				String url = "http://www.dondereciclar.com/api_edit_location.php?id=" + id;
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
			        			mLatitude = Double.parseDouble(x.getAttributeValue(null, "latitude"));
			        			mLongitude = Double.parseDouble(x.getAttributeValue(null, "longitude"));
			        			spType.setSelection(Integer.parseInt(x.getAttributeValue(null, "group"))-1);
			        		}
			        		
			        		if (x.getName().equals("description")) {
			        			tDescription.setText(x.nextText());
			        		}
			        		
			        		if (x.getName().equals("address")) {
			        			mAddress = x.nextText();
			        			tvAddress.setText(mAddress);
			        		}
			        		
			        		if (x.getName().equals("comment-address")) {
			        			tCommentAddress.setText(x.nextText());
			        		}
			        		
			        		if (x.getName().equals("contact")) {
			        			tContact.setText(x.nextText());
			        		}
			        		
			        		if (x.getName().equals("more-details")) {
			        			tMoreInfo.setText(x.nextText());
			        		}
			        		
			        		if (x.getName().equals("image")) {
			        			this.setImageHttpUpload(x.nextText());
			        		}
			        					        		
			        	}
			        	
			        	if ( eventType == XmlPullParser.END_TAG ) {
			        		
			        	}
			        	
			        	eventType = x.next();
			        }				
					
				} catch (Exception e) {
					
				}
				
			} catch(ClientProtocolException e){
				
			} catch(IOException e){
				
			} 
		}
	}
	
	public Location getLocation() {
		return mLocation;
	}

	public boolean hasImageUpload() {
		return hasImageUpload;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
	
}
