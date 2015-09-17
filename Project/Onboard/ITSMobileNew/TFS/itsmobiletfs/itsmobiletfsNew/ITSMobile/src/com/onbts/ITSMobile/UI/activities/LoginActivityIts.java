package com.onbts.ITSMobile.UI.activities;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.view.MenuItem;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.fragments.LoginFragment;
import com.onbts.ITSMobile.interfaces.OnLoginListener;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.SyncService;

import java.io.File;

/**
 * Created by tigre on 04.04.14.
 */
public class LoginActivityIts extends com.onbts.ITSMobile.UI.activities.base.BaseActivity implements OnLoginListener {
    private LoginFragment loginFragment;
    private UserModel user;

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Crashlytics.start(this);
        String path = DbService.getDbFilePath(this);
        long freeSpace = 0;
        if (path != null) {
            File file = new File(path);
            try {
                StatFs stat = new StatFs(file.getParent());
                if (Build.VERSION.SDK_INT >= 18) {
                    long blockSize = stat.getBlockSizeLong();
                    long availableBlocks = stat.getAvailableBlocksLong();
                    freeSpace = blockSize * availableBlocks;
                } else {
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();
                    freeSpace = blockSize * availableBlocks;
                }
            } catch (Exception e) {
//                Crashlytics.logException(e);
                e.printStackTrace();
                freeSpace = file.getFreeSpace();
                if (freeSpace == 0) {
                    showErrorSpace(false);
                    freeSpace = 100000000L;
                }

            }

            if (freeSpace > 1024L * 1024L * 50L) {
                if (savedInstanceState == null) {
                    setContentView(R.layout.ac_login);
                    loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                } else {
                    user = savedInstanceState.getParcelable("user");
                    setContentView(R.layout.ac_login);
                    loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                }
            } else {
                showErrorSpace(true);
            }
        } else {
            showErrorSpace(true);
        }
    }

    private void showErrorSpace(final boolean canRead) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Error");
        builder.setMessage(canRead ? "Not enough free space on device" : "Failed to determine the free space on the device.\n" +
                "Work can be unstable.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (canRead)
                    finish();
            }
        }).show();
    }

    @Override
    protected void onHandelDBMessage(DBRequest request) {
        if (request.getType() == DBRequest.DBRequestType.LOGIN) {
            UserModel user = (request.getModel() != null && request.getModel() instanceof UserModel) ? (UserModel) request.getModel() : null;
            if (loginFragment != null)
                loginFragment.onEnterUser(user);
            if (user != null && user.getId() > 0) {
                //Toast.makeText(getApplicationContext(), "To create an issue manually swipe button right to left", Toast.LENGTH_SHORT).show();
                // toast modified by Jissa on issue 6 of itsmobile create.

                Intent intent = new Intent(this, IssueListActivityIts.class);
                intent.putExtra("user", request.getModel());
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onHandleServiceMessage(SyncService.SyncTaskState currentTask, String message) {

    }

    @Override
    protected void onHandleUpdaterTimer(long timeLeft) {

    }

    @Override
    public void onSetUser(UserModel user) {

    }

    @Override
    public void onLogin(String login, String psw) {
        sendDBRequest(getDB().putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.LOGIN))
                .putExtra("login", login).putExtra("psw", psw));
    }

    @Override
    public UserModel onGetUser() {
        return user;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onNeedSync() {

    }
}
