package com.onbts.ITSMobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.onbts.ITSMobile.UI.activities.BaseActivity;
import com.onbts.ITSMobile.UI.activities.LoginActivity;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.util.Settings;
@Deprecated
public class StartScreen  extends BaseActivity{
    
 
 

    public StartScreen(){
    }
   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.startscreen);
        
        Button exit =  (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	SyncService.getInstance().EndBackgroundSync();
                Intent intent = new Intent(StartScreen.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish(); 
            }
        });

        
        SyncService.getInstance().StartBackgroundSync(Settings.getInstance(this).getSettingAsInteger("syncTimeInterval"));
    }
    
    @Override
    public void onResume() {
        super.onResume();

        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

