package com.onbts.ITSMobile.UI.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

import com.onbts.ITSMobile.R;

@Deprecated
public class AlertListActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.alert_list_radio);
		//setListAdapter(adapter);
	}
}
