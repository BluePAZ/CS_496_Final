package com.example.encryptedchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CriminalPosseList extends Activity {
    ArrayList<User> criminals;
    private static final String IP_ADDRESS = "192.168.150.87";
    private Timer t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criminal_posse_list);
        ListView v = (ListView)findViewById(R.id.listView);
        new RetrievePosse().execute();
        t1 = new Timer();
        t1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new RetrievePosse().execute();
            }
        }, 0, 5000);
    }
    private class RetrievePosse extends AsyncTask<String, String, User[] > {
        @Override
        protected User[] doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            Boolean returnValue = false;
            try {
                try {
                    // TODO: Implement method to issue put request
                    HttpClient client = new DefaultHttpClient();
                    // Construct URI
                    URI uri;
                    //url of server
                    uri = URIUtils.createURI("http", IP_ADDRESS, 8081, "/RetrievePosseList",
                            null, null);
                    // Construct request
                    HttpPost request = new HttpPost(uri);
                    // Add JSON object to request
                    //StringEntity reqEntity = new StringEntity(urls[0]);
                    //reqEntity.setContentType("application/json");
                    //request.setEntity(reqEntity);
                    // Execute request
                    HttpResponse response = client.execute(request);
                    // Parse response
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        // Copy the response body to a string
                        User[] ls = JSON.getObjectMapper().readValue(response.getEntity().getContent(), User[].class);
                        //System.out.println(ls);
                        return ls;
                    }else{
                        return null;
                    }
                    // Return null if invalid response
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (IOException e) {
                return null;
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(User[] us) {
            if(us != null) {
                if (criminals == null) {
                    criminals = new ArrayList<User>();
                }
                ArrayList<String> n = new ArrayList<String>();
                SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                //System.out.println(settings.getString("userKey", null));
                String d = settings.getString("userKey", "NONE");
                if (criminals.size() != us.length) {
                    criminals.clear();
                    for (int i = 0; i < us.length; i++) {
                        if (!us[i].getKeyValue().equals(d)) {
                            criminals.add(us[i]);
                        }
                    }
                }
                setContentView(R.layout.activity_criminal_posse_list);
                ListView v = (ListView) findViewById(R.id.listView);
                ArrayAdapter<User> ar = new ArrayAdapter<User>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, criminals) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        text.setText(criminals.get(position).getUsername());
                        return view;
                    }
                };

                v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println(criminals.get(position).getKeyValue());
                        Intent intent = new Intent(getApplicationContext(), IllegalDealing.class);
                        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                        String d = settings.getString("userKey", "NONE");
                        intent.putExtra("username", criminals.get(position).getUsername());
                        intent.putExtra("sourceKey", d);
                        intent.putExtra("destKey", criminals.get(position).getKeyValue());
                        t1.cancel();
                        t1.purge();
                        startActivity(intent);
                    }
                });
                v.setAdapter(ar);
            }
        }
    }
}
