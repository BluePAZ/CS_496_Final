package com.example.encryptedchat;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TextView;

public class HomeActivity extends ActivityGroup {
    public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_criminal_posse_list);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    //System.out.println(settings.getString("userKey", null));
	    String d = settings.getString("userKey", "NONE");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
