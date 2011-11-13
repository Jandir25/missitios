package com.geekool.dondereciclar;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

public class RowInfoLocationAdapter extends BaseAdapter {

	public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
	public final ArrayAdapter<String> headers;
	public final Map<Integer,String> positionsButtons = new LinkedHashMap<Integer, String>();
	//public final ArrayList<Integer> positionsButtons;
	
	public final static int MAX_COMMENTS = 3;
	
	public final static int TYPE_SECTION_HEADER = 0;
	public final static int TYPE_SECTION_BUTTON = 1;
	public final static int TYPE_SECTION_DATA = 2;
	
	public final static String INFOITEM_TITLE = "title";
	public final static String INFOITEM_INFO = "info";
	
	public final static String COMMENTITEM_NICK = "nick";
	public final static String COMMENTITEM_TEXT = "text";
	
    private InfoLocation mInfoLocation;
    private int id = -1;
    
    private int lastPosition = 0;
	
    /**
     * Constructor - Adaptador que crea la vista de cada una de las
     * filas de la lista de rutas
     * 
     * @param mContext Context
     * @param elements Lista de elementos
     */
    
    public RowInfoLocationAdapter(InfoLocation mInfoLocation, int id)
    {
        this.mInfoLocation = mInfoLocation;
        
        headers = new ArrayAdapter<String>(mInfoLocation, R.layout.list_header);
        
        this.id = id;
    }
    
	public static Map<String,?> createInfoItem(String title, String data) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(INFOITEM_TITLE, title);
		item.put(INFOITEM_INFO, data);
		return item;
	}
	
	public static Map<String,?> createCommentItem(String nick, String text) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(COMMENTITEM_NICK, nick);
		item.put(COMMENTITEM_TEXT, text);
		return item;
	}
    
    public void createListView() {	
		lastPosition = 0;
    	try {
    		lastPosition = mInfoLocation.readLocation();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		addSection(mInfoLocation.getString(R.string.inappropriate), new ArrayAdapter<String>(mInfoLocation,
				R.layout.list_item, new String[] { mInfoLocation.getString(R.string.poi_inappropriate) }));
    	
		lastPosition += 2;
		
		positionsButtons.put(lastPosition, "inappropriate");
		
		addSection(mInfoLocation.getString(R.string.publish), new ArrayAdapter<String>(mInfoLocation,
				R.layout.list_item, new String[] { mInfoLocation.getString(R.string.write_comment) }));
		
		lastPosition += 2;
		
		positionsButtons.put(lastPosition, "publish");
		
		try {
			readComments();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
    
	public void addSection(String section, Adapter adapter) {
		this.headers.add(section);
		this.sections.put(section, adapter);
	}

	public Object getItem(int position) {
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position == 0) return section;
			if(position < size) return adapter.getItem(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}
    
	private void readComments() throws XmlPullParserException, IOException {
		
		List<Map<String,?>> comments = new LinkedList<Map<String,?>>();
		
		String url = "http://www.dondereciclar.com/api_comments.php?id=" + id;
		
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
	        	if (comments.size()<MAX_COMMENTS) {
		        	if ( eventType == XmlPullParser.START_TAG ) {
		        		if (x.getName().equals("comment")) {
		        			comments.add(createCommentItem(x.getAttributeValue(null, "nick"), x.nextText()));
		        		}
		        		
		        	}
	        	}
	        	eventType = x.next();
	        }
			
		} catch (Exception e) {
			
		}
		
		if (comments.size()<=0) {
			addSection(mInfoLocation.getString(R.string.comments), new ArrayAdapter<String>(mInfoLocation,
					R.layout.list_item_small, new String[] { mInfoLocation.getString(R.string.no_comments) }));
			lastPosition += 2;
		} else {
			addSection(mInfoLocation.getString(R.string.comments), new SimpleAdapter(mInfoLocation, comments, R.layout.list_comment,
				new String[] { RowInfoLocationAdapter.COMMENTITEM_NICK, RowInfoLocationAdapter.COMMENTITEM_TEXT }, new int[] { R.id.list_comment_nick, R.id.list_comment_text }));
			lastPosition += comments.size()+1;
		}
		
		if (comments.size()>=MAX_COMMENTS) {
			addSection(mInfoLocation.getString(R.string.more_comments), new ArrayAdapter<String>(mInfoLocation,
					R.layout.list_item, new String[] { mInfoLocation.getString(R.string.access_more_comments) }));
			lastPosition += 2;
			positionsButtons.put(lastPosition, "more_comments");
		}
		
	}
	
	
	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for(Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for(Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}
		
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		return (positionsButtons.containsKey(position));
	}
	
	public int getItemViewType(int position) {
		int type = 1;
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			if(position == 0) return TYPE_SECTION_HEADER;
			if(position < size) return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position == 0) return headers.getView(sectionnum, convertView, parent);
			if(position < size) return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}
    

}