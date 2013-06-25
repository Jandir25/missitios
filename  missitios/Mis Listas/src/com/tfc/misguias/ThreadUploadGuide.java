package com.tfc.misguias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.dataframework.Entity;
import com.tfc.misguias.R;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class ThreadUploadGuide implements Runnable {

	private static final int HEIGHT = 800;
	private NewGuide mActivity;
	private String msgToWrite;
	private String address, name;

	private String title;
	private int id_creator;


	public int getId_creator() {
		return id_creator;
	}

	public void setId_creator(int id_creator) {
		this.id_creator = id_creator;
	}

	private int id_type;
	 
	private String commentAddress, description, contact, moreInfo;
	
	private double latitude, longitude;
	
	private String mPathFilenameTemp = "";
	private String mFilenameTemp = "";
	
	private int id = -1;
	private long idGuide;
	


	/**
     * Constructor - Este Thread exporta a diferentes formatos
     * 
     * @param mActivity Actividad
     * @param idGuide Id Lista
     */
	
	public ThreadUploadGuide(NewGuide mActivity) {
		this.mActivity = mActivity;
		mPathFilenameTemp = "";
		mFilenameTemp = "";
	}
	
	@Override
	public void run() {
	
		//aqui tendrá que situarse el diferenciador de si es una promoción web o no
		try {
			saveLocalDataBase();
			//saveDataBase();
			//saveWeb();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Crea el contenido del archivo XML
     * 
     */
    
    private void upload() {
    	mFilenameTemp = Utils.getNameForImage() + ".jpg";
    	mPathFilenameTemp = "/sdcard/" + mFilenameTemp;
    	
    	setMessage(R.string.resize_image);
    
    	handler.sendEmptyMessage(2);
    	
    	setMessage(R.string.upload_image);
    	uploadFile();
    	handler.sendEmptyMessage(2);

		
    }
    
	private void saveWeb() throws XmlPullParserException, IOException {
		String url = "http://www.dondereciclar.com/api_savepoi.php";		
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mActivity);
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(13);  
			nameValuePairs.add(new BasicNameValuePair("key", "abcde"));
			
			if (id<=0)
				nameValuePairs.add(new BasicNameValuePair("form_location_id", ""));
			else
				nameValuePairs.add(new BasicNameValuePair("form_location_id", id+""));
			
			nameValuePairs.add(new BasicNameValuePair("nick", name));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			nameValuePairs.add(new BasicNameValuePair("form_address_comment", commentAddress));
			nameValuePairs.add(new BasicNameValuePair("form_description", description));  
			nameValuePairs.add(new BasicNameValuePair("form_address", address));  
			nameValuePairs.add(new BasicNameValuePair("form_contact", contact));  
			nameValuePairs.add(new BasicNameValuePair("form_latitud", latitude+""));  
			nameValuePairs.add(new BasicNameValuePair("form_longitud", longitude+""));
			nameValuePairs.add(new BasicNameValuePair("form_group", id_type+""));
			nameValuePairs.add(new BasicNameValuePair("form_more_details", moreInfo));
			nameValuePairs.add(new BasicNameValuePair("image", mFilenameTemp));
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
		        				handler.sendEmptyMessage(1);
		        			} else {
		        				handler.sendEmptyMessage(3);
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
   
	private void saveDataBase() throws XmlPullParserException, IOException {
		String url = "http://www.dondereciclar.com/api_savepoi.php";
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mActivity);
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(13);  
			nameValuePairs.add(new BasicNameValuePair("key", "abcde"));
			
			if (id<=0)
				nameValuePairs.add(new BasicNameValuePair("form_location_id", ""));
			else
				nameValuePairs.add(new BasicNameValuePair("form_location_id", id+""));
			
			nameValuePairs.add(new BasicNameValuePair("nick", name));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			nameValuePairs.add(new BasicNameValuePair("form_address_comment", commentAddress));
			nameValuePairs.add(new BasicNameValuePair("form_description", description));  
			nameValuePairs.add(new BasicNameValuePair("form_address", address));  
			nameValuePairs.add(new BasicNameValuePair("form_contact", contact));  
			nameValuePairs.add(new BasicNameValuePair("form_latitud", latitude+""));  
			nameValuePairs.add(new BasicNameValuePair("form_longitud", longitude+""));
			nameValuePairs.add(new BasicNameValuePair("form_group", id_type+""));
			nameValuePairs.add(new BasicNameValuePair("form_more_details", moreInfo));
			nameValuePairs.add(new BasicNameValuePair("image", mFilenameTemp));
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
		        				handler.sendEmptyMessage(1);
		        			} else {
		        				handler.sendEmptyMessage(3);
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
	
	private void saveLocalDataBase() throws XmlPullParserException, IOException {

		Entity ent = new Entity("tbl_guides");
		ent.setValue("title", title);
		ent.setValue("type_id", id_type);
		ent.setValue("creator", id_creator);
		Date fecha = new Date();
		ent.setValue("date",fecha);
		if (ent.save()) {
			handler.sendEmptyMessage(1);
		} else {
			handler.sendEmptyMessage(3);
		} 
		
	}
	
	
	
	    
    public void uploadFile(){
		try {
			FileInputStream fis = new FileInputStream(mPathFilenameTemp);
			HttpFileUploader htfu = new HttpFileUploader("http://www.dondereciclar.com/upload.php","noparamshere", mPathFilenameTemp);
			htfu.doStart(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
	public void setMessage(int identifier) {
		msgToWrite = mActivity.getResources().getString(identifier);
		handler.sendEmptyMessage(0);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContact() {
		return contact;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setCommentAddress(String commentAddress) {
		this.commentAddress = commentAddress;
	}

	public String getCommentAddress() {
		return commentAddress;
	}

	public void setType(int type) {
		this.id_type = type;
	}

	public int getType() {
		return id_type;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public long getIdList() {
		return idGuide;
	}

	public void setIdList(long idList) {
		this.idGuide = idList;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mActivity.writeInProgressDialog(msgToWrite);
			}
			if (msg.what == 1) {
				mActivity.endSave(true);
			}
			if (msg.what == 2) {
				mActivity.incrementProgress();
			}
			if (msg.what == 3) {
				mActivity.endSave(false);
			}
		}
	};
	
}
