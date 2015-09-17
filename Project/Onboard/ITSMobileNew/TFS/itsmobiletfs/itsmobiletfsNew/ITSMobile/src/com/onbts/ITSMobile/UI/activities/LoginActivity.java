package com.onbts.ITSMobile.UI.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.StartScreen;
import com.onbts.ITSMobile.SyncConfigActivity;
import com.onbts.ITSMobile.UI.dialogs.VerifyPassword;
import com.onbts.ITSMobile.UI.dialogs.VerifyPassword.VerifyPasswordDialogListener;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.util.Settings;
// 

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@Deprecated
public class LoginActivity extends Activity implements VerifyPasswordDialogListener {


    public static final String TAG = "Inspections";
    // Values for email and password at the time of the login attempt.
    private String mUserName;
    private String mPassword;

    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.getInstance(this).setProperOrientation(this);

        setContentView(R.layout.activity_login);


        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.username);
        mUserNameView.setText("");

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setText("");

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUserName = mUserNameView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user.
        if (TextUtils.isEmpty(mUserName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //0: succussful, 1: no user, 2: password wrong
            int result = DbService.getInstance(this).checkLogin(mUserName, mPassword);
            if (result == 1) {

                mUserNameView.setError(getString(R.string.error_user_not_found));
                focusView = mUserNameView;
                focusView.requestFocus();
            } else if (result == 2) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                focusView = mPasswordView;
                focusView.requestFocus();
            } else { // logged in ok
                Intent mainActivity = new Intent(this, StartScreen.class);
                startActivity(mainActivity);
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first


    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

        enableScreen();
    }

    private void enableScreen() {

    	/*
    	if (DbService.getInstance(null).isAvailable())
    	{
    	 	 UiFunctions.setViewGroupEnebled((ViewGroup) getWindow().getDecorView().getRootView(), true);

    	}
    	else
    	{
    	 	UiFunctions.setViewGroupEnebled((ViewGroup) getWindow().getDecorView().getRootView(), false);
    	}
    	*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        enableScreen();
        // Activity being restarted from stopped state
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    private String getDeviceId() {
        try {
            //       final TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            //     if (mTelephony.getDeviceId() != null)
            //         return mTelephony.getDeviceId(); //*** use for mobiles
            //     else
            return Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID); //*** use for tablets
        } catch (Exception e) {
            Log.e("Inspections", "deviceid error", e);
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
                VerifyPassword newFragment = VerifyPassword.newInstance();

                newFragment.show(getFragmentManager(), "te");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFinishVerifyPasswordDialog(Result theResult) {
        if (theResult == Result.OK) {
            Intent mainActivity = new Intent(this, SyncConfigActivity.class);
            startActivity(mainActivity);
        }


    }
}
