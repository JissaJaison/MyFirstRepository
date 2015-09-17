package com.onbts.ITSMobile.UI.fragments.preference;

import util.SPHelper;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.SwitchPreference;

import com.flicksoft.Synchronization.ClientServices.CacheController;
import com.flicksoft.Synchronization.ClientServices.CacheRefreshStatistics;
import com.flicksoft.util.CallBack;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceSync;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.services.SyncService.SyncTask;
import com.onbts.ITSMobile.services.Updater;

import java.io.File;
import java.io.FileInputStream ;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.content.ContextWrapper;
import android.widget.ToggleButton;


public class UpdateServerFragment extends BaseSettingsPreferenceFragment {
    ToggleButton toggleButton1;
    //private CacheController _controller;
/*
    private OnPreferenceClickListener preferenceDialogListener = new OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.hard_load_message);
            builder.setPositiveButton(R.string.dialog_btn_ok, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runServiceSyncTask(SyncTask.HARD_SYNC_TASK);
                }
            });
            builder.setNegativeButton(R.string.dialog_btn_cancel, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartLog();
                    BackupDB();
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
    };
*/

    private OnPreferenceClickListener preferenceDialogListener = new OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {

            String key = preference.getKey();

            switch (key) {
                case "BackupDatabase":
                    BackupDB();
                    break;

                case "EnableLogging":
                    StartLog();
                    break;

                case "HardSynchronization":
                    HardSync();
                    break;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.update_server);
        toggleButton1 = (ToggleButton)getActivity().findViewById(R.id.toggle_button1);
        Preference hardSyncPref = findPreference("HardSynchronization");
        hardSyncPref.setOnPreferenceClickListener(preferenceDialogListener);

        Preference BackupPref = findPreference("BackupDatabase");
        BackupPref.setOnPreferenceClickListener(preferenceDialogListener);

        Preference EnableLoggingPref = findPreference("EnableLogging");
        EnableLoggingPref.setOnPreferenceClickListener(preferenceDialogListener);
    }

            private void HardSync() {

            Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.hard_load_message);
            builder.setPositiveButton(R.string.dialog_btn_ok, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runServiceSyncTask(SyncTask.HARD_SYNC_TASK);
                    Log.e("Hai", "From HardSync");
                    //Changes made by Jissa on Issue 10 of mobile changes.
                    //if(SyncService.getSyncStatus())
//                    toggleButton1.setEnabled(true);
//                    toggleButton1.setChecked(true);
                }
            });
            builder.setNegativeButton(R.string.dialog_btn_cancel, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    private void StartLog()
    {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            String fileName = formatter.format(now);

            File path = new File(Environment.getExternalStorageDirectory()+ "//Android//data//com.onbts.ITSMobile//logs");
            if (!path.mkdirs()) {
                path.mkdirs();
            }

            File filename = new File(Environment.getExternalStorageDirectory()+ "//Android//data//com.onbts.ITSMobile//logs//logfile-" + fileName + ".log");
            filename.createNewFile();

            String cmd = "logcat -v threadtime  -f "+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
            Log.i("Logging", "Log Started");
            Toast.makeText(getActivity(),"Logging Started" , Toast.LENGTH_LONG).show();
        }
            catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void BackupDB()
    {
        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            String fileName = formatter.format(now);

            File path = new File(sd + "//Android//data//com.onbts.ITSMobile//db");
            if (!path.mkdirs()) {
                path.mkdirs();
            }

        if (sd.canWrite()) {
            String currentDBPath = "//data//com.onbts.ITSMobile//db//ITS.db";
            String backupDBPath = "//Android//data//com.onbts.ITSMobile//db//ITS-" + fileName + ".db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            android.util.Log.i("Logging", "Logging Enabled");
            Toast.makeText(getActivity(),"Database Backup successful:" +  backupDB.toString(), Toast.LENGTH_LONG).show();
        }
    } catch (Exception e) {
         Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
    }
    }

    @Override
    public void onStart() {
        super.onStart();
        runServiceSyncTask(SyncTask.STATE_SYNC_TASK);
        if (!DbService.getInstance(getActivity()).init()) {
            findPreference("autoSynchronization").setEnabled(false);
        } else
            findPreference("autoSynchronization").setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (findPreference("autoSynchronization").isEnabled()) {
            findPreference("period").setEnabled(((SwitchPreference) findPreference("autoSynchronization")).isChecked());
        } else
            findPreference("period").setEnabled(false);
        setSummaryDataTime(findPreference("manualSynchronization"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);

        switch (key) {
            case "last_time_update":
                setSummaryDataTime(findPreference("manualSynchronization"));
                break;
            case "period":
                ListPreference listPref = (ListPreference) findPreference(key);
                startScheduleTask(Long.valueOf(listPref.getValue()));
                break;
            case "autoSynchronization":
                findPreference("period").setEnabled(((SwitchPreference) findPreference(key)).isChecked());
                if (((SwitchPreference) findPreference("autoSynchronization")).isChecked()) {
                    startScheduleTask(SPHelper.getRefreshTime(getActivity()));
                } else {
                    stopScheduleTask();
                }
                break;
        }
    }

    private void startScheduleTask(long delta) {
        Updater.init(getActivity(), true, delta);
    }

    private void stopScheduleTask() {
        Updater.init(getActivity(), false, -1);
    }

    protected void runServiceSyncTask(SyncTask task) {
        Intent intent = new Intent(getActivity(), ServiceSync.class);
        intent.putExtra(ServiceSync.KEY_KEY_TASK, task);
        getActivity().startService(intent);
    }

    private void setSummaryDataTime(Preference pref) {
        if (SPHelper.getLastUpdateTime(getActivity()) != 0) {
            java.text.DateFormat dateFormat = java.text.DateFormat
                    .getDateTimeInstance(java.text.DateFormat.MEDIUM,
                            java.text.DateFormat.MEDIUM, java.util.Locale.US);
            String lastTimeUpdate = dateFormat.format(
                    SPHelper.getLastUpdateTime(getActivity())).toString();
            pref.setSummary(lastTimeUpdate);
        }
    }
}