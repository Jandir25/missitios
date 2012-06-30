package com.tfc.misguias;

import com.tfc.misguias.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Bitmap bmp;
	private Context mContext;
    public ImageAdapter(Context mContext, Bitmap bmp)
    {
    	this.mContext = mContext;
        this.bmp = bmp;
    }
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.list_image, null);
        
        ImageView img = (ImageView)v.findViewById(R.id.list_item_image);
        img.setImageBitmap(bmp);        
        return v;
	}

}
