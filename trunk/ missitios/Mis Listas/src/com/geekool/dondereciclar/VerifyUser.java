package com.geekool.dondereciclar;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class VerifyUser extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifyuser);
        
        TextView tv = (TextView) this.findViewById(R.id.txt_user);
        
        String name = "";
		try {
			name = Utils.isUser(this);
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        String t = this.getString(R.string.verify_user_1) + " ";
		if (name.equals(""))
			t += this.getString(R.string.verify_user_2_false);
		else
			t += name + " " + this.getString(R.string.verify_user_2_true);
        
		tv.setText(t);
		
    }
    
}
