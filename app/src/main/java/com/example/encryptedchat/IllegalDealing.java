package com.example.encryptedchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class IllegalDealing extends Activity {

    private String sourceKey;
    private String destKey;
    private String chatKey;
    private String myKey;
    private String username;
    private Tweet lastTweet;
    private Timer timer1;
    private String encryptionString = "SOMEKEY";
    private ArrayAdapter<Tweet> ar;


    private static final String IP_ADDRESS = "192.168.150.87";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getDestKey() {
        return destKey;
    }

    public void setDestKey(String destKey) {
        this.destKey = destKey;
    }

    ArrayList<Tweet> discussion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Encryption Key");
        alert.setMessage("Please Enter Key For Secrecy");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                encryptionString = input.getText().toString();
                // Do something with value!
                Tweet d = new Tweet();
                d.content = "";
                d.senderKey = sourceKey;
                d.receiverKey = destKey;
                d.chatKey = chatKey;
                lastTweet = d;
                new SendMessage().execute(d);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new GetMessage().execute(lastTweet);
            }
        }, 0, 500);
        alert.show();
        setContentView(R.layout.activity_illegal_dealing);
        Bundle extras = getIntent().getExtras();
        getActionBar().setTitle(extras.getString("username"));
        sourceKey = extras.getString("sourceKey");
        destKey = extras.getString("destKey");
        chatKey = "NULL";
        ListView lv = (ListView)findViewById(R.id.listView2);
        if(discussion == null){
            discussion = new ArrayList<Tweet>();
        }
        ar = new ArrayAdapter<Tweet>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, discussion) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CEncrypt.setKey(encryptionString);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setText(CEncrypt.decryptString(discussion.get(position).content));
                if(discussion.get(position).senderKey.equals(sourceKey)) {
                    text.setGravity(Gravity.RIGHT);
                }
                else{
                    text.setGravity(Gravity.LEFT);
                }
                return view;
            }
        };
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
        lv.setAdapter(ar);

        Button bs1 = (Button)findViewById(R.id.buttonSmall1);
        bs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tweet d = new Tweet();
                EditText x = (EditText)findViewById(R.id.editText2);
                CEncrypt.setKey(encryptionString);
                d.content = CEncrypt.encryptString(x.getText().toString());
                d.senderKey = sourceKey;
                d.receiverKey = destKey;
                d.chatKey = chatKey;
                lastTweet = d;
                x.setText("");
                ListView lv = (ListView)findViewById(R.id.listView2);
                lv.setSelection(lv.getCount() - 1);
                new SendMessage().execute(d);
                if(discussion.size() != 0) {
                    discussion.add(d);
                }
                if(!d.chatKey.equals("NULL")) {
                    new GetMessage().execute(d);
                }
            }
        });
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.illegal_dealing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SendMessage extends AsyncTask<Tweet, String, String> {
        @Override
        protected String doInBackground(Tweet... msg) {
            // params comes from the execute() call: params[0] is the url.
            Boolean returnValue = false;
            try {
                try {
                    // TODO: Implement method to issue put request
                    HttpClient client = new DefaultHttpClient();
                    // Construct URI
                    URI uri;
                    //url of server
                    uri = URIUtils.createURI("http", IP_ADDRESS, 8081, "/ReceiveMessage",
                            null, null);
                    // Construct request
                    HttpPost request = new HttpPost(uri);
                    // Add JSON object to request
                    StringWriter sw = new StringWriter();
                    JSON.getObjectMapper().writeValue(sw, msg[0]);
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
                        //System.out.println(sb.toString());
                        return sb.toString();
                    }else{
                        return "ERROR";
                    }
                    // Return null if invalid response
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "NULL";
            }
            return "NULL";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String s) {
            if(s.equals("NULL") || s.equals("ERROR")){

            }else{
                chatKey = s;
                lastTweet.chatKey = s;
            }

        }
    }
    private class GetMessage extends AsyncTask<Tweet, String, Tweet[]> {
        @Override
        protected Tweet[] doInBackground(Tweet... msg) {
            // params comes from the execute() call: params[0] is the url.
            Boolean returnValue = false;
            try {
                try {
                    // TODO: Implement method to issue put request
                    HttpClient client = new DefaultHttpClient();
                    // Construct URI
                    URI uri;
                    //url of server
                    uri = URIUtils.createURI("http", IP_ADDRESS, 8081, "/GetMessages",
                            null, null);
                    // Construct request
                    HttpPost request = new HttpPost(uri);
                    // Add JSON object to request
                    StringWriter sw = new StringWriter();
                    JSON.getObjectMapper().writeValue(sw, msg[0]);
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
                        return JSON.getObjectMapper().readValue(entity.getContent(), Tweet[].class);
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
        protected void onPostExecute(Tweet[] s) {
            if(s != null) {
                if(discussion.size() != s.length) {
                    for (int i = discussion.size(); i < s.length; i++) {
                        discussion.add(s[i]);
                    }
                    ar.notifyDataSetChanged();
                }
            }
        }
    }




}
