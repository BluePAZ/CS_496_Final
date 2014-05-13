package com.example.encryptedchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

public class Main extends Activity {
	
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String IP_ADDRESS = "192.168.150.87";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    //System.out.println(settings.getString("userKey", null));
	    String d = settings.getString("userKey", "NONE");
        //System.out.println("PRINT KEY ON STATRTUP: "+ d);

        new CheckUserKey().execute(d);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void doSomething(View view){
		EditText user = (EditText)findViewById(R.id.userNameField);
		//EditText password = (EditText)findViewById(R.id.enterPasswordField);
		new RegisterUser().execute(user.getText().toString());
		
	}
	private class RegisterUser extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
        	Boolean returnValue = false;
            try {
				try {
					// TODO: Implement method to issue put request
					HttpClient client = new DefaultHttpClient();
					// Construct URI
					URI uri;
					//url of server
					uri = URIUtils.createURI("http", IP_ADDRESS, 8081, "/Register",
							    null, null);
					// Construct request
					HttpPost request = new HttpPost(uri);
					// Create JSON object from username
					User u1 = new User();
					u1.setUsername(urls[0]);
					StringWriter sw = new StringWriter();
					JSON.getObjectMapper().writeValue(sw, u1);
					// Add JSON object to request
					StringEntity reqEntity = new StringEntity(sw.toString());
					reqEntity.setContentType("application/json");
					request.setEntity(reqEntity);
					// Execute request
					HttpResponse response = client.execute(request);
					// Parse response
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						// Copy the response body to a string
						HttpEntity entity = response.getEntity();
						// Parse JSON
						InputStream in = entity.getContent()/* your InputStream */;
						InputStreamReader is = new InputStreamReader(in);
						StringBuilder sb=new StringBuilder();
						BufferedReader br = new BufferedReader(is);
						String read = br.readLine();
						while(read != null) {
						    //System.out.println(read);
						    sb.append(read);
						    read =br.readLine();
						}
						System.out.println(sb.toString());
						return sb.toString();
					}
                    else{
                        return "NULL";
                    }
					// Return null if invalid response					
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
            } catch (IOException e) {
            	return "NULL";
            }
			return "NULL";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String s) {
        	// We need an Editor object to make preference changes.
            // All objects are from android.context.Context
            if(!s.equals("NULL")) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("userKey", s);
                // Commit the edits!
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), CriminalPosseList.class);
                startActivity(intent);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Criminal Streetname Taken", Toast.LENGTH_SHORT);
                toast.show();
            }
            
       }
    }
    private class CheckUserKey extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            Boolean returnValue = false;
            try {
                try {
                    // TODO: Implement method to issue put request
                    HttpClient client = new DefaultHttpClient();
                    // Construct URI
                    URI uri;
                    //url of server
                    uri = URIUtils.createURI("http", IP_ADDRESS, 8081, "/VerifyUserKey",
                            null, null);
                    // Construct request
                    HttpPost request = new HttpPost(uri);
                    // Add JSON object to request
                    StringEntity reqEntity = new StringEntity(urls[0]);
                    reqEntity.setContentType("application/json");
                    request.setEntity(reqEntity);
                    // Execute request
                    HttpResponse response = client.execute(request);
                    // Parse response
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                        // Copy the response body to a string
                        System.out.println("I AM HERE");
                        return "NULL";
                    }else{
                        return "ERROR";
                    }
                    // Return null if invalid response
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (IOException e) {
                return "NULL";
            }
            return "NULL";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String s) {
            // We need an Editor object to make preference changes.
            // All objects are from android.context.Context
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            if(s == "NULL"){
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), CriminalPosseList.class);
                startActivity(intent);

            }
            else{
                editor.remove("userKey");
                editor.commit();
                //Intent intent = new Intent(getApplicationContext(), Main.class);
                //startActivity(intent);
            }
            // Commit the edits!
        }
    }
    
}
