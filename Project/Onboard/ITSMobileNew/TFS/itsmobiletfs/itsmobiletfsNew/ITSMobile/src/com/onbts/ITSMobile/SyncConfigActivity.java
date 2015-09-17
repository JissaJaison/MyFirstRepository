package com.onbts.ITSMobile;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onbts.ITSMobile.UI.activities.BaseActivity;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.util.Settings;
@Deprecated
public class SyncConfigActivity extends BaseActivity {
    private EditText _txtUrl;
    private TextView _textViewBuildNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_config);

        // Set up the login form.
        _txtUrl = (EditText) findViewById(R.id.syncUrl);
        _txtUrl.setText(Settings.getInstance(this).getSettingAsString("url"));
        this.syncTask = SyncService.SyncTask.HARD_SYNC_TASK;

        // Display build number
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            _textViewBuildNo = (TextView) findViewById(R.id.buildNo);
            _textViewBuildNo.setText(String.format("Version %s, Build %d", pi.versionName,
                    pi.versionCode));
        } catch (NameNotFoundException e) {
            // Name not found
        }

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.getInstance(SyncConfigActivity.this).setSetting("url", _txtUrl.getText().toString());
                SyncService.getInstance().setSyncServiceUrl(
                        Settings.getInstance(SyncConfigActivity.this).getSettingAsString("url"));
            }
        });

        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.sync_config, menu);
        return true;

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
