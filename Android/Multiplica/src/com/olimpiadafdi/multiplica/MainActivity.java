package com.olimpiadafdi.multiplica;

import java.util.ArrayList;

import com.olimpiadafdi.multiplica.java.DBHandler;
import com.olimpiadafdi.multiplica.java.DBHelper;
import com.olimpiadafdi.multiplica.java.Item;
import com.olimpiadafdi.multiplica.java.JsonRequest;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private String input = "";
	private int value = 0;
	private Button myButton;
	private EditText myEditText;
	private TextView myTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Asociamos los widgets del layout a variables java
		this.myButton = (Button) findViewById(R.id.button1);
		this.myEditText = (EditText) findViewById(R.id.editText1);
		this.myTextView = (TextView) findViewById(R.id.result);
		this.handleButtons();
	}
	
	private void handleButtons(){
		View.OnClickListener handle_sign = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//Transformar valores
				input = myEditText.getText().toString();
				
//-----------------------------------------------------------------------------------------------
				
				//Convertir a int
				boolean converted = false;
				try{
					value = Integer.parseInt(input);
					converted = true;
					
				}catch(NumberFormatException e){
					Context context = getApplicationContext();
					//CharSequence text = "Invalid number";
					String text = context.getString(R.string.invalid_number);
					int duration = Toast.LENGTH_SHORT;
					
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				
//-----------------------------------------------------------------------------------------------
				
				//Probar que estamos conectados a internet
				ConnectivityManager connMgr = (ConnectivityManager) 
			    getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			    
			    boolean conectados = false;
			    
			    Context context = getApplicationContext();
			    String text = "";
				int duration = Toast.LENGTH_SHORT;
				
			    if (networkInfo != null && networkInfo.isConnected()) {
			        // fetch data
			    	conectados = true;
			    	text = context.getString(R.string.connected);
			    } else {
			        // display error
			    	text = context.getString(R.string.not_connected);
			    }
			    Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
//-----------------------------------------------------------------------------------------------
				
				//Enviar info
				if ((conectados) && (converted)){
					JsonRequest jsonRequest = new JsonRequest(
							"http://api.duckduckgo.com/?q=" + input + "*2&format=json&pretty=1", 
							getApplicationContext(), updateDataSuccess, updateDataError);
					jsonRequest.request();
				}
				
//-----------------------------------------------------------------------------------------------
				
			}
		};
		myButton.setOnClickListener(handle_sign);
	}
	
	private Runnable updateDataSuccess = new Runnable() { 
		public void run() { 
			Log.d("test", "success");
			//Visualizar el resultado
			DBHandler db = new DBHandler(getApplicationContext());
			ArrayList<Item> items = db.readItems();
			
			ArrayList<String> listItems = new ArrayList<String>();
			
			if (!items.isEmpty()){
				for (int i = 0; i < Math.min(items.size(), 3); i++) {
						listItems.add(items.get(i).getUrl() + ":\n"
								+ items.get(i).getText());
				}
				String toShow = items.get(0).getText();
				myTextView.setText(toShow);
			}
			else{
				Log.e("test", "Empty ArrayList MAINACTIVITY line 130");
			}
		} 
	}; 
	private Runnable updateDataError = new Runnable() { 
		public void run() { 
			Log.d("test", "error"); 
		} 
	};
}
