package com.onbts.ITSMobile.UI.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.SettingsPreferenceActivity;
import com.onbts.ITSMobile.UI.fragments.base.BaseLoginFragment;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DbService;

import util.SPHelper;

public class LoginFragment extends BaseLoginFragment implements OnClickListener {
    private Button btnLogin;
    private EditText etLogin, etPassword;
    private AlertDialog alert;
    private TextView tvVersionLabel;
    //Here we store our app version
    private String version = "Cant get version";

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        View v = inflater.inflate(R.layout.frag_login, container, false);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        etLogin = (EditText) v.findViewById(R.id.etLogin);
        etPassword = (EditText) v.findViewById(R.id.etPassword);
        etLogin.requestFocus();
        btnLogin.setOnClickListener(this);

        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersionLabel = (TextView) v.findViewById(R.id.tvVersionLabel);
        tvVersionLabel.setText(version);
        return v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (etLogin.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    etLogin.setError("Enter your login");
                    etPassword.setError("Enter your password");
                    return;
                } else {
                    etLogin.setError(null);
                    etPassword.setError(null);
                    mListener.onLogin(etLogin.getText().toString(), etPassword.getText().toString());
//                    auth(etLogin.getText().toString(), etPassword.getText().toString());
                }
                break;

            default:
                break;
        }
    }

    @Deprecated
    public void auth(String login, String password) {
        // 0: succussful, 1: no user, 2: password wrong
//        int result = DbService.getInstance(getActivity()).checkLogin(login, password);
        UserModel user = DbService.getInstance(getActivity()).checkLoginUser(login, password);
        if (user == null) {
            etLogin.setError("No such user");
        } else if (user.getId() < 0) {
            etPassword.setError("Wrong password");
        } else { // logged in ok
            mListener.onSetUser(user);
//            issueListCallBack.onStartIssueList();
            hideSoftKeyboard();
        }



    }

    public void onEnterUser(UserModel model) {

        if (model == null) {
            etLogin.setError("No such user");
        } else if (model.getId() < 0) {
            etPassword.setError("Wrong password");
        } else { // logged in ok

            mListener.onSetUser(model);
//            issueListCallBack.onStartIssueList();
            hideSoftKeyboard();
        }
    }

    private void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        DbService.getInstance(getActivity().getApplicationContext()).init();
        SPHelper.clearSP(getActivity());
//        issueListCallBack.onTitleChange("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_showSyncOptions:
                showDialog();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public AlertDialog showDialog() {
        Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.password);
        final EditText inputText = new EditText(getActivity());
        inputText.setSingleLine();
        inputText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) ;
                        // Capture soft enters in a singleLine EditText that is the last
                        // EditText.
                        // searchButton();
                    else if (actionId == EditorInfo.IME_ACTION_NEXT) ;
                        // Capture soft enters in other singleLine EditTexts
                        // searchButton();
                    else
                        return false; // Let system handle all other null KeyEvents
                } else if (actionId == EditorInfo.IME_NULL) {
                    // Capture most soft enters in multi-line EditTexts and all hard
                    // enters.
                    // They supply a zero actionId and a valid KeyEvent rather than
                    // a non-zero actionId and a null event like the previous cases.
                    if (event.getAction() == KeyEvent.ACTION_DOWN) ;
                        // We capture the event when key is first pressed.
                    else
                        return true; // We consume the event when the key is released.
                } else
                    return false;
                // We let the system handle it when the listener
                // is triggered by something that wasn't an enter.

                // Code from this point on will execute whenever the user
                // presses enter in an attached view, regardless of position,
                // keyboard, or singleLine status.
                checkAndStartActivity(inputText);
                alert.dismiss();
                return false;
            }
        });
        inputText.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        builder.setView(inputText);
        builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkAndStartActivity(inputText);
            }
        });

        builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert = builder.create();
        alert.show();
        return alert;
    }

    public void checkAndStartActivity(EditText inputText) {
        String password = inputText.getText().toString();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (password.equals(preferences.getString("application_password", "its"))) {
            Intent intentToSettings = new Intent(getActivity(), SettingsPreferenceActivity.class);
            startActivity(intentToSettings);
        } else {
            Toast toast = Toast.makeText(getActivity(), "Invalid password!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
