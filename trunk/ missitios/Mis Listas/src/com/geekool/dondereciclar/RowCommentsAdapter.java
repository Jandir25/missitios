package com.geekool.dondereciclar;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RowCommentsAdapter extends BaseAdapter {

    private Context mContext;
    private int id;
    List<Map<String,?>> comments = new LinkedList<Map<String,?>>();
    
	public final static String COMMENTITEM_NICK = "nick";
	public final static String COMMENTITEM_TEXT = "text";
	
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de rutas
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowCommentsAdapter(Context mContext, int id)
    {
    	this.id = id;
        this.mContext = mContext;
        try {
			readComments();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public static Map<String,?> createCommentItem(String nick, String text) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(COMMENTITEM_NICK, nick);
		item.put(COMMENTITEM_TEXT, text);
		return item;
	}
    
	private void readComments() throws XmlPullParserException, IOException {
				
		String url = "http://www.dondereciclar.com/api_all_comments.php?id=" + id;
		
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
	        		if (x.getName().equals("comment")) {
	        			comments.add(createCommentItem(x.getAttributeValue(null, "nick"), x.nextText()));
	        		}
	        		
	        	}
	        	eventType = x.next();
	        }
			
		} catch (Exception e) {
			
		}
		
	}
    
    /**
     * Numero de elementos en la lista
     * 
     * @return Numero de elementos
     */
    
	@Override
	public int getCount() {
		return comments.size();
	}
	
    /**
     * Devuelve un objeto de la lista
     * 
     * @param position Posicion del elemento en la lista
     * @return Objeto
     */

	@Override
	public Object getItem(int position) {
        return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
    /**
     * Devuelve la vista de la fila
     * 
     * @param position Posicion del elemento en la lista
     * @param convertView View
     * @param parent ViewGroup
     * @return Vista
     */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String,?> item = comments.get(position);

        View v = View.inflate(mContext, R.layout.list_comment, null);
        
        TextView nick = (TextView)v.findViewById(R.id.list_comment_nick);       
        nick.setText(item.get(COMMENTITEM_NICK).toString());
        
        TextView text = (TextView)v.findViewById(R.id.list_comment_text);       
        text.setText(item.get(COMMENTITEM_TEXT).toString());

        return v;
	}

}
