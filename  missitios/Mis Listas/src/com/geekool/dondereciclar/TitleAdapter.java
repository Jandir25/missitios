package com.geekool.dondereciclar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleAdapter extends BaseAdapter {
	private String mTitle;
	private int mIcoResource;
	private Context mContext;
    public TitleAdapter(Context mContext, String mTitle, int mIcoResource)
    {
    	this.mContext = mContext;
        this.mTitle = mTitle;
        this.mIcoResource = mIcoResource;
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
        View v = View.inflate(mContext, R.layout.list_title, null);
        
        ImageView img = (ImageView)v.findViewById(R.id.list_title_ico);
        img.setImageResource(mIcoResource);
        
        TextView text = (TextView)v.findViewById(R.id.list_title_text);
        text.setText(mTitle);
        
        return v;
	}

}
