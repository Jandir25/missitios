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

import android.annotation.SuppressLint;
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
import android.widget.Spinner;
import android.widget.TextView;


public class NewGuide extends Activity {

	private EditText tTitle;
	private Spinner spCategory;
	private List <Entity> categories;
	private ProgressDialog pd = null;
	private ThreadUploadGuide threadUpload;
	private long id = -1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_guide);
		//Leemos el xml de los propios recursos de la bbdd
		try {
		    DataFramework.getInstance().open(this, "com.tfc.misguias");
		} catch (Exception e) {
		    e.printStackTrace();
		}
		id = -1;
		//De momento se podrán dar de alta guías en local sin id de usuario.
		tTitle = (EditText)this.findViewById(R.id.text_guide_title_comment);
		spCategory = (Spinner) this.findViewById(R.id.type);
		categories = DataFramework.getInstance().getEntityList("tbl_guide_category");
		ArrayAdapter<Entity> adapter = new ArrayAdapter<Entity>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCategory.setAdapter(adapter);
		ImageView btnSave = (ImageView) this.findViewById(R.id.ico_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
			}
			
		});
		
		
		
		
	}
	
    public void writeInProgressDialog(String msg) {
    	if (pd!=null) pd.setMessage(msg);
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
    public void incrementProgress() {
    	if (pd!=null) pd.incrementProgressBy(1);
    }
    
	private void cancel() {
    	setResult(RESULT_OK);
        finish();
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
        threadUpload = new ThreadUploadGuide(this);
       
        threadUpload.setTitle(tTitle.getText().toString());
        threadUpload.setType(spCategory.getSelectedItemPosition()+1); 
        //El creator deberia ser el logado a través de facebook
        threadUpload.setId_creator(1);

    	Thread thread = new Thread(threadUpload);
		thread.start();    
        
    }
	
    

    
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
	
}
